import { createOrder, createPickupSlot, createProduct, signIn, signUp } from "./api.js";
import { applyAuthUiState, clearAuthState, saveAuthState } from "./auth.js";
import { refreshPageData } from "./data.js";
import { renderAll } from "./render.js";
import { AUTH_STATE, DOM, STATE } from "./state.js";

/**
 * Sets the feedback message for admin controls.
 * 
 * @param {*} message The feedback message to display.
 * @param {*} isError Whether the message indicates an error.
 */
function setAdminFeedback(message, isError = false) {
	if (!DOM.adminControlsFeedback) return;
	DOM.adminControlsFeedback.textContent = message;
	DOM.adminControlsFeedback.style.color = isError ? "#bb3a1d" : "var(--success)";
}

/**
 * Sets the feedback message for product-related actions.
 * 
 * @param {*} message The feedback message to display.
 * @param {*} isError Whether the message indicates an error.
 */
function setProductFeedback(message, isError = false) {
	if (!DOM.addProductFeedback) return;
	DOM.addProductFeedback.textContent = message;
	DOM.addProductFeedback.style.color = isError ? "#bb3a1d" : "var(--success)";
}

/**
 * Sets the feedback message for pickup slot-related actions.
 * 
 * @param {*} message The feedback message to display.
 * @param {*} isError Whether the message indicates an error.
 */
function setPickupSlotFeedback(message, isError = false) {
	if (!DOM.addPickupSlotFeedback) return;
	DOM.addPickupSlotFeedback.textContent = message;
	DOM.addPickupSlotFeedback.style.color = isError ? "#bb3a1d" : "var(--success)";
}

/**
 * Sets the feedback message for order-related actions.
 * 
 * @param {*} message The feedback message to display.
 * @param {*} isError Whether the message indicates an error.
 */
function setOrderFeedback(message, isError = false) {
	if (!DOM.orderFeedback) return;
	DOM.orderFeedback.textContent = message;
	DOM.orderFeedback.style.color = isError ? "#bb3a1d" : "var(--success)";
}

/**
 * Checks if the current user has admin access and sets feedback if not.
 * 
 * @returns {boolean} True if the user has admin access, false otherwise. 
 */
function ensureAdminAccess() {
	if (AUTH_STATE.isAdmin) return true;
	setAdminFeedback("Only admins can use dashboard configuration actions.", true);
	return false;
}

/**
 * Prepares the payload for creating an order based on the current cart state.
 * 
 * @returns {Array} An array of items to be included in the order, each with product_id and quantity.
 */
function getCartItemsPayload() {
	return Object.entries(STATE.cart).filter((entry) => entry[1] > 0).map((entry) => ({ product_id: Number(entry[0]), quantity: entry[1] }));
}

/**
 * Handles the process of placing an order, including validation and API interaction.
 * 
 * @returns {Promise<void>} A promise that resolves when the order placement process is complete.
 */
export async function placeOrder() {
	setOrderFeedback("");
	const items = getCartItemsPayload();
	if (items.length === 0) {
		setOrderFeedback("Your cart is empty.", true);
		return;
	}

	if (!STATE.selectedSlot) {
		setOrderFeedback("Please select a pickup slot.", true);
		return;
	}

	const emailInput = DOM.customerEmailInput;
	const customerEmail = emailInput ? emailInput.value.trim() : "";
	
	if (!customerEmail) {
		setOrderFeedback("Please enter your email to place the order.", true);
		if (emailInput) emailInput.focus();
		return;
	}

	try {
        const slotObj = STATE.slots.find(s => s.id === STATE.selectedSlot);
        const slotLabel = slotObj ? `${slotObj.day} (${slotObj.startTime} - ${slotObj.endTime})` : String(STATE.selectedSlot);

		await createOrder({
			customer_email: customerEmail,
			pickup_slot: slotLabel,
			pickup_date: new Date().toISOString().slice(0, 10),
			items
		});

		STATE.cart = {};
		await refreshPageData();
		setOrderFeedback("Order successfully placed!");
	} catch (error) {
		console.error(error);
		setOrderFeedback(`Could not place order: ${error.message}`, true);
	}
}

/**
 * Wires up event listeners for popups related to authentication and account management, as well as form submissions for signing in and creating accounts.
 */
export function wirePopups() {
	if (DOM.navSignInBtn && DOM.signInPopup) {
		DOM.navSignInBtn.addEventListener("click", () => {
			if (AUTH_STATE.email) {
				clearAuthState();
				applyAuthUiState();
				refreshPageData();
				return;
			}
			DOM.signInPopup.style.display = "flex";
		});
	}

	if (DOM.openCreateAccountBtn && DOM.createAccountPopup) {
		DOM.openCreateAccountBtn.addEventListener("click", () => {
			DOM.signInPopup.style.display = "none";
			DOM.createAccountPopup.style.display = "flex";
		});
	}

	if (DOM.closeSignInPopupBtn && DOM.signInPopup) DOM.closeSignInPopupBtn.addEventListener("click", () => DOM.signInPopup.style.display = "none");

	if (DOM.closeCreatePopupBtn && DOM.createAccountPopup) DOM.closeCreatePopupBtn.addEventListener("click", () => DOM.createAccountPopup.style.display = "none");

	if (DOM.signInForm) {
		DOM.signInForm.addEventListener("submit", async (event) => {
			event.preventDefault();

			const email = DOM.signInForm.elements.namedItem("email")?.value || "";
			const password = DOM.signInForm.elements.namedItem("password")?.value || "";

			try {
				const user = await signIn({ email, password });
				AUTH_STATE.email = user.email;
				AUTH_STATE.isAdmin = Boolean(user.is_admin);
				saveAuthState();
				applyAuthUiState();
				DOM.signInForm.reset();
				DOM.signInPopup.style.display = "none";
				await refreshPageData();
			} catch (error) {
				console.error(error);
				window.showAlert(`Could not sign in: ${error.message}`);
			}
		});
	}

	if (DOM.createAccountForm) {
		DOM.createAccountForm.addEventListener("submit", async (event) => {
			event.preventDefault();

			const email = DOM.createAccountForm.elements.namedItem("new-email")?.value || "";
			const password = DOM.createAccountForm.elements.namedItem("new-password")?.value || "";

			try {
				await signUp({ email, password });
				const user = await signIn({ email, password });
				AUTH_STATE.email = user.email;
				AUTH_STATE.isAdmin = Boolean(user.is_admin);
				saveAuthState();
				applyAuthUiState();

				DOM.createAccountForm.reset();
				DOM.createAccountPopup.style.display = "none";
				await refreshPageData();
			} catch (error) {
				console.error(error);
				window.showAlert(`Could not create account or sign in: ${error.message}`);
			}
		});
	}
}

/**
 * Wires up event listeners for various interactive elements on the page, including buttons for opening and closing popups, form submissions for creating products and pickup slots, and actions related to placing orders and managing the cart.
 */
export function wireActions() {
	/* Handle opening of the add product popup and focus the first field */
	if (DOM.openAddProductBtn && DOM.addProductPopup) {
		DOM.openAddProductBtn.addEventListener("click", () => {
			DOM.addProductPopup.style.display = "flex";
			const FIRST_FIELD = DOM.adminProductForm?.elements.namedItem("name");
			if (FIRST_FIELD && typeof FIRST_FIELD.focus === "function") FIRST_FIELD.focus();
		});
	}

	/* Handle closing of the add product popup */
	if (DOM.closeAddProductPopupBtn && DOM.addProductPopup) DOM.closeAddProductPopupBtn.addEventListener("click", () => DOM.addProductPopup.style.display = "none");

	/* Handle opening of the add pickup slot popup and focus the first field */
	if (DOM.openAddPickupSlotBtn && DOM.addPickupSlotPopup) {
		DOM.openAddPickupSlotBtn.addEventListener("click", () => {
			DOM.addPickupSlotPopup.style.display = "flex";
			const FIRST_FIELD = DOM.adminSlotForm?.elements.namedItem("day");
			if (FIRST_FIELD && typeof FIRST_FIELD.focus === "function") FIRST_FIELD.focus();
		});
	}
	/* Handle closing of the add pickup slot popup */
	if (DOM.closeAddPickupSlotPopupBtn && DOM.addPickupSlotPopup) DOM.closeAddPickupSlotPopupBtn.addEventListener("click", () => DOM.addPickupSlotPopup.style.display = "none");

	/* Handle opening of the order filters popup */
	if (DOM.openOrderFiltersBtn && DOM.orderFiltersPopup) DOM.openOrderFiltersBtn.addEventListener("click", () => DOM.orderFiltersPopup.style.display = "flex");

	/* Handle closing of the order filters popup */
	if (DOM.closeOrderFiltersPopupBtn && DOM.orderFiltersPopup) DOM.closeOrderFiltersPopupBtn.addEventListener("click", () => DOM.orderFiltersPopup.style.display = "none");

	/* Handle opening of the product filters popup */
	if (DOM.openProductFiltersBtn && DOM.productFiltersPopup) DOM.openProductFiltersBtn.addEventListener("click", () => DOM.productFiltersPopup.style.display = "flex");

	/* Handle closing of the product filters popup */
	if (DOM.closeProductFiltersPopupBtn && DOM.productFiltersPopup) DOM.closeProductFiltersPopupBtn.addEventListener("click", () => DOM.productFiltersPopup.style.display = "none");

	/* Handle opening of the pickup slot filters popup */
	if (DOM.openPickupSlotFiltersBtn && DOM.pickupSlotFiltersPopup) DOM.openPickupSlotFiltersBtn.addEventListener("click", () => DOM.pickupSlotFiltersPopup.style.display = "flex");

	/* Handle closing of the pickup slot filters popup */
	if (DOM.closePickupSlotFiltersPopupBtn && DOM.pickupSlotFiltersPopup) DOM.closePickupSlotFiltersPopupBtn.addEventListener("click", () => DOM.pickupSlotFiltersPopup.style.display = "none");

	/* Handle checkout button click to place an order */
	if (DOM.checkoutBtn) DOM.checkoutBtn.addEventListener("click", placeOrder);

	/* Handle clear basket button click to reset the cart state */
	if (DOM.clearBasketBtn) {
		DOM.clearBasketBtn.addEventListener("click", () => {
			STATE.cart = {};
			renderAll();
		});
	}

	/* Handle admin product form submission */
	if (DOM.adminProductForm) {
		DOM.adminProductForm.addEventListener("submit", async (event) => {
			event.preventDefault();
			if (!ensureAdminAccess()) return;

			const NAME = DOM.adminProductForm.elements.namedItem("name")?.value || "";
			const DESCRIPTION = DOM.adminProductForm.elements.namedItem("description")?.value || "";
			const PRICE = Number(DOM.adminProductForm.elements.namedItem("price")?.value || "0");
			const STOCK = Number(DOM.adminProductForm.elements.namedItem("stock")?.value || "0");
			const IS_ACTIVE = Boolean(DOM.adminProductForm.elements.namedItem("is-active")?.checked);

			if (Number.isNaN(PRICE) || PRICE < 0) {
				setProductFeedback("Price must be a non-negative number.", true);
				return;
			}

			if (!Number.isInteger(STOCK) || STOCK < 0) {
				setProductFeedback("Stock must be a non-negative integer.", true);
				return;
			}

			try {
				await createProduct({
					name: NAME,
					description: DESCRIPTION,
					price_cents: Math.round(PRICE * 100),
					stock: STOCK,
					is_active: IS_ACTIVE
				});
				DOM.adminProductForm.reset();
				setProductFeedback("Product created successfully.");
				setTimeout(() => {
					if (DOM.addProductPopup) DOM.addProductPopup.style.display = "none";
				}, 1500);
				await refreshPageData();
			} catch (error) {
				console.error(error);
				setProductFeedback(`Could not create product: ${error.message}`, true);
			}
		});
	}

	/* Handle admin pickup slot form submission */
	if (DOM.adminSlotForm) {
		DOM.adminSlotForm.addEventListener("submit", async (event) => {
			event.preventDefault();
			if (!ensureAdminAccess()) return;

			const DAY = DOM.adminSlotForm.elements.namedItem("day")?.value || "";
			const START_TIME = DOM.adminSlotForm.elements.namedItem("start-time")?.value || "";
			const END_TIME = DOM.adminSlotForm.elements.namedItem("end-time")?.value || "";
			const CAPACITY = Number(DOM.adminSlotForm.elements.namedItem("capacity")?.value || "0");
			const IS_ACTIVE = Boolean(DOM.adminSlotForm.elements.namedItem("is-enabled")?.checked);

			if (!Number.isInteger(CAPACITY) || CAPACITY <= 0) {
				setPickupSlotFeedback("Capacity must be a positive integer.", true);
				return;
			}

			try {
				await createPickupSlot({
					day: DAY,
					start_time: START_TIME,
					end_time: END_TIME,
					capacity: CAPACITY,
					is_enabled: IS_ACTIVE
				});
				DOM.adminSlotForm.reset();
				setPickupSlotFeedback("Pickup slot created successfully.");
				setTimeout(() => {
					if (DOM.addPickupSlotPopup) DOM.addPickupSlotPopup.style.display = "none";
				}, 1500);
				await refreshPageData();
			} catch (error) {
				console.error(error);
				setPickupSlotFeedback(`Could not create pickup slot: ${error.message}`, true);
			}
		});
	}
}


