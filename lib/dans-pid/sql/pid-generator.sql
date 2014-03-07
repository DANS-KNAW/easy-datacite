/*
CREATE ROLE pidadmin LOGIN
  ENCRYPTED PASSWORD ' xx pidpass '
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE NOREPLICATION;
*/

/*
CREATE DATABASE pid_generator
  WITH OWNER = pidadmin
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       LC_COLLATE = 'en_US.UTF-8'
       LC_CTYPE = 'en_US.UTF-8'
       CONNECTION LIMIT = -1;
*/

CREATE TABLE pid_counter ( 
	pid_prefix character varying(256), 
	pid_last_generated integer 
	) 
	WITH  ( OIDS=FALSE );

ALTER TABLE pid_counter 
	OWNER TO pidadmin;

CREATE INDEX pid_prefix_index 
	ON pid_counter 
	USING btree (pid_prefix); 
  
INSERT INTO pid_counter 
	VALUES ('urn:nbn:nl:ui:13-', 0);