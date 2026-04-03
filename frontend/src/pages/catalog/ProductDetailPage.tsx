import { useEffect, useMemo, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { useCart } from '../../context/useCart'
import { getApiErrorMessage } from '../../lib/api/errors'
import { getProductBySlug } from '../../lib/api/catalog'
import type { CatalogProductDetail } from '../../lib/api/types'
import './ProductDetailPage.css'

const FALLBACK_IMAGES = [
  '/images/vessi.com/shoe-1.jpg',
  '/images/vessi.com/shoe-2.jpg',
  '/images/vessi.com/shoe-3.jpg',
  '/images/vessi.com/shoe-4.jpg',
]

const ProductDetailPage: React.FC = () => {
  const { slug = '' } = useParams()
  const { addItem } = useCart()
  const [product, setProduct] = useState<CatalogProductDetail | null>(null)
  const [selectedVariantId, setSelectedVariantId] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let cancelled = false

    async function loadProduct() {
      setLoading(true)
      setError('')

      try {
        const response = await getProductBySlug(slug)
        if (cancelled) {
          return
        }

        setProduct(response)
        setSelectedVariantId(response.variants[0]?.id ?? '')
      } catch (error) {
        if (!cancelled) {
          setError(getApiErrorMessage(error, 'Unable to load product details.'))
          setProduct(null)
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    if (slug) {
      void loadProduct()
    }

    return () => {
      cancelled = true
    }
  }, [slug])

  const selectedVariant = useMemo(
    () => product?.variants.find((variant) => variant.id === selectedVariantId) ?? product?.variants[0] ?? null,
    [product, selectedVariantId],
  )

  const primaryImage = product?.images[0]?.imageUrl ?? FALLBACK_IMAGES[0]
  const gallery = product?.images.length
    ? product.images
    : FALLBACK_IMAGES.map((imageUrl, index) => ({
        id: `fallback-${index}`,
        imageUrl,
        altText: product?.name ?? 'StrideHub product image',
        sortOrder: index,
        variantId: null,
      }))

  const handleAddToCart = () => {
    if (!product || !selectedVariant) {
      return
    }

    addItem({
      id: selectedVariant.id,
      name: product.name,
      price: selectedVariant.price,
      image: primaryImage,
      quantity: 1,
      variant: `${selectedVariant.color} / ${selectedVariant.size}`,
    })
  }

  if (loading) {
    return <div className="page-shell page-shell--center">Loading product details...</div>
  }

  if (error || !product) {
    return <div className="page-shell page-shell--center page-shell--error">{error || 'Product not found.'}</div>
  }

  return (
    <div className="page-shell product-detail-page">
      <div className="container product-detail-layout">
        <div className="product-gallery">
          {gallery.map((image) => (
            <img key={image.id} src={image.imageUrl} alt={image.altText ?? product.name} />
          ))}
        </div>

        <div className="product-detail-content">
          <span className="eyebrow">{product.brand.name}</span>
          <h1>{product.name}</h1>
          <p className="product-detail-subtitle">{product.shortDescription}</p>
          <p className="product-detail-price">
            {selectedVariant ? `${product.currencyCode} ${selectedVariant.price.toFixed(2)}` : product.currencyCode}
          </p>

          <div className="variant-group">
            <span>Variant</span>
            <div className="variant-options">
              {product.variants.map((variant) => {
                const isActive = variant.id === selectedVariant?.id
                return (
                  <button
                    key={variant.id}
                    type="button"
                    className={`variant-pill ${isActive ? 'variant-pill--active' : ''}`}
                    onClick={() => setSelectedVariantId(variant.id)}
                  >
                    {variant.color} / {variant.size}
                  </button>
                )
              })}
            </div>
          </div>

          <div className="detail-actions">
            <button type="button" className="btn btn-primary" onClick={handleAddToCart}>
              Add to cart
            </button>
            <Link to={`/collections/${product.category.slug}`} className="text-link">
              Explore {product.category.name}
            </Link>
          </div>

          <div className="detail-meta">
            <div>
              <span className="meta-label">Category</span>
              <strong>{product.category.name}</strong>
            </div>
            <div>
              <span className="meta-label">SKU</span>
              <strong>{selectedVariant?.sku ?? 'N/A'}</strong>
            </div>
          </div>

          <section className="detail-story">
            <h2>Why it belongs in the rotation</h2>
            <p>{product.longDescription ?? 'Phase 1 detail pages render immutable catalog copy from the backend.'}</p>
          </section>
        </div>
      </div>
    </div>
  )
}

export default ProductDetailPage
