-- path: src/main/resources/data.sql
-- Seed 2 tài khoản mẫu (nếu bảng khác tên, giữ theo tên em đang dùng)
IF NOT EXISTS (SELECT 1 FROM [user] WHERE email='admin@example.com')
INSERT INTO [user] (fullname, email, password, phone, role)
VALUES ('Admin', 'admin@example.com', '123456', '0900000000', 'ADMIN');

IF NOT EXISTS (SELECT 1 FROM [user] WHERE email='user@example.com')
INSERT INTO [user] (fullname, email, password, phone, role)
VALUES ('Normal User', 'user@example.com', '123456', '0911111111', 'USER');
