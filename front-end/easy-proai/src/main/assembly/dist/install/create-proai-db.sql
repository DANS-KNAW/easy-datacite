CREATE ROLE {{ proai_db_admin_username }} WITH LOGIN PASSWORD '{{ proai_db_admin_password }}';

CREATE DATABASE proai_db
  WITH OWNER = {{ proai_db_admin_username }}
       ENCODING = 'UTF8'
       CONNECTION LIMIT = -1;