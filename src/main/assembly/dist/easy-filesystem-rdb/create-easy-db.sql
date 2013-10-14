CREATE ROLE easy_db_admin WITH LOGIN;

CREATE DATABASE easy_db
  WITH OWNER = easy_db_admin
       ENCODING = 'UTF8'
       CONNECTION LIMIT = -1;