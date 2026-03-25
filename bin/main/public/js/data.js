import { fetchOrderItems, fetchOrders, fetchPickupSlots, fetchProducts } from "./api.js";
import { renderAll } from "./render.js";
import { AUTH_STATE, STATE } from "./state.js";

/**
 * Normalizes product data to ensure consistent structure.
 * 
 * @param {*} product The product data to normalize.
 * @returns {Object} The normalized product object.
 */
function normalizeProduct(product) {
	return {
		id: Number(product?.id ?? 0),
		name: String(product?.name ?? "Unknown product"),
		description: String(product?.description ?? ""),
		priceCents: Number(product?.priceCents ?? product?.price_cents ?? 0),
		stock: Number(product?.stock ?? 0),
		isActive: Boolean(product?.isActive ?? product?.is_active ?? true)
	};
}

/**
 * Normalizes pickup slot data to ensure consistent structure.
 * 
 * @param {*} slot The pickup slot data to normalize.
 * @returns {Object} The normalized pickup slot object.
 */
function normalizeSlot(slot) {
	return {
		id: Number(slot?.id ?? 0),
		day: String(slot?.day ?? "N/A"),
		startTime: String(slot?.startTime ?? slot?.start_time ?? ""),
		endTime: String(slot?.endTime ?? slot?.end_time ?? ""),
		capacity: Number(slot?.capacity ?? 0),
		isEnabled: Boolean(slot?.isEnabled ?? slot?.is_enabled ?? true)
	};
}

/**
 * Normalizes order data to ensure consistent structure.
 * 
 * @param {*} order The order data to normalize.
 * @returns {Object} The normalized order object.
 */
function normalizeOrder(order) {
	return {
		id: Number(order?.id ?? 0),
		customerEmail: String(order?.customerEmail ?? order?.customer_email ?? "unknown@customer"),
		status: String(order?.status ?? "PENDING"),
		pickupSlot: String(order?.pickupSlot ?? order?.pickup_slot ?? "N/A"),
		totalCents: Number(order?.totalCents ?? order?.total_cents ?? 0)
	};
}

function normalizeOrderItem(item) {
	return {
		id: Number(item?.id ?? 0),
		orderId: Number(item?.orderId ?? item?.order_id ?? 0),
		productId: Number(item?.productId ?? item?.product_id ?? 0),
		quantity: Number(item?.quantity ?? 0)
	};
}

/**
 * Fetches and loads all necessary data for the application state.
 * 
 * @returns {Promise<void>} A promise that resolves when the state has been loaded. 
 */
export async function loadState() {
	const PRODUCTS = await fetchProducts(AUTH_STATE.isAdmin);
	const SLOTS = await fetchPickupSlots(AUTH_STATE.isAdmin);
	const ORDERS = AUTH_STATE.isAdmin ? await fetchOrders() : [];

	STATE.products = Array.isArray(PRODUCTS) ? PRODUCTS.map(normalizeProduct) : [];
	STATE.slots = Array.isArray(SLOTS) ? SLOTS.map(normalizeSlot) : [];
	STATE.orders = Array.isArray(ORDERS) ? ORDERS.map(normalizeOrder) : [];

	if (!STATE.selectedSlot && STATE.slots.length > 0) {
		const enabledSlots = STATE.slots.filter(s => s.isEnabled);
		if(enabledSlots.length > 0) STATE.selectedSlot = enabledSlots[0].id;
	}

	if (!AUTH_STATE.isAdmin) {
		STATE.orderItemsByOrderId = {};
		return;
	}

	const orderItemsEntries = [];
	for (const order of STATE.orders) {
		const items = await fetchOrderItems(order.id);
		orderItemsEntries.push([order.id, Array.isArray(items) ? items.map(normalizeOrderItem) : []]);
	}

	STATE.orderItemsByOrderId = Object.fromEntries(orderItemsEntries);
}

/**
 * Refreshes the page data by reloading the state and re-rendering the UI.
 */
export async function refreshPageData() {
	try {
		await loadState();
		renderAll();
	} catch (error) {
		console.error(error);
		window.showAlert(`Failed to load data: ${error.message}`);
	}
}
