CREATE TABLE persistent_logins (
username VARCHAR(64) NOT NULL,
series VARCHAR(64) NOT NULL,
token VARCHAR(64) NOT NULL,
last_used TIMESTAMP NOT NULL,
PRIMARY KEY(series)) ENGINE=InnoDB;

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
voipaccount_id BIGINT,
type INTEGER NOT NULL,
partner VARCHAR(255) NOT NULL,
starttime DATETIME NOT NULL,
status INTEGER NOT NULL,
endtime DATETIME,
errorcode INTEGER,
errorMessage VARCHAR(2000),
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

CREATE TABLE tbl_sipaddressbinding (
id BIGINT NOT NULL AUTO_INCREMENT,
address VARCHAR(255) NOT NULL,
expires INTEGER DEFAULT 3600,
call_id VARCHAR(255),
last_check INT,
remote_end VARCHAR(255),
user_id BIGINT NOT NULL,
PRIMARY KEY (id)) ENGINE=InnoDB;

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
locale VARCHAR(16),
time_zone VARCHAR(64),
status INTEGER NOT NULL,
username VARCHAR(64) NOT NULL,
PRIMARY KEY (id),
UNIQUE (username, deletedate),
UNIQUE (email, deletedate)) ENGINE=InnoDB;

CREATE TABLE tbl_useractivation (
id BIGINT NOT NULL UNIQUE,
active_code VARCHAR(32) NOT NULL,
expire_date DATETIME NOT NULL,
method INTEGER NOT NULL,
PRIMARY KEY (id)) ENGINE=InnoDB;

CREATE TABLE tbl_userrole (
user_id BIGINT NOT NULL,
role_id INTEGER NOT NULL,
PRIMARY KEY (user_id, role_id)) ENGINE=InnoDB;

CREATE TABLE tbl_usersipprofile (
id BIGINT NOT NULL AUTO_INCREMENT,
createdate TIMESTAMP DEFAULT 0,
lastmodify TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
deletedate DATETIME DEFAULT 0,
user_id BIGINT NOT NULL,
allow_local_directly BIT(1) NOT NULL,
area_code VARCHAR(10),
phonenumber VARCHAR(32) NOT NULL,
phonenumberstatus INTEGER NOT NULL DEFAULT 0,
sipstatus INTEGER NOT NULL,
PRIMARY KEY (id),
UNIQUE (phonenumber, deletedate),
UNIQUE (user_id, deletedate)) ENGINE=InnoDB;

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
online BIT(1) NOT NULL DEFAULT b'0',
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

ALTER TABLE tbl_sipaddressbinding
ADD INDEX FK_SIPADDRESSBINDING_USERSIPPROFILE (user_id),
ADD CONSTRAINT FK_SIPADDRESSBINDING_USERSIPPROFILE FOREIGN KEY (user_id) REFERENCES tbl_usersipprofile (id);

ALTER TABLE tbl_useractivation
ADD INDEX FK_USERACTIVATION_USER (id),
ADD CONSTRAINT FK_USERACTIVATION_USER FOREIGN KEY (id) REFERENCES tbl_user (id);

ALTER TABLE tbl_userrole
ADD INDEX FK_USERROLE_ROLE (role_id),
ADD CONSTRAINT FK_USERROLE_ROLE FOREIGN KEY (role_id) REFERENCES tbl_role (id);

ALTER TABLE tbl_userrole
ADD INDEX FK_USERROLE_USER (user_id),
ADD CONSTRAINT FK_USERROLE_USER FOREIGN KEY (user_id) REFERENCES tbl_user (id);

ALTER TABLE tbl_usersipprofile
ADD INDEX FK_USERSIPPROFILE_USER (user_id),
ADD CONSTRAINT FK_USERSIPPROFILE_USER FOREIGN KEY (user_id) REFERENCES tbl_user (id);

ALTER TABLE tbl_uservoipaccount
ADD INDEX FK_USERVOIPACCOUNT_USERSIPPROFILE (user_id),
ADD CONSTRAINT FK_USERVOIPACCOUNT_USER FOREIGN KEY (user_id) REFERENCES tbl_usersipprofile (id);

ALTER TABLE tbl_uservoipaccount
ADD INDEX FK_USERVOIPACCOUNT_VOIPVENDOR (voipvendor_id),
ADD CONSTRAINT FK_USERVOIPACCOUNT_VOIPVENDOR FOREIGN KEY (voipvendor_id) REFERENCES tbl_voipvendor (id);

CREATE OR REPLACE VIEW vw_user AS
SELECT username AS username, lower(password) AS password
FROM tbl_user
WHERE status = 1
AND deletedate = 0;

CREATE OR REPLACE VIEW vw_userrole AS
SELECT u.username AS username, r.name AS roles
FROM tbl_user u, tbl_userrole ur, tbl_role r
WHERE u.id = ur.user_id
AND r.id = ur.role_id
AND u.status = 1
AND u.deletedate = 0
AND r.deletedate = 0;

DELIMITER //
DROP TRIGGER IF EXISTS tgr_addressbinding_lastcheck //
CREATE TRIGGER tgr_addressbinding_lastcheck BEFORE INSERT ON tbl_sipaddressbinding
FOR EACH ROW BEGIN
  IF NEW.last_check IS NULL THEN
    SET NEW.last_check = UNIX_TIMESTAMP();
  END IF;
END;//

DROP TRIGGER IF EXISTS tgr_user_update //
CREATE TRIGGER tgr_user_update BEFORE UPDATE ON tbl_user
FOR EACH ROW BEGIN
  IF NEW.deletedate <> 0 THEN
    UPDATE tbl_uservoipaccount SET deletedate = NEW.deletedate WHERE user_id = NEW.id;
    UPDATE tbl_usersipprofile SET deletedate = NEW.deletedate, sipstatus = 0 WHERE user_id = NEW.id;
  ELSEIF NEW.status <> 1 THEN
    UPDATE tbl_usersipprofile SET sipstatus = 0 WHERE user_id = NEW.id;
  END IF;
END;//

DROP TRIGGER IF EXISTS tgr_user_delete//
CREATE TRIGGER tgr_user_delete BEFORE DELETE ON tbl_user
FOR EACH ROW BEGIN
  DELETE FROM tbl_usersipprofile WHERE user_id = OLD.id;
  DELETE FROM tbl_uservoipaccount WHERE user_id = OLD.id;
END;//

DROP TRIGGER IF EXISTS tgr_usersipprofile_update //
CREATE TRIGGER tgr_usersipprofile_update BEFORE UPDATE ON tbl_usersipprofile
FOR EACH ROW BEGIN
  IF NEW.deletedate <> 0 OR NEW.sipstatus = 0 THEN
    DELETE FROM tbl_sipaddressbinding WHERE user_id = NEW.id;
  END IF;
END;//

DROP TRIGGER IF EXISTS tgr_usersipprofile_delete//
CREATE TRIGGER tgr_usersipprofile_delete BEFORE DELETE ON tbl_usersipprofile
FOR EACH ROW BEGIN
  DELETE FROM tbl_sipaddressbinding WHERE user_id = OLD.id;
END;//

DROP PROCEDURE IF EXISTS AddressBindingExpires//

CREATE PROCEDURE AddressBindingExpires()
  LANGUAGE SQL
  NOT DETERMINISTIC
  SQL SECURITY DEFINER
  COMMENT ''
BEGIN
  DECLARE unix_now INT;
  SELECT UNIX_TIMESTAMP() INTO unix_now;

  UPDATE tbl_sipaddressbinding SET expires = expires - (unix_now - last_check), last_check = unix_now;
  DELETE FROM tbl_sipaddressbinding WHERE expires < 0;

  DROP TEMPORARY TABLE IF EXISTS uspid;
  CREATE TEMPORARY TABLE uspid (id BIGINT NOT NULL);

  INSERT INTO uspid (id)
    SELECT u.id
      FROM tbl_usersipprofile u
      LEFT JOIN tbl_sipaddressbinding a
        ON a.user_id = u.id
      WHERE u.sipstatus = 1
      GROUP BY u.id HAVING count(a.id) = 0;

  UPDATE tbl_usersipprofile u, uspid t
    SET u.sipstatus = 0
    WHERE u.id = t.id;

  SELECT id FROM uspid;

  DROP TEMPORARY TABLE uspid;

END//

DELIMITER ;
