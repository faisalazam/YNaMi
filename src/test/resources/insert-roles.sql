DELETE FROM Roles;

INSERT INTO roles(id, name, createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES(1001, 'ROLE_USER', 'Test', now(), 'Test', now());
INSERT INTO roles(id, name, createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES(1002, 'ROLE_ADMIN', 'Test', now(), 'Test', now());
INSERT INTO roles(id, name, createdBy, createdDate, lastModifiedBy, lastModifiedDate) VALUES(1003, 'ROLE_SUPPORT', 'Test', now(), 'Test', now());