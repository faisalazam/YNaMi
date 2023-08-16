DELETE
FROM Roles;

INSERT INTO Roles(id, name, createdBy, createdDate, lastModifiedBy, lastModifiedDate)
VALUES ('rol_1001', 'ROLE_USER', 'Test', now(), 'Test', now());

INSERT INTO Roles(id, name, createdBy, createdDate, lastModifiedBy, lastModifiedDate)
VALUES ('rol_1002', 'ROLE_ADMIN', 'Test', now(), 'Test', now());

INSERT INTO Roles(id, name, createdBy, createdDate, lastModifiedBy, lastModifiedDate)
VALUES ('rol_1003', 'ROLE_SUPPORT', 'Test', now(), 'Test', now());