class ProductOrderSuccessPopup extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
        <div class="sign-in-popup" id="sign-in-popup">
            <div class="popup-content">
                <span class="close-btn" id="close-popup">&times;</span>
                <h2>Order Placed Successfully!</h2>
                <p>Thank you for your order. Your delicious croissants will be ready for pickup for [PICKUP_SLOT] on [PICKUP_DATE].
                We look forward to serving you again soon!</p>
                </p>
            </div>
        </div>
        `;
    }
}
customElements.define('inc-product-order-success', ProductOrderSuccessPopup);