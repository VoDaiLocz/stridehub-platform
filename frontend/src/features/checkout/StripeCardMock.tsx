import React, { useState } from 'react';
import { CreditCard } from 'lucide-react';
import './StripeCardMock.css';

interface StripeCardMockProps {
  disabled?: boolean;
}

const StripeCardMock: React.FC<StripeCardMockProps> = ({ disabled }) => {
  const [isFocused, setIsFocused] = useState(false);

  return (
    <div className={`stripe-card-container ${isFocused ? 'focused' : ''} ${disabled ? 'disabled' : ''}`}>
      <div className="stripe-card-icon">
        <CreditCard size={20} />
      </div>
      <div className="stripe-card-inputs">
        <input 
          type="text" 
          placeholder="Card number" 
          className="stripe-input card-num"
          onFocus={() => setIsFocused(true)}
          onBlur={() => setIsFocused(false)}
          disabled={disabled}
        />
        <div className="stripe-divider"></div>
        <input 
          type="text" 
          placeholder="MM / YY" 
          className="stripe-input card-expiry"
          onFocus={() => setIsFocused(true)}
          onBlur={() => setIsFocused(false)}
          disabled={disabled}
        />
        <div className="stripe-divider"></div>
        <input 
          type="text" 
          placeholder="CVC" 
          className="stripe-input card-cvc"
          onFocus={() => setIsFocused(true)}
          onBlur={() => setIsFocused(false)}
          disabled={disabled}
        />
      </div>
    </div>
  );
};

export default StripeCardMock;
