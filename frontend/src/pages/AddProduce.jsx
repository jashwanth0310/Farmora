import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useSession } from "../SessionContext";
import { postProduce } from "../api/api";

export default function AddProduce() {
  const { session } = useSession();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    crop: "Tomato", quantityKg: "", pricePerKg: "", quality: "GRADE_A",
    availabilityDate: "", locationText: session?.farmer?.village || "",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  if (!session?.farmer) { navigate("/farmer/login"); return null; }

  async function handleSubmit(e) {
    e.preventDefault();
    setError(""); setLoading(true);
    try {
      await postProduce({
        farmerId: session.farmer.id,
        crop: form.crop,
        quantityKg: parseFloat(form.quantityKg),
        pricePerKg: parseFloat(form.pricePerKg),
        quality: form.quality,
        availabilityDate: form.availabilityDate || null,
        locationText: form.locationText,
      });
      navigate("/farmer/dashboard");
    } catch (err) {
      setError(err.response?.data?.error || "Could not post produce.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="card" style={{ maxWidth: 460, margin: "0 auto" }}>
      <h2>Add Produce</h2>
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
            <label>Expected Price (₹/kg)</label>
            <input type="number" value={form.pricePerKg} onChange={(e) => setForm({ ...form, pricePerKg: e.target.value })} required />
          </div>
        </div>
        <div className="row">
          <div className="field">
            <label>Quality</label>
            <select value={form.quality} onChange={(e) => setForm({ ...form, quality: e.target.value })}>
              <option value="GRADE_A">Grade A</option>
              <option value="GRADE_B">Grade B</option>
              <option value="STANDARD">Standard</option>
            </select>
          </div>
          <div className="field">
            <label>Availability date</label>
            <input type="date" value={form.availabilityDate} onChange={(e) => setForm({ ...form, availabilityDate: e.target.value })} />
          </div>
        </div>
        <div className="field">
          <label>Location</label>
          <input value={form.locationText} onChange={(e) => setForm({ ...form, locationText: e.target.value })} />
        </div>
        {error && <div className="error-text">{error}</div>}
        <button className="btn btn-primary btn-block" disabled={loading}>
          {loading ? "Posting..." : "Post Produce"}
        </button>
      </form>
    </div>
  );
}
