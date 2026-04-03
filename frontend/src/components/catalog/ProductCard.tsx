import React from 'react'
import { Link } from 'react-router-dom'
import Badge from '../common/Badge'
import { useCart } from '../../context/useCart'
import type { ProductCardModel } from '../../features/catalog/types'
import './ProductCard.css'

const ProductCard: React.FC<ProductCardModel> = ({
  id,
  slug,
  name,
  price,
  image,
  category,
  stock,
  isNew,
  variantLabel,
}) => {
  const { addItem } = useCart()
  const isOutOfStock = stock === 0
  const isLowStock = stock > 0 && stock <= 5

  const handleAddToCart = (event: React.MouseEvent<HTMLButtonElement>) => {
    event.preventDefault()
    event.stopPropagation()
    addItem({ id, name, price, image, quantity: 1, variant: variantLabel })
  }

  return (
    <Link to={`/products/${slug}`} className={`product-card ${isOutOfStock ? 'product-card--out-of-stock' : ''}`} data-product-id={id}>
      <div className="product-card__image-wrapper">
        <img src={image} alt={name} className="product-card__image" />

        <div className="product-card__badges">
          {isOutOfStock ? <Badge text="Out of Stock" type="default" className="badge--oos" /> : null}
          {isLowStock ? <Badge text={`Only ${stock} Left!`} type="inventory" /> : null}
          {isNew && !isOutOfStock ? <Badge text="New Arrival" type="new" /> : null}
        </div>

        {!isOutOfStock ? (
          <div className="product-card__quick-add">
            <button className="btn btn-primary btn-sm" onClick={handleAddToCart}>
              Add to Cart
            </button>
          </div>
        ) : null}
      </div>

      <div className="product-card__info">
        <span className="product-card__category">{category}</span>
        <h3 className="product-card__title">{name}</h3>
        <p className="product-card__price">${price.toFixed(2)}</p>

        {isLowStock ? (
          <p className="product-card__stock-text product-card__stock-text--low">
            Hurry, only {stock} left in stock!
          </p>
        ) : null}

        {isOutOfStock ? (
          <p className="product-card__stock-text product-card__stock-text--none">
            Currently unavailable
          </p>
        ) : null}
      </div>
    </Link>
  )
}

export default ProductCard

