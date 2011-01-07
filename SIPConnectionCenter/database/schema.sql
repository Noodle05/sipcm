CREATE TABLE tbl_address (
id BIGINT NOT NULL AUTO_INCREMENT,
createdate TIMESTAMP DEFAULT 0,
lastmodify TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
deletedate DATETIME DEFAULT 0,
address_line_1 VARCHAR(256) NOT NULL,
address_line_2 VARCHAR(256),
city VARCHAR(256) NOT NULL,
state VARCHAR(64) NOT NULL,
zipcode VARCHAR(16) NOT NULL,
country_id INTEGER,
PRIMARY KEY (id)) ENGINE=InnoDB;

CREATE TABLE tbl_calllog (
id BIGINT NOT NULL AUTO_INCREMENT,
user_id BIGINT NOT NULL,
voipaccount_id BIGINT NOT NULL,
type INTEGER NOT NULL,
target VARCHAR(255) NOT NULL,
starttime DATETIME NOT NULL,
status INTEGER NOT NULL,
endtime DATETIME,
errorcode INTEGER,
errorMessage VARCHAR(255),
KEY (user_id, starttime),
PRIMARY KEY (id)) ENGINE=InnoDB;

CREATE TABLE tbl_config (
id INTEGER NOT NULL AUTO_INCREMENT,
propertykey VARCHAR(255) NOT NULL UNIQUE,
propertyvalue VARCHAR(2000),
PRIMARY KEY (id)) ENGINE=InnoDB;

CREATE TABLE tbl_country (
id INTEGER NOT NULL AUTO_INCREMENT,
createdate TIMESTAMP DEFAULT 0,
lastmodify TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
deletedate DATETIME DEFAULT 0,
code INTEGER NOT NULL,
iso_3316_code VARCHAR(64) NOT NULL,
name VARCHAR(64) NOT NULL,
subcode INTEGER,
PRIMARY KEY (id),
UNIQUE  (iso_3316_code, deletedate),
UNIQUE (name, deletedate)) ENGINE=InnoDB;

CREATE TABLE tbl_role (
id INTEGER NOT NULL AUTO_INCREMENT,
createdate TIMESTAMP DEFAULT 0,
lastmodify TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
deletedate DATETIME DEFAULT 0,
description VARCHAR(2000),
name VARCHAR(64) NOT NULL,
PRIMARY KEY (id),
UNIQUE (name, deletedate)) ENGINE=InnoDB;

CREATE TABLE tbl_user (
id BIGINT NOT NULL AUTO_INCREMENT,
createdate TIMESTAMP DEFAULT 0,
lastmodify TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
deletedate DATETIME DEFAULT 0,
birthday DATE,
display_name VARCHAR(64),
email VARCHAR(255) NOT NULL,
first_name VARCHAR(64) NOT NULL,
last_name VARCHAR(64) NOT NULL,
middle_name VARCHAR(64),
password CHAR(32),
status INTEGER NOT NULL,
username VARCHAR(32) NOT NULL,
PRIMARY KEY (id),
UNIQUE (username, deletedate),
UNIQUE (email, deletedate)) ENGINE=InnoDB;

CREATE TABLE tbl_userrole (
user_id BIGINT NOT NULL,
role_id INTEGER NOT NULL,
PRIMARY KEY (user_id, role_id)) ENGINE=InnoDB;

CREATE TABLE tbl_usersipprofile (
id BIGINT NOT NULL UNIQUE,
createdate TIMESTAMP DEFAULT 0,
lastmodify TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
deletedate DATETIME DEFAULT 0,
allow_local_directly BIT(1) NOT NULL,
area_code VARCHAR(10),
phonenumber VARCHAR(32) NOT NULL,
phonenumberstatus INTEGER,
sipstatus INTEGER NOT NULL,
PRIMARY KEY (id),
UNIQUE (phonenumber, deletedate)) ENGINE=InnoDB;

CREATE TABLE tbl_uservoipaccount (
id BIGINT NOT NULL AUTO_INCREMENT,
createdate TIMESTAMP DEFAULT 0,
lastmodify TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
deletedate DATETIME DEFAULT 0,
name VARCHAR(32) NOT NULL,
account VARCHAR(256) NOT NULL,
password VARCHAR(256) NOT NULL,
phone_number VARCHAR(32),
callback_number VARCHAR(32),
type INTEGER NOT NULL,
user_id BIGINT NOT NULL,
voipvendor_id INTEGER NOT NULL,
PRIMARY KEY (id),
KEY (voipvendor_id, account, deletedate),
UNIQUE (user_id, name, deletedate)) ENGINE=InnoDB;

CREATE TABLE tbl_voipvendor (
id INTEGER NOT NULL AUTO_INCREMENT,
createdate TIMESTAMP DEFAULT 0,
lastmodify TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
deletedate DATETIME DEFAULT 0,
domainname VARCHAR(255) NOT NULL,
name VARCHAR(64) NOT NULL,
proxy VARCHAR(256),
type INTEGER NOT NULL,
PRIMARY KEY (id),
UNIQUE (domainname, deletedate),
UNIQUE (name, deletedate)) ENGINE=InnoDB;

ALTER TABLE tbl_address
ADD INDEX FK_ADDRESS_COUNTRY (country_id),
ADD CONSTRAINT FK_ADDRESS_COUNTRY FOREIGN KEY (country_id) REFERENCES tbl_country (id);

ALTER TABLE tbl_calllog
ADD INDEX FK_CALLLOG_USER (user_id),
ADD CONSTRAINT FK_CALLLOG_USER FOREIGN KEY (user_id) REFERENCES tbl_usersipprofile (id);

ALTER TABLE tbl_calllog
ADD INDEX FK_CALLLOG_VOIPACCOUNT (voipaccount_id),
ADD CONSTRAINT FK_CALLLOG_VOIPACCOUNT FOREIGN KEY (voipaccount_id) REFERENCES tbl_uservoipaccount (id);

ALTER TABLE tbl_userrole
ADD INDEX FK_USERROLE_ROLE (role_id),
ADD CONSTRAINT FK_USERROLE_ROLE FOREIGN KEY (role_id) REFERENCES tbl_role (id);

ALTER TABLE tbl_userrole
ADD INDEX FK_USERROLE_USER (user_id),
ADD CONSTRAINT FK_USERROLE_USER FOREIGN KEY (user_id) REFERENCES tbl_user (id);

ALTER TABLE tbl_usersipprofile
ADD INDEX FK_USERSIPPROFILE_USER (id),
ADD CONSTRAINT FK_USERSIPPROFILE_USER FOREIGN KEY (id) REFERENCES tbl_user (id);

ALTER TABLE tbl_uservoipaccount
ADD INDEX FK_USERVOIPACCOUNT_USERSIPPROFILE (user_id),
ADD CONSTRAINT FK_USERVOIPACCOUNT_USER FOREIGN KEY (user_id) REFERENCES tbl_usersipprofile (id);

ALTER TABLE tbl_uservoipaccount
ADD INDEX FK_USERVOIPACCOUNT_VOIPVENDOR (voipvendor_id),
ADD CONSTRAINT FK_USERVOIPACCOUNT_VOIPVENDOR FOREIGN KEY (voipvendor_id) REFERENCES tbl_voipvendor (id);

CREATE OR REPLACE VIEW vw_user AS
SELECT username AS username, password AS password
FROM tbl_user
WHERE status = 0
AND deletedate = 0;

CREATE OR REPLACE VIEW vw_userrole AS
SELECT u.username AS username, r.name AS roles
FROM tbl_user u, tbl_userrole ur, tbl_role r
WHERE u.id = ur.user_id
AND r.id = ur.role_id
AND u.status = 0
AND u.deletedate = 0
AND r.deletedate = 0;

DELIMITER //
DROP TRIGGER tgr_user_delete IF EXISTS //
CREATE TRIGGER tgr_user BEFORE UPDATE ON tbl_user
FOR EACH ROW BEGIN
  IF NEW.deletedate <> 0 THEN
    UPDATE tbl_usersipprofile SET deletedate = NEW.deletedate WHERE id = NEW.id;
    UPDATE tbl_uservoipaccount SET deletedate = NEW.deletedate WHERE user_id = NEW.id;
  END IF;
END;//
DELIMITER ;
