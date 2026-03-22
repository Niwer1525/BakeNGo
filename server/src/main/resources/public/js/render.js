import { deletePickupSlot, deleteProduct, updateOrderStatus, updateProductDetails, updatePickupSlotStatus } from "./api.js";
import { authState, dom, state } from "./state.js";
import { applyAuthUiState } from "./auth.js";

let refreshPageDataHandler = async () => {};

export function setRefreshPageDataHandler(handler) {
	refreshPageDataHandler = handler;
}

function formatPrice(cents) {
	return `$${(Number(cents) / 100).toFixed(2)}`;
}

function getBasketTotalCents() {
	return Object.entries(state.cart).reduce((acc, [productId, qty]) => {
		const product = state.products.find((item) => item.id === Number(productId));
		if (!product) return acc;
		return acc + (Number(product.priceCents) * Number(qty));
	}, 0);
}

function getCartCount() {
	return Object.values(state.cart).reduce((acc, qty) => acc + qty, 0);
}

export function renderCartSummary() {
	if (!dom.heroActions) return;

	let summary = document.getElementById("cart-summary");
	if (!summary) {
		summary = document.createElement("p");
		summary.id = "cart-summary";
		summary.className = "slot-note";
		dom.heroActions.insertAdjacentElement("afterend", summary);
	}

	const count = getCartCount();
	const selectedSlotObj = state.slots.find(s => s.id === state.selectedSlot);
	const slotLabel = selectedSlotObj ? `${selectedSlotObj.day} (${selectedSlotObj.startTime} - ${selectedSlotObj.endTime})` : "N/A";
	
	summary.textContent = count > 0
		? `Cart: ${count} item(s) ready for slot ${slotLabel}`
		: "Cart is empty. Add pastries to start an order.";
}

export function renderBasket() {
	if (!dom.basketList || !dom.basketTotal) return;

	dom.basketList.innerHTML = "";
	const entries = Object.entries(state.cart).filter((entry) => Number(entry[1]) > 0);

	if (entries.length === 0) {
		const empty = document.createElement("li");
		empty.className = "basket-empty";
		empty.textContent = "Your basket is empty.";
		dom.basketList.appendChild(empty);
		dom.basketTotal.textContent = formatPrice(0);
		return;
	}

	entries.forEach(([productId, quantity]) => {
		const product = state.products.find((item) => item.id === Number(productId));
		if (!product) return;

		const lineTotal = Number(product.priceCents) * Number(quantity);
		const row = document.createElement("li");
		row.innerHTML = `
			<span>${product.name}</span>
			<div>
				<button class="btn btn-outline small icon-btn" type="button" data-qty-change="-1" data-product-id="${product.id}" aria-label="Decrease quantity">
					<i class="fa-solid fa-minus"></i>
				</button>
				<span style="min-width: 1.5rem; text-align: center;">${quantity}</span>
				<button class="btn btn-outline small icon-btn" type="button" data-qty-change="1" data-product-id="${product.id}" aria-label="Increase quantity">
					<i class="fa-solid fa-plus"></i>
				</button>
			</div>
			<span>${formatPrice(lineTotal)}</span>
			<button class="btn btn-outline small icon-btn" type="button" data-remove-id="${product.id}" aria-label="Remove ${product.name} from basket">
				<i class="fa-solid fa-trash"></i>
			</button>
		`;
		dom.basketList.appendChild(row);
	});

	dom.basketList.querySelectorAll("button[data-remove-id]").forEach((button) => {
		button.addEventListener("click", () => {
			const productId = Number(button.getAttribute("data-remove-id"));
			delete state.cart[productId];
			renderProducts();
			renderBasket();
			renderCartSummary();
		});
	});

	dom.basketList.querySelectorAll("button[data-qty-change]").forEach((button) => {
		button.addEventListener("click", () => {
			const productId = Number(button.getAttribute("data-product-id"));
			const delta = Number(button.getAttribute("data-qty-change"));
			if (state.cart[productId]) {
				const newQty = state.cart[productId] + delta;
				if (newQty <= 0) {
					delete state.cart[productId];
				} else {
					// ensure it does not exceed stock
					const product = state.products.find((item) => item.id === productId);
					if (product && newQty <= product.stock) {
						state.cart[productId] = newQty;
					}
				}
				renderProducts();
				renderBasket();
				renderCartSummary();
			}
		});
	});

	dom.basketTotal.textContent = formatPrice(getBasketTotalCents());
}

export function renderProducts() {
	if (!dom.productGrid) return;

	dom.productGrid.innerHTML = "";
	state.products.forEach((product) => {
		const card = document.createElement("article");
		card.className = "product-card";

		const qty = state.cart[product.id] || 0;
		const stockClass = product.stock <= 10 ? "low-stock" : "in-stock";
		const stockLabel = `${product.stock} left today`;

		card.innerHTML = `
			<h3>${product.name}</h3>
			<p class="product-meta">${product.description || "Freshly baked"}</p>
			<p class="product-stock ${stockClass}">${stockLabel}</p>
			<div class="product-footer">
				<span class="price">${formatPrice(product.priceCents)}</span>
				<button class="btn btn-solid small" type="button" data-action="add" data-id="${product.id}" ${product.stock <= 0 ? "disabled" : ""}>
					Add${qty > 0 ? ` (${qty})` : ""}
				</button>
			</div>
		`;

		dom.productGrid.appendChild(card);
	});

	dom.productGrid.querySelectorAll("button[data-action='add']").forEach((button) => {
		button.addEventListener("click", () => {
			const productId = Number(button.getAttribute("data-id"));
			const product = state.products.find((item) => item.id === productId);
			if (!product) return;

			const current = state.cart[productId] || 0;
			if (current >= product.stock) {
				window.showAlert("Cannot add more than available stock.");
				return;
			}

			state.cart[productId] = current + 1;
			renderProducts();
			renderBasket();
			renderCartSummary();
		});
	});
}

export function renderSlots() {
	if (!dom.slotList) return;

	dom.slotList.innerHTML = "";
	state.slots.filter((slot) => slot.isEnabled).forEach((slot) => {
		const button = document.createElement("button");
		const slotLabel = `${slot.day} (${slot.startTime} - ${slot.endTime})`;
		button.className = `slot ${state.selectedSlot === slot.id ? "active" : ""}`.trim();
		button.type = "button";
		button.textContent = slotLabel;
		button.addEventListener("click", () => {
			state.selectedSlot = slot.id;
			renderSlots();
			renderCartSummary();
		});
		dom.slotList.appendChild(button);
	});
}

export function renderInventory() {
	if (!authState.isAdmin) return;
	if (!dom.stockList) return;

	dom.stockList.innerHTML = "";
	state.products.forEach((product) => {
		const item = document.createElement("li");
		item.className = "inventory-item";
		const stockInputId = `stock-input-${product.id}`;
		const priceInputId = `price-input-${product.id}`;
		const activeInputId = `active-input-${product.id}`;
		const productNameId = `inventory-product-name-${product.id}`;
		const unitPrice = (Number(product.priceCents) / 100).toFixed(2);
		item.innerHTML = `
			<div class="inventory-item-summary">
				<strong id="${productNameId}" class="inventory-item-title">${product.name}</strong>
				<span class="inventory-item-description">${product.description || "No description"}</span>
			</div>
			<div class="inventory-item-controls" aria-labelledby="${productNameId}">
				<div class="inventory-control">
					<label for="${stockInputId}">Stock quantity</label>
					<input id="${stockInputId}" class="stock-input" type="number" min="0" value="${product.stock}" aria-label="Stock quantity for ${product.name}">
				</div>
				<div class="inventory-control">
					<label for="${priceInputId}">Unit price ($)</label>
					<input id="${priceInputId}" class="stock-input" type="number" min="0" step="0.01" value="${unitPrice}" aria-label="Unit price in dollars for ${product.name}">
				</div>
				<div class="inventory-control checkbox-control">
					<input id="${activeInputId}" type="checkbox" ${product.isActive ? "checked" : ""} aria-label="Is ${product.name} available for order">
					<label for="${activeInputId}">Available</label>
				</div>
			</div>
			<div class="manage-actions">
				<button class="btn btn-outline small" type="button" data-action="update" aria-label="Update details for ${product.name}">Update</button>
				<button class="btn btn-outline small icon-btn" type="button" data-action="remove" aria-label="Remove product ${product.name}">
					<i class="fa-solid fa-trash"></i>
				</button>
			</div>
		`;

		const stockInput = item.querySelector(`#${stockInputId}`);
		const priceInput = item.querySelector(`#${priceInputId}`);
		const activeInput = item.querySelector(`#${activeInputId}`);
		const updateButton = item.querySelector("button[data-action='update']");
		const removeButton = item.querySelector("button[data-action='remove']");

		stockInput.addEventListener("input", function() {
			if (this.value < 0) this.value = Math.abs(this.value);
		});
		priceInput.addEventListener("input", function() {
			if (this.value < 0) this.value = Math.abs(this.value);
			if (this.value.includes(".")) {
				const parts = this.value.split(".");
				if (parts[1].length > 2) this.value = parts[0] + "." + parts[1].substring(0, 2);
			}
		});

		updateButton.addEventListener("click", async () => {
			const newStock = Number(stockInput.value);
			const newUnitPrice = Number(priceInput.value);
			const newIsActive = activeInput.checked;

			try {
				await updateProductDetails(product.id, {
					stock: newStock,
					price_cents: Math.round(newUnitPrice * 100),
					is_active: newIsActive
				});
				await refreshPageDataHandler();
			} catch (error) {
				console.error(error);
				window.showAlert(`Could not update product: ${error.message}`);
			}
		});

		removeButton.addEventListener("click", async () => {
			try {
				await deleteProduct(product.id);
				delete state.cart[product.id];
				await refreshPageDataHandler();
			} catch (error) {
				console.error(error);
				window.showAlert(`Could not remove product: ${error.message}`);
			}
		});

		dom.stockList.appendChild(item);
	});
}

export function renderPickupSlotManagement() {
	if (!authState.isAdmin) return;
	if (!dom.adminSlotList) return;

	dom.adminSlotList.innerHTML = "";

	state.slots.forEach((slot) => {
		const item = document.createElement("li");
		item.className = "inventory-item";
		const slotLabel = `${slot.day} (${slot.startTime} - ${slot.endTime})`;
		const activeInputId = `slot-active-input-${slot.id}`;
		const slotNameId = `slot-name-${slot.id}`;

		item.innerHTML = `
			<div class="inventory-item-summary">
				<strong id="${slotNameId}" class="inventory-item-title">${slotLabel}</strong>
				<span class="inventory-item-description">Capacity: ${slot.capacity}</span>
			</div>
			<div class="inventory-item-controls" aria-labelledby="${slotNameId}">
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

		const activeInput = item.querySelector(`#${activeInputId}`);
		const updateButton = item.querySelector("button[data-action='update']");
		const removeButton = item.querySelector("button[data-action='remove']");

		updateButton.addEventListener("click", async () => {
			const newIsEnabled = activeInput.checked;
			try {
				await updatePickupSlotStatus(slot.id, newIsEnabled);
				if (!newIsEnabled && state.selectedSlot === slot.id) {
					state.selectedSlot = null;
				}
				await refreshPageDataHandler();
			} catch (error) {
				console.error(error);
				window.showAlert(`Could not update pickup slot: ${error.message}`);
			}
		});

		removeButton.addEventListener("click", async () => {
			try {
				await deletePickupSlot(slot.id);
				if (state.selectedSlot === slot.id) {
					state.selectedSlot = null;
				}
				await refreshPageDataHandler();
			} catch (error) {
				console.error(error);
				window.showAlert(`Could not remove pickup slot: ${error.message}`);
			}
		});

		dom.adminSlotList.appendChild(item);
	});

	if (state.slots.length === 0) {
		const empty = document.createElement("li");
		empty.textContent = "No pickup slots configured.";
		dom.adminSlotList.appendChild(empty);
	}
}

export function renderOrders() {
	if (!authState.isAdmin) return;
	if (!dom.orderList) return;

	const searchInput = document.getElementById("order-search");
	const statusFilter = document.getElementById("order-status-filter");

	if (searchInput && !searchInput.dataset.listenerAttached) {
		searchInput.addEventListener("input", renderOrders);
		searchInput.dataset.listenerAttached = "true";
	}
	if (statusFilter && !statusFilter.dataset.listenerAttached) {
		statusFilter.addEventListener("change", renderOrders);
		statusFilter.dataset.listenerAttached = "true";
	}

	const searchTerm = searchInput ? searchInput.value.toLowerCase() : "";
	const statusTerm = statusFilter ? statusFilter.value : "";

	let filteredOrders = state.orders;
	if (searchTerm) {
		filteredOrders = filteredOrders.filter(o => 
			(o.customerEmail && o.customerEmail.toLowerCase().includes(searchTerm)) ||
			String(o.id).includes(searchTerm)
		);
	}
	if (statusTerm) {
		filteredOrders = filteredOrders.filter(o => (o.status || "PENDING") === statusTerm);
	}

	dom.orderList.innerHTML = "";
	filteredOrders.forEach((order) => {
		const rawItems = state.orderItemsByOrderId[order.id] || [];
		const orderItemsMarkup = rawItems.length > 0
			? rawItems.map((entry) => {
				const product = state.products.find((item) => item.id === Number(entry.productId));
				const productName = product?.name || `Product #${entry.productId}`;
				return `<li>${entry.quantity}x ${productName}</li>`;
			}).join("")
			: "<li>No items</li>";

		const item = document.createElement("li");
		item.className = "order-item";
		item.innerHTML = `
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

		const statusSelect = item.querySelector(".status-select");
		statusSelect.value = order.status || "PENDING";
		const button = item.querySelector("button");

		button.addEventListener("click", async () => {
			const newStatus = statusSelect.value;
			try {
				await updateOrderStatus(order.id, newStatus);
				await refreshPageDataHandler();
			} catch (error) {
				console.error(error);
				window.showAlert(`Could not update order status: ${error.message}`);
			}
		});

		dom.orderList.appendChild(item);
	});
}

export function renderAnalytics() {
	if (!authState.isAdmin) return;
	if (!dom.analyticsGrid) return;

	const salesByProduct = {};
	Object.values(state.orderItemsByOrderId).forEach((items) => {
		items.forEach((item) => {
			salesByProduct[item.productId] = (salesByProduct[item.productId] || 0) + item.quantity;
		});
	});

	const topProductEntry = Object.entries(salesByProduct).sort((a, b) => b[1] - a[1])[0];
	const topProduct = topProductEntry
		? state.products.find((product) => product.id === Number(topProductEntry[0]))
		: null;

	const lowStockCount = state.products.filter((product) => Number(product.stock || 0) <= 10).length;
	const risk = state.products.length > 0 ? ((lowStockCount / state.products.length) * 100).toFixed(1) : "0.0";

	const peakSlots = {};
	state.orders.forEach((order) => {
		peakSlots[order.pickupSlot] = (peakSlots[order.pickupSlot] || 0) + 1;
	});
	const peakEntry = Object.entries(peakSlots).sort((a, b) => b[1] - a[1])[0];

	dom.analyticsGrid.innerHTML = `
		<article class="metric-card">
			<p class="metric-label">Top Seller</p>
			<p class="metric-value">${topProduct ? topProduct.name : "No sales yet"}</p>
			<p class="metric-note">${topProductEntry ? `${topProductEntry[1]} item(s) sold` : "Place first order"}</p>
		</article>
		<article class="metric-card">
			<p class="metric-label">Low Stock Risk</p>
			<p class="metric-value">${risk}%</p>
			<p class="metric-note">${lowStockCount} products at or below 10 left</p>
		</article>
		<article class="metric-card">
			<p class="metric-label">Peak Pickup</p>
			<p class="metric-value">${peakEntry ? peakEntry[0] : "N/A"}</p>
			<p class="metric-note">${peakEntry ? `${peakEntry[1]} order(s)` : "No orders yet"}</p>
		</article>
	`;
}

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
