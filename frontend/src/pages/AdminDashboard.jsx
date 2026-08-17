import React, { useEffect, useState } from "react";
import { getAdminOverview } from "../api/api";

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);

  useEffect(() => {
    getAdminOverview().then((res) => setStats(res.data));
  }, []);

  if (!stats) return <div className="card">Loading admin overview...</div>;

  return (
    <div>
      <div className="card">
        <h2>Admin Dashboard</h2>
        <p className="muted">Platform-wide overview.</p>
      </div>

      <div className="stat-grid">
        <div className="stat-box"><div className="value">{stats.farmers}</div><div className="label">Farmers</div></div>
        <div className="stat-box"><div className="value">{stats.buyers}</div><div className="label">Buyers</div></div>
        <div className="stat-box"><div className="value">{stats.activeOrders}</div><div className="label">Active Orders</div></div>
        <div className="stat-box"><div className="value">₹{stats.gmv?.toLocaleString("en-IN")}</div><div className="label">GMV</div></div>
        <div className="stat-box"><div className="value">{stats.successfulDeliveryRate}%</div><div className="label">Successful Deliveries</div></div>
        <div className="stat-box"><div className="value">{stats.totalProduceListings}</div><div className="label">Produce Listings</div></div>
        <div className="stat-box"><div className="value">{stats.totalRequirements}</div><div className="label">Requirements Posted</div></div>
      </div>
    </div>
  );
}
