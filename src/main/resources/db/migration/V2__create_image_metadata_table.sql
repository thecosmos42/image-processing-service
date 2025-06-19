CREATE TABLE image_metadata (
    id UUID PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL,
    image_name VARCHAR(255) NOT NULL,
    storage_path VARCHAR(255) NOT NULL,
    uploaded_tsp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
