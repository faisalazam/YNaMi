INSERT INTO Users (id, name, username, email, password, createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES (10021, 'user', 'user', 'user@user.com', '$2a$10$EaGaBu541ZUNS0DpRgyghOBJZYH08U/fX68J88sXVkA7dXJeZ9Ye2', 'Crazy', now(), 'Crazy', now());
INSERT INTO Users (id, name, username, email, password, createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES (10022, 'admin', 'admin', 'admin@admin.com', '$2a$10$UAL4X2jWOVk5EknlXBm6fO0WHPqfrzHKT4JJ.Y799Q/.4awjbYHsa', 'Crazy', now(), 'Crazy', now());
INSERT INTO Users (id, name, username, email, password, createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES (10023, 'support', 'support', 'support@support.com', '$2a$10$znBrUmfgUnZa5J7Ph1KzxeJqhHyvtDP02Wlxtzwf68khYEV3KaAz6', 'Crazy', now(), 'Crazy', now());

INSERT INTO UserRoles (userId, roleId) VALUES (10021, 1001);
INSERT INTO UserRoles (userId, roleId) VALUES (10022, 1002);
INSERT INTO UserRoles (userId, roleId) VALUES (10023, 1003);