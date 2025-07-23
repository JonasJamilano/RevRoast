-- Insert Users (with SHA-256 hashed passwords)
INSERT INTO users (name, email, password, role) VALUES
('Admin Rev', 'admin@revandroast.com', 'fuel123', 'admin'),
('Pit Crew Staff', 'pitcrew@revandroast.com', 'fuel123', 'staff'),
('Speedster Customer', 'speedster@example.com', 'fuel123', 'customer');

-- Insert Currencies
INSERT INTO currencies (currency_code, symbol, exchange_rate_to_php) VALUES
('PHP', '₱', 1.0000),
('USD', '$', 56.0000),
('JPY', '¥', 0.3800);

-- Insert Products (F1-themed)
INSERT INTO products (name, description, price, stock_quantity, currency_id) VALUES
('Pit Stop Espresso', 'Quick and bold shot for speed lovers', 100.00, 50, 1),          -- PHP
('Turbocharged Cappuccino', 'Foamy boost with a velvety finish', 120.00, 40, 1),
('DRS Americano', 'Smooth black coffee with an overtaking kick', 110.00, 30, 1),
('Podium Muffin', 'Victory treat with fresh blueberries', 3.00, 60, 2),               -- USD
('Chicane Bagel', 'Twisted and toasted with cream cheese', 2.50, 70, 2),
('Suzuka Matcha Latte', 'Smooth Japanese green tea, race-prepped', 600.00, 20, 3);    -- JPY

-- Insert Orders (user_id = 3 is Speedster Customer)
INSERT INTO orders (user_id, total_amount, currency_id) VALUES
(3, 330.00, 1),  -- PHP
(3, 6.00, 2),    -- USD
(3, 1200.00, 3); -- JPY

-- Insert Order Items
INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1, 1, 2, 100.00),  -- 2x Espresso @ ₱100.00
(1, 2, 1, 120.00),  -- 1x Cappuccino @ ₱120.00
(2, 4, 1, 3.00),    -- 1x Blueberry Muffin @ $3.00
(2, 5, 1, 3.00),    -- 1x Bagel @ $2.50 + $0.50 extra
(3, 6, 2, 600.00);  -- 2x Matcha Latte @ ¥600.00

-- Insert Transaction Logs
INSERT INTO transaction_log (order_id, payment_method, payment_status, amount, rpm_points_earned) VALUES
(1, 'Cash', 'completed', 330.00, 0),
(2, 'Cashless', 'completed', 6.00, 6),
(3, 'Cashless', 'completed', 1200.00, 12);
