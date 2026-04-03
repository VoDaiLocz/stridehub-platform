import React, { useEffect, useState } from 'react'
import { Clock } from 'lucide-react'
import { useCart } from '../../context/useCart'
import './CheckoutSummary.css'

const CheckoutSummary: React.FC = () => {
  const { items, subtotal, shipping, tax, total } = useCart()
  const [timeLeft, setTimeLeft] = useState(600)

  useEffect(() => {
    if (timeLeft <= 0) return
    const timer = setInterval(() => {
      setTimeLeft(prev => prev - 1);
    }, 1000);
    return () => clearInterval(timer);
  }, [timeLeft]);

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  return (
    <div className="checkout-summary">
      <div className="urgency-timer">
        <Clock size={18} className="timer-icon" />
        <span>Order reserved for <strong>{formatTime(timeLeft)}</strong></span>
      </div>

      <div className="summary-items">
        {items.map(item => (
          <div key={item.id} className="summary-item">
            <div className="item-image-container">
              <img src={item.image} alt={item.name} />
              <span className="item-qty-badge">{item.quantity}</span>
            </div>
            <div className="item-details">
              <p className="item-name">{item.name}</p>
              <p className="item-variant">{item.variant || 'Standard Width'}</p>
            </div>
            <p className="item-price">${(item.price * item.quantity).toFixed(2)}</p>
          </div>
        ))}
      </div>

      <div className="summary-calculations">
        <div className="calc-row">
          <span>Subtotal</span>
          <span>${subtotal.toFixed(2)}</span>
        </div>
        <div className="calc-row">
          <span>Shipping</span>
          <span>{shipping === 0 ? 'FREE' : `$${shipping.toFixed(2)}`}</span>
        </div>
        <div className="calc-row">
          <span>Estimated Taxes</span>
          <span>${tax.toFixed(2)}</span>
        </div>
        <div className="calc-row total-row">
          <span>Total</span>
          <span className="total-price">USD ${total.toFixed(2)}</span>
        </div>
      </div>
    </div>
  )
}

export default CheckoutSummary
