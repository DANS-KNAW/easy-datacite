CREATE ROLE easy_admin WITH LOGIN;

CREATE DATABASE easy_db
  WITH OWNER = easy_admin
       ENCODING = 'UTF8'
       CONNECTION LIMIT = -1;