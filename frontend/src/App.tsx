import { useEffect, useState } from 'react'
import './App.css'

type ApiStatus = 'checking' | 'online' | 'offline'

type CollectionCard = {
  eyebrow: string
  title: string
  description: string
}

const collectionCards: CollectionCard[] = [
  {
    eyebrow: 'Urban Commute',
    title: 'Move through rain without changing pace',
    description:
      'Built for buyers who want weatherproof comfort, lighter materials, and a sharper everyday silhouette.',
  },
  {
    eyebrow: 'Weekend Trails',
    title: 'Grip, cushion, and structure for mixed terrain',
    description:
      'The marketplace direction supports curated performance drops from specialist sellers without losing a clean buyer experience.',
  },
  {
    eyebrow: 'Travel Rotation',
    title: 'Fewer pairs, better coverage across the week',
    description:
      'StrideHub pairs a premium storefront with a backend-ready commerce foundation for catalog, checkout, and inventory growth.',
  },
]

const experiencePoints = [
  'Buyer web prepared for a Vessi-inspired premium storefront',
  'Spring Boot backend isolated in its own workspace for domain-heavy implementation',
  'Monorepo root kept for orchestration, docs, CI, and shared platform decisions',
]

function App() {
  const [apiStatus, setApiStatus] = useState<ApiStatus>('checking')

  useEffect(() => {
    const controller = new AbortController()
    const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

    fetch(`${apiBaseUrl}/actuator/health`, {
      method: 'GET',
      signal: controller.signal,
    })
      .then((response) => {
        setApiStatus(response.ok ? 'online' : 'offline')
      })
      .catch(() => {
        setApiStatus('offline')
      })

    return () => controller.abort()
  }, [])

  return (
    <div className="app-shell">
      <header className="site-header">
        <div className="brand-lockup">
          <span className="brand-mark">S</span>
          <div>
            <p className="brand-name">StrideHub</p>
            <p className="brand-subtitle">Marketplace Platform</p>
          </div>
        </div>
        <nav className="site-nav" aria-label="Primary">
          <a href="#collections">Collections</a>
          <a href="#experience">Experience</a>
          <a href="#stack">Stack</a>
        </nav>
      </header>

      <main>
        <section className="hero-section">
          <div className="hero-copy">
            <p className="eyebrow">Buyer Web Workspace</p>
            <h1>Premium storefront direction, now paired with a real backend.</h1>
            <p className="hero-text">
              The repository now carries both halves of the platform: a Spring commerce API in
              `backend/` and a buyer-facing web workspace in `frontend/`, ready to evolve toward
              the Vessi-inspired experience.
            </p>
            <div className="hero-actions">
              <a className="primary-action" href="#collections">
                Explore direction
              </a>
              <a className="secondary-action" href="#stack">
                View platform split
              </a>
            </div>
          </div>

          <aside className="hero-panel">
            <div className="status-card">
              <p className="status-label">Backend connectivity</p>
              <div className={`status-pill status-${apiStatus}`}>
                <span className="status-dot" />
                <span>{apiStatus === 'checking' ? 'Checking API' : apiStatus === 'online' ? 'API online' : 'API unavailable'}</span>
              </div>
              <p className="status-note">
                Frontend reads `VITE_API_BASE_URL` and probes the Spring health endpoint during
                local development.
              </p>
            </div>

            <div className="metrics-grid">
              <article>
                <span>01</span>
                <strong>Backend</strong>
                <p>Spring Boot commerce core in its own workspace</p>
              </article>
              <article>
                <span>02</span>
                <strong>Frontend</strong>
                <p>React + Vite buyer web shell for premium retail flows</p>
              </article>
              <article>
                <span>03</span>
                <strong>Platform</strong>
                <p>Shared docs, CI, compose, and monorepo governance at root</p>
              </article>
            </div>
          </aside>
        </section>

        <section className="collections-section" id="collections">
          <div className="section-heading">
            <p className="eyebrow">Storefront Direction</p>
            <h2>Collections built like product stories, not placeholder grids</h2>
          </div>
          <div className="collection-grid">
            {collectionCards.map((card) => (
              <article className="collection-card" key={card.title}>
                <p className="card-eyebrow">{card.eyebrow}</p>
                <h3>{card.title}</h3>
                <p>{card.description}</p>
              </article>
            ))}
          </div>
        </section>

        <section className="experience-section" id="experience">
          <div className="section-heading">
            <p className="eyebrow">Monorepo Rationale</p>
            <h2>Clean separation without losing one-product discipline</h2>
          </div>
          <div className="experience-grid">
            {experiencePoints.map((point) => (
              <article className="experience-card" key={point}>
                <span className="experience-bullet" />
                <p>{point}</p>
              </article>
            ))}
          </div>
        </section>

        <section className="stack-section" id="stack">
          <div className="section-heading">
            <p className="eyebrow">Workspace Split</p>
            <h2>One repository, two apps, one delivery flow</h2>
          </div>
          <div className="stack-columns">
            <article className="stack-card">
              <p className="stack-title">backend/</p>
              <ul>
                <li>Spring Boot 4 modular monolith</li>
                <li>Security, Flyway, PostgreSQL, Redis baseline</li>
                <li>Commerce modules implemented task-by-task from the plan</li>
              </ul>
            </article>
            <article className="stack-card">
              <p className="stack-title">frontend/</p>
              <ul>
                <li>React 19 + TypeScript + Vite</li>
                <li>Buyer-facing shell prepared for premium marketplace UI</li>
                <li>Ready to consume the API as identity and catalog land</li>
              </ul>
            </article>
          </div>
        </section>
      </main>
    </div>
  )
}

export default App
