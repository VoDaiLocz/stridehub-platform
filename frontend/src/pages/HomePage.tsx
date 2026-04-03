import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import FeatureSection from '../components/home/FeatureSection'
import ProductGrid from '../components/catalog/ProductGrid'
import { getProducts } from '../lib/api/catalog'
import { getApiErrorMessage } from '../lib/api/errors'
import type { CatalogProductListItem } from '../lib/api/types'
import type { ProductCardModel } from '../features/catalog/types'
import './HomePage.css'

const FALLBACK_PRODUCTS: ProductCardModel[] = [
  {
    id: 'fallback-1',
    slug: 'everyday-move',
    name: 'Everyday Move',
    price: 135,
    category: "Men's Sneakers",
    image: '/images/vessi.com/shoe-1.jpg',
    stock: 12,
    isNew: true,
    variantLabel: 'Slate / 42',
  },
  {
    id: 'fallback-2',
    slug: 'weekend-sneaker',
    name: 'Weekend Sneaker',
    price: 110,
    category: 'Unisex',
    image: '/images/vessi.com/shoe-2.jpg',
    stock: 3,
    variantLabel: 'Ivory / 39',
  },
  {
    id: 'fallback-3',
    slug: 'stormside-boot',
    name: 'Stormside Boot',
    price: 165,
    category: 'All-Weather',
    image: '/images/vessi.com/shoe-3.jpg',
    stock: 0,
    variantLabel: 'Onyx / 43',
  },
  {
    id: 'fallback-4',
    slug: 'sunday-slipper',
    name: 'Sunday Slipper',
    price: 85,
    category: 'Home & Travel',
    image: '/images/vessi.com/shoe-4.jpg',
    stock: 25,
    isNew: true,
    variantLabel: 'Sand / 38',
  },
]

function mapProduct(product: CatalogProductListItem, index: number): ProductCardModel {
  const fallbackImage = FALLBACK_PRODUCTS[index % FALLBACK_PRODUCTS.length]?.image ?? '/images/hero-banner.webp'
  return {
    id: product.id,
    slug: product.slug,
    name: product.name,
    price: product.primaryVariant.price,
    category: `${product.brand.name} / ${product.category.name}`,
    image: product.primaryImageUrl || fallbackImage,
    stock: 10 - (index % 3) * 2,
    variantId: product.primaryVariant.id,
    variantLabel: `${product.primaryVariant.color} / ${product.primaryVariant.size}`,
    shortDescription: product.shortDescription ?? undefined,
  }
}

const HomePage: React.FC = () => {
  const [featuredProducts, setFeaturedProducts] = useState<ProductCardModel[]>(FALLBACK_PRODUCTS)
  const [catalogMessage, setCatalogMessage] = useState('Catalog API warming up. Showing curated highlights in the meantime.')

  useEffect(() => {
    let cancelled = false

    async function loadFeaturedProducts() {
      try {
        const response = await getProducts()

        if (cancelled) {
          return
        }

        if (response.items.length > 0) {
          setFeaturedProducts(response.items.slice(0, 4).map(mapProduct))
          setCatalogMessage('Powered by the live Phase 1 catalog endpoints.')
        } else {
          setCatalogMessage('Catalog is connected but currently empty, so curated preview products are shown.')
        }
      } catch (error) {
        if (!cancelled) {
          const detail = getApiErrorMessage(error, 'Fallback products are shown.')
          setCatalogMessage(`Catalog API unavailable. ${detail}`)
        }
      }
    }

    void loadFeaturedProducts()

    return () => {
      cancelled = true
    }
  }, [])

  return (
    <div className="homepage">
      <section className="hero">
        <div className="hero-background">
          <img src="/images/vessi.com/hero.webp" alt="Person walking in rain wearing StrideHub shoes" />
          <div className="hero-overlay"></div>
        </div>

        <div className="container hero-content">
          <div className="hero-text">
            <span className="hero-badge">The famous cities collection</span>
            <h1>
              Comfort for everywhere. <br /> Waterproof for anywhere.
            </h1>
            <p>Rain-proof movement for everyday life, tuned for the buyer flow you build in Phase 1.</p>
            <div className="hero-actions">
              <Link to="/collections/women" className="btn btn-primary">
                Shop Women
              </Link>
              <Link to="/collections/men" className="btn btn-primary">
                Shop Men
              </Link>
            </div>
          </div>
        </div>
      </section>

      <div className="container live-catalog-note">{catalogMessage}</div>

      <ProductGrid subtitle="Customer Favorites" title="Our Best Sellers" products={featuredProducts} />

      <FeatureSection
        subtitle="Cloudfeel Performance"
        title="Pockets of air make every step feel like a soft landing"
        description="Our premium knit upper flexes with you, keeping the silhouette clean while staying light enough for everyday mileage."
        image="/images/feature-1.jpg"
        linkText="Learn more"
      />

      <FeatureSection
        subtitle="Dyma-tex Waterproof"
        title="Stay bone-dry through puddles and spills"
        description="A breathable waterproof membrane is built into the shell so the product story stays premium without giving up weather protection."
        image="/images/feature-2.jpg"
        reversed
        linkText="Learn more"
      />

      <FeatureSection
        subtitle="All-Day Comfort"
        title="The only shoes you'll need for any weather"
        description="From wet commutes to dry weekends, StrideHub keeps the storefront polished and the footwear story consistent."
        image="/images/feature-3.jpg"
        linkText="Shop the collection"
      />
    </div>
  )
}

export default HomePage

