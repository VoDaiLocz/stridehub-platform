import React from 'react';
import './FeatureSection.css';

interface FeatureProps {
  title: string;
  subtitle: string;
  description: string;
  image: string;
  reversed?: boolean;
  linkText?: string;
}

const FeatureSection: React.FC<FeatureProps> = ({ title, subtitle, description, image, reversed, linkText = "Learn more" }) => {
  return (
    <section className={`feature-section ${reversed ? 'reversed' : ''}`}>
      <div className="container feature-inner">
        <div className="feature-content">
          <span className="feature-subtitle">{subtitle}</span>
          <h2 className="feature-title">{title}</h2>
          <p className="feature-description">{description}</p>
          <button className="feature-link">{linkText}</button>
        </div>
        <div className="feature-image">
          <img src={image} alt={title} />
        </div>
      </div>
    </section>
  );
};

export default FeatureSection;
