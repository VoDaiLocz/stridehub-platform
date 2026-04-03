import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../../context/useAuth'

const ProtectedRoute: React.FC = () => {
  const { isAuthenticated, loading } = useAuth()
  const location = useLocation()

  if (loading) {
    return <div className="page-shell page-shell--center">Loading your account...</div>
  }

  if (!isAuthenticated) {
    return <Navigate to="/auth/login" replace state={{ from: location.pathname }} />
  }

  return <Outlet />
}

export default ProtectedRoute
