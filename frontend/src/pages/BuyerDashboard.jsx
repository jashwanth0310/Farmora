import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useSession } from "../SessionContext";
import { getRequirementsByBuyer, getOrdersByBuyer } from "../api/api";

export default function BuyerDashboard() {
  const { session } = useSession();
  const navigate = useNavigate();
  const [requirements, setRequirements] = useState([]);
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    if (!session?.buyer) { navigate("/buyer/login"); return; }
    getRequirementsByBuyer(session.buyer.id).then((res) => setRequirements(res.data));
    getOrdersByBuyer(session.buyer.id).then((res) => setOrders(res.data));
  }, [session, navigate]);

  if (!session?.buyer) return null;

  return (
    <div>
      <div className="card">
        <h2>Welcome, {session.buyer.businessName}</h2>
        <p className="muted">{session.buyer.businessType} · {session.buyer.deliveryLocation}</p>
      </div>

      <div className="row" style={{ marginBottom: 16 }}>
        <Link to="/buyer/post-requirement" className="btn btn-primary" style={{ flex: 1, textAlign: "center" }}>
          + Post Requirement
        </Link>
        <Link to="/buyer/find-farmers" className="btn btn-secondary" style={{ flex: 1, textAlign: "center" }}>
          📍 Find Nearby Farmers
        </Link>
      </div>

      <div className="card">
        <h3>Active Requirements</h3>
        {requirements.length === 0 && <p className="muted">No requirements posted yet.</p>}
        {requirements.map((r) => (
          <div key={r.id} className="produce-item">
            <div>
              <strong>{r.crop}</strong>
              <div className="muted">{r.quantityKg} kg · {r.frequency?.replace("_", " ")} · Max ₹{r.maxPricePerKg}/kg</div>
            </div>
            <div>
              <span className="badge badge-green">{r.status}</span>{" "}
              <Link to={`/buyer/matches/${r.id}`} className="btn btn-secondary">View Matches</Link>
            </div>
          </div>
        ))}
      </div>

      <div className="card">
        <h3>Orders</h3>
        {orders.length === 0 && <p className="muted">No orders yet.</p>}
        {orders.map((o) => (
          <div key={o.orderId} className="produce-item">
            <div>
              <strong>{o.crop}</strong>
              <div className="muted">{o.totalQuantityKg} kg · ₹{o.totalAmount}</div>
            </div>
            <div>
              <span className="badge badge-amber">{o.status?.replaceAll("_", " ")}</span>{" "}
              <Link to={`/orders/${o.orderId}`} className="btn btn-secondary">Track</Link>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
