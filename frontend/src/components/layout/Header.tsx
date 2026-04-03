import React, { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { ShoppingBag, Search, User, Globe, Menu, LogOut } from 'lucide-react'
import { useCart } from '../../context/useCart'
import { useAuth } from '../../context/useAuth'
import './Header.css'

const announcements = [
  'Free shipping over $120 + free exchanges',
  'Weatherproof sneakers built for city movement',
  'Phase 1 buyer storefront baseline is live',
]

const Header: React.FC = () => {
  const [isScrolled, setIsScrolled] = useState(false)
  const [isVisible, setIsVisible] = useState(true)
  const [lastScrollY, setLastScrollY] = useState(0)
  const [announcementIndex, setAnnouncementIndex] = useState(0)
  const { totalItems, setIsCartOpen } = useCart()
  const { user, isAuthenticated, logout } = useAuth()

  useEffect(() => {
    const timer = window.setInterval(() => {
      setAnnouncementIndex((previousIndex) => (previousIndex + 1) % announcements.length)
    }, 4000)
    return () => window.clearInterval(timer)
  }, [])

  useEffect(() => {
    const handleScroll = () => {
      const currentScrollY = window.scrollY
      setIsScrolled(currentScrollY > 10)

      if (currentScrollY > lastScrollY && currentScrollY > 400) {
        setIsVisible(false)
      } else {
        setIsVisible(true)
      }
      setLastScrollY(currentScrollY)
    }

    window.addEventListener('scroll', handleScroll, { passive: true })
    return () => window.removeEventListener('scroll', handleScroll)
  }, [lastScrollY])

  return (
    <div className={`header-wrapper ${isScrolled ? 'scrolled' : ''} ${!isVisible ? 'hidden' : ''}`}>
      <div className="announcement-bar">
        <p key={announcementIndex} className="announcement-text fade-in">
          {announcements[announcementIndex]}
        </p>
      </div>

      <header className="header">
        <div className="container header-inner">
          <Link to="/" className="logo">
            stridehub
          </Link>

          <nav className="nav-main" aria-label="Primary">
            <Link to="/collections/women">Women</Link>
            <Link to="/collections/men">Men</Link>
            <Link to="/collections/kids">Kids</Link>
            <Link to="/collections/outdoor">Outdoor</Link>
            <Link to="/more">More</Link>
          </nav>

          <div className="header-actions">
            <button aria-label="Language selection" className="icon-btn">
              <Globe size={18} />
            </button>
            <button aria-label="Search" className="icon-btn">
              <Search size={18} />
            </button>
            {isAuthenticated && user ? (
              <>
                <Link to="/account/orders" aria-label="Account" className="icon-btn icon-btn--account">
                  <User size={18} />
                  <span className="icon-btn__label">{user.firstName ?? user.name}</span>
                </Link>
                <button aria-label="Sign out" className="icon-btn" onClick={logout}>
                  <LogOut size={18} />
                </button>
              </>
            ) : (
              <Link to="/auth/login" aria-label="Account" className="icon-btn">
                <User size={18} />
              </Link>
            )}
            <button 
              aria-label="Shopping bag"
              className="icon-btn cart-btn"
              onClick={() => setIsCartOpen(true)}
            >
              <ShoppingBag size={18} />
              {totalItems > 0 && <span className="cart-count">{totalItems}</span>}
            </button>
            <button className="mobile-menu-btn" aria-label="Menu">
              <Menu size={24} />
            </button>
          </div>
        </div>
      </header>
    </div>
  )
}

export default Header

