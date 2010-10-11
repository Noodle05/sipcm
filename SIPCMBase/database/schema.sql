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
PRIMARY KEY (id));

CREATE TABLE tbl_config (
id INTEGER NOT NULL AUTO_INCREMENT, 
propertykey VARCHAR(255) NOT NULL UNIQUE, 
propertyvalue VARCHAR(2000), 
PRIMARY KEY (id));

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
UNIQUE (name, deletedate));

CREATE TABLE tbl_user (
id BIGINT NOT NULL AUTO_INCREMENT, 
createdate DATETIME, 
deletedate DATETIME, 
lastmodify DATETIME, 
birthday DATE, 
display_name VARCHAR(64), 
email VARCHAR(256) NOT NULL, 
first_name VARCHAR(64) NOT NULL, 
last_name VARCHAR(64) NOT NULL, 
middle_name VARCHAR(64), 
password VARCHAR(256), 
phonenumber VARCHAR(32), 
phonenumberstatus INTEGER, 
sipid VARCHAR(32) NOT NULL, 
sippassword VARCHAR(256) NOT NULL, 
sipstatus INTEGER NOT NULL, 
status INTEGER NOT NULL, 
username VARCHAR(64) NOT NULL, 
PRIMARY KEY (id), 
UNIQUE (sipid, deletedate), 
UNIQUE (username, deletedate), 
UNIQUE (email, deletedate));

CREATE TABLE tbl_uservoipaccount (
id BIGINT NOT NULL AUTO_INCREMENT, 
createdate DATETIME, 
deletedate DATETIME, 
lastmodify DATETIME, 
account VARCHAR(256) NOT NULL, 
password VARCHAR(256) NOT NULL, 
type INTEGER NOT NULL, 
user_id BIGINT, 
voipvendor_id INTEGER, 
PRIMARY KEY (id), 
UNIQUE (user_id, voipvendor_id, deletedate));

CREATE TABLE tbl_voipvendor (
id INTEGER NOT NULL AUTO_INCREMENT, 
createdate DATETIME, 
deletedate DATETIME, 
lastmodify DATETIME, 
domainname VARCHAR(256) NOT NULL, 
name VARCHAR(64) NOT NULL, 
proxy VARCHAR(256), 
type INTEGER NOT NULL, 
PRIMARY KEY (id), 
UNIQUE (domainname, deletedate), 
UNIQUE (name, deletedate));

ALTER TABLE tbl_address 
ADD INDEX FKD0C464735B1CB6C9 (country_id), 
ADD CONSTRAINT FKD0C464735B1CB6C9 foreign key (country_id) references tbl_country (id);

ALTER TABLE tbl_uservoipaccount 
ADD index FKC63955615DCD2F6B (user_id), 
ADD CONSTRAINT FKC63955615DCD2F6B foreign key (user_id) references tbl_user (id);

ALTER TABLE tbl_uservoipaccount 
ADD index FKC63955617143D8AA (voipvendor_id), 
ADD CONSTRAINT FKC63955617143D8AA foreign key (voipvendor_id) references tbl_voipvendor (id);


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

