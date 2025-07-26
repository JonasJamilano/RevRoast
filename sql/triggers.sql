-- 1. before_order_item_insert for CUSTOMER
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

-- 2. before_product_insert for ADMIN
DELIMITER //
CREATE TRIGGER before_product_insert
    BEFORE INSERT ON products
    FOR EACH ROW
BEGIN
    IF EXISTS(SELECT 1 FROM products WHERE LOWER(name) = LOWER(NEW.name)) THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Product name already exists';
END IF;
END //
DELIMITER ;




