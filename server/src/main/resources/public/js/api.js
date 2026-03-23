export async function apiRequest(path, options = {}) {
    const response = await fetch(path, {
        headers: { "Content-Type": "application/json" },
        ...options
    });

    if (response.status === 204) return null;

    const bodyText = await response.text();
    if (!response.ok) throw new Error(bodyText || `HTTP ${response.status}`);
    return bodyText ? JSON.parse(bodyText) : null;
}

export function fetchProducts(includeInactive = false) {
    return apiRequest(`/api/products?include_inactive=${includeInactive ? "true" : "false"}`);
}

export function fetchPickupSlots(includeDisabled = false) {
    return apiRequest(`/api/pickup-slots?include_disabled=${includeDisabled ? "true" : "false"}`);
}

export function fetchOrders() {
    return apiRequest("/api/orders");
}

export function fetchOrderItems(orderId) {
    return apiRequest(`/api/orders/${orderId}/items`);
}

export function createOrder(payload) {
    return apiRequest("/api/orders", {
        method: "POST",
        body: JSON.stringify(payload)
    });
}

export function updateProductStock(productId, stock) {
    return apiRequest(`/api/products/${productId}/stock`, {
        method: "PUT",
        body: JSON.stringify({ stock })
    });
}

export function updateProductDetails(productId, payload) {
    return apiRequest(`/api/products/${productId}`, {
        method: "PUT",
        body: JSON.stringify(payload)
    });
}

export function updatePickupSlotStatus(id, isEnabled) {
    return apiRequest(`/api/pickup-slots/${id}/status`, {
        method: "PUT",
        body: JSON.stringify({ is_enabled: isEnabled })
    });
}

export function updatePickupSlotCapacity(id, capacity) {
    return apiRequest(`/api/pickup-slots/${id}/capacity`, {
        method: "PUT",
        body: JSON.stringify({ capacity })
    });
}

export function updateOrderStatus(orderId, status) {
    return apiRequest(`/api/orders/${orderId}/status`, {
        method: "PUT",
        body: JSON.stringify({ status })
    });
}

export function createProduct(payload) {
    return apiRequest("/api/products", {
        method: "POST",
        body: JSON.stringify(payload)
    });
}

export function createPickupSlot(payload) {
    return apiRequest("/api/pickup-slots", {
        method: "POST",
        body: JSON.stringify(payload)
    });
}

export function deleteProduct(productId) {
    return apiRequest(`/api/products/${productId}`, {
        method: "DELETE"
    });
}

export function deletePickupSlot(id) {
    return apiRequest(`/api/pickup-slots/${id}`, {
        method: "DELETE"
    });
}

export function signUp(payload) {
    return apiRequest("/api/auth/sign-up", {
        method: "POST",
        body: JSON.stringify(payload)
    });
}

export function signIn(payload) {
    return apiRequest("/api/auth/sign-in", {
        method: "POST",
        body: JSON.stringify(payload)
    });
}
