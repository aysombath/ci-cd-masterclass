-- Manual seed script for the fixed SYSADMIN accounts.
-- Not required if the app is running: SysAdminSeeder (CommandLineRunner) does this
-- automatically and idempotently on every startup. Use this only if you need to
-- seed the database directly (e.g. before the app has ever started, or via a DB console).
--
-- Passwords are BCrypt-hashed (strength 10), matching Spring Security's
-- BCryptPasswordEncoder default so the app can authenticate against these rows.
-- Run this AFTER Hibernate has created the app_user / user_roles tables
-- (i.e. after starting the app at least once with spring.jpa.hibernate.ddl-auto=update),
-- or after running the equivalent DDL manually.

INSERT INTO app_user (email, full_name, password, enabled, created_at)
VALUES ('aysombath@gmail.com', 'Ay Sombath', '$2a$10$IWINOJdWZGQx.E50fcNcX.kI.Do9gGEHqCnFigTqcyMW47N3udGki', true, now())
ON CONFLICT (email) DO NOTHING;

INSERT INTO app_user (email, full_name, password, enabled, created_at)
VALUES ('henrique.santana127@altmail.kr', 'Henrique Santana', '$2a$10$uXUf.8ECPJWXI87ujSNecOP637DKQsZSWznyS/fEmAmqdSeC/a2ZO', true, now())
ON CONFLICT (email) DO NOTHING;

INSERT INTO app_user (email, full_name, password, enabled, created_at)
VALUES ('rith.magnificent.9@gmail.com', 'Rith Magnificent', '$2a$10$8Ii0INnXxaf5vhRSof8MNOXlyRKqiUJoXtbSZaXsObPx1sjWwRM.q', true, now())
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role)
SELECT u.id, 'SYSADMIN' FROM app_user u
WHERE u.email = 'aysombath@gmail.com'
  AND NOT EXISTS (SELECT 1 FROM user_roles r WHERE r.user_id = u.id AND r.role = 'SYSADMIN');

INSERT INTO user_roles (user_id, role)
SELECT u.id, 'SYSADMIN' FROM app_user u
WHERE u.email = 'henrique.santana127@altmail.kr'
  AND NOT EXISTS (SELECT 1 FROM user_roles r WHERE r.user_id = u.id AND r.role = 'SYSADMIN');

INSERT INTO user_roles (user_id, role)
SELECT u.id, 'SYSADMIN' FROM app_user u
WHERE u.email = 'rith.magnificent.9@gmail.com'
  AND NOT EXISTS (SELECT 1 FROM user_roles r WHERE r.user_id = u.id AND r.role = 'SYSADMIN');
