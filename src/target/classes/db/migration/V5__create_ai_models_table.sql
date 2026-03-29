CREATE TABLE ai_models (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    version VARCHAR(50) NOT NULL,
    description TEXT,
    file_path VARCHAR(255) NOT NULL,
    model_size BIGINT,
    accuracy DOUBLE,
    precision_val DOUBLE,
    recall DOUBLE,
    f1_score DOUBLE,
    active BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_model_type_version UNIQUE (type, version)
); 