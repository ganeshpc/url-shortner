import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import UrlShortener from './components/UrlShortener';
import NotFound from './components/NotFound';
import './App.css';

function App() {
  return (
    <Router>
      <div className="App">
        <header className="App-header">
          <h1>🔗 URL Shortener</h1>
          <p>Transform your long URLs into short, shareable links</p>
        </header>
        <main>
          <Routes>
            <Route path="/" element={<UrlShortener />} />
            <Route path="/not-found" element={<NotFound />} />
          </Routes>
        </main>
      </div>
    </Router>
  );
}

export default App;
