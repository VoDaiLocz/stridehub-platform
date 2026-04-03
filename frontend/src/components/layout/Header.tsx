import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { ShoppingBag, Search, User, Globe, Menu } from 'lucide-react';
import { useCart } from '../../context/CartContext';
import './Header.css';

const announcements = [
  "🇺🇸 Free shipping over $120 + Free exchanges",
  "🇨🇦 Free shipping over $150 + Free exchanges",
  "Shop The Famous Cities Collection"
];

const Header: React.FC = () => {
  const [isScrolled, setIsScrolled] = useState(false);
  const [isVisible, setIsVisible] = useState(true);
  const [lastScrollY, setLastScrollY] = useState(0);
  const [announcementIndex, setAnnouncementIndex] = useState(0);
  const { totalItems, setIsCartOpen } = useCart();

  useEffect(() => {
    const timer = setInterval(() => {
      setAnnouncementIndex((prev) => (prev + 1) % announcements.length);
    }, 4000);
    return () => clearInterval(timer);
  }, []);

  useEffect(() => {
    const handleScroll = () => {
      const currentScrollY = window.scrollY;
      setIsScrolled(currentScrollY > 10);
      
      if (currentScrollY > lastScrollY && currentScrollY > 400) {
        setIsVisible(false);
      } else {
        setIsVisible(true);
      }
      setLastScrollY(currentScrollY);
    };
    window.addEventListener('scroll', handleScroll, { passive: true });
    return () => window.removeEventListener('scroll', handleScroll);
  }, [lastScrollY]);

  return (
    <div className={`header-wrapper ${isScrolled ? 'scrolled' : ''} ${!isVisible ? 'hidden' : ''}`}>
      {/* Announcement Bar */}
      <div className="announcement-bar">
        <p key={announcementIndex} className="announcement-text fade-in">
          {announcements[announcementIndex]}
        </p>
      </div>

      <header className="header">
        <div className="container header-inner">
          {/* Logo */}
          <Link to="/" className="logo">
            stridehub
          </Link>

          {/* Navigation */}
          <nav className="nav-main">
            <Link to="/collections/women">Women</Link>
            <Link to="/collections/men">Men</Link>
            <Link to="/collections/kids">Kids</Link>
            <Link to="/collections/outdoor">Outdoor</Link>
            <Link to="/more">More</Link>
          </nav>

          {/* Actions */}
          <div className="header-actions">
            <button aria-label="Language selection" className="icon-btn">
              <Globe size={18} />
            </button>
            <button aria-label="Search" className="icon-btn">
              <Search size={18} />
            </button>
            <Link to="/auth/login" aria-label="Account" className="icon-btn">
              <User size={18} />
            </Link>
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
  );
};

export default Header;

