import { describe, it, expect, beforeEach, vi } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import App from './App'

describe('App', () => {
  beforeEach(() => {
    globalThis.fetch = vi.fn(() => new Promise<Response>(() => {}))
  })

  afterEach(() => {
    vi.restoreAllMocks()
  })

  it('should render the site header with brand name', () => {
    render(<App />)

    expect(screen.getByText('StrideHub')).toBeInTheDocument()
    expect(screen.getByText('Marketplace Platform')).toBeInTheDocument()
  })

  it('should render navigation links', () => {
    render(<App />)

    expect(screen.getByRole('link', { name: /collections/i })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /experience/i })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /stack/i })).toBeInTheDocument()
  })

  it('should render hero section with main heading', () => {
    render(<App />)

    expect(
      screen.getByText(/Premium storefront direction, now paired with a real backend/i)
    ).toBeInTheDocument()
    expect(screen.getByText(/Buyer Web Workspace/i)).toBeInTheDocument()
  })

  it('should display checking status initially', () => {
    vi.mocked(fetch).mockImplementation(
      () => new Promise(() => {}) // Never resolves
    )

    render(<App />)

    expect(screen.getByText('Checking API')).toBeInTheDocument()
  })

  it('should display online status when API is available', async () => {
    vi.mocked(fetch).mockResolvedValue({
      ok: true,
    } as Response)

    render(<App />)

    await waitFor(() => {
      expect(screen.getByText('API online')).toBeInTheDocument()
    })
  })

  it('should display offline status when API is unavailable', async () => {
    vi.mocked(fetch).mockResolvedValue({
      ok: false,
    } as Response)

    render(<App />)

    await waitFor(() => {
      expect(screen.getByText('API unavailable')).toBeInTheDocument()
    })
  })

  it('should display offline status when fetch fails', async () => {
    vi.mocked(fetch).mockRejectedValue(new Error('Network error'))

    render(<App />)

    await waitFor(() => {
      expect(screen.getByText('API unavailable')).toBeInTheDocument()
    })
  })

  it('should call health endpoint with correct URL', async () => {
    vi.mocked(fetch).mockResolvedValue({
      ok: true,
    } as Response)

    render(<App />)

    await waitFor(() => {
      expect(fetch).toHaveBeenCalledWith(
        'http://localhost:8080/actuator/health',
        expect.objectContaining({
          method: 'GET',
        })
      )
    })
  })

  it('should use VITE_API_BASE_URL environment variable if provided', async () => {
    const originalEnv = import.meta.env.VITE_API_BASE_URL
    import.meta.env.VITE_API_BASE_URL = 'https://api.example.com'

    vi.mocked(fetch).mockResolvedValue({
      ok: true,
    } as Response)

    render(<App />)

    await waitFor(() => {
      expect(fetch).toHaveBeenCalledWith(
        'https://api.example.com/actuator/health',
        expect.anything()
      )
    })

    import.meta.env.VITE_API_BASE_URL = originalEnv
  })

  it('should render all collection cards', () => {
    render(<App />)

    expect(screen.getByText('Urban Commute')).toBeInTheDocument()
    expect(screen.getByText('Weekend Trails')).toBeInTheDocument()
    expect(screen.getByText('Travel Rotation')).toBeInTheDocument()
    expect(
      screen.getByText(/Move through rain without changing pace/i)
    ).toBeInTheDocument()
  })

  it('should render experience points', () => {
    render(<App />)

    expect(
      screen.getByText(/Buyer web prepared for a Vessi-inspired premium storefront/i)
    ).toBeInTheDocument()
    expect(
      screen.getByText(/Spring Boot backend isolated in its own workspace/i)
    ).toBeInTheDocument()
    expect(
      screen.getByText(/Monorepo root kept for orchestration, docs, CI/i)
    ).toBeInTheDocument()
  })

  it('should render stack section with backend and frontend details', () => {
    render(<App />)

    expect(screen.getByText('backend/')).toBeInTheDocument()
    expect(screen.getByText('frontend/')).toBeInTheDocument()
    expect(screen.getByText(/Spring Boot 4 modular monolith/i)).toBeInTheDocument()
    expect(screen.getByText(/React 19 \+ TypeScript \+ Vite/i)).toBeInTheDocument()
  })

  it('should render metrics grid with three articles', () => {
    render(<App />)

    expect(screen.getByText('Backend')).toBeInTheDocument()
    expect(screen.getByText('Frontend')).toBeInTheDocument()
    expect(screen.getByText('Platform')).toBeInTheDocument()
    expect(
      screen.getByText(/Spring Boot commerce core in its own workspace/i)
    ).toBeInTheDocument()
  })

  it('should have proper section IDs for navigation', () => {
    const { container } = render(<App />)

    expect(container.querySelector('#collections')).toBeInTheDocument()
    expect(container.querySelector('#experience')).toBeInTheDocument()
    expect(container.querySelector('#stack')).toBeInTheDocument()
  })

  it('should abort fetch on component unmount', async () => {
    const abortSpy = vi.spyOn(AbortController.prototype, 'abort')

    vi.mocked(fetch).mockImplementation(
      () => new Promise(() => {}) // Never resolves
    )

    const { unmount } = render(<App />)

    unmount()

    expect(abortSpy).toHaveBeenCalled()
  })
})
