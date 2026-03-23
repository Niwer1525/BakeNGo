export const AUTH_STORAGE_KEY = "bake-n-go-auth";

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

/**
 * Centralized DOM element references for easy access across modules.
 * This helps avoid repeated queries and keeps the code organized.
 */
export const dom = {
	productGrid: document.querySelector(".product-grid"),
	basketPanel: document.getElementById("basket-panel"),
	basketList: document.getElementById("basket-list"),
	basketTotal: document.getElementById("basket-total"),
	clearBasketBtn: document.getElementById("clear-basket-btn"),
	checkoutBtn: document.getElementById("checkout-btn"),
	customerEmailInput: document.getElementById("customer-email-input"),
	orderFeedback: document.getElementById("order-feedback"),
	slotList: document.querySelector(".slot-list"),
	stockList: document.querySelector(".stock-list"),
	orderList: document.querySelector(".order-list"),
	analyticsGrid: document.querySelector(".analytics-grid"),
	analyticsSection: document.getElementById("analytics"),
	dashboardSection: document.getElementById("dashboard"),
	dashboardNavLink: document.getElementById("dashboard-nav-link"),
	analyticsNavLink: document.getElementById("analytics-nav-link"),
	authStatus: document.getElementById("auth-status"),
	heroActions: document.querySelector(".hero-actions"),
	signInPopup: document.getElementById("sign-in-popup"),
	createAccountPopup: document.getElementById("create-account-popup"),
	closeSignInPopupBtn: document.getElementById("close-popup"),
	closeCreatePopupBtn: document.getElementById("close-create-account-popup"),
	signInForm: document.getElementById("sign-in-form"),
	createAccountForm: document.getElementById("create-account-form"),
	adminProductForm: document.getElementById("admin-product-form"),
	openAddProductBtn: document.getElementById("open-add-product-btn"),
	addProductPopup: document.getElementById("add-product-popup"),
	closeAddProductPopupBtn: document.getElementById("close-add-product-popup"),
	openAddPickupSlotBtn: document.getElementById("open-add-pickup-slot-btn"),
	addPickupSlotPopup: document.getElementById("add-pickup-slot-popup"),
	closeAddPickupSlotPopupBtn: document.getElementById("close-add-pickup-slot-popup"),
	adminSlotForm: document.getElementById("admin-slot-form"),
	adminSlotList: document.getElementById("admin-slot-list"),
		openOrderFiltersBtn: document.getElementById("open-order-filters-btn"),
		orderFiltersPopup: document.getElementById("order-filters-popup"),
		closeOrderFiltersPopupBtn: document.getElementById("close-order-filters-popup"),
		openProductFiltersBtn: document.getElementById("open-product-filters-btn"),
		productFiltersPopup: document.getElementById("product-filters-popup"),
		closeProductFiltersPopupBtn: document.getElementById("close-product-filters-popup"),
		openPickupSlotFiltersBtn: document.getElementById("open-pickup-slot-filters-btn"),
		pickupSlotFiltersPopup: document.getElementById("pickup-slot-filters-popup"),
		closePickupSlotFiltersPopupBtn: document.getElementById("close-pickup-slot-filters-popup"),
	addProductFeedback: document.getElementById("add-product-feedback"),
	addPickupSlotFeedback: document.getElementById("add-pickup-slot-feedback"),
	adminControlsFeedback: document.getElementById("admin-controls-feedback"),
	openCreateAccountBtn: document.getElementById("open-create-account-btn"),
	navSignInBtn: document.getElementById("sign-in-btn"),
};


