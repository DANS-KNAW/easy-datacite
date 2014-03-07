CREATE ROLE proai_db_admin WITH LOGIN;

CREATE DATABASE proai_db
  WITH OWNER = proai_db_admin
       ENCODING = 'UTF8'
       CONNECTION LIMIT = -1;