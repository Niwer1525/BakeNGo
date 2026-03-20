import { applyAuthUiState, restoreAuthState } from "./js/auth.js";
import { refreshPageData } from "./js/data.js";
import { wireActions, wirePopups } from "./js/interactions.js";
import { setRefreshPageDataHandler } from "./js/render.js";

document.addEventListener("DOMContentLoaded", async () => {
	setRefreshPageDataHandler(refreshPageData);
	restoreAuthState();
	applyAuthUiState();
	wirePopups();
	wireActions();
	await refreshPageData();
});
