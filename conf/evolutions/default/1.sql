# ---!Ups
CREATE TABLE RegisteredInfo (
id int(11) AUTO_INCREMENT PRIMARY KEY,
fname varchar(12) NOT NULL,
mname varchar(12),
lname varchar(12) NOT NULL,
email varchar(20) NOT NULL UNIQUE,
password varchar(15) NOT NULL,
mobile varchar(10) NOT NULL,
gender varchar(7) NOT NULL,
age int(11) NOT NULL,
isEnable tinyint(1) NOT NULL,
isUser tinyint(1) NOT NULL
);

CREATE TABLE AssignmentDetails (
id int(11) AUTO_INCREMENT PRIMARY KEY,
title varchar(15) NOT NULL,
description text
);

# --- !Downs
DROP TABLE RegisteredInfo;
DROP TABLE AssignmentDetails;