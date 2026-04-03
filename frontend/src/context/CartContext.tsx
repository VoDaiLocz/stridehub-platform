import React, { useEffect, useState } from 'react'
import { CartContext } from './cart-context'
import type { CartItem } from './cart-context'

const FREE_SHIPPING_THRESHOLD = 120
const STANDARD_SHIPPING = 15
const TAX_RATE = 0.08

export const CartProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [items, setItems] = useState<CartItem[]>(() => {
    const saved = window.localStorage.getItem('stridehub_cart')
    return saved ? (JSON.parse(saved) as CartItem[]) : []
  })
  const [isCartOpen, setIsCartOpen] = useState(false)

  useEffect(() => {
    window.localStorage.setItem('stridehub_cart', JSON.stringify(items))
  }, [items])

  const addItem = (newItem: CartItem) => {
    setItems((previousItems) => {
      const existingItem = previousItems.find((item) => item.id === newItem.id)
      if (existingItem) {
        return previousItems.map((item) =>
          item.id === newItem.id ? { ...item, quantity: item.quantity + newItem.quantity } : item,
        )
      }
      return [...previousItems, newItem]
    })
    setIsCartOpen(true)
  }

  const removeItem = (id: string) => {
    setItems((previousItems) => previousItems.filter((item) => item.id !== id))
  }

  const updateQuantity = (id: string, delta: number) => {
    setItems((previousItems) =>
      previousItems
        .map((item) =>
          item.id === id ? { ...item, quantity: Math.max(0, item.quantity + delta) } : item,
        )
        .filter((item) => item.quantity > 0),
    )
  }

  const clearCart = () => setItems([])

  const totalItems = items.reduce((sum, item) => sum + item.quantity, 0)
  const subtotal = items.reduce((sum, item) => sum + item.price * item.quantity, 0)
  const shipping = subtotal === 0 || subtotal >= FREE_SHIPPING_THRESHOLD ? 0 : STANDARD_SHIPPING
  const tax = subtotal * TAX_RATE
  const total = subtotal + shipping + tax

  return (
    <CartContext.Provider
      value={{
        items,
        addItem,
        removeItem,
        updateQuantity,
        clearCart,
        isCartOpen,
        setIsCartOpen,
        totalItems,
        subtotal,
        shipping,
        tax,
        total,
      }}
    >
      {children}
    </CartContext.Provider>
  )
}
