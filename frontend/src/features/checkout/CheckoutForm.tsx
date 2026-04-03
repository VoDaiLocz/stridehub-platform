import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useCart } from '../../context/useCart'
import StripeCardMock from './StripeCardMock'
import './CheckoutForm.css'

interface CheckoutOrderSnapshotItem {
  id: string
  name: string
  image: string
  price: number
  quantity: number
  variant?: string
}

interface CheckoutOrderSnapshot {
  orderNumber: string
  items: CheckoutOrderSnapshotItem[]
  subtotal: number
  tax: number
  total: number
}

const CheckoutForm: React.FC = () => {
  const [isSubmitting, setIsSubmitting] = useState(false)
  const navigate = useNavigate()
  const { items, clearCart, subtotal, tax, total } = useCart()

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setIsSubmitting(true)

    const orderSnapshot: CheckoutOrderSnapshot = {
      orderNumber: `SH-DEMO-${Date.now().toString().slice(-6)}`,
      items: [...items],
      subtotal,
      tax,
      total,
    }

    await new Promise((resolve) => setTimeout(resolve, 1200))

    setIsSubmitting(false)
    clearCart()
    navigate('/checkout/success', { state: { order: orderSnapshot } })
  }

  return (
    <form className="checkout-form" onSubmit={handleSubmit}>
      <nav className="checkout-breadcrumb">
        <span className="breadcrumb-item-link">Cart</span>
        <span className="breadcrumb-separator">/</span>
        <span className="breadcrumb-item active">Information</span>
        <span className="breadcrumb-separator">/</span>
        <span className="breadcrumb-item">Shipping</span>
        <span className="breadcrumb-separator">/</span>
        <span className="breadcrumb-item">Payment</span>
      </nav>

      <section className="form-section">
        <h2 className="section-title">Contact</h2>
        <div className="form-group">
          <input type="email" placeholder="Email" className="form-input" required disabled={isSubmitting} />
        </div>
      </section>

      <section className="form-section">
        <h2 className="section-title">Shipping Address</h2>
        <div className="form-grid">
          <input type="text" placeholder="First Name" className="form-input" required disabled={isSubmitting} />
          <input type="text" placeholder="Last Name" className="form-input" required disabled={isSubmitting} />
        </div>
        <div className="form-group">
          <input type="text" placeholder="Address" className="form-input" required disabled={isSubmitting} />
        </div>
        <div className="form-group">
          <input type="text" placeholder="Apartment, suite, etc. (optional)" className="form-input" disabled={isSubmitting} />
        </div>
        <div className="form-grid city-grid">
          <input type="text" placeholder="City" className="form-input" required disabled={isSubmitting} />
          <input type="text" placeholder="Postal Code" className="form-input" required disabled={isSubmitting} />
        </div>
      </section>

      <section className="form-section">
        <h2 className="section-title">Payment</h2>
        <p className="section-subtitle">All transactions are secure and encrypted.</p>
        <div className="payment-placeholder">
          <StripeCardMock disabled={isSubmitting} />
          <p className="payment-note">Secure payment gateway integration simulated with Stripe Mock</p>
        </div>
      </section>

      <button type="submit" className="primary-button checkout-button" disabled={isSubmitting}>
        {isSubmitting ? <div className="spinner"></div> : 'Complete Order'}
      </button>
    </form>
  )
}

export default CheckoutForm
