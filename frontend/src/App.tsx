import { BrowserRouter as Router, Route, Routes } from 'react-router-dom'
import Layout from './components/layout/Layout'
import ProtectedRoute from './components/routing/ProtectedRoute'
import HomePage from './pages/HomePage'
import LoginPage from './pages/auth/LoginPage'
import RegisterPage from './pages/auth/RegisterPage'
import CheckoutPage from './pages/checkout/CheckoutPage'
import CheckoutSuccessPage from './pages/checkout/CheckoutSuccessPage'
import CollectionPage from './pages/catalog/CollectionPage'
import ProductDetailPage from './pages/catalog/ProductDetailPage'
import OrdersPage from './pages/orders/OrdersPage'
import OrderDetailPage from './pages/orders/OrderDetailPage'
import { AuthProvider } from './context/AuthContext'
import { CartProvider } from './context/CartContext'
import './App.css'

function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <Router>
          <Layout>
            <Routes>
              <Route path="/" element={<HomePage />} />
              <Route path="/collections/:collectionSlug" element={<CollectionPage />} />
              <Route path="/products/:slug" element={<ProductDetailPage />} />
              <Route path="/auth/login" element={<LoginPage />} />
              <Route path="/auth/register" element={<RegisterPage />} />
              <Route path="/checkout" element={<CheckoutPage />} />
              <Route path="/checkout/success" element={<CheckoutSuccessPage />} />
              <Route element={<ProtectedRoute />}>
                <Route path="/account/orders" element={<OrdersPage />} />
                <Route path="/account/orders/:orderId" element={<OrderDetailPage />} />
              </Route>
              <Route path="*" element={<HomePage />} />
            </Routes>
          </Layout>
        </Router>
      </CartProvider>
    </AuthProvider>
  )
}

export default App
