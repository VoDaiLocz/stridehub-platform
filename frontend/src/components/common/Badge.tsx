import React from 'react';
import './Badge.css';

interface BadgeProps {
  text: string;
  type?: 'default' | 'inventory' | 'sale' | 'new';
  className?: string;
}

const Badge: React.FC<BadgeProps> = ({ text, type = 'default', className = '' }) => {
  return (
    <span className={`badge badge--${type} ${className}`}>
      {text}
    </span>
  );
};

export default Badge;
