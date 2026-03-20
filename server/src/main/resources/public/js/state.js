export const AUTH_STORAGE_KEY = "croissant-flow-auth";

export const state = {
	products: [],
	slots: [],
	orders: [],
	orderItemsByOrderId: {},
	selectedSlot: null,
	cart: {}
};

export const authState = {
	email: null,
	isAdmin: false
};

export const dom = {
	productGrid: document.querySelector(".product-grid"),
	basketPanel: document.getElementById("basket-panel"),
	basketList: document.getElementById("basket-list"),
	basketTotal: document.getElementById("basket-total"),
	clearBasketBtn: document.getElementById("clear-basket-btn"),
	checkoutBtn: document.getElementById("checkout-btn"),
	slotList: document.querySelector(".slot-list"),
	stockList: document.querySelector(".stock-list"),
	orderList: document.querySelector(".order-list"),
	allOrdersList: document.getElementById("all-orders-list"),
	analyticsGrid: document.querySelector(".analytics-grid"),
	analyticsSection: document.getElementById("analytics"),
	dashboardSection: document.getElementById("dashboard"),
	dashboardNavLink: document.getElementById("dashboard-nav-link"),
	analyticsNavLink: document.getElementById("analytics-nav-link"),
	authStatus: document.getElementById("auth-status"),
	roleSeparatorBanner: document.getElementById("role-separator-banner"),
	restartBtn: document.getElementById("restart-btn"),
	heroActions: document.querySelector(".hero-actions"),
	signInPopup: document.getElementById("sign-in-popup"),
	createAccountPopup: document.getElementById("create-account-popup"),
	closeSignInPopupBtn: document.getElementById("close-popup"),
	closeCreatePopupBtn: document.getElementById("close-create-account-popup"),
	signInForm: document.getElementById("sign-in-form"),
	createAccountForm: document.getElementById("create-account-form"),
	adminProductForm: document.getElementById("admin-product-form"),
	adminSlotForm: document.getElementById("admin-slot-form"),
	adminSlotList: document.getElementById("admin-slot-list"),
	adminControlsFeedback: document.getElementById("admin-controls-feedback"),
	navCreateAccountBtn: document.querySelector(".nav-actions .btn-outline"),
	navSignInBtn: document.querySelector(".nav-actions .btn-solid"),
	startOrderBtn: document.querySelector(".hero-actions .btn-solid"),
	viewDashboardBtn: document.getElementById("view-dashboard-btn")
};
