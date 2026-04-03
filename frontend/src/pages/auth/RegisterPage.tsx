import React, { useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/useAuth'
import { getApiErrorMessage } from '../../lib/api/errors'
import { register as registerRequest } from '../../lib/api/identity'
import './Auth.css'

const RegisterPage: React.FC = () => {
    const { login } = useAuth()
    const navigate = useNavigate()
    const location = useLocation()
    const [name, setName] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault()
        setLoading(true)
        setError('')

        try {
            const session = await registerRequest({ name, email, password })
            await login(session)
            navigate((location.state as { from?: string } | null)?.from ?? '/')
        } catch (error) {
            setError(getApiErrorMessage(error, 'Registration failed. Please try again.'))
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="auth-container">
            <div className="auth-card card-glass fade-in">
                <h1>Join StrideHub</h1>
                <p className="auth-subtitle">Elevate your movement with premium footwear.</p>

                <form onSubmit={handleSubmit} className="auth-form">
                    {error && <div className="auth-error">{error}</div>}
                    
                    <div className="form-group">
                        <label htmlFor="name">Full Name</label>
                        <input
                            type="text"
                            id="name"
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            required
                            placeholder="John Doe"
                        />
                    </div>

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
                            placeholder="Create a strong password"
                        />
                    </div>

                    <button type="submit" disabled={loading} className="btn-primary w-full">
                        {loading ? 'Creating Account...' : 'Sign Up'}
                    </button>
                </form>

                <div className="auth-footer">
                    <p>Already have an account? <Link to="/auth/login">Log in</Link></p>
                </div>
            </div>
        </div>
    )
}

export default RegisterPage
