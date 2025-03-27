-- user 테이블 생성
CREATE TABLE user (
                      id INT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      password VARCHAR(255) NOT NULL,
                      nickname VARCHAR(255) UNIQUE NOT NULL,
                      email VARCHAR(255) UNIQUE NOT NULL,
                      profile_image_url VARCHAR(255),
                      info TEXT,
                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);