import { apiClient } from './client'
import type {
  BrandSummary,
  CatalogProductDetail,
  CatalogProductListResponse,
  CategorySummary,
} from './types'

export interface CatalogQuery {
  category?: string
  brand?: string
  size?: string
  color?: string
}

export async function getCategories(): Promise<CategorySummary[]> {
  const response = await apiClient.get<CategorySummary[]>('/api/v1/catalog/categories')
  return response.data
}

export async function getBrands(): Promise<BrandSummary[]> {
  const response = await apiClient.get<BrandSummary[]>('/api/v1/catalog/brands')
  return response.data
}

export async function getProducts(query: CatalogQuery = {}): Promise<CatalogProductListResponse> {
  const response = await apiClient.get<CatalogProductListResponse>('/api/v1/catalog/products', {
    params: query,
  })
  return response.data
}

export async function getProductBySlug(slug: string): Promise<CatalogProductDetail> {
  const response = await apiClient.get<CatalogProductDetail>(`/api/v1/catalog/products/${slug}`)
  return response.data
}
