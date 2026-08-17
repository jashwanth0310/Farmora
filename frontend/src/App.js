import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { SessionProvider } from "./SessionContext";
import Navbar from "./components/Navbar";

import Home from "./pages/Home";
import FarmerLogin from "./pages/FarmerLogin";
import FarmerDashboard from "./pages/FarmerDashboard";
import AddProduce from "./pages/AddProduce";
import BuyerLogin from "./pages/BuyerLogin";
import BuyerDashboard from "./pages/BuyerDashboard";
import FindFarmers from "./pages/FindFarmers";
import PostRequirement from "./pages/PostRequirement";
import MatchedFarmers from "./pages/MatchedFarmers";
import OrderTracking from "./pages/OrderTracking";
import AdminDashboard from "./pages/AdminDashboard";

export default function App() {
  return (
    <SessionProvider>
      <BrowserRouter>
        <div className="app-shell">
          <Navbar />
          <div className="container">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/farmer/login" element={<FarmerLogin />} />
              <Route path="/farmer/dashboard" element={<FarmerDashboard />} />
              <Route path="/farmer/add-produce" element={<AddProduce />} />
              <Route path="/buyer/login" element={<BuyerLogin />} />
              <Route path="/buyer/dashboard" element={<BuyerDashboard />} />
              <Route path="/buyer/find-farmers" element={<FindFarmers />} />
              <Route path="/buyer/post-requirement" element={<PostRequirement />} />
              <Route path="/buyer/matches/:requirementId" element={<MatchedFarmers />} />
              <Route path="/orders/:orderId" element={<OrderTracking />} />
              <Route path="/admin" element={<AdminDashboard />} />
            </Routes>
          </div>
        </div>
      </BrowserRouter>
    </SessionProvider>
  );
}
