import React from "react";
import { Link } from "react-router-dom";

export default function Home() {
  return (
    <div>
      <div className="card" style={{ textAlign: "center", padding: "40px 20px" }}>
        <h1>Farmers post what they have.<br />Buyers post what they need.</h1>
        <p className="muted" style={{ fontSize: 16 }}>
          Farmora intelligently connects them — matching on quantity, price,
          quality, distance and availability, then handling logistics,
          delivery and payment.
        </p>
        <div className="row" style={{ justifyContent: "center", marginTop: 20 }}>
          <Link to="/farmer/login" className="btn btn-primary" style={{ maxWidth: 220 }}>
            I'm a Farmer
          </Link>
          <Link to="/buyer/login" className="btn btn-secondary" style={{ maxWidth: 220 }}>
            I'm a Buyer
          </Link>
        </div>
      </div>

      <div className="card">
        <h3>How it works</h3>
        <div className="row">
          <div>
            <strong>SUPPLY</strong>
            <p className="muted">Farmer A — 300 kg<br />Farmer B — 200 kg</p>
          </div>
          <div>
            <strong>⬇ FARMORA MATCHING ENGINE ⬇</strong>
            <p className="muted">Scores on quantity, price, distance, quality, availability</p>
          </div>
          <div>
            <strong>DEMAND</strong>
            <p className="muted">Buyer needs 500 kg</p>
          </div>
        </div>
        <p className="muted">↓ Logistics → Delivery → Payment → Rating</p>
      </div>
    </div>
  );
}
