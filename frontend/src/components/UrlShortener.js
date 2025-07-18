import React, { useState } from 'react';
import axios from 'axios';
import './UrlShortener.css';

const UrlShortener = () => {
  const [originalUrl, setOriginalUrl] = useState('');
  const [shortUrl, setShortUrl] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [copied, setCopied] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setShortUrl('');
    setCopied(false);

    try {
      const response = await axios.post('/api/shorten', {
        originalUrl: originalUrl
      });
      
      setShortUrl(response.data.shortUrl);
    } catch (err) {
      if (err.response?.status === 400) {
        setError('Please enter a valid URL starting with http:// or https://');
      } else {
        setError('Something went wrong. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleCopy = async () => {
    try {
      await navigator.clipboard.writeText(shortUrl);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (err) {
      console.error('Failed to copy text: ', err);
    }
  };

  const isValidUrl = (string) => {
    try {
      new URL(string);
      return string.startsWith('http://') || string.startsWith('https://');
    } catch (_) {
      return false;
    }
  };

  return (
    <div className="url-shortener">
      <div className="container">
        <form onSubmit={handleSubmit} className="url-form">
          <div className="input-group">
            <input
              type="url"
              value={originalUrl}
              onChange={(e) => setOriginalUrl(e.target.value)}
              placeholder="Enter your long URL here..."
              required
              className="url-input"
              disabled={loading}
            />
            <button 
              type="submit" 
              disabled={loading || !isValidUrl(originalUrl)}
              className="shorten-btn"
            >
              {loading ? 'Shortening...' : 'Shorten URL'}
            </button>
          </div>
        </form>

        {error && (
          <div className="error-message">
            <span>⚠️ {error}</span>
          </div>
        )}

        {shortUrl && (
          <div className="result-container">
            <div className="result-card">
              <h3>Your shortened URL is ready! 🎉</h3>
              <div className="url-result">
                <input
                  type="text"
                  value={shortUrl}
                  readOnly
                  className="short-url-input"
                />
                <button onClick={handleCopy} className="copy-btn">
                  {copied ? '✓ Copied!' : '📋 Copy'}
                </button>
              </div>
              <div className="original-url">
                <small>Original: {originalUrl}</small>
              </div>
            </div>
          </div>
        )}

        <div className="features">
          <div className="feature">
            <div className="feature-icon">🚀</div>
            <h4>Fast & Reliable</h4>
            <p>Get your shortened URLs instantly</p>
          </div>
          <div className="feature">
            <div className="feature-icon">🔒</div>
            <h4>Secure</h4>
            <p>Your URLs are safe and secure</p>
          </div>
          <div className="feature">
            <div className="feature-icon">📊</div>
            <h4>Analytics</h4>
            <p>Track clicks on your links</p>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UrlShortener;
