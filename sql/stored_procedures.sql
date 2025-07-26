-- 1. add_to_cart for CUSTOMER
DELIMITER //

CREATE PROCEDURE add_to_cart(
    IN p_product_id INT,
    IN p_quantity INT
)
BEGIN
    IF NOT EXISTS (SELECT 1 FROM products
                  WHERE product_id = p_product_id
                  AND stock_quantity >= p_quantity) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Product not available or insufficient stock';
END IF;
END //

-- 2. complete_order for STAFF
CREATE PROCEDURE complete_order(
    IN p_order_id INT,
    IN p_staff_id INT
)
BEGIN
    DECLARE v_user_id INT;
    DECLARE v_total_amount DECIMAL(10,2);
    DECLARE v_rpm_points INT;

    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
RESIGNAL;
END;

START TRANSACTION;

-- Get user_id and total_amount from the order
SELECT user_id, total_amount
INTO v_user_id, v_total_amount
FROM orders
WHERE order_id = p_order_id;

-- Calculate RPM points (1 point per whole peso)
SET v_rpm_points = FLOOR(v_total_amount);

    -- Update order status to completed with timestamp and staff who completed it
UPDATE orders
SET orderstatus = 'completed',
    completed_at = CURRENT_TIMESTAMP,
    completed_by = p_staff_id,
    updated_at = CURRENT_TIMESTAMP
WHERE order_id = p_order_id;

-- Update user's RPM points
UPDATE users
SET rpm_points = rpm_points + v_rpm_points
WHERE user_id = v_user_id;

-- Log the completed transaction (without processed_by since column doesn't exist)
INSERT INTO transaction_log (
    order_id,
    payment_method,
    payment_status,
    amount,
    rpm_points_earned
)
SELECT
    o.order_id,
    o.payment_method,
    'completed',
    o.total_amount,
    v_rpm_points
FROM orders o
WHERE o.order_id = p_order_id;

COMMIT;
END //

DELIMITER ;

-- 3. update_product_stock for STAFF
DELIMITER $$

CREATE PROCEDURE update_product_stock(
    IN p_product_id INT,
    IN p_new_quantity INT,
    IN p_staff_username VARCHAR(100)
)
BEGIN
    DECLARE v_old_quantity INT;
    DECLARE v_staff_id INT;

START TRANSACTION;

SELECT user_id INTO v_staff_id
FROM users
WHERE name = p_staff_username AND role IN ('staff', 'admin') FOR UPDATE;

IF v_staff_id IS NULL THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Staff username not found or not authorized';
END IF;

SELECT stock_quantity INTO v_old_quantity
FROM products
WHERE product_id = p_product_id FOR UPDATE;

UPDATE products
SET stock_quantity = p_new_quantity
WHERE product_id = p_product_id;

INSERT INTO inventory_audit (product_id, staff_id, old_quantity, new_quantity, change_date)
VALUES (p_product_id, v_staff_id, v_old_quantity, p_new_quantity, NOW());

COMMIT;
SELECT CONCAT('Stock updated from ', v_old_quantity, ' to ', p_new_quantity) AS message;
END$$

DELIMITER ;

-- 4. Send Order for CUSTOMER
DELIMITER //

CREATE PROCEDURE send_order(
    IN p_user_id INT,
    IN p_currency_id INT,
    IN p_payment_method VARCHAR(50),
    IN p_order_type VARCHAR(50),
    IN p_special_instructions TEXT,
    IN p_total_amount DECIMAL(10,2),
    OUT p_order_id INT,
    OUT p_rpm_points INT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
BEGIN
ROLLBACK;
RESIGNAL;
END;

START TRANSACTION;

-- Insert the order with 'pending' status
INSERT INTO orders (
    user_id,
    currency_id,
    payment_method,
    order_type,
    special_instructions,
    total_amount,
    status
)
VALUES (
           p_user_id,
           p_currency_id,
           p_payment_method,
           p_order_type,
           p_special_instructions,
           p_total_amount,
           'pending'
       );

SET p_order_id = LAST_INSERT_ID();
    SET p_rpm_points = FLOOR(p_total_amount);

    -- Record the transaction with 'pending' status
INSERT INTO transaction_log (
    order_id,
    payment_method,
    payment_status,
    amount,
    rpm_points_earned
)
VALUES (
           p_order_id,
           p_payment_method,
           'pending',
           p_total_amount,
           p_rpm_points
       );

COMMIT;
END //

DELIMITER ;

-- 5 Add Products for Admin

DELIMITER //

CREATE PROCEDURE add_product(
    IN p_name VARCHAR(255),
    IN p_description TEXT,
    IN p_price DECIMAL(10,2),
    IN p_stock INT,
    IN p_currency_id INT
)
BEGIN
    INSERT INTO products (name, description, price, stock_quantity, currency_id)
    VALUES (p_name, p_description, p_price, p_stock, p_currency_id);
END //

DELIMITER ;

-- 6 Delete Products for Admin

DELIMITER //

CREATE PROCEDURE delete_product(
    IN p_product_id INT
)
BEGIN
    DELETE FROM products WHERE product_id = p_product_id;
END //

DELIMITER ;