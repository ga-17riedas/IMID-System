    ALTER TABLE ai_models
ADD COLUMN precision_val DOUBLE,
ADD COLUMN recall DOUBLE,
ADD COLUMN f1_score DOUBLE,
ADD COLUMN model_size BIGINT; 