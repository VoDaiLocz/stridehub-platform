import { useLocation, useNavigate } from 'react-router-dom'
import { ArrowRight, CheckCircle, Package } from 'lucide-react'
import './CheckoutSuccessPage.css'

interface CheckoutSuccessOrderItem {
  id: string
  name: string
  image: string
  price: number
  quantity: number
}

interface CheckoutSuccessOrder {
  orderNumber: string
  items: CheckoutSuccessOrderItem[]
  subtotal: number
  tax: number
  total: number
}

const CheckoutSuccessPage: React.FC = () => {
  const navigate = useNavigate()
  const location = useLocation()
  const orderData = (location.state?.order as CheckoutSuccessOrder | undefined) ?? null
  const orderNumber = orderData?.orderNumber ?? 'SH-DEMO-000001'

  return (
    <div className="success-page">
      <div className="container success-container">
        <div className="success-card">
          <div className="success-header">
            <div className="success-icon-wrapper">
              <CheckCircle size={64} className="success-icon" />
            </div>
            <h1 className="success-title">Thank you for your order!</h1>
            <p className="success-message">
              Confirmation email sent to your inbox. Order <strong>{orderNumber}</strong>.
            </p>
          </div>

          {orderData ? (
            <div className="order-recap-section">
              <div className="recap-header">
                <Package size={20} />
                <span>Order Summary</span>
              </div>
              <div className="recap-items">
                {orderData.items.map((item) => (
                  <div key={item.id} className="recap-item">
                    <div className="item-thumb-wrapper">
                      <img src={item.image} alt={item.name} className="item-thumb" />
                      <span className="item-qty">{item.quantity}</span>
                    </div>
                    <div className="item-info">
                      <p className="item-name">{item.name}</p>
                      <p className="item-meta">Standard Shipping</p>
                    </div>
                    <span className="item-price">${(item.price * item.quantity).toFixed(2)}</span>
                  </div>
                ))}
              </div>
              <div className="recap-totals">
                <div className="total-row">
                  <span>Subtotal</span>
                  <span>${orderData.subtotal.toFixed(2)}</span>
                </div>
                <div className="total-row">
                  <span>Estimated Taxes</span>
                  <span>${orderData.tax.toFixed(2)}</span>
                </div>
                <div className="total-row grand-total">
                  <span>Total</span>
                  <span>USD ${(orderData.total).toFixed(2)}</span>
                </div>
              </div>
            </div>
          ) : null}

          <div className="success-footer-info">
            <div className="info-grid">
              <div className="info-column">
                <h3>Shipping to</h3>
                <p>Stride Hub Customer</p>
                <p>123 Movement Ave, Unit 4B</p>
                <p>Seoul, 06234, KR</p>
              </div>
              <div className="info-column">
                <h3>Payment</h3>
                <p>Stripe (Visa **** 4242)</p>
                <p>Total: ${orderData?.total.toFixed(2) || '0.00'}</p>
              </div>
              <div className="info-column">
                <h3>Method</h3>
                <p>Standard Shipping</p>
                <p>(3-5 Business Days)</p>
              </div>
            </div>

            <div className="help-note">
              <p>Need help with your order? <a href="mailto:support@stridehub.com">Contact Support</a></p>
            </div>
          </div>

          <div className="success-actions">
            <button className="primary-button continue-btn" onClick={() => navigate('/')}>
              Continue Shopping
              <ArrowRight size={18} />
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default CheckoutSuccessPage
