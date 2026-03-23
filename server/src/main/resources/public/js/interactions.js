import { createOrder, createPickupSlot, createProduct, signIn, signUp } from "./api.js";
import { authState, dom, state } from "./state.js";
import { applyAuthUiState, clearAuthState, saveAuthState } from "./auth.js";
import { refreshPageData } from "./data.js";
import { renderAll } from "./render.js";

function setAdminFeedback(message, isError = false) {
	if (!dom.adminControlsFeedback) return;
	dom.adminControlsFeedback.textContent = message;
	dom.adminControlsFeedback.style.color = isError ? "#bb3a1d" : "var(--success)";
}

function setProductFeedback(message, isError = false) {
	if (!dom.addProductFeedback) return;
	dom.addProductFeedback.textContent = message;
	dom.addProductFeedback.style.color = isError ? "#bb3a1d" : "var(--success)";
}

function setPickupSlotFeedback(message, isError = false) {
	if (!dom.addPickupSlotFeedback) return;
	dom.addPickupSlotFeedback.textContent = message;
	dom.addPickupSlotFeedback.style.color = isError ? "#bb3a1d" : "var(--success)";
}

function setOrderFeedback(message, isError = false) {
	if (!dom.orderFeedback) return;
	dom.orderFeedback.textContent = message;
	dom.orderFeedback.style.color = isError ? "#bb3a1d" : "var(--success)";
}

function ensureAdminAccess() {
	if (authState.isAdmin) return true;
	setAdminFeedback("Only admins can use dashboard configuration actions.", true);
	return false;
}

function getCartItemsPayload() {
	return Object.entries(state.cart)
		.filter((entry) => entry[1] > 0)
		.map((entry) => ({ product_id: Number(entry[0]), quantity: entry[1] }));
}

export async function placeOrder() {
	setOrderFeedback("");
	const items = getCartItemsPayload();
	if (items.length === 0) {
		setOrderFeedback("Your cart is empty.", true);
		return;
	}

	if (!state.selectedSlot) {
		setOrderFeedback("Please select a pickup slot.", true);
		return;
	}

	const emailInput = dom.customerEmailInput;
	const customerEmail = emailInput ? emailInput.value.trim() : "";
	
	if (!customerEmail) {
		setOrderFeedback("Please enter your email to place the order.", true);
		if (emailInput) emailInput.focus();
		return;
	}

	try {
        const slotObj = state.slots.find(s => s.id === state.selectedSlot);
        const slotLabel = slotObj ? `${slotObj.day} (${slotObj.startTime} - ${slotObj.endTime})` : String(state.selectedSlot);

		await createOrder({
			customer_email: customerEmail,
			pickup_slot: slotLabel,
			pickup_date: new Date().toISOString().slice(0, 10),
			items
		});

		state.cart = {};
		await refreshPageData();
		setOrderFeedback("Order successfully placed!");
	} catch (error) {
		console.error(error);
		setOrderFeedback(`Could not place order: ${error.message}`, true);
	}
}

export function wirePopups() {
	if (dom.navSignInBtn && dom.signInPopup) {
		dom.navSignInBtn.addEventListener("click", () => {
			if (authState.email) {
				clearAuthState();
				applyAuthUiState();
				refreshPageData();
				return;
			}
			dom.signInPopup.style.display = "flex";
		});
	}

	if (dom.openCreateAccountBtn && dom.createAccountPopup) {
		dom.openCreateAccountBtn.addEventListener("click", () => {
			dom.signInPopup.style.display = "none"; dom.createAccountPopup.style.display = "flex";
		});
	}

	if (dom.closeSignInPopupBtn && dom.signInPopup) {
		dom.closeSignInPopupBtn.addEventListener("click", () => {
			dom.signInPopup.style.display = "none";
		});
	}

	if (dom.closeCreatePopupBtn && dom.createAccountPopup) {
		dom.closeCreatePopupBtn.addEventListener("click", () => {
			dom.createAccountPopup.style.display = "none";
		});
	}

	if (dom.signInForm) {
		dom.signInForm.addEventListener("submit", async (event) => {
			event.preventDefault();

			const email = dom.signInForm.elements.namedItem("email")?.value || "";
			const password = dom.signInForm.elements.namedItem("password")?.value || "";

			try {
				const user = await signIn({ email, password });
				authState.email = user.email;
				authState.isAdmin = Boolean(user.is_admin);
				saveAuthState();
				applyAuthUiState();
				dom.signInForm.reset();
				dom.signInPopup.style.display = "none";
				await refreshPageData();
			} catch (error) {
				console.error(error);
				window.showAlert(`Could not sign in: ${error.message}`);
			}
		});
	}

	if (dom.createAccountForm) {
		dom.createAccountForm.addEventListener("submit", async (event) => {
			event.preventDefault();

			const email = dom.createAccountForm.elements.namedItem("new-email")?.value || "";
			const password = dom.createAccountForm.elements.namedItem("new-password")?.value || "";

			try {
				await signUp({ email, password });
				const user = await signIn({ email, password });
				authState.email = user.email;
				authState.isAdmin = Boolean(user.is_admin);
				saveAuthState();
				applyAuthUiState();

				dom.createAccountForm.reset();
				dom.createAccountPopup.style.display = "none";
				await refreshPageData();
			} catch (error) {
				console.error(error);
				window.showAlert(`Could not create account or sign in: ${error.message}`);
			}
		});
	}
}

export function wireActions() {
	if (dom.openAddProductBtn && dom.addProductPopup) {
		dom.openAddProductBtn.addEventListener("click", () => {
			dom.addProductPopup.style.display = "flex";
			const firstField = dom.adminProductForm?.elements.namedItem("name");
			if (firstField && typeof firstField.focus === "function") {
				firstField.focus();
			}
		});
	}

	if (dom.closeAddProductPopupBtn && dom.addProductPopup) {
		dom.closeAddProductPopupBtn.addEventListener("click", () => {
			dom.addProductPopup.style.display = "none";
		});
	}

	if (dom.openAddPickupSlotBtn && dom.addPickupSlotPopup) {
		dom.openAddPickupSlotBtn.addEventListener("click", () => {
			dom.addPickupSlotPopup.style.display = "flex";
			const firstField = dom.adminSlotForm?.elements.namedItem("day");
			if (firstField && typeof firstField.focus === "function") {
				firstField.focus();
			}
		});
	}

	if (dom.closeAddPickupSlotPopupBtn && dom.addPickupSlotPopup) {
		dom.closeAddPickupSlotPopupBtn.addEventListener("click", () => {
			dom.addPickupSlotPopup.style.display = "none";
		});
	}

	        if (dom.openOrderFiltersBtn && dom.orderFiltersPopup) {
                dom.openOrderFiltersBtn.addEventListener("click", () => {
                        dom.orderFiltersPopup.style.display = "flex";
                });
        }

        if (dom.closeOrderFiltersPopupBtn && dom.orderFiltersPopup) {
                dom.closeOrderFiltersPopupBtn.addEventListener("click", () => {
                        dom.orderFiltersPopup.style.display = "none";
                });
        }

        if (dom.openProductFiltersBtn && dom.productFiltersPopup) {
                dom.openProductFiltersBtn.addEventListener("click", () => {
                        dom.productFiltersPopup.style.display = "flex";
                });
        }

        if (dom.closeProductFiltersPopupBtn && dom.productFiltersPopup) {
                dom.closeProductFiltersPopupBtn.addEventListener("click", () => {
                        dom.productFiltersPopup.style.display = "none";
                });
        }

        if (dom.openPickupSlotFiltersBtn && dom.pickupSlotFiltersPopup) {
                dom.openPickupSlotFiltersBtn.addEventListener("click", () => {
                        dom.pickupSlotFiltersPopup.style.display = "flex";
                });
        }

        if (dom.closePickupSlotFiltersPopupBtn && dom.pickupSlotFiltersPopup) {
                dom.closePickupSlotFiltersPopupBtn.addEventListener("click", () => {
                        dom.pickupSlotFiltersPopup.style.display = "none";
                });
        }

		if (dom.checkoutBtn) {
		dom.checkoutBtn.addEventListener("click", placeOrder);
	}

	if (dom.clearBasketBtn) {
		dom.clearBasketBtn.addEventListener("click", () => {
			state.cart = {};
			renderAll();
		});
	}

	if (dom.adminProductForm) {
		dom.adminProductForm.addEventListener("submit", async (event) => {
			event.preventDefault();
			if (!ensureAdminAccess()) return;

			const name = dom.adminProductForm.elements.namedItem("name")?.value || "";
			const description = dom.adminProductForm.elements.namedItem("description")?.value || "";
			const price = Number(dom.adminProductForm.elements.namedItem("price")?.value || "0");
			const stock = Number(dom.adminProductForm.elements.namedItem("stock")?.value || "0");
			const isActive = Boolean(dom.adminProductForm.elements.namedItem("is-active")?.checked);

			if (Number.isNaN(price) || price < 0) {
				setProductFeedback("Price must be a non-negative number.", true);
				return;
			}

			if (!Number.isInteger(stock) || stock < 0) {
				setProductFeedback("Stock must be a non-negative integer.", true);
				return;
			}

			try {
				await createProduct({
					name,
					description,
					price_cents: Math.round(price * 100),
					stock,
					is_active: isActive
				});
				dom.adminProductForm.reset();
				setProductFeedback("Product created successfully.");
				setTimeout(() => {
					if (dom.addProductPopup) {
						dom.addProductPopup.style.display = "none";
					}
				}, 1500);
				await refreshPageData();
			} catch (error) {
				console.error(error);
				setProductFeedback(`Could not create product: ${error.message}`, true);
			}
		});
	}

	if (dom.adminSlotForm) {
		dom.adminSlotForm.addEventListener("submit", async (event) => {
			event.preventDefault();
			if (!ensureAdminAccess()) return;

			const day = dom.adminSlotForm.elements.namedItem("day")?.value || "";
			const startTime = dom.adminSlotForm.elements.namedItem("start-time")?.value || "";
			const endTime = dom.adminSlotForm.elements.namedItem("end-time")?.value || "";
			const capacity = Number(dom.adminSlotForm.elements.namedItem("capacity")?.value || "0");
			const isEnabled = Boolean(dom.adminSlotForm.elements.namedItem("is-enabled")?.checked);

			if (!Number.isInteger(capacity) || capacity <= 0) {
				setPickupSlotFeedback("Capacity must be a positive integer.", true);
				return;
			}

			try {
				await createPickupSlot({
					day,
					start_time: startTime,
					end_time: endTime,
					capacity,
					is_enabled: isEnabled
				});
				dom.adminSlotForm.reset();
				setPickupSlotFeedback("Pickup slot created successfully.");
				setTimeout(() => {
					if (dom.addPickupSlotPopup) {
						dom.addPickupSlotPopup.style.display = "none";
					}
				}, 1500);
				await refreshPageData();
			} catch (error) {
				console.error(error);
				setPickupSlotFeedback(`Could not create pickup slot: ${error.message}`, true);
			}
		});
	}
}


