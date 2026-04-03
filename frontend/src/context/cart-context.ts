import { createContext } from 'react'

export interface CartItem {
  id: string
  name: string
  price: number
  image: string
  quantity: number
  variant?: string
}

export interface CartContextType {
  items: CartItem[]
  addItem: (item: CartItem) => void
  removeItem: (id: string) => void
  updateQuantity: (id: string, delta: number) => void
  clearCart: () => void
  isCartOpen: boolean
  setIsCartOpen: (open: boolean) => void
  totalItems: number
  subtotal: number
  shipping: number
  tax: number
  total: number
}

export const CartContext = createContext<CartContextType | undefined>(undefined)
