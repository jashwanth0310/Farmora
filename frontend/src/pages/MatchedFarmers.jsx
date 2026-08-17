import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { useSession } from "../SessionContext";
import { getMatchesForRequirement, createOrder } from "../api/api";

export default function MatchedFarmers() {
  const { requirementId } = useParams();
  const { session } = useSession();
  const navigate = useNavigate();
  const [matches, setMatches] = useState([]);
  const [selected, setSelected] = useState({}); // produceId -> quantityKg
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!session?.buyer) { navigate("/buyer/login"); return; }
    getMatchesForRequirement(requirementId).then((res) => setMatches(res.data));
  }, [requirementId, session, navigate]);

  if (!session?.buyer) return null;

  function toggleSelect(m) {
    setSelected((prev) => {
      const next = { ...prev };
      if (next[m.produceId] != null) {
        delete next[m.produceId];
      } else {
        next[m.produceId] = Math.min(m.availableQuantityKg, 100);
      }
      return next;
    });
  }

  function updateQty(produceId, value, max) {
    setSelected((prev) => ({ ...prev, [produceId]: Math.min(parseFloat(value) || 0, max) }));
  }

  const totalSelected = Object.values(selected).reduce((a, b) => a + b, 0);

  async function handleCreateOrder() {
    setError(""); setLoading(true);
    try {
      const selections = Object.entries(selected).map(([produceId, quantityKg]) => ({
        produceId: parseInt(produceId, 10),
        quantityKg,
      }));
      const { data: order } = await createOrder({
        requirementId: parseInt(requirementId, 10),
        buyerId: session.buyer.id,
        selections,
      });
      navigate(`/orders/${order.orderId}`);
    } catch (err) {
      setError(err.response?.data?.error || "Could not create order.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div>
      <div className="card">
        <h2>AI Matched Farmers</h2>
        <p className="muted">Ranked by match score (quantity, price, distance, quality, availability).</p>
      </div>

      <div className="card">
        {matches.length === 0 && <p className="muted">No matches found yet for this requirement.</p>}
        {matches.map((m) => (
          <div key={m.matchId} className="match-item">
            <div>
              <strong>{m.farmerName}</strong> <span className="muted">— {m.village}</span>
              <div className="muted">
                {m.availableQuantityKg} kg · ₹{m.pricePerKg}/kg · {m.quality?.replace("_", " ")} · Available {m.availabilityDate}
              </div>
              <div className="muted">Reliability {m.reliabilityScore}%</div>
            </div>
            <div style={{ textAlign: "right" }}>
              <div className="match-score">{m.matchScore}% Match</div>
              <div className="distance-pill">📍 {m.distanceKm != null ? `${m.distanceKm} km` : "N/A"}</div>
              <div style={{ marginTop: 8 }}>
                <label style={{ fontSize: 13 }}>
                  <input
                    type="checkbox"
                    checked={selected[m.produceId] != null}
                    onChange={() => toggleSelect(m)}
                    style={{ marginRight: 6 }}
                  />
                  Include in order
                </label>
                {selected[m.produceId] != null && (
                  <div style={{ marginTop: 6 }}>
                    <input
                      type="number"
                      style={{ width: 90, padding: 6, borderRadius: 8, border: "1.5px solid #d8dde1" }}
                      value={selected[m.produceId]}
                      max={m.availableQuantityKg}
                      onChange={(e) => updateQty(m.produceId, e.target.value, m.availableQuantityKg)}
                    />{" "}
                    kg
                  </div>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>

      {Object.keys(selected).length > 0 && (
        <div className="card">
          <h3>Order can be fulfilled</h3>
          <p>
            {Object.entries(selected)
              .map(([pid, qty]) => {
                const m = matches.find((mm) => mm.produceId === parseInt(pid, 10));
                return `${m?.farmerName}: ${qty} kg`;
              })
              .join(" + ")}
            {" = "}
            <strong>{totalSelected} kg total</strong>
          </p>
          {error && <div className="error-text">{error}</div>}
          <button className="btn btn-primary" onClick={handleCreateOrder} disabled={loading}>
            {loading ? "Creating..." : "CREATE COMBINED ORDER"}
          </button>
        </div>
      )}
    </div>
  );
}
