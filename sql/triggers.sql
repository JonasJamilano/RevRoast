-- 1. before_order_item_insert
DELIMITER //
CREATE TRIGGER before_order_item_insert
    BEFORE INSERT ON order_items
    FOR EACH ROW
BEGIN
    DECLARE v_stock INT;

    SELECT stock_quantity INTO v_stock
    FROM products
    WHERE product_id = NEW.product_id;

    IF v_stock < NEW.quantity THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Insufficient stock for this product';
END IF;
END //
DELIMITER ;

-- 2. after_transaction_log_insert
DELIMITER //
CREATE TRIGGER after_transaction_log_insert
    AFTER INSERT ON transaction_log
    FOR EACH ROW
BEGIN
    IF NEW.payment_status = 'completed' THEN
        -- Update user points or other post-payment actions
    UPDATE users
    SET rpm_points = COALESCE(rpm_points, 0) + NEW.rpm_points_earned
    WHERE user_id = (SELECT user_id FROM orders WHERE order_id = NEW.order_id);
END IF;
END //
DELIMITER ;

-- 3. after_product_price_update
DELIMITER //
CREATE TRIGGER after_product_price_update
    AFTER UPDATE ON products
    FOR EACH ROW
BEGIN
    IF OLD.price != NEW.price THEN
        INSERT INTO price_history (product_id, old_price, new_price, change_date)
        VALUES (NEW.product_id, OLD.price, NEW.price, NOW());
END IF;
END //
DELIMITER ;