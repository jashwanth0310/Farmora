import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSession } from "../SessionContext";
import { postRequirement } from "../api/api";

export default function PostRequirement() {
  const { session } = useSession();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    crop: "Tomato", quantityKg: "", frequency: "ONE_TIME", requiredDate: "",
    quality: "GRADE_A", maxPricePerKg: "", deliveryLocation: session?.buyer?.deliveryLocation || "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  if (!session?.buyer) { navigate("/buyer/login"); return null; }

  async function handleSubmit(e) {
    e.preventDefault();
    setError(""); setLoading(true);
    try {
      const { data: requirement } = await postRequirement({
        buyerId: session.buyer.id,
        crop: form.crop,
        quantityKg: parseFloat(form.quantityKg),
        frequency: form.frequency,
        requiredDate: form.requiredDate || null,
        quality: form.quality,
        maxPricePerKg: parseFloat(form.maxPricePerKg),
        deliveryLocation: form.deliveryLocation,
      });
      navigate(`/buyer/matches/${requirement.id}`);
    } catch (err) {
      setError(err.response?.data?.error || "Could not post requirement.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="card" style={{ maxWidth: 460, margin: "0 auto" }}>
      <h2>Post Requirement</h2>
      <form onSubmit={handleSubmit}>
        <div className="field">
          <label>Crop</label>
          <select value={form.crop} onChange={(e) => setForm({ ...form, crop: e.target.value })}>
            <option>Tomato</option>
            <option>Onion</option>
            <option>Potato</option>
            <option>Cabbage</option>
            <option>Other</option>
          </select>
        </div>
        <div className="row">
          <div className="field">
            <label>Quantity (kg)</label>
            <input type="number" value={form.quantityKg} onChange={(e) => setForm({ ...form, quantityKg: e.target.value })} required />
          </div>
          <div className="field">
            <label>Required frequency</label>
            <select value={form.frequency} onChange={(e) => setForm({ ...form, frequency: e.target.value })}>
              <option value="ONE_TIME">One-time</option>
              <option value="DAILY">Daily</option>
              <option value="ALTERNATE_DAYS">Alternate days</option>
              <option value="WEEKLY">Weekly</option>
              <option value="CUSTOM">Custom</option>
            </select>
          </div>
        </div>
        <div className="row">
          <div className="field">
            <label>Required date</label>
            <input type="date" value={form.requiredDate} onChange={(e) => setForm({ ...form, requiredDate: e.target.value })} />
          </div>
          <div className="field">
            <label>Quality</label>
            <select value={form.quality} onChange={(e) => setForm({ ...form, quality: e.target.value })}>
              <option value="GRADE_A">Grade A</option>
              <option value="GRADE_B">Grade B</option>
              <option value="STANDARD">Standard</option>
            </select>
          </div>
        </div>
        <div className="field">
          <label>Maximum price (₹/kg)</label>
          <input type="number" value={form.maxPricePerKg} onChange={(e) => setForm({ ...form, maxPricePerKg: e.target.value })} required />
        </div>
        <div className="field">
          <label>Delivery location</label>
          <input value={form.deliveryLocation} onChange={(e) => setForm({ ...form, deliveryLocation: e.target.value })} required />
        </div>
        {error && <div className="error-text">{error}</div>}
        <button className="btn btn-primary btn-block" disabled={loading}>
          {loading ? "Posting..." : "Post Requirement"}
        </button>
      </form>
    </div>
  );
}
