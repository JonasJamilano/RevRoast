USE revandroast;

-- Create roles if they don't exist
CREATE ROLE IF NOT EXISTS 'db_admin', 'store_staff', 'store_customer';

-- 1. ADMIN USER (Full access)
CREATE USER IF NOT EXISTS 'admin_user'@'%' IDENTIFIED BY 'SecureAdminPass123!';

-- Grant all privileges on all tables
GRANT ALL PRIVILEGES ON revandroast.* TO 'admin_user'@'%';
GRANT GRANT OPTION ON revandroast.* TO 'admin_user'@'%';

-- Assign admin role
GRANT 'db_admin' TO 'admin_user'@'%';
SET DEFAULT ROLE 'db_admin' FOR 'admin_user'@'%';

-- 2. STAFF USER (Limited access)
CREATE USER IF NOT EXISTS 'staff_user'@'%' IDENTIFIED BY 'StaffPass456!';

-- Product management permissions
GRANT SELECT, INSERT, UPDATE ON revandroast.products TO 'staff_user'@'%';
GRANT SELECT, INSERT, UPDATE ON revandroast.product_bundles TO 'staff_user'@'%';
GRANT SELECT, INSERT, UPDATE ON revandroast.bundle_items TO 'staff_user'@'%';
GRANT SELECT, INSERT ON revandroast.inventory_log TO 'staff_user'@'%';
GRANT SELECT, INSERT ON revandroast.inventory_audit TO 'staff_user'@'%';

-- Order processing permissions
GRANT SELECT, INSERT, UPDATE ON revandroast.orders TO 'staff_user'@'%';
GRANT SELECT, INSERT, UPDATE ON revandroast.order_items TO 'staff_user'@'%';
GRANT SELECT, INSERT, UPDATE ON revandroast.transaction_log TO 'staff_user'@'%';

-- Customer view (read-only)
GRANT SELECT ON revandroast.users TO 'staff_user'@'%';

-- Assign staff role
GRANT 'store_staff' TO 'staff_user'@'%';
SET DEFAULT ROLE 'store_staff' FOR 'staff_user'@'%';

-- 3. CUSTOMER USER (Restricted access)
CREATE USER IF NOT EXISTS 'customer_user'@'%' IDENTIFIED BY 'CustomerPass789!';

-- Product browsing (read-only)
GRANT SELECT ON revandroast.products TO 'customer_user'@'%';
GRANT SELECT ON revandroast.product_bundles TO 'customer_user'@'%';
GRANT SELECT ON revandroast.bundle_items TO 'customer_user'@'%';
GRANT SELECT ON revandroast.currencies TO 'customer_user'@'%';

-- Create secure views for customer access
CREATE OR REPLACE VIEW customer_orders AS
SELECT * FROM orders 
WHERE user_id = (SELECT user_id FROM users WHERE email = SUBSTRING_INDEX(USER(),'@',1));

CREATE OR REPLACE VIEW customer_rpm_points AS
SELECT rpm_points FROM users 
WHERE user_id = (SELECT user_id FROM users WHERE email = SUBSTRING_INDEX(USER(),'@',1));

CREATE OR REPLACE VIEW active_products AS
SELECT * FROM products WHERE stock_quantity > 0;

-- Grant view permissions
GRANT SELECT ON revandroast.customer_orders TO 'customer_user'@'%';
GRANT SELECT ON revandroast.customer_rpm_points TO 'customer_user'@'%';
GRANT SELECT ON revandroast.active_products TO 'customer_user'@'%';

-- Own order management
GRANT INSERT ON revandroast.orders TO 'customer_user'@'%';
GRANT INSERT ON revandroast.order_items TO 'customer_user'@'%';

-- Assign customer role
GRANT 'store_customer' TO 'customer_user'@'%';
SET DEFAULT ROLE 'store_customer' FOR 'customer_user'@'%';

-- Create stored procedures for role-specific actions
DELIMITER $$

-- Staff procedure to update product stock
CREATE PROCEDURE staff_update_stock(
    IN p_product_id INT,
    IN p_new_quantity INT,
    IN p_staff_email VARCHAR(100)
SQL SECURITY DEFINER
BEGIN
    DECLARE v_staff_id INT;
    
    -- Verify staff user
    SELECT user_id INTO v_staff_id FROM users 
    WHERE email = p_staff_email AND role = 'staff';
    
    IF v_staff_id IS NOT NULL THEN
        CALL update_product_stock(p_product_id, p_new_quantity, v_staff_id);
    ELSE
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Unauthorized: Staff credentials required';
    END IF;
END$$

-- Customer procedure to place orders
CREATE PROCEDURE customer_place_order(
    IN p_product_id INT,
    IN p_quantity INT,
    IN p_customer_email VARCHAR(100))
SQL SECURITY DEFINER
BEGIN
    DECLARE v_customer_id INT;
    
    -- Verify customer
    SELECT user_id INTO v_customer_id FROM users
    WHERE email = p_customer_email AND role = 'customer';
    
    IF v_customer_id IS NOT NULL THEN
        CALL place_order(v_customer_id, p_product_id, p_quantity);
    ELSE
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Unauthorized: Valid customer account required';
    END IF;
END$$

DELIMITER ;

-- Grant execute permissions
GRANT EXECUTE ON PROCEDURE revandroast.staff_update_stock TO 'staff_user'@'%';
GRANT EXECUTE ON PROCEDURE revandroast.customer_place_order TO 'customer_user'@'%';

FLUSH PRIVILEGES;