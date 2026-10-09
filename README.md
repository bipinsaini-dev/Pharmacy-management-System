# PharmaCare - Pharmacy Management System

A database-driven pharmacy management web application built with **Java Servlets, JSP, JDBC and MySQL** (no Spring, no ORM)
for a college mini-project. Two roles (Admin, Pharmacist), POS-style billing with transactions, inventory, purchases,
reports, and a Bootstrap 5 UI.

## Features
- **Authentication**: BCrypt hashed passwords, HttpSession login, remember-me (pre-fills username), forgot-password, logout, change password.
- **Authorization**: `AuthenticationFilter` (login required for `/admin/*` and `/pharmacist/*`) and `RoleFilter` (wrong role -> 403 page).
- **Admin**: dashboard with Chart.js charts, medicines CRUD (search / filter / sort / pagination), suppliers, categories, users, customers,
  purchases (increase stock), sales + invoices, reports (daily / monthly / yearly sales, top sellers, low stock, expired, near expiry, purchases; print + CSV export), billing.
- **Pharmacist**: dashboard, inventory search + details, customers (create/edit/history), billing (cart, discount, GST, Cash/Card/UPI), printable invoice, own bills, change password.
- **Inventory**: sale decreases stock, purchase increases stock, stock can never go below 0 (row locks + conditional UPDATE + DB CHECK).
- **Statuses**: AVAILABLE / LOW STOCK / OUT OF STOCK / EXPIRED (+ NEAR EXPIRY badge, 90 days).
- **Transactions**: billing and purchasing use `setAutoCommit(false)`, `commit()`, `rollback()`.
- **Security**: `PreparedStatement` everywhere (sort columns are whitelisted), server-side validation, `<c:out>` output escaping, session fixation protection, no stack traces shown to users (errors logged).

## Technologies
Java 17, Servlet 6.0 / JSP 3.1 / JSTL 3.0 (Jakarta EE 10), JDBC, MySQL 8+, Maven (WAR), Apache Tomcat **10.1.x**, jBCrypt, SLF4J, Bootstrap 5.3, Bootstrap Icons, Chart.js.

## Architecture
```
Browser -> Filter (auth/role) -> Servlet (controller) -> Service (rules, transactions) -> DAO (JDBC) -> MySQL
                                        |
                                        +-> JSP (view, JSTL/EL only)       Model = plain Java beans
```
```
src/main/java/com/pharmacare/{controller,service,dao,model,filter,util}
src/main/webapp/WEB-INF/views/{auth,admin,pharmacist,common}
src/main/webapp/assets/{css,js}
database.sql   pom.xml
```
URLs: `/login`, `/register`, `/forgot-password`, `/logout`, `/admin/*` (dashboard, medicines, billing, sales, customers, purchases, suppliers, categories, users, reports, profile) and `/pharmacist/*` (dashboard, medicines, billing, sales, customers, profile).

## Setup
**Prerequisites**: JDK 17+, Maven 3.8+, MySQL 8+, Apache Tomcat 10.1.x (NOT 9 - this project uses the `jakarta.*` namespace). The browser needs internet access for the Bootstrap / Chart.js CDN files.

1. **Install MySQL** 8+ and make sure the server is running.
2. **Create the database + data**: `mysql -u root -p < database.sql` (creates `pharmacy_management`, all tables, constraints, indexes and sample data; re-running resets the data).
3. **Configure DB credentials** - either edit `src/main/resources/db.properties`
   (`db.url`, `db.username`, `db.password`) **or** set environment variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` before starting Tomcat (they take priority).
4. **Install Maven** and check `mvn -v`.
5. **Build the WAR**: `mvn clean package` -> `target/pharmacy-management.war`.
6. **Install Tomcat 10.1.x** (unzip it somewhere).
7. **Deploy**: copy the WAR into `<tomcat>/webapps/` (or use the IDE's Tomcat integration with the exploded WAR).
8. **Start Tomcat**: `<tomcat>/bin/startup.sh` (Linux/macOS) or `startup.bat` (Windows).
9. **Open**: **http://localhost:8080/pharmacy-management/**

## Default accounts
| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `Admin@123` |
| Pharmacist | `pharmacist1` | `Pharma@123` |

Change these after the first login (My Profile -> Change password). New pharmacists can self-register at `/register`;
public registration always creates a PHARMACIST - admin accounts are created by an admin under *Users*.

## Demo walkthrough
Register -> Login -> Dashboard -> (admin) Medicines -> Add medicine -> Customers -> Add customer -> New Bill (search, add to cart, discount, payment) -> Complete sale -> Invoice (Print) -> check Medicines (stock reduced) -> Sales -> Logout.

## Notes / design decisions
- GST is a flat **12%** on (subtotal - discount); change `BillingService.GST_RATE`. Discount is entered as a percentage.
- Invoice numbers: `INV-yyyyMMdd-0001`, generated inside the billing transaction.
- Deleting a medicine / user that already appears in bills or purchases deactivates it instead (history stays intact). Customers, suppliers and categories in use cannot be deleted (friendly message).
- "Forgot password" is demo-level: identity is confirmed by username + registered email (no email server needed).
- "Remember me" only remembers the username on the login form; it never stores a password.
- Invoice header (address, phone, GSTIN) is placeholder text in `common/invoice.jsp`.
- Logs go to Tomcat's `catalina.out` / console via SLF4J.

## Screenshots
_Add screenshots here (login, dashboard, medicines, billing, invoice, reports)._

## Future improvements
CSRF tokens, per-medicine GST slabs, barcode scanning, email-based password reset, connection pooling (HikariCP), unit tests, returns / refunds, PDF invoices.
