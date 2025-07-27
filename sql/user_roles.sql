/* ====================== */
/*  ROLE CREATION         */
/* ====================== */
-- Creates three distinct access levels for the application
CREATE ROLE IF NOT EXISTS
  'revandroast_admin',    -- Full system administrator
  'revandroast_staff',    -- Staff with modification rights
  'revandroast_customer'; -- End customer access

/* ====================== */
/*  PRIVILEGE ASSIGNMENT  */
/* ====================== */
-- Administrator: Unlimited access to all database objects
GRANT ALL PRIVILEGES ON revandroast.* TO 'revandroast_admin';

-- Staff: Can view and modify data but not alter structure
GRANT SELECT, INSERT, UPDATE ON revandroast.* TO 'revandroast_staff';

-- Customer: Restricted read-only access with order placement
GRANT SELECT ON revandroast.products TO 'revandroast_customer';
GRANT SELECT, INSERT ON revandroast.orders TO 'revandroast_customer';

/* ====================== */
/*  USER CONFIGURATION    */
/* ====================== */
-- Assign all application roles to the database user
GRANT 'revandroast_admin', 'revandroast_staff', 'revandroast_customer'
TO 'root'@'localhost';

-- Activate all roles by default for the application user
SET DEFAULT ROLE ALL TO 'root'@'localhost';

-- Apply privilege changes immediately
FLUSH PRIVILEGES;