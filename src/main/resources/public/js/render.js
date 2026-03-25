import { deletePickupSlot, deleteProduct, updateOrderStatus, updateProductDetails, updatePickupSlotStatus, updatePickupSlotCapacity } from "./api.js";
import { AUTH_STATE, DOM, STATE } from "./state.js";
import { applyAuthUiState } from "./auth.js";

let refreshPageDataHandler = async () => {};

export function setRefreshPageDataHandler(handler) { refreshPageDataHandler = handler; }

function formatPrice(cents) { return `$${(Number(cents) / 100).toFixed(2)}`; }

/**
 * Calculates the total price of all items in the shopping cart by iterating through each product in the cart, multiplying its quantity by its price, and summing these values.
 * @returns {number} The total price of the items in the cart in cents.
 */
function getBasketTotalCents() {
	return Object.entries(STATE.cart).reduce((acc, [productId, qty]) => {
		const PRODUCT = STATE.products.find((item) => item.id === Number(productId));
		if (!PRODUCT) return acc;

		return acc + (Number(PRODUCT.priceCents) * Number(qty));
	}, 0);
}

/**
 * Calculates the total number of items in the shopping cart by summing the quantities of all products. This is used to provide a quick overview of the cart status in the UI.
 */
function getCartCount() { return Object.values(STATE.cart).reduce((acc, qty) => acc + qty, 0); }

/**
 * Renders a summary of the shopping cart status, including the total number of items and the selected pickup slot. If the cart is empty, a prompt to add items is shown instead.
 */
export function renderCartSummary() {
	if (!DOM.heroActions) return;

	let summary = document.getElementById("cart-summary");
	if (!summary) {
		summary = document.createElement("p");
		summary.id = "cart-summary";
		summary.className = "slot-note";
		DOM.heroActions.insertAdjacentElement("afterend", summary);
	}

	const COUT = getCartCount();
	const SELECTED_SLOT = STATE.slots.find(s => s.id === STATE.selectedSlot);
	const SLOT_LABEL = SELECTED_SLOT ? `${SELECTED_SLOT.day} (${SELECTED_SLOT.startTime} - ${SELECTED_SLOT.endTime})` : "N/A";
	
	summary.textContent = COUT > 0 ? `Cart: ${COUT} item(s) ready for slot ${SLOT_LABEL}` : "Cart is empty. Add pastries to start an order.";
}

/**
 * Renders the contents of the shopping basket, allowing users to adjust quantities or remove items, and updates the total price. If the basket is empty, a message is shown instead.
 */
export function renderBasket() {
	if (!DOM.basketList || !DOM.basketTotal) return;

	DOM.basketList.innerHTML = "";
	const ENTRIES = Object.entries(STATE.cart).filter((entry) => Number(entry[1]) > 0);

	if (ENTRIES.length === 0) {
		const EMPTY = document.createElement("li");
		EMPTY.className = "basket-empty";
		EMPTY.textContent = "Your basket is empty.";
		DOM.basketList.appendChild(EMPTY);
		DOM.basketTotal.textContent = formatPrice(0);
		return;
	}

	ENTRIES.forEach(([productId, quantity]) => {
		const PRODUCT = STATE.products.find((item) => item.id === Number(productId));
		if (!PRODUCT) return;

		const LINE_TOTAL = Number(PRODUCT.priceCents) * Number(quantity);
		const ROW = document.createElement("li");
		ROW.innerHTML = `
			<span>${PRODUCT.name}</span>
			<div>
				<button class="btn btn-outline small icon-btn" type="button" data-qty-change="-1" data-product-id="${PRODUCT.id}" aria-label="Decrease quantity">
					<i class="fa-solid fa-minus"></i>
				</button>
				<span style="min-width: 1.5rem; text-align: center;">${quantity}</span>
				<button class="btn btn-outline small icon-btn" type="button" data-qty-change="1" data-product-id="${PRODUCT.id}" aria-label="Increase quantity">
					<i class="fa-solid fa-plus"></i>
				</button>
			</div>
			<span>${formatPrice(LINE_TOTAL)}</span>
			<button class="btn btn-outline small icon-btn" type="button" data-remove-id="${PRODUCT.id}" aria-label="Remove ${PRODUCT.name} from basket">
				<i class="fa-solid fa-trash"></i>
			</button>
		`;
		DOM.basketList.appendChild(ROW);
	});

	DOM.basketList.querySelectorAll("button[data-remove-id]").forEach((button) => {
		button.addEventListener("click", () => {
			const PRODUCT_ID = Number(button.getAttribute("data-remove-id"));
			delete STATE.cart[PRODUCT_ID];
			renderProducts();
			renderBasket();
			renderCartSummary();
		});
	});

	DOM.basketList.querySelectorAll("button[data-qty-change]").forEach((button) => {
		button.addEventListener("click", () => {
			const PRODUCT_ID = Number(button.getAttribute("data-product-id"));
			const DELTA = Number(button.getAttribute("data-qty-change"));
			if (STATE.cart[PRODUCT_ID]) {
				const NEW_QTY = STATE.cart[PRODUCT_ID] + DELTA;
				if (NEW_QTY <= 0) delete STATE.cart[PRODUCT_ID];
				else { // ensure it does not exceed stock
					const PRODUCT = STATE.products.find((item) => item.id === PRODUCT_ID);
					if (PRODUCT && NEW_QTY <= PRODUCT.stock) STATE.cart[PRODUCT_ID] = NEW_QTY;
				}
				renderProducts();
				renderBasket();
				renderCartSummary();
			}
		});
	});

	DOM.basketTotal.textContent = formatPrice(getBasketTotalCents());
}

/**
 * Renders the product catalog.
 */
export function renderProducts() {
	if (!DOM.productGrid) return;

	DOM.productGrid.innerHTML = "";
	STATE.products.filter(p => p.isActive).forEach((product) => {
		const CARD = document.createElement("article");
		CARD.className = "product-card";

		const QTY = STATE.cart[product.id] || 0;
		const STOCK_CLASS = product.stock <= 10 ? "low-stock" : "in-stock";
		const STOCK_LABEL = `${product.stock} left today`;

		CARD.innerHTML = `
			<h3>${product.name}</h3>
			<p class="product-meta">${product.description || "Freshly baked"}</p>
			<p class="product-stock ${STOCK_CLASS}">${STOCK_LABEL}</p>
			<div class="product-footer">
				<span class="price">${formatPrice(product.priceCents)}</span>
				<button class="btn btn-solid small" type="button" data-action="add" data-id="${product.id}" ${product.stock <= 0 ? "disabled" : ""}>
					Add${QTY > 0 ? ` (${QTY})` : ""}
				</button>
			</div>
		`;

		DOM.productGrid.appendChild(CARD);
	});

	DOM.productGrid.querySelectorAll("button[data-action='add']").forEach((button) => {
		button.addEventListener("click", () => {
			const PRODUCT_ID = Number(button.getAttribute("data-id"));
			const PRODUCT = STATE.products.find((item) => item.id === PRODUCT_ID);
			if (!PRODUCT) return;

			const CURRENT = STATE.cart[PRODUCT_ID] || 0;
			if (CURRENT >= PRODUCT.stock) {
				window.showAlert("Cannot add more than available stock.");
				return;
			}

			STATE.cart[PRODUCT_ID] = CURRENT + 1;
			renderProducts();
			renderBasket();
			renderCartSummary();
		});
	});
}

/**
 * Renders available pickup slots for the current day, allowing users to select a slot for their order. Only enabled slots for the current day are shown, and the selected slot is highlighted.
 */
export function renderSlots() {
	if (!DOM.slotList) return;

	DOM.slotList.innerHTML = "";
	
	const DAYS = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
	const CURRENT_DAY = DAYS[new Date().getDay()].toLowerCase();

	STATE.slots.filter((slot) => slot.isEnabled && slot.day.toLowerCase() === CURRENT_DAY).forEach((slot) => {
		const BUTTON = document.createElement("button");
		const SLOT_LABEL = `${slot.startTime} - ${slot.endTime}`;
		BUTTON.className = `slot ${STATE.selectedSlot === slot.id ? "active" : ""}`.trim();
		BUTTON.type = "button";
		BUTTON.textContent = SLOT_LABEL;
		BUTTON.addEventListener("click", () => {
			STATE.selectedSlot = slot.id;
			renderSlots();
			renderCartSummary();
		});
		DOM.slotList.appendChild(BUTTON);
	});
}

/**
 * Renders the inventory management interface for admin users, allowing filtering by search term and status, as well as updating stock, price, and availability or removing products.
 */
export function renderInventory() {
	if (!AUTH_STATE.isAdmin) return;
	if (!DOM.stockList) return;

	const SEARCH_INPUT = document.getElementById("product-search");
	const STATUS_FILTER = document.getElementById("product-status-filter");

	if (SEARCH_INPUT && !SEARCH_INPUT.dataset.listenerAttached) {
		SEARCH_INPUT.addEventListener("input", renderInventory);
		SEARCH_INPUT.dataset.listenerAttached = "true";
	}
	if (STATUS_FILTER && !STATUS_FILTER.dataset.listenerAttached) {
		STATUS_FILTER.addEventListener("change", renderInventory);
		STATUS_FILTER.dataset.listenerAttached = "true";
	}

	const SEARCH_TERM = SEARCH_INPUT ? SEARCH_INPUT.value.toLowerCase() : "";
	const STATUS_TERM = STATUS_FILTER ? STATUS_FILTER.value : "";

	let filteredProducts = STATE.products;
	if (SEARCH_TERM) {
		filteredProducts = filteredProducts.filter(p => 
			(p.name && p.name.toLowerCase().includes(SEARCH_TERM)) ||
			(p.description && p.description.toLowerCase().includes(SEARCH_TERM))
		);
	}
	if (STATUS_TERM === "ACTIVE") filteredProducts = filteredProducts.filter(p => p.isActive);
	else if (STATUS_TERM === "INACTIVE") filteredProducts = filteredProducts.filter(p => !p.isActive);

	DOM.stockList.innerHTML = "";
	filteredProducts.forEach((product) => {
		const ITEM = document.createElement("li");
		ITEM.className = "inventory-item";
		const STOCK_INPUT_ID = `stock-input-${product.id}`;
		const PRICE_INPUT_ID = `price-input-${product.id}`;
		const ACTIVE_INPUT_ID = `active-input-${product.id}`;
		const PRODUCT_NAME_ID = `inventory-product-name-${product.id}`;
		const UNIT_PRICE = (Number(product.priceCents) / 100).toFixed(2);
		ITEM.innerHTML = `
			<div class="inventory-item-summary">
				<strong id="${PRODUCT_NAME_ID}" class="inventory-item-title">${product.name}</strong>
				<span class="inventory-item-description">${product.description || "No description"}</span>
			</div>
			<div class="inventory-item-controls" aria-labelledby="${PRODUCT_NAME_ID}">
				<div class="inventory-control">
					<label for="${STOCK_INPUT_ID}">Stock quantity</label>
					<input id="${STOCK_INPUT_ID}" class="stock-input" type="number" min="0" value="${product.stock}" aria-label="Stock quantity for ${product.name}">
				</div>
				<div class="inventory-control">
					<label for="${PRICE_INPUT_ID}">Unit price ($)</label>
					<input id="${PRICE_INPUT_ID}" class="stock-input" type="number" min="0" step="0.01" value="${UNIT_PRICE}" aria-label="Unit price in dollars for ${product.name}">
				</div>
				<div class="inventory-control checkbox-control">
					<input id="${ACTIVE_INPUT_ID}" type="checkbox" ${product.isActive ? "checked" : ""} aria-label="Is ${product.name} available for order">
					<label for="${ACTIVE_INPUT_ID}">Available</label>
				</div>
			</div>
			<div class="manage-actions">
				<button class="btn btn-outline small" type="button" data-action="update" aria-label="Update details for ${product.name}">Update</button>
				<button class="btn btn-outline small icon-btn" type="button" data-action="remove" aria-label="Remove product ${product.name}">
					<i class="fa-solid fa-trash"></i>
				</button>
			</div>
		`;

		const STOCK_INPUT = ITEM.querySelector(`#${STOCK_INPUT_ID}`);
		const PRICE_INPUT = ITEM.querySelector(`#${PRICE_INPUT_ID}`);
		const ACTIVE_INPUT = ITEM.querySelector(`#${ACTIVE_INPUT_ID}`);
		const UPDATE_BUTTON = ITEM.querySelector("button[data-action='update']");
		const REMOVE_BUTTON = ITEM.querySelector("button[data-action='remove']");

		STOCK_INPUT.addEventListener("input", () => {
			if (this.value < 0) this.value = Math.abs(this.value);
		});
		PRICE_INPUT.addEventListener("input", () => {
			if (this.value < 0) this.value = Math.abs(this.value);
			if (this.value.includes(".")) {
				const PARTS = this.value.split(".");
				if (PARTS[1].length > 2) this.value = PARTS[0] + "." + PARTS[1].substring(0, 2);
			}
		});

		UPDATE_BUTTON.addEventListener("click", async () => {
			const NEW_STOCK = Number(STOCK_INPUT.value);
			const NEW_UNIT_PRICE = Number(PRICE_INPUT.value);
			const NEW_IS_ENABLED = ACTIVE_INPUT.checked;

			try {
				await updateProductDetails(product.id, {
					stock: NEW_STOCK,
					price_cents: Math.round(NEW_UNIT_PRICE * 100),
					is_active: NEW_IS_ENABLED
				});
				await refreshPageDataHandler();
			} catch (error) {
				console.error(error);
				window.showAlert(`Could not update product: ${error.message}`);
			}
		});

		REMOVE_BUTTON.addEventListener("click", async () => {
			try {
				await deleteProduct(product.id);
				delete STATE.cart[product.id];
				await refreshPageDataHandler();
			} catch (error) {
				console.error(error);
				window.showAlert(`Could not remove product: ${error.message}`);
			}
		});

		DOM.stockList.appendChild(ITEM);
	});
}

export function renderPickupSlotManagement() {
	if (!AUTH_STATE.isAdmin) return;
	if (!DOM.adminSlotList) return;

	const SEARCH_FIELD = document.getElementById("slot-search");
	const STATUS_FILTER = document.getElementById("slot-status-filter");

	if (SEARCH_FIELD && !SEARCH_FIELD.dataset.listenerAttached) {
		SEARCH_FIELD.addEventListener("input", renderPickupSlotManagement);
		SEARCH_FIELD.dataset.listenerAttached = "true";
	}
	if (STATUS_FILTER && !STATUS_FILTER.dataset.listenerAttached) {
		STATUS_FILTER.addEventListener("change", renderPickupSlotManagement);
		STATUS_FILTER.dataset.listenerAttached = "true";
	}

	const SEARCH_TERM = SEARCH_FIELD ? SEARCH_FIELD.value.toLowerCase() : "";
	const STATUS_TERM = STATUS_FILTER ? STATUS_FILTER.value : "";

	let filteredSlots = STATE.slots;
	if (SEARCH_TERM) {
		filteredSlots = filteredSlots.filter(s => 
			(s.day && s.day.toLowerCase().includes(SEARCH_TERM)) ||
			(s.startTime && s.startTime.toLowerCase().includes(SEARCH_TERM)) ||
			(s.endTime && s.endTime.toLowerCase().includes(SEARCH_TERM))
		);
	}
	if (STATUS_TERM === "ENABLED") filteredSlots = filteredSlots.filter(s => s.isEnabled);
	else if (STATUS_TERM === "DISABLED") filteredSlots = filteredSlots.filter(s => !s.isEnabled);

	DOM.adminSlotList.innerHTML = "";

	filteredSlots.forEach((slot) => {
		const item = document.createElement("li");
		item.className = "inventory-item";
		const slotLabel = `${slot.day} (${slot.startTime} - ${slot.endTime})`;
		const capacityInputId = `slot-capacity-input-${slot.id}`;
		const activeInputId = `slot-active-input-${slot.id}`;
		const slotNameId = `slot-name-${slot.id}`;

		item.innerHTML = `
			<div class="inventory-item-summary">
				<strong id="${slotNameId}" class="inventory-item-title">${slotLabel}</strong>
			</div>
			<div class="inventory-item-controls" aria-labelledby="${slotNameId}">
				<div class="inventory-control">
					<label for="${capacityInputId}">Capacity</label>
					<input id="${capacityInputId}" class="stock-input" type="number" min="0" value="${slot.capacity}" aria-label="Capacity for pickup slot ${slotLabel}">
				</div>
				<div class="inventory-control checkbox-control">
					<input id="${activeInputId}" type="checkbox" ${slot.isEnabled ? "checked" : ""} aria-label="Is pickup slot ${slotLabel} enabled">
					<label for="${activeInputId}">Enabled</label>
				</div>
			</div>
			<div class="manage-actions">
				<button class="btn btn-outline small" type="button" data-action="update" aria-label="Update details for pickup slot ${slotLabel}">Update</button>
				<button class="btn btn-outline small icon-btn" type="button" data-action="remove" aria-label="Remove pickup slot ${slotLabel}">
					<i class="fa-solid fa-trash"></i>
				</button>
			</div>
		`;

		const CAPACITY_INPUT = item.querySelector(`#${capacityInputId}`);
		const ACTIVE_INPUT = item.querySelector(`#${activeInputId}`);
		const UPDATE_BUTTON = item.querySelector("button[data-action='update']");
		const REMOVE_BUTTON = item.querySelector("button[data-action='remove']");

		CAPACITY_INPUT.addEventListener("input", function() {
			if (this.value < 0) this.value = Math.abs(this.value);
		});

		UPDATE_BUTTON.addEventListener("click", async () => {
			const NEW_CAPACITY = Number(CAPACITY_INPUT.value);
			const NEW_IS_ACTIVE = ACTIVE_INPUT.checked;
			try {
				if (NEW_CAPACITY !== slot.capacity) await updatePickupSlotCapacity(slot.id, NEW_CAPACITY);
				if (NEW_IS_ACTIVE !== slot.isEnabled) await updatePickupSlotStatus(slot.id, NEW_IS_ACTIVE);
				if (!NEW_IS_ACTIVE && STATE.selectedSlot === slot.id) STATE.selectedSlot = null;
				await refreshPageDataHandler();
			} catch (error) {
				console.error(error);
				window.showAlert(`Could not update pickup slot: ${error.message}`);
			}
		});

		REMOVE_BUTTON.addEventListener("click", async () => {
			try {
				await deletePickupSlot(slot.id);
				if (STATE.selectedSlot === slot.id) STATE.selectedSlot = null;
				await refreshPageDataHandler();
			} catch (error) {
				console.error(error);
				window.showAlert(`Could not remove pickup slot: ${error.message}`);
			}
		});

		DOM.adminSlotList.appendChild(item);
	});

	if (filteredSlots.length === 0) {
		const EMPTY = document.createElement("li");
		EMPTY.textContent = "No pickup slots configured or matching filters.";
		DOM.adminSlotList.appendChild(EMPTY);
	}
}

/**
 * Renders the order management interface for admin users, allowing filtering by search term, status, and pickup slot, as well as updating order statuses.
 */
export function renderOrders() {
	if (!AUTH_STATE.isAdmin) return;
	if (!DOM.orderList) return;

	const SEARCH_INPUT = document.getElementById("order-search");
	const STATUS_FILTER = document.getElementById("order-status-filter");
	const SLOT_FILTER = document.getElementById("order-slot-filter");

	if (SEARCH_INPUT && !SEARCH_INPUT.dataset.listenerAttached) {
		SEARCH_INPUT.addEventListener("input", renderOrders);
		SEARCH_INPUT.dataset.listenerAttached = "true";
	}
	if (STATUS_FILTER && !STATUS_FILTER.dataset.listenerAttached) {
		STATUS_FILTER.addEventListener("change", renderOrders);
		STATUS_FILTER.dataset.listenerAttached = "true";
	}
	if (SLOT_FILTER && !SLOT_FILTER.dataset.listenerAttached) {
		SLOT_FILTER.addEventListener("change", renderOrders);
		SLOT_FILTER.dataset.listenerAttached = "true";
	}

	if (SLOT_FILTER && SLOT_FILTER.options.length <= 1) {
		STATE.slots.forEach(slot => {
			const option = document.createElement("option");
			const slotLabel = `${slot.day} (${slot.startTime} - ${slot.endTime})`;
			option.value = slotLabel;
			option.textContent = slotLabel;
			SLOT_FILTER.appendChild(option);
		});
	}

	const SEARCH_TERM = SEARCH_INPUT ? SEARCH_INPUT.value.toLowerCase() : "";
	const STATUS_TERM = STATUS_FILTER ? STATUS_FILTER.value : "";
	const SLOT_TERM = SLOT_FILTER ? SLOT_FILTER.value : "";

	let filteredOrders = STATE.orders;
	if (SEARCH_TERM) {
		filteredOrders = filteredOrders.filter(o => 
			(o.customerEmail && o.customerEmail.toLowerCase().includes(SEARCH_TERM)) ||
			String(o.id).includes(SEARCH_TERM)
		);
	}
	if (STATUS_TERM) filteredOrders = filteredOrders.filter(o => (o.status || "PENDING") === STATUS_TERM);
	if (SLOT_TERM) filteredOrders = filteredOrders.filter(o => o.pickupSlot === SLOT_TERM);

	DOM.orderList.innerHTML = "";
	filteredOrders.forEach((order) => {
		const rawItems = STATE.orderItemsByOrderId[order.id] || [];
		const orderItemsMarkup = rawItems.length > 0
			? rawItems.map((entry) => {
				const PRODUCT = STATE.products.find((item) => item.id === Number(entry.productId));
				const PRODUCT_NAME = PRODUCT?.name || `Product #${entry.productId}`;
				return `<li>${entry.quantity}x ${PRODUCT_NAME}</li>`;
			}).join("")
			: "<li>No items</li>";

		const ITEM = document.createElement("li");
		ITEM.className = "order-item";
		ITEM.innerHTML = `
			<span>#${order.id}</span>
			<span>${order.customerEmail || "unknown@customer"}</span>
			<span>Pickup ${order.pickupSlot}</span>
			<strong>${formatPrice(order.totalCents)}</strong>
			<div class="order-items" aria-label="Ordered items for order ${order.id}">
				<span class="order-items-title">Items</span>
				<ul class="order-items-list">
					${orderItemsMarkup}
				</ul>
			</div>
			<select class="status-select" aria-label="Order ${order.id} status">
				<option value="PENDING">PENDING</option>
				<option value="CONFIRMED">RETRIEVED</option>
				<option value="READY">READY</option>
				<option value="CANCELLED">CANCELLED</option>
			</select>
			<button class="btn btn-outline small" type="button" aria-label="Update order ${order.id} status">Update</button>
		`;

		const STATUS_SELECT = ITEM.querySelector(".status-select");
		STATUS_SELECT.value = order.status || "PENDING";
		const BUTTON = ITEM.querySelector("button");

		BUTTON.addEventListener("click", async () => {
			const newStatus = STATUS_SELECT.value;
			try {
				await updateOrderStatus(order.id, newStatus);
				await refreshPageDataHandler();
			} catch (error) {
				console.error(error);
				window.showAlert(`Could not update order status: ${error.message}`);
			}
		});

		DOM.orderList.appendChild(ITEM);
	});
}

/**
 * Renders key analytics metrics for admin users, including top-selling product, low stock risk, and peak pickup times.
 */
export function renderAnalytics() {
	if (!AUTH_STATE.isAdmin) return;
	if (!DOM.analyticsGrid) return;

	const SALES_BY_PRODUCT = {};
	Object.values(STATE.orderItemsByOrderId).forEach((items) => {
		items.forEach((item) => SALES_BY_PRODUCT[item.productId] = (SALES_BY_PRODUCT[item.productId] || 0) + item.quantity);
	});

	const TOP_PRODUCT_ENTRY = Object.entries(SALES_BY_PRODUCT).sort((a, b) => b[1] - a[1])[0];
	const TOP_PRODUCT = TOP_PRODUCT_ENTRY ? STATE.products.find((product) => product.id === Number(TOP_PRODUCT_ENTRY[0])) : null;

	const LOW_STOCK_COUNT = STATE.products.filter((product) => Number(product.stock || 0) <= 10).length;
	const RISK = STATE.products.length > 0 ? ((LOW_STOCK_COUNT / STATE.products.length) * 100).toFixed(1) : "0.0";

	const PEAK_SLOTS = {};
	STATE.orders.forEach((order) => PEAK_SLOTS[order.pickupSlot] = (PEAK_SLOTS[order.pickupSlot] || 0) + 1);
	const peakEntry = Object.entries(PEAK_SLOTS).sort((a, b) => b[1] - a[1])[0];

	DOM.analyticsGrid.innerHTML = `
		<article class="metric-card">
			<p class="metric-label">Top Seller</p>
			<p class="metric-value">${TOP_PRODUCT ? TOP_PRODUCT.name : "No sales yet"}</p>
			<p class="metric-note">${TOP_PRODUCT_ENTRY ? `${TOP_PRODUCT_ENTRY[1]} item(s) sold` : "Place first order"}</p>
		</article>
		<article class="metric-card">
			<p class="metric-label">Low Stock Risk</p>
			<p class="metric-value">${RISK}%</p>
			<p class="metric-note">${LOW_STOCK_COUNT} products at or below 10 left</p>
		</article>
		<article class="metric-card">
			<p class="metric-label">Peak Pickup</p>
			<p class="metric-value">${peakEntry ? peakEntry[0] : "N/A"}</p>
			<p class="metric-note">${peakEntry ? `${peakEntry[1]} order(s)` : "No orders yet"}</p>
		</article>
	`;
}

/**
 * Render all dynamic UI components.
 */
export function renderAll() {
	applyAuthUiState();
	renderProducts();
	renderBasket();
	renderSlots();
	renderInventory();
	renderPickupSlotManagement();
	renderOrders();
	renderAnalytics();
	renderCartSummary();
}
