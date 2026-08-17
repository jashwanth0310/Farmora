import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { useSession } from "../SessionContext";

export default function Navbar() {
  const { session, setSession } = useSession();
  const navigate = useNavigate();

  function logout() {
    setSession(null);
    navigate("/");
  }

  return (
    <div className="navbar">
      <div>
        <Link to="/" style={{ color: "white", textDecoration: "none" }}>
          <div className="brand">🌾 FARMORA</div>
          <div className="tagline">Fresh from Farmers. Smarter Markets.</div>
        </Link>
      </div>
      <nav>
        {!session && (
          <>
            <Link to="/farmer/login">Farmer Login</Link>
            <Link to="/buyer/login">Buyer Login</Link>
            <Link to="/admin">Admin</Link>
          </>
        )}
        {session?.role === "FARMER" && (
          <>
            <Link to="/farmer/dashboard">Dashboard</Link>
            <a href="#logout" onClick={logout}>Logout</a>
          </>
        )}
        {session?.role === "BUYER" && (
          <>
            <Link to="/buyer/dashboard">Dashboard</Link>
            <a href="#logout" onClick={logout}>Logout</a>
          </>
        )}
      </nav>
    </div>
  );
}
