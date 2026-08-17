import React from "react";

const STEPS = [
  "ORDER_CONFIRMED",
  "FARMERS_CONFIRMED",
  "PRODUCE_COLLECTED",
  "IN_TRANSIT",
  "DELIVERED",
  "BUYER_CONFIRMED",
  "PAYMENT_RELEASED",
];

const LABELS = {
  ORDER_CONFIRMED: "Order Confirmed",
  FARMERS_CONFIRMED: "Farmers Confirmed",
  PRODUCE_COLLECTED: "Produce Collected",
  IN_TRANSIT: "In Transit",
  DELIVERED: "Delivered",
  BUYER_CONFIRMED: "Buyer Confirmation",
  PAYMENT_RELEASED: "Payment Released",
};

export default function StatusTimeline({ status }) {
  const currentIndex = STEPS.indexOf(status);
  return (
    <ul className="timeline">
      {STEPS.map((step, idx) => (
        <li key={step} className={idx <= currentIndex ? "done" : ""}>
          {LABELS[step]}
        </li>
      ))}
    </ul>
  );
}
