import React from 'react'
import ProductCard from './ProductCard'
import type { ProductCardModel } from '../../features/catalog/types'
import './ProductGrid.css'

interface ProductGridProps {
  title?: string
  subtitle?: string
  products: ProductCardModel[]
}

const ProductGrid: React.FC<ProductGridProps> = ({ title, subtitle, products }) => {
  return (
    <section className="product-grid-section container">
      <div className="product-grid-header">
        {subtitle ? <span className="section-subtitle">{subtitle}</span> : null}
        {title ? <h2 className="section-title">{title}</h2> : null}
      </div>

      <div className="product-grid">
        {products.map((product) => (
          <ProductCard key={product.id} {...product} />
        ))}
      </div>
    </section>
  )
}

export default ProductGrid
