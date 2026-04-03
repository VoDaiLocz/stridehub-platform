import React from 'react';
import Header from './Header';
import CartDrawer from '../../features/cart/CartDrawer';
import './Layout.css';

interface LayoutProps {
  children: React.ReactNode;
}

const Layout: React.FC<LayoutProps> = ({ children }) => {
  return (
    <div className="layout-root">
      <Header />
      <CartDrawer />
      <main className="main-content">
        {children}
      </main>
      <footer className="footer-site">
        <div className="container footer-inner">
          <p className="copyright">© 2026 StrideHub. All rights reserved.</p>
        </div>
      </footer>
    </div>
  );
};

export default Layout;

