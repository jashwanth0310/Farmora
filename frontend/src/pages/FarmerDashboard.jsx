import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useSession } from "../SessionContext";
import { getProduceByFarmer } from "../api/api";

export default function FarmerDashboard() {
  const { session } = useSession();
  const navigate = useNavigate();
  const [produce, setProduce] = useState([]);

  useEffect(() => {
    if (!session?.farmer) { navigate("/farmer/login"); return; }
    getProduceByFarmer(session.farmer.id).then((res) => setProduce(res.data));
  }, [session, navigate]);

  if (!session?.farmer) return null;

  const earnings = produce.reduce((sum, p) => {
    const sold = (p.quantityKg || 0) - (p.availableQuantityKg || 0);
    return sum + sold * (p.pricePerKg || 0);
  }, 0);

  return (
    <div>
      <div className="card">
        <h2>Good morning, {session.farmer.user.name}</h2>
        <p className="muted">{session.farmer.village}, {session.farmer.district}, {session.farmer.state}</p>
      </div>

      <div className="stat-grid" style={{ marginBottom: 16 }}>
        <div className="stat-box">
          <div className="value">{produce.length}</div>
          <div className="label">Produce Listings</div>
        </div>
        <div className="stat-box">
          <div className="value">₹{earnings.toLocaleString("en-IN")}</div>
          <div className="label">Earnings</div>
        </div>
        <div className="stat-box">
          <div className="value">{session.farmer.reliabilityScore}%</div>
          <div className="label">Reliability</div>
        </div>
      </div>

      <div className="card">
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
          <h3>My Produce</h3>
          <Link to="/farmer/add-produce" className="btn btn-primary">+ Add Produce</Link>
        </div>
        {produce.length === 0 && <p className="muted">No produce listed yet.</p>}
        {produce.map((p) => (
          <div key={p.id} className="produce-item">
            <div>
              <strong>🍅 {p.crop}</strong>
              <div className="muted">
                {p.availableQuantityKg} kg available · ₹{p.pricePerKg}/kg · {p.quality?.replace("_", " ")}
              </div>
              <div className="muted">{p.locationText} · Available {p.availabilityDate}</div>
            </div>
            <span className={`badge ${p.status === "ACTIVE" ? "badge-green" : "badge-gray"}`}>
              {p.status === "ACTIVE" ? "🟢 Active" : p.status}
            </span>
          </div>
        ))}
      </div>
    </div>
  );
}
