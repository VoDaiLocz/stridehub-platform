import React from 'react';
import FeatureSection from '../components/home/FeatureSection';
import './HomePage.css';

const HomePage: React.FC = () => {
  return (
    <div className="homepage">
      {/* Hero Section */}
      <section className="hero">
        <div className="hero-background">
          <img 
            src="/images/hero-banner.webp" 
            alt="Person walking in rain wearing StrideHub shoes" 
          />
          <div className="hero-overlay"></div>
        </div>
        
        <div className="container hero-content">
          <div className="hero-text">
            <span className="hero-badge">The Famous Cities Collection</span>
            <h1>Comfort for everywhere. <br/> Waterproof for anywhere.</h1>
            <p>Our most versatile collection yet. Designed for the urban explorer.</p>
            <div className="hero-actions">
              <button className="btn btn-primary">Shop Women</button>
              <button className="btn btn-primary">Shop Men</button>
            </div>
          </div>
        </div>
      </section>

      {/* Storytelling Sections */}
        <FeatureSection
          subtitle="Cloudfeel Performance"
          title="Pockets of air make every step feel like a soft landing"
          description="Our patented knit technology isn't just about breathability—it's about a 4-way stretch that moves exactly how you do."
          image="/images/feature-1.jpg"
          linkText="Learn more"
        />

        <FeatureSection
          subtitle="Dyma-tex Waterproof"
          title="Stay bone-dry through puddles and spills"
          description="A breathable, 100% waterproof membrane integrated directly into the knit. Not a coating, but a structural part of our DNA."
          image="/images/feature-2.jpg"
          reversed
          linkText="Learn more"
        />

        <FeatureSection
          subtitle="All-Day Comfort"
          title="The only shoes you'll need for any weather"
          description="From sunny strolls to rainy commutes, StrideHub keeps you moving with 100% waterproof protection and lightweight comfort."
          image="/images/feature-3.jpg"
          linkText="Shop the collection"
        />
    </div>
  );
};

export default HomePage;
