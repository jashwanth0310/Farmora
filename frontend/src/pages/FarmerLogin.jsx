import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerFarmer, login, getFarmer } from "../api/api";
import { useSession } from "../SessionContext";

export default function FarmerLogin() {
  const [mode, setMode] = useState("login"); // login | register
  const [phone, setPhone] = useState("");
  const [otp, setOtp] = useState("");
  const [form, setForm] = useState({
    name: "", village: "", district: "", state: "",
    farmSize: "", latitude: "12.9716", longitude: "77.5946",
  });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const { setSession } = useSession();
  const navigate = useNavigate();

  async function handleLogin(e) {
    e.preventDefault();
    setError(""); setLoading(true);
    try {
      const { data: user } = await login({ phone, otp });
      const { data: farmer } = await getFarmer((await getFarmerIdFromUser(user.id)));
      setSession({ role: "FARMER", user, farmer });
      navigate("/farmer/dashboard");
    } catch (err) {
      setError(err.response?.data?.error || "Login failed. Try registering first.");
    } finally {
      setLoading(false);
    }
  }

  async function getFarmerIdFromUser(userId) {
    const res = await fetch(
      `${process.env.REACT_APP_API_BASE_URL || "http://localhost:8080/api"}/farmers/by-user/${userId}`
    );
    const data = await res.json();
    return data.id;
  }

  async function handleRegister(e) {
    e.preventDefault();
    setError(""); setLoading(true);
    try {
      const { data: farmer } = await registerFarmer({
        name: form.name,
        phone,
        village: form.village,
        district: form.district,
        state: form.state,
        farmSize: parseFloat(form.farmSize) || null,
        latitude: parseFloat(form.latitude),
        longitude: parseFloat(form.longitude),
      });
      setSession({ role: "FARMER", user: farmer.user, farmer });
      navigate("/farmer/dashboard");
    } catch (err) {
      setError(err.response?.data?.error || "Registration failed.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="card" style={{ maxWidth: 460, margin: "0 auto" }}>
      <h2>{mode === "login" ? "Farmer Login" : "Create Farmer Profile"}</h2>
      <p className="muted">
        {mode === "login" ? "Enter your mobile number and Password." : "Tell us about your farm."}
      </p>

      {mode === "login" ? (
        <form onSubmit={handleLogin}>
          <div className="field">
            <label>Mobile number</label>
            <input value={phone} onChange={(e) => setPhone(e.target.value)} placeholder="9876543210" required />
          </div>
          <div className="field">
            <label>Password</label>
            <input value={otp} onChange={(e) => setOtp(e.target.value)} placeholder="1234" required />
          </div>
          {error && <div className="error-text">{error}</div>}
          <button className="btn btn-primary btn-block" disabled={loading}>
            {loading ? "Logging in..." : "Login"}
          </button>
          <p className="muted" style={{ marginTop: 12 }}>
            New farmer?{" "}
            <a href="#register" onClick={(e) => { e.preventDefault(); setMode("register"); }}>
              Create a profile
            </a>
          </p>
        </form>
      ) : (
        <form onSubmit={handleRegister}>
          <div className="field">
            <label>Name</label>
            <input value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
          </div>
          <div className="field">
            <label>Mobile number</label>
            <input value={phone} onChange={(e) => setPhone(e.target.value)} required />
          </div>
          <div className="row">
            <div className="field">
              <label>Village</label>
              <input value={form.village} onChange={(e) => setForm({ ...form, village: e.target.value })} required />
            </div>
            <div className="field">
              <label>District</label>
              <input value={form.district} onChange={(e) => setForm({ ...form, district: e.target.value })} required />
            </div>
          </div>
          <div className="row">
            <div className="field">
              <label>State</label>
              <input value={form.state} onChange={(e) => setForm({ ...form, state: e.target.value })} required />
            </div>
            <div className="field">
              <label>Farm size (acres, optional)</label>
              <input value={form.farmSize} onChange={(e) => setForm({ ...form, farmSize: e.target.value })} />
            </div>
          </div>
          <div className="row">
            <div className="field">
              <label>Latitude</label>
              <input value={form.latitude} onChange={(e) => setForm({ ...form, latitude: e.target.value })} required />
            </div>
            <div className="field">
              <label>Longitude</label>
              <input value={form.longitude} onChange={(e) => setForm({ ...form, longitude: e.target.value })} required />
            </div>
          </div>
          <p className="muted" style={{ marginTop: -8 }}>
            Used by Farmora's distance finder so buyers can see how far your farm is.
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
