export interface ProductCardModel {
  id: string
  slug: string
  name: string
  price: number
  category: string
  image: string
  stock: number
  isNew?: boolean
  variantId?: string
  variantLabel?: string
  shortDescription?: string
}
