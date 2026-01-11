-- BUS
INSERT INTO bus (id, bus_number) VALUES (1, 'NB-2231');
INSERT INTO bus (id, bus_number) VALUES (2, 'NB-2089');

-- USERS
--INSERT INTO users (id, username, password, role)
--VALUES (1, 'owner1', 'password123', 'OWNER');
--
--INSERT INTO users (id, username, password, role)
--VALUES (2, 'conductor1', 'password123', 'CONDUCTOR');

-- INCOME
INSERT INTO income (id, amount, description, created_at, bus_id)
VALUES (1, 5000, 'Morning trip', NOW(), 1);

INSERT INTO income (id, amount, description, created_at, bus_id)
VALUES (2, 3000, 'Evening trip', NOW(), 1);

-- EXPENSE
INSERT INTO expense (id, amount, category, created_at, bus_id)
VALUES (1, 1000, 'Fuel', NOW(), 1);

INSERT INTO expense (id, amount, category, created_at, bus_id)
VALUES (2, 500, 'Maintenance', NOW(), 2);
