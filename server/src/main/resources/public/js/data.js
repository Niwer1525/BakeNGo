import { fetchOrderItems, fetchOrders, fetchPickupSlots, fetchProducts } from "./api.js";
import { authState, state } from "./state.js";
import { renderAll } from "./render.js";

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

export async function loadState() {
	const products = await fetchProducts();
	const slots = await fetchPickupSlots(authState.isAdmin);
	const orders = authState.isAdmin ? await fetchOrders() : [];

	state.products = Array.isArray(products) ? products.map(normalizeProduct) : [];
	state.slots = Array.isArray(slots) ? slots.map(normalizeSlot) : [];
	state.orders = Array.isArray(orders) ? orders.map(normalizeOrder) : [];

	if (!state.selectedSlot && state.slots.length > 0) {
		const enabledSlots = state.slots.filter(s => s.isEnabled);
		if(enabledSlots.length > 0) {
			state.selectedSlot = enabledSlots[0].id;
		}
	}

	if (!authState.isAdmin) {
		state.orderItemsByOrderId = {};
		return;
	}

	const orderItemsEntries = [];
	for (const order of state.orders) {
		const items = await fetchOrderItems(order.id);
		orderItemsEntries.push([order.id, Array.isArray(items) ? items.map(normalizeOrderItem) : []]);
	}

	state.orderItemsByOrderId = Object.fromEntries(orderItemsEntries);
}

export async function refreshPageData() {
	try {
		await loadState();
		renderAll();
	} catch (error) {
		console.error(error);
		window.showAlert(`Failed to load data: ${error.message}`);
	}
}
