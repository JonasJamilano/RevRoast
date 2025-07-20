USE revandroast;

DELIMITER $$

-- 1. BEFORE ORDER INSERT: Validate stock availability
CREATE TRIGGER before_order_insert
BEFORE INSERT ON order_items
FOR EACH ROW
BEGIN
    DECLARE v_stock INT;
    DECLARE v_product_name VARCHAR(100);
    
    -- Get current stock and product name
    SELECT stock_quantity, name INTO v_stock, v_product_name
    FROM products
    WHERE product_id = NEW.product_id;
    
    -- Validate stock
    IF v_stock < NEW.quantity THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = CONCAT('Insufficient stock for ', v_product_name, 
                                '. Available: ', v_stock, ', Requested: ', NEW.quantity);
    END IF;
END$$

-- 2. AFTER ORDER INSERT: Update stock and log inventory change
CREATE TRIGGER after_order_insert
AFTER INSERT ON order_items
FOR EACH ROW
BEGIN
    -- Update product stock
    UPDATE products
    SET stock_quantity = stock_quantity - NEW.quantity
    WHERE product_id = NEW.product_id;
    
    -- Log inventory change (for analytics)
    INSERT INTO inventory_log (product_id, change_type, quantity, reference_id, log_date)
    VALUES (NEW.product_id, 'order', -NEW.quantity, NEW.order_id, NOW());
END$$

-- 3. AFTER PAYMENT INSERT: Update order status and RPM points
CREATE TRIGGER after_payment_insert
AFTER INSERT ON transaction_log
FOR EACH ROW
BEGIN
    -- If payment is completed, update user's total RPM points
    IF NEW.payment_status = 'completed' THEN
        UPDATE users u
        JOIN orders o ON u.user_id = o.user_id
        SET u.rpm_points = IFNULL(u.rpm_points, 0) + NEW.rpm_points_earned
        WHERE o.order_id = NEW.order_id;
        
        -- Log RPM points activity
        INSERT INTO loyalty_log (user_id, points_change, reason, reference_id, log_date)
        SELECT o.user_id, NEW.rpm_points_earned, 'order_payment', NEW.transaction_id, NOW()
        FROM orders o
        WHERE o.order_id = NEW.order_id;
    END IF;
END$$

-- 4. BEFORE PRODUCT DELETE: Prevent deletion of products with orders
CREATE TRIGGER before_product_delete
BEFORE DELETE ON products
FOR EACH ROW
BEGIN
    DECLARE v_order_count INT;
    
    -- Check if product has existing orders
    SELECT COUNT(*) INTO v_order_count
    FROM order_items
    WHERE product_id = OLD.product_id;
    
    IF v_order_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = CONCAT('Cannot delete product #', OLD.product_id, 
                                ' - it has ', v_order_count, ' associated orders');
    END IF;
END$$

-- 5. AFTER USER UPDATE: Log role changes and send notification
CREATE TRIGGER after_user_update
AFTER UPDATE ON users
FOR EACH ROW
BEGIN
    -- Get admin ID from session
    SET @admin_id = (SELECT user_id FROM users WHERE email = SUBSTRING_INDEX(USER(),'@',1) AND role = 'admin' LIMIT 1);
    
    -- Only log if role changed and valid admin made the change
    IF OLD.role <> NEW.role AND @admin_id IS NOT NULL THEN
        -- Log role change
        INSERT INTO admin_log (admin_id, action, target_user_id, details, log_date)
        VALUES (
            @admin_id, 
            'role_change', 
            NEW.user_id, 
            CONCAT(OLD.role, ' → ', NEW.role), 
            NOW()
        );
        
        -- Create notification for the user
        INSERT INTO notifications (user_id, message, is_read, created_at)
        VALUES (
            NEW.user_id,
            CONCAT('Your account role has been changed to ', NEW.role),
            0,
            NOW()
        );
    END IF;
END$$

DELIMITER ;