import { deleteOrder, deletePickupSlot, deleteProduct, updateOrderStatus, updateProductStock } from "./api.js";
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
	let summary = document.getElementById("cart-summary");
	if (!summary) {
		summary = document.createElement("p");
		summary.id = "cart-summary";
		summary.className = "slot-note";
		dom.heroActions.insertAdjacentElement("afterend", summary);
	}

	const count = getCartCount();
	summary.textContent = count > 0
		? `Cart: ${count} item(s) ready for slot ${state.selectedSlot || "N/A"}`
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
				alert("Cannot add more than available stock.");
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
		button.className = `slot ${state.selectedSlot === slot.label ? "active" : ""}`.trim();
		button.type = "button";
		button.textContent = slot.label;
		button.addEventListener("click", () => {
			state.selectedSlot = slot.label;
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
		item.innerHTML = `
			<span>${product.name}</span>
			<strong>${product.stock}</strong>
			<input class="stock-input" type="number" min="0" value="${product.stock}" aria-label="Set stock for ${product.name}">
			<div class="manage-actions">
				<button class="btn btn-outline small" type="button" data-action="set">Set</button>
				<button class="btn btn-outline small icon-btn" type="button" data-action="remove" aria-label="Remove product ${product.name}">
					<i class="fa-solid fa-trash"></i>
				</button>
			</div>
		`;

		const input = item.querySelector(".stock-input");
		const setButton = item.querySelector("button[data-action='set']");
		const removeButton = item.querySelector("button[data-action='remove']");
		setButton.addEventListener("click", async () => {
			const newStock = Number(input.value);
			if (!Number.isInteger(newStock) || newStock < 0) {
				alert("Stock must be a non-negative integer.");
				return;
			}

			try {
				await updateProductStock(product.id, newStock);
				await refreshPageDataHandler();
			} catch (error) {
				console.error(error);
				alert(`Could not update stock: ${error.message}`);
			}
		});

		removeButton.addEventListener("click", async () => {
			try {
				await deleteProduct(product.id);
				delete state.cart[product.id];
				await refreshPageDataHandler();
			} catch (error) {
				console.error(error);
				alert(`Could not remove product: ${error.message}`);
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
		item.className = "admin-slot-item";
		item.innerHTML = `
			<span>${slot.label}</span>
			<span>${slot.isEnabled ? "enabled" : "disabled"}</span>
			<button class="btn btn-outline small icon-btn" type="button" aria-label="Remove pickup slot ${slot.label}">
				<i class="fa-solid fa-trash"></i>
			</button>
		`;

		const removeButton = item.querySelector("button");
		removeButton.addEventListener("click", async () => {
			try {
				await deletePickupSlot(slot.label);
				if (state.selectedSlot === slot.label) {
					state.selectedSlot = null;
				}
				await refreshPageDataHandler();
			} catch (error) {
				console.error(error);
				alert(`Could not remove pickup slot: ${error.message}`);
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

	dom.orderList.innerHTML = "";
	state.orders.forEach((order) => {
		const item = document.createElement("li");
		item.className = "order-item";
		item.innerHTML = `
			<span>#${order.id}</span>
			<span>${order.customerEmail || "unknown@customer"}</span>
			<span>Pickup ${order.pickupSlot}</span>
			<strong>${formatPrice(order.totalCents)}</strong>
			<select class="status-select" aria-label="Order ${order.id} status">
				<option value="PENDING">PENDING</option>
				<option value="CONFIRMED">CONFIRMED</option>
				<option value="READY">READY</option>
				<option value="CANCELLED">CANCELLED</option>
			</select>
			<button class="btn btn-outline small" type="button">Set</button>
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
				alert(`Could not update order status: ${error.message}`);
			}
		});

		dom.orderList.appendChild(item);
	});
}

function isCompletedOrder(order) {
	const status = String(order?.status || "").toUpperCase();
	return status === "READY" || status === "COMPLETED";
}

export function renderAllOrdersManagement() {
	if (!authState.isAdmin) return;
	if (!dom.allOrdersList) return;

	dom.allOrdersList.innerHTML = "";
	state.orders.forEach((order) => {
		const item = document.createElement("li");
		const completed = isCompletedOrder(order);
		item.className = "admin-order-item";
		item.innerHTML = `
			<span>#${order.id}</span>
			<span>${order.customerEmail || "unknown@customer"}</span>
			<span>${order.status || "PENDING"}</span>
			<button class="btn btn-outline small icon-btn" type="button" ${completed ? "" : "disabled"} aria-label="Remove order #${order.id}">
				<i class="fa-solid fa-trash"></i>
			</button>
		`;

		const removeButton = item.querySelector("button");
		removeButton.addEventListener("click", async () => {
			if (!isCompletedOrder(order)) {
				alert("You can only remove completed orders.");
				return;
			}

			try {
				await deleteOrder(order.id);
				await refreshPageDataHandler();
			} catch (error) {
				console.error(error);
				alert(`Could not remove order: ${error.message}`);
			}
		});

		dom.allOrdersList.appendChild(item);
	});

	if (state.orders.length === 0) {
		const empty = document.createElement("li");
		empty.textContent = "No orders yet.";
		dom.allOrdersList.appendChild(empty);
	}
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
	renderAllOrdersManagement();
	renderAnalytics();
	renderCartSummary();
}
