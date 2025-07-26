-- Create users with passwords
CREATE USER 'admin_user'@'localhost' IDENTIFIED BY 'AdminPass123';
CREATE USER 'staff_user'@'localhost' IDENTIFIED BY 'StaffPass123';
CREATE USER 'customer_user'@'localhost' IDENTIFIED BY 'CustomerPass123';

-- =====================
-- Admin User - Full Access
-- =====================
GRANT ALL PRIVILEGES ON revandroast.* TO 'admin_user'@'localhost';

-- =====================
-- Staff User - Manage orders/products, but not user roles
-- =====================
GRANT SELECT, INSERT, UPDATE, DELETE ON revandroast.orders TO 'staff_user'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON revandroast.order_items TO 'staff_user'@'localhost';
GRANT SELECT, INSERT, UPDATE, DELETE ON revandroast.products TO 'staff_user'@'localhost';
GRANT SELECT ON revandroast.users TO 'staff_user'@'localhost'; -- To look up customer names

-- =====================
-- Customer User - Register, Login, Order, Checkout
-- =====================
GRANT SELECT (user_id, email, password, name, role, rpm_points) ON revandroast.users TO 'customer_user'@'localhost';
GRANT INSERT (name, email, password, role) ON revandroast.users TO 'customer_user'@'localhost';
GRANT UPDATE (rpm_points) ON revandroast.users TO 'customer_user'@'localhost';

GRANT SELECT ON revandroast.products TO 'customer_user'@'localhost';
GRANT SELECT, INSERT, UPDATE ON revandroast.orders TO 'customer_user'@'localhost';
GRANT SELECT, INSERT, UPDATE ON revandroast.order_items TO 'customer_user'@'localhost';
GRANT SELECT, INSERT, UPDATE ON revandroast.transaction_log TO 'customer_user'@'localhost';
GRANT SELECT ON revandroast.currencies TO 'customer_user'@'localhost';

GRANT EXECUTE ON PROCEDURE revandroast.process_payment TO 'customer_user'@'localhost';

FLUSH PRIVILEGES;
