import axios from "axios";

const api = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL || "http://localhost:8080/api",
  headers: { "Content-Type": "application/json" },
});

// ---- Auth ----
export const registerFarmer = (data) => api.post("/auth/register/farmer", data);
export const registerBuyer = (data) => api.post("/auth/register/buyer", data);
export const login = (data) => api.post("/auth/login", data);

// ---- Farmer / Buyer profile ----
export const getFarmer = (id) => api.get(`/farmers/${id}`);
export const getBuyer = (id) => api.get(`/buyers/${id}`);

// ---- Produce ----
export const postProduce = (data) => api.post("/produce", data);
export const getProduceByFarmer = (farmerId) => api.get(`/produce/farmer/${farmerId}`);
export const getAllActiveProduce = () => api.get("/produce");

// DISTANCE FINDER — buyer search for nearby farmers/produce
export const searchProduceNearBuyer = ({ crop, buyerLat, buyerLng, maxDistanceKm }) =>
  api.get("/produce/search", { params: { crop, buyerLat, buyerLng, maxDistanceKm } });

// ---- Requirements ----
export const postRequirement = (data) => api.post("/requirements", data);
export const getRequirementsByBuyer = (buyerId) => api.get(`/requirements/buyer/${buyerId}`);

// ---- Matching ----
export const getMatchesForRequirement = (requirementId) =>
  api.get(`/matches/requirement/${requirementId}`);

// ---- Orders ----
export const createOrder = (data) => api.post("/orders", data);
export const getOrder = (id) => api.get(`/orders/${id}`);
export const getOrdersByBuyer = (buyerId) => api.get(`/orders/buyer/${buyerId}`);
export const advanceOrderStatus = (id, status) =>
  api.patch(`/orders/${id}/status`, null, { params: { status } });

// ---- Logistics ----
export const getLogisticsByOrder = (orderId) => api.get(`/logistics/order/${orderId}`);

// ---- Payments ----
export const getPaymentByOrder = (orderId) => api.get(`/payments/order/${orderId}`);
export const securePayment = (orderId) => api.post(`/payments/order/${orderId}/secure`);
export const releasePayment = (orderId) => api.post(`/payments/order/${orderId}/release`);

// ---- Admin ----
export const getAdminOverview = () => api.get("/admin/overview");

export default api;
