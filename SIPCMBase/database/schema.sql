CREATE TABLE tbl_address (
id BIGINT NOT NULL AUTO_INCREMENT, 
createdate DATETIME, 
deletedate DATETIME, 
lastmodify DATETIME, 
address_line_1 VARCHAR(256) NOT NULL, 
address_ine_2 VARCHAR(256), 
city VARCHAR(256) NOT NULL, 
state VARCHAR(64) NOT NULL, 
zipcode VARCHAR(16) NOT NULL, 
country_id INTEGER, 
PRIMARY KEY (id)) ENGINE=InnoDB;

CREATE TABLE tbl_config (
id INTEGER NOT NULL AUTO_INCREMENT, 
propertykey VARCHAR(255) NOT NULL UNIQUE, 
propertyvalue VARCHAR(2000), 
PRIMARY KEY (id)) ENGINE=InnoDB;

CREATE TABLE tbl_country (
id INTEGER NOT NULL AUTO_INCREMENT, 
createdate DATETIME, 
deletedate DATETIME, 
lastmodify DATETIME, 
code INTEGER NOT NULL, 
iso_3316_code VARCHAR(64) NOT NULL, 
name VARCHAR(64) NOT NULL, 
subcode INTEGER, 
PRIMARY KEY (id), 
UNIQUE (iso_3316_code, deletedate), 
UNIQUE (name, deletedate)) ENGINE=InnoDB;

CREATE TABLE tbl_user (
id BIGINT NOT NULL AUTO_INCREMENT, 
createdate DATETIME, 
deletedate DATETIME, 
lastmodify DATETIME, 
birthday DATE, 
display_name VARCHAR(64), 
email VARCHAR(255) NOT NULL, 
first_name VARCHAR(64) NOT NULL, 
last_name VARCHAR(64) NOT NULL, 
middle_name VARCHAR(64), 
password CHAR(32), 
phonenumber VARCHAR(32), 
phonenumberstatus INTEGER, 
sipid VARCHAR(32) NOT NULL, 
sippassword VARCHAR(64) NOT NULL, 
sipstatus INTEGER NOT NULL, 
status INTEGER NOT NULL, 
username VARCHAR(32) NOT NULL, 
PRIMARY KEY (id), 
UNIQUE (sipid, deletedate), 
UNIQUE (username, deletedate), 
UNIQUE (email, deletedate)) ENGINE=InnoDB;

CREATE TABLE tbl_uservoipaccount (
id BIGINT NOT NULL AUTO_INCREMENT, 
createdate DATETIME, 
deletedate DATETIME, 
lastmodify DATETIME, 
account VARCHAR(256) NOT NULL, 
password VARCHAR(256) NOT NULL, 
type INTEGER NOT NULL, 
user_id BIGINT NOT NULL, 
voipvendor_id INTEGER NOT NULL, 
PRIMARY KEY (id), 
UNIQUE (user_id, voipvendor_id, deletedate)) ENGINE=InnoDB;

CREATE TABLE tbl_voipvendor (
id INTEGER NOT NULL AUTO_INCREMENT, 
createdate DATETIME, 
deletedate DATETIME, 
lastmodify DATETIME, 
domainname VARCHAR(255) NOT NULL, 
name VARCHAR(64) NOT NULL, 
proxy VARCHAR(256), 
type INTEGER NOT NULL, 
PRIMARY KEY (id), 
UNIQUE (domainname, deletedate), 
UNIQUE (name, deletedate)) ENGINE=InnoDB;

ALTER TABLE tbl_address 
ADD INDEX FKD0C464735B1CB6C9 (country_id), 
ADD CONSTRAINT FKD0C464735B1CB6C9 FOREIGN KEY (country_id) REFERENCES tbl_country (id);

ALTER TABLE tbl_uservoipaccount 
ADD index FKC63955615DCD2F6B (user_id), 
ADD CONSTRAINT FKC63955615DCD2F6B FOREIGN KEY (user_id) REFERENCES tbl_user (id);

ALTER TABLE tbl_uservoipaccount 
ADD index FKC63955617143D8AA (voipvendor_id), 
ADD CONSTRAINT FKC63955617143D8AA FOREIGN KEY (voipvendor_id) REFERENCES tbl_voipvendor (id);


DELIMITER //
CREATE TRIGGER tgr_user_createdate BEFORE INSERT ON tbl_user
FOR EACH ROW
BEGIN
  IF NEW.createdate IS NULL THEN
    SET NEW.createdate = CURRENT_TIMESTAMP;
  END IF;
  IF NEW.lastmodify IS NULL THEN
    SET NEW.lastmodify = CURRENT_TIMESTAMP;
  END IF;
END;//

CREATE TRIGGER tgr_address_createdate BEFORE INSERT ON tbl_address
FOR EACH ROW
BEGIN
  IF NEW.createdate IS NULL THEN
    SET NEW.createdate = CURRENT_TIMESTAMP;
  END IF;
  IF NEW.lastmodify IS NULL THEN
    SET NEW.lastmodify = CURRENT_TIMESTAMP;
  END IF;
END;//

CREATE TRIGGER tgr_country_createdate BEFORE INSERT ON tbl_country
FOR EACH ROW
BEGIN
  IF NEW.createdate IS NULL THEN
    SET NEW.createdate = CURRENT_TIMESTAMP;
  END IF;
  IF NEW.lastmodify IS NULL THEN
    SET NEW.lastmodify = CURRENT_TIMESTAMP;
  END IF;
END;//

CREATE TRIGGER tgr_voipvendor_createdate BEFORE INSERT ON tbl_voipvendor
FOR EACH ROW
BEGIN
  IF NEW.createdate IS NULL THEN
    SET NEW.createdate = CURRENT_TIMESTAMP;
  END IF;
  IF NEW.lastmodify IS NULL THEN
    SET NEW.lastmodify = CURRENT_TIMESTAMP;
  END IF;
END;//

CREATE TRIGGER tgr_uservoiopaccount_createdate BEFORE INSERT ON tbl_uservoipaccount
FOR EACH ROW
BEGIN
  IF NEW.createdate IS NULL THEN
    SET NEW.createdate = CURRENT_TIMESTAMP;
  END IF;
  IF NEW.lastmodify IS NULL THEN
    SET NEW.lastmodify = CURRENT_TIMESTAMP;
  END IF;
END;//
DELIMITER ;
