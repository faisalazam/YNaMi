INSERT INTO Users (id, name, username, email, password, createdBy, createdDate, lastModifiedBy, lastModifiedDate)
VALUES ('uro_10021', 'user', 'user', 'user@user.com', '$2a$10$EaGaBu541ZUNS0DpRgyghOBJZYH08U/fX68J88sXVkA7dXJeZ9Ye2',
        'Crazy', now(), 'Crazy', now());

INSERT INTO Users (id, name, username, email, password, createdBy, createdDate, lastModifiedBy, lastModifiedDate)
VALUES ('uro_10022', 'admin', 'admin', 'admin@admin.com',
        '$2a$10$UAL4X2jWOVk5EknlXBm6fO0WHPqfrzHKT4JJ.Y799Q/.4awjbYHsa',
        'Crazy', now(), 'Crazy', now());

INSERT INTO Users (id, name, username, email, password, createdBy, createdDate, lastModifiedBy, lastModifiedDate)
VALUES ('uro_10023', 'support', 'support', 'support@support.com',
        '$2a$10$znBrUmfgUnZa5J7Ph1KzxeJqhHyvtDP02Wlxtzwf68khYEV3KaAz6', 'Crazy', now(), 'Crazy', now());



INSERT INTO UserRoles (userId, roleId)
VALUES ('uro_10021', 'rol_1001');

INSERT INTO UserRoles (userId, roleId)
VALUES ('uro_10022', 'rol_1002');

INSERT INTO UserRoles (userId, roleId)
VALUES ('uro_10023', 'rol_1003');