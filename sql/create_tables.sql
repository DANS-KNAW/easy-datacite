CREATE TABLE pid_counter ( pid_prefix character varying(256), pid_last_generated integer ) WITH  ( OIDS=FALSE );

ALTER TABLE pid_counter OWNER TO pidgen;

CREATE INDEX pid_prefix_index ON pid_counter USING btree (pid_prefix); 
  
INSERT INTO pid_counter VALUES ('urn:nbn:nl:ui:13-', 0);