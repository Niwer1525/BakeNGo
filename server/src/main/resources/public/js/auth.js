import { AUTH_STORAGE_KEY, authState, dom } from "./state.js";

export function saveAuthState() {
	localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(authState));
}

export function clearAuthState() {
	authState.email = null;
	authState.isAdmin = false;
	localStorage.removeItem(AUTH_STORAGE_KEY);
}

export function restoreAuthState() {
	try {
		const raw = localStorage.getItem(AUTH_STORAGE_KEY);
		if (!raw) return;
		const parsed = JSON.parse(raw);
		authState.email = parsed.email || null;
		authState.isAdmin = Boolean(parsed.isAdmin);
	} catch (_error) {
		clearAuthState();
	}
}

export function applyAuthUiState() {
	const signedIn = Boolean(authState.email);
	if (dom.authStatus) dom.authStatus.textContent = signedIn ? `${authState.email}${authState.isAdmin ? " (admin)" : ""}` : "Guest";
	if (dom.navSignInBtn) dom.navSignInBtn.textContent = signedIn ? "Sign Out" : "Sign In";
	if (dom.navCreateAccountBtn) dom.navCreateAccountBtn.classList.toggle("is-hidden", signedIn);
	if (dom.dashboardSection) dom.dashboardSection.classList.toggle("is-hidden", !authState.isAdmin);
	if (dom.dashboardNavLink) dom.dashboardNavLink.classList.toggle("is-hidden", !authState.isAdmin);
	if (dom.analyticsSection) dom.analyticsSection.classList.toggle("is-hidden", !authState.isAdmin);
	if (dom.analyticsNavLink) dom.analyticsNavLink.classList.toggle("is-hidden", !authState.isAdmin);
	if (dom.customerEmailInput) dom.customerEmailInput.value = signedIn ? authState.email : "";
}
