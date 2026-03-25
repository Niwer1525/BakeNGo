import { AUTH_STORAGE_KEY, AUTH_STATE, DOM } from "./state.js";

/**
 * Saves the current authentication state to localStorage.
 */
export function saveAuthState() {
	localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(AUTH_STATE));
}

/*
 * Updates the authentication state with the provided email and admin status, then saves it.
 */
export function clearAuthState() {
	AUTH_STATE.email = null;
	AUTH_STATE.isAdmin = false;
	localStorage.removeItem(AUTH_STORAGE_KEY);
}

/**
 * Restores the authentication state from localStorage, if available. If the stored data is invalid, it clears the authentication state.
 */
export function restoreAuthState() {
	try {
		const raw = localStorage.getItem(AUTH_STORAGE_KEY);
		if (!raw) return;
		const parsed = JSON.parse(raw);
		AUTH_STATE.email = parsed.email || null;
		AUTH_STATE.isAdmin = Boolean(parsed.isAdmin);
	} catch (_error) {
		clearAuthState();
	}
}

/**
 * Applies the current authentication state to the UI by updating relevant elements based on whether the user is signed in and if they have admin privileges.
 */
export function applyAuthUiState() {
	const signedIn = Boolean(AUTH_STATE.email);
	if (DOM.authStatus) DOM.authStatus.textContent = signedIn ? `${AUTH_STATE.email}${AUTH_STATE.isAdmin ? " (admin)" : ""}` : "Guest";
	if (DOM.navSignInBtn) DOM.navSignInBtn.textContent = signedIn ? "Sign Out" : "Sign In";
	if (DOM.navCreateAccountBtn) DOM.navCreateAccountBtn.classList.toggle("is-hidden", signedIn);
	if (DOM.dashboardSection) DOM.dashboardSection.classList.toggle("is-hidden", !AUTH_STATE.isAdmin);
	if (DOM.dashboardNavLink) DOM.dashboardNavLink.classList.toggle("is-hidden", !AUTH_STATE.isAdmin);
	if (DOM.analyticsSection) DOM.analyticsSection.classList.toggle("is-hidden", !AUTH_STATE.isAdmin);
	if (DOM.analyticsNavLink) DOM.analyticsNavLink.classList.toggle("is-hidden", !AUTH_STATE.isAdmin);
	if (DOM.customerEmailInput) DOM.customerEmailInput.value = signedIn ? AUTH_STATE.email : "";
}