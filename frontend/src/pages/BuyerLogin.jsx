import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerBuyer, login } from "../api/api";
import { useSession } from "../SessionContext";

export default function BuyerLogin() {
  const [mode, setMode] = useState("login");
  const [phone, setPhone] = useState("");
  const [otp, setOtp] = useState("");
  const [form, setForm] = useState({
    name: "", email: "", businessName: "", businessType: "Restaurant",
    deliveryLocation: "", deliveryLatitude: "12.9716", deliveryLongitude: "77.5946",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { setSession } = useSession();
  const navigate = useNavigate();

  async function getBuyerIdFromUser(userId) {
    const res = await fetch(
      `${process.env.REACT_APP_API_BASE_URL || "http://localhost:8080/api"}/buyers/by-user/${userId}`
    );
    return res.json();
  }

  async function handleLogin(e) {
    e.preventDefault();
    setError(""); setLoading(true);
    try {
      const { data: user } = await login({ phone, otp });
      const buyer = await getBuyerIdFromUser(user.id);
      setSession({ role: "BUYER", user, buyer });
      navigate("/buyer/dashboard");
    } catch (err) {
      setError(err.response?.data?.error || "Login failed. Try registering first.");
    } finally {
      setLoading(false);
    }
  }

  async function handleRegister(e) {
    e.preventDefault();
    setError(""); setLoading(true);
    try {
      const { data: buyer } = await registerBuyer({
        name: form.name,
        phone,
        email: form.email,
        businessName: form.businessName,
        businessType: form.businessType,
        deliveryLocation: form.deliveryLocation,
        deliveryLatitude: parseFloat(form.deliveryLatitude),
        deliveryLongitude: parseFloat(form.deliveryLongitude),
      });
      setSession({ role: "BUYER", user: buyer.user, buyer });
      navigate("/buyer/dashboard");
    } catch (err) {
      setError(err.response?.data?.error || "Registration failed.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="card" style={{ maxWidth: 460, margin: "0 auto" }}>
      <h2>{mode === "login" ? "Buyer Login" : "Create Business Profile"}</h2>

      {mode === "login" ? (
        <form onSubmit={handleLogin}>
          <div className="field">
            <label>Mobile number</label>
            <input value={phone} onChange={(e) => setPhone(e.target.value)} required />
          </div>
          <div className="field">
            <label>Password</label>
            <input value={otp} onChange={(e) => setOtp(e.target.value)} required />
          </div>
          {error && <div className="error-text">{error}</div>}
          <button className="btn btn-primary btn-block" disabled={loading}>
            {loading ? "Logging in..." : "Login"}
          </button>
          <p className="muted" style={{ marginTop: 12 }}>
            New buyer?{" "}
            <a href="#register" onClick={(e) => { e.preventDefault(); setMode("register"); }}>
              Create a business profile
            </a>
          </p>
        </form>
      ) : (
        <form onSubmit={handleRegister}>
          <div className="row">
            <div className="field">
              <label>Contact name</label>
              <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
            </div>
            <div className="field">
              <label>Mobile number</label>
              <input value={phone} onChange={(e) => setPhone(e.target.value)} required />
            </div>
          </div>
          <div className="field">
            <label>Business name</label>
            <input value={form.businessName} onChange={(e) => setForm({ ...form, businessName: e.target.value })} required />
          </div>
          <div className="field">
            <label>Business type</label>
            <select value={form.businessType} onChange={(e) => setForm({ ...form, businessType: e.target.value })}>
              <option>Restaurant</option>
              <option>Hotel</option>
              <option>Retailer</option>
              <option>Wholesaler</option>
              <option>Institution</option>
            </select>
          </div>
          <div className="field">
            <label>Delivery location</label>
            <input value={form.deliveryLocation} onChange={(e) => setForm({ ...form, deliveryLocation: e.target.value })} required />
          </div>
          <div className="row">
            <div className="field">
              <label>Latitude</label>
              <input value={form.deliveryLatitude} onChange={(e) => setForm({ ...form, deliveryLatitude: e.target.value })} required />
            </div>
            <div className="field">
              <label>Longitude</label>
              <input value={form.deliveryLongitude} onChange={(e) => setForm({ ...form, deliveryLongitude: e.target.value })} required />
            </div>
          </div>
          <p className="muted" style={{ marginTop: -8 }}>
            Powers the distance finder when you search for farmers.
          </p>
          {error && <div className="error-text">{error}</div>}
          <button className="btn btn-primary btn-block" disabled={loading}>
            {loading ? "Creating..." : "Create Profile"}
          </button>
          <p className="muted" style={{ marginTop: 12 }}>
            Already registered?{" "}
            <a href="#login" onClick={(e) => { e.preventDefault(); setMode("login"); }}>Log in</a>
          </p>
        </form>
      )}
    </div>
  );
}
