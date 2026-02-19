CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    email VARCHAR(255),
    age INT,
    salary DOUBLE,
    is_active BOOLEAN,
    profile_pic BLOB,
    created_at TIMESTAMP
);

INSERT INTO users (name, email, age, salary, is_active, created_at)
VALUES ('H2 User', 'test@h2.com', 30, 9999999.99 true, CURRENT_TIMESTAMP());
