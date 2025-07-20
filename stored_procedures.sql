USE revandroast;

DELIMITER $$

-- 1. PLACE ORDER (with inventory check and RPM points)
CREATE PROCEDURE place_order(
    IN p_user_id INT,
    IN p_product_id INT,
    IN p_quantity INT
)
BEGIN
    DECLARE v_stock INT;
    DECLARE v_price DECIMAL(10,2);
    DECLARE v_total DECIMAL(10,2);
    DECLARE v_currency_id INT;
    DECLARE v_rpm_points INT;
    
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SELECT 'Error processing order' AS message;
    END;
    
    START TRANSACTION;
    
    -- Check stock and get price
    SELECT stock_quantity, price, currency_id 
    INTO v_stock, v_price, v_currency_id
    FROM products 
    WHERE product_id = p_product_id FOR UPDATE;
    
    IF v_stock >= p_quantity THEN
        -- Calculate total and RPM points (10 points per ₱100)
        SET v_total = v_price * p_quantity;
        SET v_rpm_points = FLOOR(v_total / 100) * 10;
        
        -- Create order
        INSERT INTO orders (user_id, total_amount, currency_id)
        VALUES (p_user_id, v_total, v_currency_id);
        
        SET @last_order_id = LAST_INSERT_ID();
        
        -- Add order item
        INSERT INTO order_items (order_id, product_id, quantity, price)
        VALUES (@last_order_id, p_product_id, p_quantity, v_price);
        
        -- Update stock
        UPDATE products 
        SET stock_quantity = stock_quantity - p_quantity
        WHERE product_id = p_product_id;
        
        -- Log transaction with RPM points
        INSERT INTO transaction_log (order_id, payment_method, payment_status, amount, rpm_points_earned)
        VALUES (@last_order_id, 'Pending', 'pending', v_total, v_rpm_points);
        
        COMMIT;
        SELECT CONCAT('Order #', @last_order_id, ' placed! Earned ', v_rpm_points, ' RPM points') AS message;
    ELSE
        ROLLBACK;
        SELECT 'Insufficient stock' AS error;
    END IF;
END$$

-- 2. UPDATE PRODUCT STOCK (with audit logging)
CREATE PROCEDURE update_product_stock(
    IN p_product_id INT,
    IN p_new_quantity INT,
    IN p_staff_id INT
)
BEGIN
    DECLARE v_old_quantity INT;
    
    START TRANSACTION;
    
    -- Get current stock
    SELECT stock_quantity INTO v_old_quantity
    FROM products
    WHERE product_id = p_product_id FOR UPDATE;
    
    -- Update stock
    UPDATE products
    SET stock_quantity = p_new_quantity
    WHERE product_id = p_product_id;
    
    -- Log inventory change
    INSERT INTO inventory_audit (product_id, staff_id, old_quantity, new_quantity, change_date)
    VALUES (p_product_id, p_staff_id, v_old_quantity, p_new_quantity, NOW());
    
    COMMIT;
    SELECT CONCAT('Stock updated from ', v_old_quantity, ' to ', p_new_quantity) AS message;
END$$

-- 3. GET ORDER HISTORY (with product details)
CREATE PROCEDURE get_order_history(
    IN p_user_id INT
)
BEGIN
    SELECT 
        o.order_id,
        o.order_date,
        o.total_amount,
        c.symbol AS currency,
        GROUP_CONCAT(CONCAT(oi.quantity, 'x ', p.name) SEPARATOR ', ') AS items,
        SUM(t.rpm_points_earned) AS rpm_points_earned
    FROM orders o
    JOIN order_items oi ON o.order_id = oi.order_id
    JOIN products p ON oi.product_id = p.product_id
    JOIN currencies c ON o.currency_id = c.currency_id
    LEFT JOIN transaction_log t ON o.order_id = t.order_id
    WHERE o.user_id = p_user_id
    GROUP BY o.order_id
    ORDER BY o.order_date DESC;
END$$

-- 4. PROCESS PAYMENT (with status validation)
CREATE PROCEDURE process_payment(
    IN p_order_id INT,
    IN p_payment_method VARCHAR(50)
BEGIN
    DECLARE v_order_status VARCHAR(20);
    DECLARE v_total DECIMAL(10,2);
    
    -- Verify order exists and get total
    SELECT o.total_amount, t.payment_status INTO v_total, v_order_status
    FROM orders o
    JOIN transaction_log t ON o.order_id = t.order_id
    WHERE o.order_id = p_order_id;
    
    IF v_order_status = 'pending' THEN
        UPDATE transaction_log
        SET 
            payment_method = p_payment_method,
            payment_status = 'completed',
            timestamp = NOW()
        WHERE order_id = p_order_id;
        
        SELECT CONCAT('Payment of ', v_total, ' processed via ', p_payment_method) AS message;
    ELSE
        SELECT CONCAT('Order already ', v_order_status) AS error;
    END IF;
END$$

-- 5. CURRENCY CONVERTER (dynamic rates)
CREATE PROCEDURE convert_currency(
    IN p_amount DECIMAL(10,2),
    IN p_from_currency VARCHAR(5),
    IN p_to_currency VARCHAR(5)
)
BEGIN
    DECLARE v_from_rate DECIMAL(10,4);
    DECLARE v_to_rate DECIMAL(10,4);
    DECLARE v_converted DECIMAL(10,2);
    
    -- Get exchange rates
    SELECT exchange_rate_to_php INTO v_from_rate
    FROM currencies
    WHERE currency_code = p_from_currency;
    
    SELECT exchange_rate_to_php INTO v_to_rate
    FROM currencies
    WHERE currency_code = p_to_currency;
    
    -- Convert via PHP base (e.g., USD→KRW: USD→PHP→KRW)
    IF v_from_rate IS NOT NULL AND v_to_rate IS NOT NULL THEN
        SET v_converted = (p_amount * v_from_rate) / v_to_rate;
        SELECT ROUND(v_converted, 2) AS converted_amount;
    ELSE
        SELECT 'Invalid currency code(s)' AS error;
    END IF;
END$$

DELIMITER ;