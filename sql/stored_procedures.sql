DELIMITER //

CREATE PROCEDURE add_to_cart(
    IN p_product_id INT,
    IN p_quantity INT
)
BEGIN
    -- Just validate stock, don't store in temp table
    IF NOT EXISTS (SELECT 1 FROM products
                  WHERE product_id = p_product_id
                  AND stock_quantity >= p_quantity) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Product not available or insufficient stock';
END IF;

    -- No temp table operations since Java maintains cart
END //

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

    -- Start transaction
START TRANSACTION;

-- 1. Create the order
INSERT INTO orders (user_id, total_amount, currency_id)
VALUES (p_user_id, p_total_amount, p_currency_id);

SET p_order_id = LAST_INSERT_ID();

    -- 2. Calculate RPM points (1 point per currency unit)
    SET p_rpm_points = FLOOR(p_total_amount);

    -- 3. Update user's RPM points
UPDATE users
SET rpm_points = rpm_points + p_rpm_points
WHERE user_id = p_user_id;

-- 4. Record transaction
INSERT INTO transaction_log (order_id, payment_method, payment_status, amount, rpm_points_earned)
VALUES (p_order_id, p_payment_method, 'completed', p_total_amount, p_rpm_points);

-- Commit transaction
COMMIT;
END //
DELIMITER ;