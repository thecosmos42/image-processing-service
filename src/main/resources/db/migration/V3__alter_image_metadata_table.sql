ALTER TABLE image_metadata ADD COLUMN IF NOT EXISTS transform_path VARCHAR(255);
ALTER TABLE image_metadata RENAME COLUMN storage_path TO input_path;

ALTER TABLE image_metadata RENAME COLUMN uploaded_tsp TO created_tsp;
ALTER TABLE image_metadata ADD COLUMN IF NOT EXISTS updated_tsp TIMESTAMP;