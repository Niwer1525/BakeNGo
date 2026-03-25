class AddProductPopup extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
        <div class="add-product-popup" id="add-product-popup">
            <div class="popup-content">
                <span class="close-btn" id="close-add-product-popup">&times;</span>
                <h2>Add new product</h2>
                <form id="admin-product-form">
                    <label for="name">Product Name:</label>
                    <input type="text" name="name" placeholder="Croissant" required>
                    <label for="description">Description:</label>
                    <input type="text" name="description" placeholder="Delicious croissant" required>
                    <label for="price">Price:</label>
                    <input type="number" name="price" min="0" placeholder="3.50" step="0.01" required>
                    <label for="stock">Stock available:</label>
                    <input type="number" name="stock" min="0" placeholder="10" required>
                    <label class="checkbox-label">
                        <input type="checkbox" name="is-active" checked>
                        Available for order
                    </label>
                    <button class="btn btn-solid small" type="submit">Create Product</button>
                </form>
                <p id="add-product-feedback" class="admin-feedback" aria-live="polite"></p>
            </div>
        </div>
        `;
    }
}
customElements.define('inc-add-product', AddProductPopup);

class AddPickupSlotPopup extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
        <div class="add-pickup-slot-popup" id="add-pickup-slot-popup">
            <div class="popup-content">
                <span class="close-btn" id="close-add-pickup-slot-popup">&times;</span>
                <h2>Add pickup slot</h2>
                <form id="admin-slot-form" class="admin-form">
                    <h4>New Pickup Slot</h4>
                    <select name="day" required>
                        <option value="Monday">Monday</option>
                        <option value="Tuesday">Tuesday</option>
                        <option value="Wednesday">Wednesday</option>
                        <option value="Thursday">Thursday</option>
                        <option value="Friday">Friday</option>
                        <option value="Saturday">Saturday</option>
                        <option value="Sunday">Sunday</option>
                    </select>
                    <div class="inline-fields">
                        <input type="time" name="start-time" required>
                        <input type="time" name="end-time" required>
                    </div>
                    <input type="number" name="capacity" min="1" placeholder="Customer capacity" required>
                    <label class="checkbox-label">
                        <input type="checkbox" name="is-enabled" checked>
                        Enabled slot
                    </label>
                    <button class="btn btn-solid small" type="submit">Create Pickup Slot</button>
                </form>
                <p id="add-pickup-slot-feedback" class="admin-feedback" aria-live="polite"></p>
            </div>
        </div>
        `;
    }
}
customElements.define('inc-add-pickup-slot', AddPickupSlotPopup);
class OrderFiltersPopup extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
        <div class="order-filters-popup" id="order-filters-popup">
            <div class="popup-content">
                <span class="close-btn" id="close-order-filters-popup">&times;</span>
                <h2>Filter Orders</h2>
                <form id="order-filters-form" class="admin-form" onsubmit="event.preventDefault();">
                    <label for="order-search">Search Customer or ID:</label>
                    <input type="text" id="order-search" placeholder="Search customer email or ID..." class="stock-input">
                    <label for="order-status-filter">Status:</label>
                    <select id="order-status-filter" class="status-select stock-input">
                        <option value="">All Status</option>
                        <option value="PENDING">PENDING</option>
                        <option value="CONFIRMED">CONFIRMED</option>
                        <option value="READY">READY</option>
                        <option value="CANCELLED">CANCELLED</option>
                    </select>
                    <label for="order-slot-filter">Pickup Slot:</label>
                    <select id="order-slot-filter" class="status-select stock-input">
                        <option value="">All Slots</option>
                    </select>
                </form>
            </div>
        </div>
        `;
    }
}
customElements.define('inc-order-filters', OrderFiltersPopup);

class ProductFiltersPopup extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
        <div class="product-filters-popup" id="product-filters-popup">
            <div class="popup-content">
                <span class="close-btn" id="close-product-filters-popup">&times;</span>
                <h2>Filter Products</h2>
                <form id="product-filters-form" class="admin-form" onsubmit="event.preventDefault();">
                    <label for="product-search">Search Name or Description:</label>
                    <input type="text" id="product-search" placeholder="Search products..." class="stock-input">
                    <label for="product-status-filter">Status:</label>
                    <select id="product-status-filter" class="status-select stock-input">
                        <option value="">All</option>
                        <option value="ACTIVE">Active</option>
                        <option value="INACTIVE">Inactive</option>
                    </select>
                </form>
            </div>
        </div>
        `;
    }
}
customElements.define('inc-product-filters', ProductFiltersPopup);

class PickupSlotFiltersPopup extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
        <div class="pickup-slot-filters-popup" id="pickup-slot-filters-popup">
            <div class="popup-content">
                <span class="close-btn" id="close-pickup-slot-filters-popup">&times;</span>
                <h2>Filter Pickup Slots</h2>
                <form id="pickup-slot-filters-form" class="admin-form" onsubmit="event.preventDefault();">
                    <label for="slot-search">Search Day or Time:</label>
                    <input type="text" id="slot-search" placeholder="Search slots..." class="stock-input">
                    <label for="slot-status-filter">Status:</label>
                    <select id="slot-status-filter" class="status-select stock-input">
                        <option value="">All</option>
                        <option value="ENABLED">Enabled</option>
                        <option value="DISABLED">Disabled</option>
                    </select>
                </form>
            </div>
        </div>
        `;
    }
}
customElements.define('inc-pickup-slot-filters', PickupSlotFiltersPopup);

