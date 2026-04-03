import { useEffect, useState } from 'react'
import { Link, useParams, useSearchParams } from 'react-router-dom'
import ProductGrid from '../../components/catalog/ProductGrid'
import { getBrands, getProducts } from '../../lib/api/catalog'
import { getApiErrorMessage } from '../../lib/api/errors'
import type { BrandSummary, CatalogProductListItem } from '../../lib/api/types'
import type { ProductCardModel } from '../../features/catalog/types'
import './CollectionPage.css'

const FALLBACK_IMAGES = [
  '/images/vessi.com/shoe-1.jpg',
  '/images/vessi.com/shoe-2.jpg',
  '/images/vessi.com/shoe-3.jpg',
  '/images/vessi.com/shoe-4.jpg',
]

function mapCatalogProduct(product: CatalogProductListItem, index: number): ProductCardModel {
  return {
    id: product.id,
    slug: product.slug,
    name: product.name,
    price: product.primaryVariant.price,
    category: `${product.brand.name} / ${product.category.name}`,
    image: product.primaryImageUrl || FALLBACK_IMAGES[index % FALLBACK_IMAGES.length],
    stock: 12 - (index % 4) * 3,
    variantId: product.primaryVariant.id,
    variantLabel: `${product.primaryVariant.color} / ${product.primaryVariant.size}`,
    shortDescription: product.shortDescription ?? undefined,
  }
}

const CollectionPage: React.FC = () => {
  const { collectionSlug = 'all' } = useParams()
  const [searchParams, setSearchParams] = useSearchParams()
  const [products, setProducts] = useState<ProductCardModel[]>([])
  const [brands, setBrands] = useState<BrandSummary[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const selectedBrand = searchParams.get('brand') ?? ''

  useEffect(() => {
    let cancelled = false

    async function loadCollection() {
      setLoading(true)
      setError('')

      try {
        const [brandOptions, productResponse] = await Promise.all([
          getBrands(),
          getProducts({
            category: collectionSlug === 'all' ? undefined : collectionSlug,
            brand: selectedBrand || undefined,
          }),
        ])

        if (cancelled) {
          return
        }

        setBrands(brandOptions)
        setProducts(productResponse.items.map(mapCatalogProduct))
      } catch (error) {
        if (!cancelled) {
          setError(getApiErrorMessage(error, 'Unable to load the collection right now.'))
          setProducts([])
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    void loadCollection()

    return () => {
      cancelled = true
    }
  }, [collectionSlug, selectedBrand])

  const handleBrandChange = (brandSlug: string) => {
    const nextParams = new URLSearchParams(searchParams)

    if (brandSlug) {
      nextParams.set('brand', brandSlug)
    } else {
      nextParams.delete('brand')
    }

    setSearchParams(nextParams)
  }

  return (
    <div className="page-shell collection-page">
      <section className="collection-hero container">
        <div>
          <span className="eyebrow">Catalog</span>
          <h1>{collectionSlug === 'all' ? 'All collections' : `${collectionSlug} collection`}</h1>
          <p>
            Browse the live Phase 1 catalog by category and brand. Listings are powered by the Spring
            catalog APIs and fall back to empty states cleanly when no products match.
          </p>
        </div>
        <div className="collection-actions">
          <Link to="/" className="text-link">
            Back to home
          </Link>
        </div>
      </section>

      <section className="collection-controls container">
        <label className="filter-field">
          <span>Brand</span>
          <select value={selectedBrand} onChange={(event) => handleBrandChange(event.target.value)}>
            <option value="">All brands</option>
            {brands.map((brand) => (
              <option key={brand.id} value={brand.slug}>
                {brand.name}
              </option>
            ))}
          </select>
        </label>
      </section>

      {loading ? <div className="page-shell page-shell--center">Loading collection...</div> : null}
      {!loading && error ? <div className="page-shell page-shell--center page-shell--error">{error}</div> : null}
      {!loading && !error && products.length === 0 ? (
        <div className="page-shell page-shell--center">
          No products matched this collection yet. Seed catalog data or switch filters.
        </div>
      ) : null}
      {!loading && !error && products.length > 0 ? (
        <ProductGrid subtitle="Live catalog results" title="Shop the collection" products={products} />
      ) : null}
    </div>
  )
}

export default CollectionPage
