USE revandroast;

-- Insert currencies
INSERT INTO currencies (currency_code, symbol, exchange_rate_to_php) VALUES
('PHP', '₱', 1.0000),    -- Philippine Peso
('USD', '$', 56.7500),   -- US Dollar (e.g., for int'l customers)
('KRW', '₩', 0.0425);    -- Korean Won (for K-pop/esports fans)

-- Insert users 
INSERT INTO users (name, email, password, role) VALUES
('Admin Rev', 'admin@revandroast.com', SHA2('vroom123', 256), 'admin'),
('Pit Crew Staff', 'pitcrew@revandroast.com', SHA2('fuel123', 256), 'staff'),
('Speedster Customer', 'speedster@example.com', SHA2('turbo123', 256), 'customer');

-- Insert racing-themed coffee products
INSERT INTO products (name, description, price, stock_quantity, currency_id) VALUES
('Turbo Shot', 'Double espresso with nitro cold foam - 0 to 100mph in one sip', 150.00, 50, 1),
('Fuel Blend 98', 'Premium octane-grade arabica with dark chocolate & smoky notes', 180.00, 30, 1),
('Pit Crew Latte', 'Crew-chief approved latte with caramel torque boost', 160.00, 40, 1),
('Checkered Flag Cold Brew', '12-hour steeped victory brew with vanilla finish', 190.00, 25, 1),
('Dragster Americano', 'High-compression long black with cinnamon exhaust', 140.00, 45, 1),
('Overdrive Mocha', 'Espresso with dark chocolate & chili turbocharger', 200.00, 35, 1),
('Grid Energy Bar', 'Oatmeal bar with espresso beans - perfect pre-race fuel', 90.00, 60, 1);

-- Insert Pit Stop Bundles 
INSERT INTO product_bundles (name, description, price, currency_id) VALUES
('Pre-Race Energy Pack', 'Turbo Shot + Grid Energy Bar + electrolyte gel', 350.00, 1),
('Chill & Cruise Combo', 'Pit Crew Latte + Checkered Flag Cold Brew + biscotti', 520.00, 1),
('Team Garage Pack', '4x Overdrive Mochas + 4x Grid Energy Bars', 880.00, 1);

-- Link bundle items 
INSERT INTO bundle_items (bundle_id, product_id, quantity) VALUES
(1, 1, 1),  -- Turbo Shot in Pre-Race Pack
(1, 7, 2),  -- 2x Grid Energy Bars
(2, 3, 1),  -- Pit Crew Latte in Chill & Cruise
(2, 4, 1),  -- Checkered Flag Cold Brew
(3, 6, 4),  -- 4x Overdrive Mochas in Team Garage
(3, 7, 4);  -- 4x Grid Energy Bars

-- Sample order (Speedster Customer buys a Pre-Race Pack)
INSERT INTO orders (user_id, total_amount, currency_id) VALUES
(3, 350.00, 1);

-- Order items (bundle components)
INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1, 1, 1, 150.00),  -- Turbo Shot
(1, 7, 2, 90.00);   -- Grid Energy Bars (2x)

-- RPM Points transaction (10 points per ₱100 spent)
INSERT INTO transaction_log (order_id, payment_method, payment_status, amount, rpm_points_earned) VALUES
(1, 'GCash', 'completed', 350.00, 35);