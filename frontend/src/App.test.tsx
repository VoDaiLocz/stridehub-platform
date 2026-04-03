import { describe, it, expect } from 'vitest'
import { render, screen, within } from '@testing-library/react'
import App from './App'

describe('StrideHub Storefront', () => {
  it('should render the premium header with logo link', () => {
    render(<App />)
    expect(screen.getByRole('link', { name: /stridehub/i })).toBeInTheDocument()
  })

  it('should render navigation links for shopping', () => {
    render(<App />)
    const nav = screen.getByRole('navigation', { name: /primary/i })
    expect(within(nav).getByRole('link', { name: /^women$/i })).toBeInTheDocument()
    expect(within(nav).getByRole('link', { name: /^men$/i })).toBeInTheDocument()
  })

  it('should render the Vessi-inspired hero section', () => {
    render(<App />)
    expect(screen.getByText(/Rain-proof movement for everyday life/i)).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /shop women/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /shop men/i })).toBeInTheDocument()
  })

  it('should render product storytelling features', () => {
    render(<App />)
    expect(screen.getByText(/Cloudfeel Performance/i)).toBeInTheDocument()
    expect(screen.getByText(/Dyma-tex Waterproof/i)).toBeInTheDocument()
  })

  it('should render utility action icons', () => {
    render(<App />)
    expect(screen.getByRole('button', { name: /search/i })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /account/i })).toBeInTheDocument()
    expect(screen.getByRole('button', { name: /cart/i })).toBeInTheDocument()
  })
})
