CREATE TABLE pid_counter (
	pid_prefix character varying(256), 
	pid_last_generated integer 
) WITH  ( OIDS=FALSE );

ALTER TABLE public.pid_counter OWNER TO easy_db_admin;

CREATE INDEX pid_prefix_index ON pid_counter USING btree (pid_prefix); 
  
INSERT INTO pid_counter VALUES ('urn:nbn:nl:ui:13-', 0);

GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE pid_counter TO easy_webui, easy_sword, easy_rest, easy_ebiu, easy_tool;
