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
DELIMITER //
CREATE PROCEDURE complete_order(
    IN p_user_id INT,
    IN p_currency_id INT,
    IN p_payment_method VARCHAR(50),
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

INSERT INTO orders (user_id, total_amount, currency_id)
VALUES (p_user_id, p_total_amount, p_currency_id);

SET p_order_id = LAST_INSERT_ID();


    SET p_rpm_points = FLOOR(p_total_amount);

UPDATE users
SET rpm_points = rpm_points + p_rpm_points
WHERE user_id = p_user_id;

INSERT INTO transaction_log (order_id, payment_method, payment_status, amount, rpm_points_earned)
VALUES (p_order_id, p_payment_method, 'completed', p_total_amount, p_rpm_points);
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
WHERE name = p_staff_username AND role = 'staff'
    FOR UPDATE;

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

INSERT INTO orders (
    user_id,
    currency_id,
    payment_method,
    order_type,
    special_instructions,
    total_amount,
    orderstatus
)
VALUES (
           p_user_id,
           p_currency_id,
           p_payment_method,
           p_order_type,
           p_special_instructions,
           p_total_amount,
           'processing'
       );

SET p_order_id = LAST_INSERT_ID();
    SET p_rpm_points = FLOOR(p_total_amount);

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