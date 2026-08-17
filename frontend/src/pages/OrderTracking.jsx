import React, { useEffect, useState, useCallback } from "react";
import { useParams } from "react-router-dom";
import {
  getOrder, getLogisticsByOrder, getPaymentByOrder,
  advanceOrderStatus, securePayment, releasePayment,
} from "../api/api";
import StatusTimeline from "../components/StatusTimeline";

const NEXT_STATUS = {
  ORDER_CONFIRMED: "FARMERS_CONFIRMED",
  FARMERS_CONFIRMED: "PRODUCE_COLLECTED",
  PRODUCE_COLLECTED: "IN_TRANSIT",
  IN_TRANSIT: "DELIVERED",
  DELIVERED: "BUYER_CONFIRMED",
  BUYER_CONFIRMED: "PAYMENT_RELEASED",
};

export default function OrderTracking() {
  const { orderId } = useParams();
  const [order, setOrder] = useState(null);
  const [logistics, setLogistics] = useState(null);
  const [payment, setPayment] = useState(null);
  const [busy, setBusy] = useState(false);

  const refresh = useCallback(() => {
    getOrder(orderId).then((res) => setOrder(res.data));
    getLogisticsByOrder(orderId).then((res) => setLogistics(res.data)).catch(() => {});
    getPaymentByOrder(orderId).then((res) => setPayment(res.data)).catch(() => {});
  }, [orderId]);

  useEffect(() => { refresh(); }, [refresh]);

  if (!order) return <div className="card">Loading order...</div>;

  async function handlePayNow() {
    setBusy(true);
    try {
      await securePayment(orderId);
      refresh();
    } finally { setBusy(false); }
  }

  async function handleAdvance() {
    const next = NEXT_STATUS[order.status];
    if (!next) return;
    setBusy(true);
    try {
      await advanceOrderStatus(orderId, next);
      if (next === "PAYMENT_RELEASED") {
        await releasePayment(orderId);
      }
      refresh();
    } finally { setBusy(false); }
  }

  return (
    <div>
      <div className="card">
        <h2>Order #{order.orderId} — {order.crop}</h2>
        <p className="muted">{order.totalQuantityKg} kg · Total distance {order.totalDistanceKm} km</p>
        <div style={{ marginTop: 10 }}>
          {order.farmerBreakdown.map((line, i) => <div key={i} className="muted">• {line}</div>)}
        </div>
      </div>

      <div className="card">
        <h3>Order Summary</h3>
        <table>
          <tbody>
            <tr><td>Produce value</td><td>₹{order.produceValue}</td></tr>
            <tr><td>Estimated logistics</td><td>₹{order.logisticsCost}</td></tr>
            <tr><td>Platform fee</td><td>₹{order.platformFee}</td></tr>
            <tr><td><strong>Total</strong></td><td><strong>₹{order.totalAmount}</strong></td></tr>
          </tbody>
        </table>
      </div>

      {logistics && (
        <div className="card">
          <h3>🚚 Logistics</h3>
          <p className="muted">Pickup: {logistics.pickupSummary}</p>
          <p className="muted">Drop: {logistics.deliveryLocation}</p>
          <p className="muted">
            {logistics.vehicleType} · {logistics.totalDistanceKm} km · ~₹{logistics.estimatedCost} estimated
          </p>
          <span className="badge badge-amber">{logistics.status}</span>
        </div>
      )}

      <div className="card">
        <h3>Order Tracking</h3>
        <StatusTimeline status={order.status} />
      </div>

      {payment && (
        <div className="card">
          <h3>Payment</h3>
          <p>Status: <span className="badge badge-green">{payment.status}</span></p>
          {payment.status === "PENDING" && (
            <button className="btn btn-primary" onClick={handlePayNow} disabled={busy}>
              Pay ₹{order.totalAmount}
            </button>
          )}
          {payment.status === "SECURED" && <p className="muted">💳 Payment secured — released after delivery confirmation.</p>}
          {payment.status === "RELEASED" && <p className="muted">✅ ₹{payment.amount} released to farmers.</p>}
        </div>
      )}

      {NEXT_STATUS[order.status] && (
        <div className="card">
          <button className="btn btn-secondary" onClick={handleAdvance} disabled={busy}>
            {busy ? "Updating..." : `Mark as: ${NEXT_STATUS[order.status].replaceAll("_", " ")}`}
          </button>
          <p className="muted" style={{ marginTop: 8 }}>
            (Demo control — in production this is driven by the logistics partner / buyer confirmation.)
          </p>
        </div>
      )}
    </div>
  );
}
