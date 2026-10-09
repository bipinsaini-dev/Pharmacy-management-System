-- =====================================================================
--  PharmaCare - Pharmacy Management System
--  MySQL 8+ schema, constraints, indexes and sample data
--  WARNING: re-running this script drops and recreates all tables.
-- =====================================================================
CREATE DATABASE IF NOT EXISTS pharmacy_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE pharmacy_management;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS purchase_items, purchases, sale_items, sales, medicines, customers, suppliers, categories, users;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE users (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  full_name   VARCHAR(100) NOT NULL,
  email       VARCHAR(120) NOT NULL,
  phone       VARCHAR(20)  NOT NULL,
  username    VARCHAR(50)  NOT NULL,
  password    VARCHAR(100) NOT NULL,              -- BCrypt hash
  role        ENUM('ADMIN','PHARMACIST') NOT NULL DEFAULT 'PHARMACIST',
  status      ENUM('ACTIVE','INACTIVE')  NOT NULL DEFAULT 'ACTIVE',
  created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_users_username UNIQUE (username),
  CONSTRAINT uq_users_email    UNIQUE (email)
) ENGINE=InnoDB;

CREATE TABLE categories (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  category_name VARCHAR(80) NOT NULL,
  description   VARCHAR(255),
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_categories_name UNIQUE (category_name)
) ENGINE=InnoDB;

CREATE TABLE suppliers (
  id             INT AUTO_INCREMENT PRIMARY KEY,
  supplier_name  VARCHAR(120) NOT NULL,
  contact_person VARCHAR(100),
  phone          VARCHAR(20),
  email          VARCHAR(120),
  address        VARCHAR(255),
  created_at     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_suppliers_name (supplier_name)
) ENGINE=InnoDB;

CREATE TABLE medicines (
  id               INT AUTO_INCREMENT PRIMARY KEY,
  medicine_name    VARCHAR(150) NOT NULL,
  generic_name     VARCHAR(150),
  category_id      INT NOT NULL,
  supplier_id      INT NOT NULL,
  batch_number     VARCHAR(50) NOT NULL,
  manufacture_date DATE NOT NULL,
  expiry_date      DATE NOT NULL,
  purchase_price   DECIMAL(10,2) NOT NULL,
  selling_price    DECIMAL(10,2) NOT NULL,
  stock_quantity   INT NOT NULL DEFAULT 0,
  reorder_level    INT NOT NULL DEFAULT 10,
  description      VARCHAR(500),
  status           ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_med_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
  CONSTRAINT fk_med_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)  ON DELETE RESTRICT,
  CONSTRAINT uq_med_name_batch UNIQUE (medicine_name, batch_number),
  CONSTRAINT chk_med_purchase CHECK (purchase_price >= 0),
  CONSTRAINT chk_med_selling  CHECK (selling_price >= 0),
  CONSTRAINT chk_med_stock    CHECK (stock_quantity >= 0),
  CONSTRAINT chk_med_reorder  CHECK (reorder_level >= 0),
  CONSTRAINT chk_med_dates    CHECK (expiry_date > manufacture_date),
  INDEX idx_med_name (medicine_name),
  INDEX idx_med_generic (generic_name),
  INDEX idx_med_batch (batch_number),
  INDEX idx_med_expiry (expiry_date)
) ENGINE=InnoDB;

CREATE TABLE customers (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  customer_name VARCHAR(100) NOT NULL,
  phone         VARCHAR(20),
  email         VARCHAR(120),
  address       VARCHAR(255),
  created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_cust_name (customer_name),
  INDEX idx_cust_phone (phone)
) ENGINE=InnoDB;

CREATE TABLE sales (
  id             INT AUTO_INCREMENT PRIMARY KEY,
  invoice_number VARCHAR(30) NOT NULL,
  customer_id    INT NOT NULL,
  user_id        INT NOT NULL,
  subtotal       DECIMAL(12,2) NOT NULL,
  discount       DECIMAL(12,2) NOT NULL DEFAULT 0,
  gst            DECIMAL(12,2) NOT NULL DEFAULT 0,
  grand_total    DECIMAL(12,2) NOT NULL,
  payment_method ENUM('Cash','Card','UPI') NOT NULL DEFAULT 'Cash',
  sale_date      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_sales_invoice UNIQUE (invoice_number),
  CONSTRAINT fk_sales_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE RESTRICT,
  CONSTRAINT fk_sales_user     FOREIGN KEY (user_id)     REFERENCES users(id)     ON DELETE RESTRICT,
  INDEX idx_sales_date (sale_date)
) ENGINE=InnoDB;

CREATE TABLE sale_items (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  sale_id     INT NOT NULL,
  medicine_id INT NOT NULL,
  quantity    INT NOT NULL,
  price       DECIMAL(10,2) NOT NULL,
  total       DECIMAL(12,2) NOT NULL,
  CONSTRAINT fk_si_sale     FOREIGN KEY (sale_id)     REFERENCES sales(id)     ON DELETE CASCADE,
  CONSTRAINT fk_si_medicine FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE RESTRICT,
  CONSTRAINT chk_si_qty CHECK (quantity > 0)
) ENGINE=InnoDB;

CREATE TABLE purchases (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  supplier_id   INT NOT NULL,
  user_id       INT NOT NULL,
  purchase_date DATE NOT NULL,
  total_amount  DECIMAL(12,2) NOT NULL,
  CONSTRAINT fk_pur_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id) ON DELETE RESTRICT,
  CONSTRAINT fk_pur_user     FOREIGN KEY (user_id)     REFERENCES users(id)     ON DELETE RESTRICT,
  INDEX idx_pur_date (purchase_date)
) ENGINE=InnoDB;

CREATE TABLE purchase_items (
  id             INT AUTO_INCREMENT PRIMARY KEY,
  purchase_id    INT NOT NULL,
  medicine_id    INT NOT NULL,
  quantity       INT NOT NULL,
  purchase_price DECIMAL(10,2) NOT NULL,
  total          DECIMAL(12,2) NOT NULL,
  CONSTRAINT fk_pi_purchase FOREIGN KEY (purchase_id) REFERENCES purchases(id) ON DELETE CASCADE,
  CONSTRAINT fk_pi_medicine FOREIGN KEY (medicine_id) REFERENCES medicines(id) ON DELETE RESTRICT,
  CONSTRAINT chk_pi_qty CHECK (quantity > 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
--  Seed data
-- ---------------------------------------------------------------------
-- Default accounts (BCrypt hashes).  admin / Admin@123    pharmacist1 / Pharma@123
INSERT INTO users (id, full_name, email, phone, username, password, role, status) VALUES
 (1, 'System Administrator', 'admin@pharmacare.local',      '9999999999', 'admin',       '$2a$10$BRNOCtibLW1Qq7n1wcRObOybDLrvs/FtNdLmVKAppNF121qgTfNxK', 'ADMIN',      'ACTIVE'),
 (2, 'Demo Pharmacist',      'pharmacist@pharmacare.local', '9888888888', 'pharmacist1', '$2a$10$lmA9.JpdlW3sipfRwy8UROLpVWBhbWWRogD1ASmcr7uz3ueqLoeBy', 'PHARMACIST', 'ACTIVE');

INSERT INTO categories (id, category_name, description) VALUES
 (1, 'Antibiotics',  'Anti-bacterial medicines'),
 (2, 'Painkillers',  'Analgesics and anti-inflammatory drugs'),
 (3, 'Vitamins',     'Vitamin and mineral supplements'),
 (4, 'Syrups',       'Liquid oral medicines'),
 (5, 'Tablets',      'General tablets'),
 (6, 'Capsules',     'General capsules'),
 (7, 'Injections',   'Injectable medicines'),
 (8, 'Other',        'Other pharmacy products');

INSERT INTO suppliers (id, supplier_name, contact_person, phone, email, address) VALUES
 (1, 'MediLife Distributors',  'Suresh Patil',  '9822001122', 'sales@medilife.example',   'Plot 14, MIDC Bhosari, Pune'),
 (2, 'Apex Pharma Wholesale',  'Neha Joshi',    '9822003344', 'orders@apexpharma.example','22 Laxmi Road, Pune'),
 (3, 'HealthFirst Traders',    'Rakesh Mehta',  '9822005566', 'info@healthfirst.example', 'Shop 5, Kasba Peth, Pune'),
 (4, 'CareWell Supplies',      'Anita Rao',     '9822007788', 'care@carewell.example',    '8 Station Road, Pimpri');

-- Expiry dates are relative to the day the script is run so the demo data never goes stale.
INSERT INTO medicines (id, medicine_name, generic_name, category_id, supplier_id, batch_number, manufacture_date, expiry_date, purchase_price, selling_price, stock_quantity, reorder_level, description) VALUES
 (1,  'Paracetamol 500mg',   'Acetaminophen',        2, 1, 'PCM-2401', DATE_SUB(CURDATE(), INTERVAL 150 DAY), DATE_ADD(CURDATE(), INTERVAL 420 DAY), 18.00,  25.00, 400,  80, 'Fever and mild pain relief (strip of 10)'),
 (2,  'Azithromycin 500mg',  'Azithromycin',         1, 2, 'AZI-2310', DATE_SUB(CURDATE(), INTERVAL 200 DAY), DATE_ADD(CURDATE(), INTERVAL 300 DAY), 72.00,  98.50, 120,  30, 'Macrolide antibiotic (strip of 3)'),
 (3,  'Cetirizine 10mg',     'Cetirizine HCl',       5, 1, 'CET-2405', DATE_SUB(CURDATE(), INTERVAL 100 DAY), DATE_ADD(CURDATE(), INTERVAL 500 DAY), 14.00,  22.00,  25,  40, 'Antihistamine for allergies (strip of 10)'),
 (4,  'Amoxicillin 500mg',   'Amoxicillin',          1, 2, 'AMX-2402', DATE_SUB(CURDATE(), INTERVAL 300 DAY), DATE_ADD(CURDATE(), INTERVAL 60 DAY),  85.00, 118.00, 200,  50, 'Penicillin antibiotic capsules (strip of 10)'),
 (5,  'Ibuprofen 400mg',     'Ibuprofen',            2, 3, 'IBU-2309', DATE_SUB(CURDATE(), INTERVAL 250 DAY), DATE_ADD(CURDATE(), INTERVAL 250 DAY), 28.00,  40.00,   0,  50, 'NSAID pain and inflammation relief (strip of 15)'),
 (6,  'Vitamin C 500mg',     'Ascorbic Acid',        3, 3, 'VTC-2404', DATE_SUB(CURDATE(), INTERVAL 90 DAY),  DATE_ADD(CURDATE(), INTERVAL 600 DAY), 32.00,  45.00, 300,  60, 'Chewable vitamin C tablets (strip of 15)'),
 (7,  'ORS Powder',          'Oral Rehydration Salts',8, 4, 'ORS-2406', DATE_SUB(CURDATE(), INTERVAL 60 DAY),  DATE_ADD(CURDATE(), INTERVAL 400 DAY), 12.00,  21.00, 600, 100, 'Electrolyte sachet'),
 (8,  'Pantoprazole 40mg',   'Pantoprazole Sodium',  5, 2, 'PAN-2403', DATE_SUB(CURDATE(), INTERVAL 120 DAY), DATE_ADD(CURDATE(), INTERVAL 450 DAY), 88.00, 125.00, 180,  40, 'Acid reflux and gastritis (strip of 10)'),
 (9,  'Cough Syrup 100ml',   'Dextromethorphan',     4, 4, 'CSY-2311', DATE_SUB(CURDATE(), INTERVAL 160 DAY), DATE_ADD(CURDATE(), INTERVAL 200 DAY), 65.00,  92.00,  90,  25, 'Dry cough suppressant syrup'),
 (10, 'Vitamin B-Complex',   'Multivitamin B',       6, 3, 'VBC-2312', DATE_SUB(CURDATE(), INTERVAL 140 DAY), DATE_ADD(CURDATE(), INTERVAL 350 DAY), 38.00,  55.00,  15,  30, 'B-vitamin capsules (strip of 10)'),
 (11, 'Diclofenac Injection','Diclofenac Sodium',    7, 2, 'DIC-2308', DATE_SUB(CURDATE(), INTERVAL 220 DAY), DATE_ADD(CURDATE(), INTERVAL 180 DAY), 15.00,  24.00,  70,  20, '75mg/3ml ampoule'),
 (12, 'Metformin 500mg',     'Metformin HCl',        5, 1, 'MET-2210', DATE_SUB(CURDATE(), INTERVAL 700 DAY), DATE_SUB(CURDATE(), INTERVAL 20 DAY),  20.00,  30.00,  40,  30, 'Type 2 diabetes tablets (strip of 20) - EXPIRED sample');

INSERT INTO customers (id, customer_name, phone, email, address) VALUES
 (1, 'Walk-in Customer', NULL,         NULL,                     NULL),
 (2, 'Rahul Sharma',     '9876543210', 'rahul.sharma@example.com','Kothrud, Pune'),
 (3, 'Priya Deshmukh',   '9823456789', 'priya.d@example.com',     'Baner, Pune'),
 (4, 'Amit Kulkarni',    '9890123456', NULL,                      'Aundh, Pune');

-- Sample sales so the dashboard charts have data
INSERT INTO sales (id, invoice_number, customer_id, user_id, subtotal, discount, gst, grand_total, payment_method, sale_date) VALUES
 (1, CONCAT('INV-', DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 6 DAY), '%Y%m%d'), '-0001'), 2, 2, 148.50, 0.00, 17.82, 166.32, 'Cash', DATE_SUB(NOW(), INTERVAL 6 DAY)),
 (2, CONCAT('INV-', DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 5 DAY), '%Y%m%d'), '-0001'), 3, 2, 167.00, 8.35, 19.04, 177.69, 'UPI',  DATE_SUB(NOW(), INTERVAL 5 DAY)),
 (3, CONCAT('INV-', DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 3 DAY), '%Y%m%d'), '-0001'), 1, 2, 227.00, 0.00, 27.24, 254.24, 'Cash', DATE_SUB(NOW(), INTERVAL 3 DAY)),
 (4, CONCAT('INV-', DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 2 DAY), '%Y%m%d'), '-0001'), 4, 2, 258.00, 0.00, 30.96, 288.96, 'Card', DATE_SUB(NOW(), INTERVAL 2 DAY)),
 (5, CONCAT('INV-', DATE_FORMAT(DATE_SUB(CURDATE(), INTERVAL 1 DAY), '%Y%m%d'), '-0001'), 2, 2, 124.00, 0.00, 14.88, 138.88, 'UPI',  DATE_SUB(NOW(), INTERVAL 1 DAY)),
 (6, CONCAT('INV-', DATE_FORMAT(CURDATE(), '%Y%m%d'), '-0001'),                           1, 2,  97.00, 0.00, 11.64, 108.64, 'Cash', NOW());

INSERT INTO sale_items (sale_id, medicine_id, quantity, price, total) VALUES
 (1, 1, 2, 25.00,  50.00), (1, 2, 1,  98.50,  98.50),
 (2, 8, 1, 125.00, 125.00),(2, 7, 2,  21.00,  42.00),
 (3, 6, 3, 45.00, 135.00), (3, 9, 1,  92.00,  92.00),
 (4, 4, 2, 118.00,236.00), (4, 3, 1,  22.00,  22.00),
 (5, 1, 4, 25.00, 100.00), (5, 11,1,  24.00,  24.00),
 (6, 10,1, 55.00,  55.00), (6, 7, 2,  21.00,  42.00);

INSERT INTO purchases (id, supplier_id, user_id, purchase_date, total_amount) VALUES
 (1, 1, 1, DATE_SUB(CURDATE(), INTERVAL 30 DAY), 2500.00);
INSERT INTO purchase_items (purchase_id, medicine_id, quantity, purchase_price, total) VALUES
 (1, 1, 100, 18.00, 1800.00), (1, 3, 50, 14.00, 700.00);
