class IncFooter extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
        <inc-login></inc-login>
        <inc-create-account></inc-create-account>
        <footer class="site-footer" id="site-footer">
            <div class="footer-content">
                <!-- Contact Info -->
                <div class="footer-section">
                    <h4>Contact Us</h4>
                    <p><a href="mailto:contact@croissantflow.com" class="footer-link">contact@croissantflow.com</a></p>
                    <p><a href="tel:+33612xx5678" class="footer-link">06 12 xx xx 78</a></p>
                    <p>123 Bakery Lane<br>75001 Paris, France</p>
                </div>
                <!-- Map -->
                <div class="footer-section">
                    <h4>Location</h4>
                    <div class="map-container">
                        <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2624.9914406081493!2d2.292292615674412!3d48.85837360866272!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x47e66e2964e34e2d%3A0x8ddca9ee380ef7e0!2sTour%20Eiffel!5e0!3m2!1sfr!2sfr!4v1689230557257!5m2!1sfr!2sfr" width="100%" height="120" style="border:0; border-radius: var(--radius-sm);" allowfullscreen="" loading="lazy" referrerpolicy="no-referrer-when-downgrade"></iframe>
                    </div>
                </div>
                <!-- Resources & Social Links -->
                <div class="footer-section">
                    <h4>Resources</h4>
                    <a href="tou.html" class="footer-link">Terms of Use (TOU)</a>
                    <div class="social-links">
                        <a href="https://twitter.com/" target="_blank" rel="noopener noreferrer" aria-label="Twitter">
                            <i class="fa-brands fa-x-twitter"></i>
                        </a>
                        <a href="https://facebook.com/" target="_blank" rel="noopener noreferrer" aria-label="Facebook">
                            <i class="fa-brands fa-facebook"></i>
                        </a>
                        <a href="https://instagram.com/" target="_blank" rel="noopener noreferrer" aria-label="Instagram">
                            <i class="fa-brands fa-instagram"></i>
                        </a>
                    </div>
                </div>
            </div>
            <!-- Footer Bottom -->
            <div class="footer-bottom">
                <p>Made with ❤️ by Niwer - Showcase</p>
                <button class="btn btn-outline" type="button" id="sign-in-btn">Sign In to Dashboard</button>
            </div>
        </footer>
        `;
    }
}
customElements.define('inc-footer', IncFooter);