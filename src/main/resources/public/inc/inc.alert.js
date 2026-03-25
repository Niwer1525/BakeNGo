class AlertPopup extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
        <div class="sign-in-popup" id="alert-popup" style="display: none; z-index: 9999;">
            <div class="popup-content">
                <span class="close-btn" id="close-alert-popup">&times;</span>
                <h2 id="alert-title">Alert</h2>
                <p id="alert-message"></p>
                <button type="button" class="auth-submit-btn" id="alert-ok-btn">OK</button>
            </div>
        </div>
        `;

        const closeBtn = this.querySelector('#close-alert-popup');
        const okBtn = this.querySelector('#alert-ok-btn');
        const popup = this.querySelector('#alert-popup');

        const closePopup = () => {
            popup.style.display = 'none';
        };

        closeBtn.addEventListener('click', closePopup);
        okBtn.addEventListener('click', closePopup);
    }

    showAlert(message, title = "Alert") {
        this.querySelector('#alert-title').textContent = title;
        this.querySelector('#alert-message').textContent = message;
        this.querySelector('#alert-popup').style.display = 'flex';
    }
}
customElements.define('inc-alert', AlertPopup);

window.showAlert = function(message) {
    let alertEl = document.querySelector('inc-alert');
    if (!alertEl) {
        alertEl = document.createElement('inc-alert');
        document.body.appendChild(alertEl);
    }
    // Wait for connectedCallback to run if it was just added
    setTimeout(() => {
        alertEl.showAlert(message);
    }, 0);
};
