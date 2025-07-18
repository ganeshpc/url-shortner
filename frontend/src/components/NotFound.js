import React from 'react';
import './NotFound.css';

const NotFound = () => {
  return (
    <div className="not-found">
      <div className="not-found-container">
        <div className="not-found-icon">🔍</div>
        <h2>Short URL Not Found</h2>
        <p>The short URL you're looking for doesn't exist or has expired.</p>
        <a href="/" className="home-link">
          ← Go back to create a new short URL
        </a>
      </div>
    </div>
  );
};

export default NotFound;
