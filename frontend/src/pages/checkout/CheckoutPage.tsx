import React from 'react';
import CheckoutSummary from '../../features/checkout/CheckoutSummary';
import CheckoutForm from '../../features/checkout/CheckoutForm';
import './CheckoutPage.css';

const CheckoutPage: React.FC = () => {
  return (
    <div className="checkout-page">
      <div className="container checkout-container">
        {/* Main Flow (Left Column) */}
        <div className="checkout-main">
          <CheckoutForm />
        </div>

        {/* Sidebar Summary (Right Column) */}
        <aside className="checkout-sidebar">
          <div className="checkout-summary-sticky">
            <CheckoutSummary />
          </div>
        </aside>
      </div>
    </div>
  );
};


export default CheckoutPage;

