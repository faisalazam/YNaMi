INSERT INTO Users (id, name, username, email, password, createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES (10021, 'user', 'user', 'user@user.com', 'user', 'Crazy', now(), 'Crazy', now());
INSERT INTO Users (id, name, username, email, password, createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES (10022, 'admin', 'admin', 'admin@admin.com', 'admin', 'Crazy', now(), 'Crazy', now());
INSERT INTO Users (id, name, username, email, password, createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES (10023, 'support', 'support', 'support@support.com', 'support', 'Crazy', now(), 'Crazy', now());

INSERT INTO UserRoles (userId, roleId) VALUES (10021, 1001);
INSERT INTO UserRoles (userId, roleId) VALUES (10022, 1002);
INSERT INTO UserRoles (userId, roleId) VALUES (10023, 1003);