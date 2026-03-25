/**
 * Helper function to make API requests with consistent error handling and JSON parsing.
 * 
 * @param {*} path API endpoint path (e.g., "/api/products")
 * @param {*} options  Fetch options (method, body, etc.)
 * @returns Parsed JSON response or null for 204 No Content
 */
export async function apiRequest(path, options = {}) {
    const RESPONSE = await fetch(path, {
        headers: { "Content-Type": "application/json" },
        ...options
    });

    if (RESPONSE.status === 204) return null;

    const BODY_TEXT = await RESPONSE.text();
    if (!RESPONSE.ok) throw new Error(BODY_TEXT || `HTTP ${RESPONSE.status}`);
    return BODY_TEXT ? JSON.parse(BODY_TEXT) : null;
}

/**
 * Fetches a list of products, optionally including inactive ones.
 * 
 * @param {boolean} includeInactive Whether to include inactive products.
 * @returns {Promise<any>} A promise resolving to the fetched products.
 */
export function fetchProducts(includeInactive = false) {
    return apiRequest(`/api/products?include_inactive=${includeInactive ? "true" : "false"}`);
}

/**
 * Fetches a list of pickup slots, optionally including disabled ones.
 * 
 * @param {boolean} includeDisabled Whether to include disabled pickup slots.
 * @returns {Promise<any>} A promise resolving to the fetched pickup slots.
 */
export function fetchPickupSlots(includeDisabled = false) {
    return apiRequest(`/api/pickup-slots?include_disabled=${includeDisabled ? "true" : "false"}`);
}

/**
 * Fetches a list of orders.
 * 
 * @return {Promise<any>} A promise resolving to the fetched orders.
 */
export function fetchOrders() {
    return apiRequest("/api/orders");
}

/**
 * Fetches the items associated with a specific order.
 * 
 * @param {*} orderId The ID of the order to fetch items for.
 * @returns {Promise<any>} A promise resolving to the fetched order items.
 */
export function fetchOrderItems(orderId) {
    return apiRequest(`/api/orders/${orderId}/items`);
}

/**
 * Creates a new order.
 * 
 * @param {*} payload The order data to be sent in the request body.
 * @returns {Promise<any>} A promise resolving to the created order.
 */
export function createOrder(payload) {
    return apiRequest("/api/orders", {
        method: "POST",
        body: JSON.stringify(payload)
    });
}

/**
 * Updates the stock quantity of a product.
 * 
 * @param {*} productId The ID of the product to update.
 * @param {*} stock The new stock quantity for the product.
 * @returns {Promise<any>} A promise resolving to the updated product.
 */
export function updateProductStock(productId, stock) {
    return apiRequest(`/api/products/${productId}/stock`, {
        method: "PUT",
        body: JSON.stringify({ stock })
    });
}

/**
 * Updates the details of a product.
 * 
 * @param {*} productId The ID of the product to update.
 * @param {*} payload The product details to be sent in the request body.
 * @returns {Promise<any>} A promise resolving to the updated product.
 */
export function updateProductDetails(productId, payload) {
    return apiRequest(`/api/products/${productId}`, {
        method: "PUT",
        body: JSON.stringify(payload)
    });
}

/**
 * Updates the status of a pickup slot.
 * 
 * @param {*} id The ID of the pickup slot to update.
 * @param {*} isEnabled Whether the pickup slot should be enabled or disabled.
 * @returns {Promise<any>} A promise resolving to the updated pickup slot.
 */
export function updatePickupSlotStatus(id, isEnabled) {
    return apiRequest(`/api/pickup-slots/${id}/status`, {
        method: "PUT",
        body: JSON.stringify({ is_enabled: isEnabled })
    });
}

/**
 * Updates the capacity of a pickup slot.
 * 
 * @param {*} id The ID of the pickup slot to update.
 * @param {*} capacity The new capacity value for the pickup slot.
 * @returns {Promise<any>} A promise resolving to the updated pickup slot.
 */
export function updatePickupSlotCapacity(id, capacity) {
    return apiRequest(`/api/pickup-slots/${id}/capacity`, {
        method: "PUT",
        body: JSON.stringify({ capacity })
    });
}

/**
 * Updates the status of an order.
 * 
 * @param {*} orderId The ID of the order to update.
 * @param {*} status The new status value for the order.
 * @returns {Promise<any>} A promise resolving to the updated order.
 */
export function updateOrderStatus(orderId, status) {
    return apiRequest(`/api/orders/${orderId}/status`, {
        method: "PUT",
        body: JSON.stringify({ status })
    });
}

/**
 * Creates a new product.
 * 
 * @param {*} payload The product data to be sent in the request body.
 * @returns {Promise<any>} A promise resolving to the created product.
 */
export function createProduct(payload) {
    return apiRequest("/api/products", {
        method: "POST",
        body: JSON.stringify(payload)
    });
}

/**
 * Creates a new pickup slot.
 * 
 * @param {*} payload The pickup slot data to be sent in the request body.
 * @returns {Promise<any>} A promise resolving to the created pickup slot.
 */
export function createPickupSlot(payload) {
    return apiRequest("/api/pickup-slots", {
        method: "POST",
        body: JSON.stringify(payload)
    });
}

/**
 * Deletes a product by its ID.
 * 
 * @param {*} productId The ID of the product to delete.
 * @returns {Promise<any>} A promise resolving to the deleted product.
 */
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

/**
 * Registers a new user account.
 * 
 * @param {*} payload The user registration data to be sent in the request body.
 * @returns {Promise<any>} A promise resolving to the created user account.
 */
export function signUp(payload) {
    return apiRequest("/api/auth/sign-up", {
        method: "POST",
        body: JSON.stringify(payload)
    });
}

/**
 * Signs in a user.
 * 
 * @param {*} payload The user login data to be sent in the request body.
 * @returns {Promise<any>} A promise resolving to the signed-in user.
 */
export function signIn(payload) {
    return apiRequest("/api/auth/sign-in", {
        method: "POST",
        body: JSON.stringify(payload)
    });
}
