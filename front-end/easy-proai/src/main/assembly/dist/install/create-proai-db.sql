CREATE ROLE {{ easy_proai_db_admin_username }} WITH LOGIN PASSWORD '{{ easy_proai_db_admin_password }}';

CREATE DATABASE proai_db
  WITH OWNER = {{ easy_proai_db_admin_username }}
       ENCODING = 'UTF8'
       CONNECTION LIMIT = -1;