import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSession } from "../SessionContext";
import { searchProduceNearBuyer } from "../api/api";

/**
 * DISTANCE FINDER
 * Lets a buyer search available produce/farmers near their delivery
 * location. Every result comes back from the backend already carrying
 * a computed distanceKm (Haversine) and an estimated delivery time,
 * and results are sorted nearest-first. Buyer can also filter by a
 * max radius in km.
 */
export default function FindFarmers() {
  const { session } = useSession();
  const navigate = useNavigate();
  const [crop, setCrop] = useState("Tomato");
  const [maxDistanceKm, setMaxDistanceKm] = useState("");
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  if (!session?.buyer) { navigate("/buyer/login"); return null; }

  async function handleSearch(e) {
    e.preventDefault();
    setError(""); setLoading(true);
    try {
      const { data } = await searchProduceNearBuyer({
        crop,
        buyerLat: session.buyer.deliveryLatitude,
        buyerLng: session.buyer.deliveryLongitude,
        maxDistanceKm: maxDistanceKm ? parseFloat(maxDistanceKm) : undefined,
      });
      setResults(data);
    } catch (err) {
      setError(err.response?.data?.error || "Search failed.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div>
      <div className="card">
        <h2>📍 Find Nearby Farmers</h2>
        <p className="muted">
          Searching from {session.buyer.deliveryLocation}. Results are sorted by distance.
        </p>
        <form onSubmit={handleSearch} className="row" style={{ alignItems: "flex-end" }}>
          <div className="field">
            <label>Crop</label>
            <select value={crop} onChange={(e) => setCrop(e.target.value)}>
              <option>Tomato</option>
              <option>Onion</option>
              <option>Potato</option>
              <option>Cabbage</option>
              <option value="">Any crop</option>
            </select>
          </div>
          <div className="field">
            <label>Max distance (km, optional)</label>
            <input
              type="number"
              placeholder="e.g. 50"
              value={maxDistanceKm}
              onChange={(e) => setMaxDistanceKm(e.target.value)}
            />
          </div>
          <div className="field" style={{ flex: "0 0 160px" }}>
            <button className="btn btn-primary btn-block" disabled={loading}>
              {loading ? "Searching..." : "🔍 Search"}
            </button>
          </div>
        </form>
        {error && <div className="error-text">{error}</div>}
      </div>

      {results && (
        <div className="card">
          <h3>{results.length} farmer{results.length !== 1 ? "s" : ""} found</h3>
          {results.length === 0 && <p className="muted">No matching produce found nearby.</p>}
          {results.map((r) => (
            <div key={r.produceId} className="produce-item">
              <div>
                <strong>🍅 {r.crop}</strong> — {r.farmerName}
                <div className="muted">
                  {r.availableQuantityKg} kg available · ₹{r.pricePerKg}/kg · {r.quality?.replace("_", " ")}
                </div>
                <div className="muted">{r.village}, {r.district} · Reliability {r.reliabilityScore}%</div>
              </div>
              <div style={{ textAlign: "right" }}>
                <div className="distance-pill">📍 {r.distanceKm != null ? `${r.distanceKm} km` : "distance unknown"}</div>
                <div className="muted" style={{ marginTop: 4 }}>ETA ~{r.estimatedDeliveryTime}</div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
