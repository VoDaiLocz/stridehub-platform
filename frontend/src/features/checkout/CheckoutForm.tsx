import React from 'react';
import './CheckoutForm.css';

const CheckoutForm: React.FC = () => {
  return (
    <div className="checkout-form">
      <nav className="checkout-breadcrumb">
        <span className="breadcrumb-item-link">Cart</span>
        <span className="breadcrumb-separator">/</span>
        <span className="breadcrumb-item active">Information</span>
        <span className="breadcrumb-separator">/</span>
        <span className="breadcrumb-item">Shipping</span>
        <span className="breadcrumb-separator">/</span>
        <span className="breadcrumb-item">Payment</span>
      </nav>

      <section className="form-section">
        <h2 className="section-title">Contact</h2>
        <div className="form-group">
          <input type="email" placeholder="Email" className="form-input" />
        </div>
      </section>

      <section className="form-section">
        <h2 className="section-title">Shipping Address</h2>
        <div className="form-grid">
          <input type="text" placeholder="First Name" className="form-input" />
          <input type="text" placeholder="Last Name" className="form-input" />
        </div>
        <div className="form-group">
          <input type="text" placeholder="Address" className="form-input" />
        </div>
        <div className="form-group">
          <input type="text" placeholder="Apartment, suite, etc. (optional)" className="form-input" />
        </div>
        <div className="form-grid city-grid">
          <input type="text" placeholder="City" className="form-input" />
          <input type="text" placeholder="Postal Code" className="form-input" />
        </div>
      </section>

      <section className="form-section">
        <h2 className="section-title">Payment</h2>
        <p className="section-subtitle">All transactions are secure and encrypted.</p>
        <div className="payment-placeholder">
          <div className="credit-card-shell">
            <div className="form-group">
              <input type="text" placeholder="Card Number" className="form-input" disabled />
            </div>
            <div className="form-grid">
              <input type="text" placeholder="Expiry (MM/YY)" className="form-input" disabled />
              <input type="text" placeholder="CVV" className="form-input" disabled />
            </div>
          </div>
          <p className="payment-note">Secure payment gateway integration coming in Task 1.6</p>
        </div>
      </section>

      <button className="primary-button checkout-button" disabled>
        Complete Order
      </button>
    </div>
  );
};

export default CheckoutForm;
