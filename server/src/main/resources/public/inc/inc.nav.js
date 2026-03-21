class IncHeader extends HTMLElement {
    connectedCallback() {
        this.innerHTML = `
        <header class="site-header">
            <nav class="navbar">
                <a class="brand" href="/">
                    CroissantFlow
                    <p class="eyebrow">Click &amp; Collect For Local Bakeries</p>
                </a>
                <ul class="nav-links">
                    <li><a href="#site-footer">Contact</a></li>
                    <li><a href="/#catalog">Catalog</a></li>
                    <li><a href="/#basket-panel">Basket</a></li>
                    <li><a id="dashboard-nav-link" href="/dashboard.html">Baker Dashboard</a></li>
                    <li><a id="analytics-nav-link" href="/dashboard.html#analytics">Analytics</a></li>
                    <li><a class="auth-status" id="auth-status" href="#site-footer">Guest</a></li>
                </ul>
            </nav>
        </header>
        `;
    }
}
customElements.define('inc-header', IncHeader);