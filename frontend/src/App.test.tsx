import { describe, expect, it, vi } from 'vitest'
import { render, screen, waitFor, within } from '@testing-library/react'
import App from './App'

vi.mock('./lib/api/catalog', () => ({
  getProducts: vi.fn().mockResolvedValue({ items: [] }),
  getBrands: vi.fn().mockResolvedValue([]),
  getProductBySlug: vi.fn(),
}))

describe('StrideHub Storefront', () => {
  const renderApp = async () => {
    render(<App />)
    await waitFor(() =>
      expect(screen.getByText(/catalog is connected but currently empty/i)).toBeInTheDocument(),
    )
  }

  it('renders the premium header shell with the brand link', async () => {
    await renderApp()
    expect(screen.getByRole('link', { name: /stridehub/i })).toBeInTheDocument()
  })

  it('renders primary navigation links for collection discovery', async () => {
    await renderApp()
    const nav = screen.getByRole('navigation', { name: /primary/i })
    expect(within(nav).getByRole('link', { name: /^women$/i })).toBeInTheDocument()
    expect(within(nav).getByRole('link', { name: /^men$/i })).toBeInTheDocument()
    expect(within(nav).getByRole('link', { name: /^outdoor$/i })).toBeInTheDocument()
  })

  it('renders the phase 1 hero with collection entry points', async () => {
    await renderApp()
    expect(screen.getByText(/rain-proof movement for everyday life/i)).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /shop women/i })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /shop men/i })).toBeInTheDocument()
  })

  it('renders storytelling features and the live catalog note', async () => {
    await renderApp()
    expect(screen.getByText(/cloudfeel performance/i)).toBeInTheDocument()
    expect(screen.getByText(/dyma-tex waterproof/i)).toBeInTheDocument()
    expect(screen.getByText(/catalog is connected but currently empty/i)).toBeInTheDocument()
  })

  it('renders utility actions without colliding with product CTA labels', async () => {
    await renderApp()
    expect(screen.getByRole('button', { name: /search/i })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /account/i })).toBeInTheDocument()
    expect(screen.getByLabelText(/shopping bag/i)).toBeInTheDocument()
  })
})
