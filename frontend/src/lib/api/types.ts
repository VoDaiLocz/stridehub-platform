export interface ApiErrorResponse {
  status: number
  code: string
  message: string
  path?: string
  correlationId?: string
  timestamp?: string
  errors?: Record<string, string>
}

export interface UserProfile {
  id: string
  email: string
  name: string
  firstName?: string | null
  lastName?: string | null
  emailVerified: boolean
  status: string
  roles: string[]
}

export interface AuthResponse {
  accessToken: string
  refreshToken: string
  user: UserProfile
}

export interface CategorySummary {
  id: string
  name: string
  slug: string
}

export interface BrandSummary {
  id: string
  name: string
  slug: string
}

export interface ProductVariant {
  id: string
  sku: string
  size: string
  color: string
  price: number
  compareAtPrice?: number | null
}

export interface CatalogProductListItem {
  id: string
  slug: string
  name: string
  shortDescription?: string | null
  currencyCode: string
  category: CategorySummary
  brand: BrandSummary
  primaryVariant: ProductVariant
  primaryImageUrl?: string | null
}

export interface CatalogProductListResponse {
  items: CatalogProductListItem[]
}

export interface CatalogProductImage {
  id: string
  variantId?: string | null
  imageUrl: string
  altText?: string | null
  sortOrder: number
}

export interface CatalogProductDetail {
  id: string
  slug: string
  name: string
  shortDescription?: string | null
  longDescription?: string | null
  currencyCode: string
  category: CategorySummary
  brand: BrandSummary
  variants: ProductVariant[]
  images: CatalogProductImage[]
}

export interface OrderSummary {
  id: string
  orderNumber: string
  status: string
  totalAmount: number
  currency: string
}

export interface OrderItem {
  id: string
  productId: string
  variantId: string
  sku: string
  productName: string
  variantName: string
  quantity: number
  unitPriceAmount: number
  totalPriceAmount: number
}

export interface OrderDetail {
  id: string
  orderNumber: string
  status: string
  totalAmount: number
  currency: string
  items: OrderItem[]
}
