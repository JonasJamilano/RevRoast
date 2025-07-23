-- Create the database
CREATE DATABASE IF NOT EXISTS revandroast;
USE revandroast;

-- Users table
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'staff', 'customer') DEFAULT 'customer',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Currencies table
CREATE TABLE currencies (
    currency_id INT AUTO_INCREMENT PRIMARY KEY,
    currency_code VARCHAR(5) NOT NULL,
    symbol VARCHAR(5) NOT NULL,
    exchange_rate_to_php DECIMAL(10,4) NOT NULL
);

-- Products table
CREATE TABLE products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    currency_id INT NOT NULL,
    FOREIGN KEY (currency_id) REFERENCES currencies(currency_id)
);

-- Orders table
CREATE TABLE orders (
    order_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    order_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    currency_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (currency_id) REFERENCES currencies(currency_id)
);

-- Order items table
CREATE TABLE order_items (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Transaction log table
CREATE TABLE transaction_log (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    payment_status ENUM('pending', 'completed', 'failed') DEFAULT 'pending',
    amount DECIMAL(10,2) NOT NULL,
    rpm_points_earned INT DEFAULT 0,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);