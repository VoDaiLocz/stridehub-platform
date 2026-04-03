import React, { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/useAuth'
import { getApiErrorMessage } from '../../lib/api/errors'
import { login as loginRequest } from '../../lib/api/identity'
import './Auth.css'

const LoginPage: React.FC = () => {
    const { login } = useAuth()
    const navigate = useNavigate()
    const location = useLocation()
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault()
        setLoading(true)
        setError('')

        try {
            const session = await loginRequest({ email, password })
            await login(session)
            navigate((location.state as { from?: string } | null)?.from ?? '/')
        } catch (error) {
            setError(getApiErrorMessage(error, 'Login failed. Please check your credentials.'))
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="auth-container">
            <div className="auth-card card-glass fade-in">
                <h1>Welcome Back</h1>
                <p className="auth-subtitle">Log in to your StrideHub account to continue.</p>

                <form onSubmit={handleSubmit} className="auth-form">
                    {error && <div className="auth-error">{error}</div>}
                    
                    <div className="form-group">
                        <label htmlFor="email">Email Address</label>
                        <input
                            type="email"
                            id="email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            required
                            placeholder="name@example.com"
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="password">Password</label>
                        <input
                            type="password"
                            id="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            placeholder="Enter your password"
                        />
                    </div>

                    <button type="submit" disabled={loading} className="btn-primary w-full">
                        {loading ? 'Logging in...' : 'Log In'}
                    </button>
                </form>

                <div className="auth-footer">
                    <p>New to StrideHub? <Link to="/auth/register">Create an account</Link></p>
                </div>
            </div>
        </div>
    )
}

export default LoginPage
