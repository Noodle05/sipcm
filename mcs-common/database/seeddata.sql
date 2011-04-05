INSERT INTO tbl_role (createdate, name, description) VALUES (NULL, 'ROLE_USER', 'Role for users');
INSERT INTO tbl_role (createdate, name, description) VALUES (NULL, 'ROLE_ADMIN', 'System administrator');
INSERT INTO tbl_role (createdate, name, description) VALUES (NULL, 'ROLE_CALLER', 'Role for voip users');

INSERT INTO tbl_voipvendor (createdate, name, domainname, type) VALUES (NULL, 'MyCallStation', 'mycallstation.com', 0);
INSERT INTO tbl_voipvendor (createdate, name, domainname, type) VALUES (NULL, 'Google Voice', 'google.com', 2);
INSERT INTO tbl_voipvendor (createdate, name, domainname, type) VALUES (NULL, 'SipGate', 'sipgate.com', 1);
INSERT INTO tbl_voipvendor (createdate, name, domainname, type) VALUES (NULL, 'Nonoh', 'nonoh.net', 1);
INSERT INTO tbl_voipvendor (createdate, name, domainname, type) VALUES (NULL, 'LocalPhone', 'localphone.com', 1);
INSERT INTO tbl_voipvendor (createdate, name, domainname, type, proxy) VALUES (NULL, 'VoIP.ms', 'voip.ms', 1, 'chicago.voip.ms');
