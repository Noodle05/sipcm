INSERT INTO tbl_role (createdate, name, description) VALUES (NULL, 'ROLE_USER', 'Role for users');
INSERT INTO tbl_role (createdate, name, description) VALUES (NULL, 'ROLE_ADMIN', 'System administrator');
INSERT INTO tbl_role (createdate, name, description) VALUES (NULL, 'ROLE_CALLER', 'Role for voip users');

INSERT INTO tbl_voipvendor (createdate, name, domainname, type) VALUES (NULL, 'Gaofamily', 'gaofamily.org', 0);
INSERT INTO tbl_voipvendor (createdate, name, domainname, type) VALUES (NULL, 'Google Voice', 'google.com', 2);
INSERT INTO tbl_voipvendor (createdate, name, domainname, type) VALUES (NULL, 'sipgate', 'sipgate.com', 1);
INSERT INTO tbl_voipvendor (createdate, name, domainname, type) VALUES (NULL, 'Nonoh', 'nonoh.net', 1);
INSERT INTO tbl_voipvendor (createdate, name, domainname, type) VALUES (NULL, 'LocalPhone', 'localphone.com', 1);
