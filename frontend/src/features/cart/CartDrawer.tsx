import React from 'react';
import { useCart } from '../../context/CartContext';
import { useNavigate } from 'react-router-dom';
import './CartDrawer.css';

const CartDrawer: React.FC = () => {
  const { items, removeItem, updateQuantity, subtotal, isCartOpen, setIsCartOpen } = useCart();
  const navigate = useNavigate();
  const freeShippingThreshold = 120;
  const deliveryProgress = Math.min((subtotal / freeShippingThreshold) * 100, 100);
  const remainingForFreeShipping = freeShippingThreshold - subtotal;

  if (!isCartOpen) return null;

  const handleCheckout = () => {
    setIsCartOpen(false);
    navigate('/checkout');
  };

  return (
    <div className={`cart-overlay ${isCartOpen ? 'cart-overlay--open' : ''}`} onClick={() => setIsCartOpen(false)}>
      <div className="cart-drawer" onClick={(e) => e.stopPropagation()}>
        <div className="cart-header">
          <h2>Your Cart ({items.length})</h2>
          <button className="cart-close" onClick={() => setIsCartOpen(false)}>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M18 6L6 18M6 6l12 12"/></svg>
          </button>
        </div>

        {/* Free Shipping Progress */}
        <div className="cart-promo">
          <p className="cart-promo__text">
            {remainingForFreeShipping > 0 
              ? `You're $${remainingForFreeShipping.toFixed(2)} away from FREE shipping!` 
              : "Congrats! You've unlocked FREE shipping!"}
          </p>
          <div className="cart-promo__bar">
            <div className="cart-promo__fill" style={{ width: `${deliveryProgress}%` }}></div>
          </div>
        </div>

        <div className="cart-items">
          {items.length === 0 ? (
            <div className="cart-empty">
              <p>Your cart is empty.</p>
              <button className="btn btn-primary" onClick={() => setIsCartOpen(false)}>Start Shopping</button>
            </div>
          ) : (
            items.map((item) => (
              <div key={item.id} className="cart-item">
                <img src={item.image} alt={item.name} className="cart-item__image" />
                <div className="cart-item__details">
                  <h4 className="cart-item__name">{item.name}</h4>
                  <p className="cart-item__variant">{item.variant || 'Standard Width'}</p>
                  <div className="cart-item__actions">
                    <div className="cart-item__quantity">
                      <button onClick={() => updateQuantity(item.id, -1)}>-</button>
                      <span>{item.quantity}</span>
                      <button onClick={() => updateQuantity(item.id, 1)}>+</button>
                    </div>
                    <p className="cart-item__price">${(item.price * item.quantity).toFixed(2)}</p>
                  </div>
                  <button className="cart-item__remove" onClick={() => removeItem(item.id)}>Remove</button>
                </div>
              </div>
            ))
          )}
        </div>

        {items.length > 0 && (
          <div className="cart-footer">
            <div className="cart-summary">
              <div className="cart-summary__row">
                <span>Subtotal</span>
                <span>${subtotal.toFixed(2)}</span>
              </div>
              <div className="cart-summary__row">
                <span>Shipping</span>
                <span>{remainingForFreeShipping > 0 ? "Calculated at checkout" : "FREE"}</span>
              </div>
            </div>
            <button className="btn btn-primary btn-block checkout-btn" onClick={handleCheckout}>
              Checkout — ${subtotal.toFixed(2)}
            </button>
          </div>
        )}
      </div>
    </div>
  );
};

export default CartDrawer;
