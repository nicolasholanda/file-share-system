CREATE TABLE file_records (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    original_name    VARCHAR(255)  NOT NULL,
    content_type     VARCHAR(255)  NOT NULL,
    size             BIGINT        NOT NULL,
    encrypted_content BLOB         NOT NULL,
    status           VARCHAR(50)   NOT NULL,
    created_at       TIMESTAMP     NOT NULL,
    updated_at       TIMESTAMP     NOT NULL
);
