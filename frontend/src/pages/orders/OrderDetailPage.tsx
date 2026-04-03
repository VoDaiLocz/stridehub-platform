import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getApiErrorMessage } from '../../lib/api/errors'
import { getOrderById } from '../../lib/api/orders'
import type { OrderDetail } from '../../lib/api/types'
import './OrdersPage.css'

const OrderDetailPage: React.FC = () => {
  const { orderId = '' } = useParams()
  const [order, setOrder] = useState<OrderDetail | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let cancelled = false

    async function loadOrder() {
      setLoading(true)
      setError('')

      try {
        const response = await getOrderById(orderId)
        if (!cancelled) {
          setOrder(response)
        }
      } catch (error) {
        if (!cancelled) {
          setError(getApiErrorMessage(error, 'Unable to load this order right now.'))
          setOrder(null)
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    if (orderId) {
      void loadOrder()
    }

    return () => {
      cancelled = true
    }
  }, [orderId])

  return (
    <div className="page-shell orders-page">
      <div className="container orders-shell">
        <Link to="/account/orders" className="text-link">
          Back to orders
        </Link>

        {loading ? <div className="page-shell--center">Loading order detail...</div> : null}
        {!loading && error ? <div className="page-shell--center page-shell--error">{error}</div> : null}

        {!loading && !error && order ? (
          <div className="order-detail-card">
            <header className="order-detail-card__header">
              <div>
                <span className="meta-label">Order</span>
                <h1>{order.orderNumber}</h1>
              </div>
              <div className="order-card__meta">
                <span>{order.status}</span>
                <strong>
                  {order.currency} {order.totalAmount.toFixed(2)}
                </strong>
              </div>
            </header>

            <div className="order-detail-items">
              {order.items.map((item) => (
                <div key={item.id} className="order-detail-item">
                  <div>
                    <h2>{item.productName}</h2>
                    <p>{item.variantName}</p>
                    <p>SKU: {item.sku}</p>
                  </div>
                  <div className="order-detail-item__numbers">
                    <span>Qty {item.quantity}</span>
                    <strong>
                      {order.currency} {item.totalPriceAmount.toFixed(2)}
                    </strong>
                  </div>
                </div>
              ))}
            </div>
          </div>
        ) : null}
      </div>
    </div>
  )
}

export default OrderDetailPage
