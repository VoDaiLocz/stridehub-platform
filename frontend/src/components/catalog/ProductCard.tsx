import React from 'react';
import Badge from '../common/Badge';
import './ProductCard.css';

interface ProductCardProps {
  id: string;
  name: string;
  price: number;
  image: string;
  category: string;
  stock: number;
  isNew?: boolean;
}

const ProductCard: React.FC<ProductCardProps> = ({ 
  id, 
  name, 
  price, 
  image, 
  category, 
  stock, 
  isNew 
}) => {
  const isOutOfStock = stock === 0;
  const isLowStock = stock > 0 && stock <= 5;

  return (
    <div className={`product-card ${isOutOfStock ? 'product-card--out-of-stock' : ''}`} data-product-id={id}>
      <div className="product-card__image-wrapper">
        <img src={image} alt={name} className="product-card__image" />
        
        {/* Inventory Badges */}
        <div className="product-card__badges">
          {isOutOfStock && <Badge text="Out of Stock" type="default" className="badge--oos" />}
          {isLowStock && <Badge text={`Only ${stock} Left!`} type="inventory" />}
          {isNew && !isOutOfStock && <Badge text="New Arrival" type="new" />}
        </div>

        {/* Quick Add (Visible on Hover in Vessi) */}
        {!isOutOfStock && (
          <div className="product-card__quick-add">
            <button className="btn btn-primary btn-sm">Add to Cart</button>
          </div>
        )}
      </div>

      <div className="product-card__info">
        <span className="product-card__category">{category}</span>
        <h3 className="product-card__title">{name}</h3>
        <p className="product-card__price">${price.toFixed(2)}</p>
        
        {isLowStock && (
          <p className="product-card__stock-text product-card__stock-text--low">
            Hurry, only {stock} left in stock!
          </p>
        )}
        
        {isOutOfStock && (
          <p className="product-card__stock-text product-card__stock-text--none">
            Currently unavailable
          </p>
        )}
      </div>
    </div>
  );
};

export default ProductCard;
