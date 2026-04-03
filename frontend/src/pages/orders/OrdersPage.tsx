import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { getApiErrorMessage } from '../../lib/api/errors'
import { getOrders } from '../../lib/api/orders'
import type { OrderSummary } from '../../lib/api/types'
import './OrdersPage.css'

const OrdersPage: React.FC = () => {
  const [orders, setOrders] = useState<OrderSummary[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let cancelled = false

    async function loadOrders() {
      setLoading(true)
      setError('')

      try {
        const response = await getOrders()
        if (!cancelled) {
          setOrders(response)
        }
      } catch (error) {
        if (!cancelled) {
          setError(getApiErrorMessage(error, 'Unable to load your orders right now.'))
          setOrders([])
        }
      } finally {
        if (!cancelled) {
          setLoading(false)
        }
      }
    }

    void loadOrders()

    return () => {
      cancelled = true
    }
  }, [])

  return (
    <div className="page-shell orders-page">
      <div className="container orders-shell">
        <div className="orders-heading">
          <span className="eyebrow">Account</span>
          <h1>Orders</h1>
          <p>Track payment-confirmed orders from the Phase 1 order APIs.</p>
        </div>

        {loading ? <div className="page-shell--center">Loading orders...</div> : null}
        {!loading && error ? <div className="page-shell--center page-shell--error">{error}</div> : null}
        {!loading && !error && orders.length === 0 ? (
          <div className="orders-empty">
            <p>No orders yet.</p>
            <Link to="/collections/all" className="btn btn-primary">
              Start shopping
            </Link>
          </div>
        ) : null}

        {!loading && !error && orders.length > 0 ? (
          <div className="orders-list">
            {orders.map((order) => (
              <Link key={order.id} to={`/account/orders/${order.id}`} className="order-card">
                <div>
                  <span className="meta-label">Order</span>
                  <h2>{order.orderNumber}</h2>
                </div>
                <div className="order-card__meta">
                  <span>{order.status}</span>
                  <strong>
                    {order.currency} {order.totalAmount.toFixed(2)}
                  </strong>
                </div>
              </Link>
            ))}
          </div>
        ) : null}
      </div>
    </div>
  )
}

export default OrdersPage
