class LoginPopup extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
        <div class="sign-in-popup" id="sign-in-popup">
            <div class="popup-content">
                <span class="close-btn" id="close-popup">&times;</span>
                <h2>Sign In</h2>
                <form id="sign-in-form">
                    <label for="email">Email:</label>
                    <input type="email" id="email" name="email" required>
                    <label for="password">Password:</label>
                    <input type="password" id="password" name="password" required autocomplete>
                    <button type="submit">Sign In</button>
                    <button type="button" class="btn btn-outline" id="open-create-account-btn" style="margin-top: 10px;">Don't have an account?</button>
                </form>
            </div>
        </div>
        `;
    }
}
customElements.define('inc-login', LoginPopup);

class CreateAccountPopup extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
        <div class="create-account-popup" id="create-account-popup">
            <div class="popup-content">
                <span class="close-btn" id="close-create-account-popup">&times;</span>
                <h2>Create Account</h2>
                <form id="create-account-form">
                    <label for="new-email">Email:</label>
                    <input type="email" id="new-email" name="new-email" required>
                    <label for="new-password">Password:</label>
                    <input type="password" id="new-password" name="new-password" required autocomplete>
                    <button type="submit">Create Account</button>
                </form>
            </div>
        </div>
        `;
    }
}
customElements.define('inc-create-account', CreateAccountPopup);