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
	const items = getCartItemsPayload();
	if (items.length === 0) {
		alert("Your cart is empty.");
		return;
	}

	if (!state.selectedSlot) {
		alert("Please select a pickup slot.");
		return;
	}

	const customerEmail = prompt("Customer email for this order:", authState.email || "customer@bakery.com");
	if (!customerEmail) return;

	try {
		await createOrder({
			customer_email: customerEmail,
			pickup_slot: state.selectedSlot,
			pickup_date: new Date().toISOString().slice(0, 10),
			items
		});

		state.cart = {};
		await refreshPageData();
		alert("Order successfully placed.");
	} catch (error) {
		console.error(error);
		alert(`Could not place order: ${error.message}`);
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

	if (dom.navCreateAccountBtn && dom.createAccountPopup) {
		dom.navCreateAccountBtn.addEventListener("click", () => {
			dom.createAccountPopup.style.display = "flex";
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
				dom.signInForm.reset();
				dom.signInPopup.style.display = "none";
				await refreshPageData();
			} catch (error) {
				console.error(error);
				alert(`Could not sign in: ${error.message}`);
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
				dom.createAccountForm.reset();
				dom.createAccountPopup.style.display = "none";
				alert("Account created. You can now sign in.");
			} catch (error) {
				console.error(error);
				alert(`Could not create account: ${error.message}`);
			}
		});
	}
}

export function wireActions() {
	if (dom.restartBtn) {
		dom.restartBtn.textContent = "Refresh Live Data";
		dom.restartBtn.addEventListener("click", refreshPageData);
	}

	if (dom.startOrderBtn) {
		dom.startOrderBtn.addEventListener("click", placeOrder);
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

	if (dom.viewDashboardBtn) {
		dom.viewDashboardBtn.addEventListener("click", () => {
			document.getElementById("dashboard")?.scrollIntoView({ behavior: "smooth" });
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
				setAdminFeedback("Price must be a non-negative number.", true);
				return;
			}

			if (!Number.isInteger(stock) || stock < 0) {
				setAdminFeedback("Stock must be a non-negative integer.", true);
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
				setAdminFeedback("Product created successfully.");
				await refreshPageData();
			} catch (error) {
				console.error(error);
				setAdminFeedback(`Could not create product: ${error.message}`, true);
			}
		});
	}

	if (dom.adminSlotForm) {
		dom.adminSlotForm.addEventListener("submit", async (event) => {
			event.preventDefault();
			if (!ensureAdminAccess()) return;

			const label = dom.adminSlotForm.elements.namedItem("label")?.value || "";
			const startTime = dom.adminSlotForm.elements.namedItem("start-time")?.value || "";
			const endTime = dom.adminSlotForm.elements.namedItem("end-time")?.value || "";
			const capacity = Number(dom.adminSlotForm.elements.namedItem("capacity")?.value || "0");
			const isEnabled = Boolean(dom.adminSlotForm.elements.namedItem("is-enabled")?.checked);

			if (!Number.isInteger(capacity) || capacity <= 0) {
				setAdminFeedback("Capacity must be a positive integer.", true);
				return;
			}

			try {
				await createPickupSlot({
					label,
					start_time: startTime,
					end_time: endTime,
					capacity,
					is_enabled: isEnabled
				});
				dom.adminSlotForm.reset();
				setAdminFeedback("Pickup slot created successfully.");
				await refreshPageData();
			} catch (error) {
				console.error(error);
				setAdminFeedback(`Could not create pickup slot: ${error.message}`, true);
			}
		});
	}
}
