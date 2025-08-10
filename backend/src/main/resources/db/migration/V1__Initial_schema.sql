-- Initial schema creation for URL Shortener
CREATE TABLE IF NOT EXISTS urls (
    id BIGSERIAL PRIMARY KEY,
    original_url VARCHAR(2048) NOT NULL,
    short_code VARCHAR(10) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    click_count BIGINT NOT NULL DEFAULT 0
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_url_short_code ON urls(short_code);
CREATE INDEX IF NOT EXISTS idx_url_created_at ON urls(created_at);
CREATE INDEX IF NOT EXISTS idx_url_click_count ON urls(click_count);

-- Insert sample data for testing (optional)
-- INSERT INTO urls (original_url, short_code, created_at, click_count) 
-- VALUES 
--     ('https://www.google.com', 'google1', CURRENT_TIMESTAMP, 0),
--     ('https://www.github.com', 'github1', CURRENT_TIMESTAMP, 5)
-- ON CONFLICT (short_code) DO NOTHING;
