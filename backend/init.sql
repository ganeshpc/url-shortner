-- Database initialization script
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Create indexes for performance
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_url_short_code ON url(short_code);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_url_created_at ON url(created_at);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_url_click_count ON url(click_count);

-- Create a view for analytics
CREATE OR REPLACE VIEW url_analytics AS
SELECT 
    short_code,
    original_url,
    click_count,
    created_at,
    EXTRACT(EPOCH FROM (NOW() - created_at))/86400 as days_old,
    CASE 
        WHEN click_count = 0 THEN 'Unused'
        WHEN click_count < 10 THEN 'Low Usage'
        WHEN click_count < 100 THEN 'Medium Usage'
        ELSE 'High Usage'
    END as usage_category
FROM url;
