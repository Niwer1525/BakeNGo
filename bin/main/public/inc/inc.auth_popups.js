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
                    <div class="password-input-group">
                        <input type="password" id="password" name="password" required autocomplete>
                        <button type="button" class="toggle-password icon-btn" aria-label="Toggle password visibility"><i class="fa-solid fa-eye"></i></button>
                    </div>
                    <button type="submit" class="auth-submit-btn">Sign In</button>
                    <button type="button" class="btn btn-outline auth-alt-btn" id="open-create-account-btn">Don't have an account?</button>
                </form>
            </div>
        </div>
        `;

        const toggleBtn = this.querySelector('.toggle-password');
        const passwordInput = this.querySelector('#password');
        toggleBtn.addEventListener('click', () => {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            toggleBtn.innerHTML = type === 'password' ? '<i class="fa-solid fa-eye"></i>' : '<i class="fa-solid fa-eye-slash"></i>';
        });
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
                    <div class="password-input-group">
                        <input type="password" id="new-password" name="new-password" required autocomplete>
                        <button type="button" class="toggle-password icon-btn" aria-label="Toggle password visibility"><i class="fa-solid fa-eye"></i></button>
                    </div>
                    <button type="submit" class="auth-submit-btn">Create Account</button>
                </form>
            </div>
        </div>
        `;

        const toggleBtn = this.querySelector('.toggle-password');
        const passwordInput = this.querySelector('#new-password');
        toggleBtn.addEventListener('click', () => {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            toggleBtn.innerHTML = type === 'password' ? '<i class="fa-solid fa-eye"></i>' : '<i class="fa-solid fa-eye-slash"></i>';
        });
    }
}
customElements.define('inc-create-account', CreateAccountPopup);