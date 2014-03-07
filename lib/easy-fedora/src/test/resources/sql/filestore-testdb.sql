-- Role    : tester 
-- Database: filestore-testdb
/*

CREATE ROLE tester LOGIN
  ENCRYPTED PASSWORD 'md58cea91d2d5c816a0d1fb32bd3847135f'
  NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE;

CREATE DATABASE "filestore-test"
  WITH OWNER = tester
       ENCODING = 'UTF8'
       TABLESPACE = pg_default
       CONNECTION LIMIT = -1;
       
*/

-- Recreate

DROP TABLE easy_files;
DROP TABLE easy_folder_accessibility_status;
DROP TABLE easy_folder_visibility_status;
DROP TABLE easy_folder_creator;
DROP TABLE easy_folders;


-- Table: easy_files
-- DROP TABLE easy_files;
CREATE TABLE easy_files
(
  pid character varying(64) NOT NULL,
  parent_sid character varying(64) NOT NULL,
  dataset_sid character varying(64) NOT NULL,
  path character varying(256),
  filename character varying(256) NOT NULL,
  size integer NOT NULL,
  mimetype character varying(64) NOT NULL,
  creator_role character varying(32) NOT NULL,
  visible_to character varying(32) NOT NULL,
  accessible_to character varying(32) NOT NULL,
  CONSTRAINT easy_files_pkey PRIMARY KEY (pid)
)
WITHOUT OIDS;
ALTER TABLE easy_files OWNER TO tester;

-- Index: easy_files_accessible_to

-- DROP INDEX easy_files_accessible_to;

CREATE INDEX easy_files_accessible_to
  ON easy_files
  USING btree
  (accessible_to);

-- Index: easy_files_creator_role

-- DROP INDEX easy_files_creator_role;

CREATE INDEX easy_files_creator_role
  ON easy_files
  USING btree
  (creator_role);

-- Index: easy_files_filename

-- DROP INDEX easy_files_filename;

CREATE INDEX easy_files_filename
  ON easy_files
  USING btree
  (filename);

-- Index: easy_files_parent_sid

-- DROP INDEX easy_files_parent_sid;

CREATE INDEX easy_files_parent_sid
  ON easy_files
  USING btree
  (parent_sid);

-- Index: easy_files_pid

-- DROP INDEX easy_files_pid;

CREATE INDEX easy_files_pid
  ON easy_files
  USING btree
  (pid);

-- Index: easy_files_visible_to

-- DROP INDEX easy_files_visible_to;

CREATE INDEX easy_files_visible_to
  ON easy_files
  USING btree
  (visible_to);
 
-- Table: easy_folders

-- DROP TABLE easy_folders;

CREATE TABLE easy_folders
(
  pid character varying(64) NOT NULL,
  path character varying(256),
  "name" character varying(256) NOT NULL,
  parent_sid character varying(64) NOT NULL,
  dataset_sid character varying(64) NOT NULL,
  CONSTRAINT easy_folders_pkey PRIMARY KEY (pid)
)
WITHOUT OIDS;
ALTER TABLE easy_folders OWNER TO tester;

-- Index: easy_folders_name

-- DROP INDEX easy_folders_name;

CREATE INDEX easy_folders_name
  ON easy_folders
  USING btree
  (name);

-- Index: easy_folders_pid

-- DROP INDEX easy_folders_pid;

CREATE INDEX easy_folders_pid
  ON easy_folders
  USING btree
  (pid);
  
-- Table: easy_folder_creator

-- DROP TABLE easy_folder_creator;

CREATE TABLE easy_folder_creator
(
  id bigserial NOT NULL,
  pid character varying(64) NOT NULL,
  creator character varying(32),
  CONSTRAINT easy_folder_creator_pkey PRIMARY KEY (id),
  CONSTRAINT easy_folder_creator_pid_fkey FOREIGN KEY (pid)
      REFERENCES easy_folders (pid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITHOUT OIDS;
ALTER TABLE easy_folder_creator OWNER TO tester;

-- Index: easy_folder_creator_pid

-- DROP INDEX easy_folder_creator_pid;

CREATE INDEX easy_folder_creator_pid
  ON easy_folder_creator
  USING btree
  (pid);

-- Table: easy_folder_visibility_status

-- DROP TABLE easy_folder_visibility_status;

CREATE TABLE easy_folder_visibility_status
(
  id bigserial NOT NULL,
  pid character varying(64) NOT NULL,
  visible_to character varying(32),
  CONSTRAINT easy_folder_visibility_status_pkey PRIMARY KEY (id),
  CONSTRAINT easy_folder_visibility_status_pid_fkey FOREIGN KEY (pid)
      REFERENCES easy_folders (pid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITHOUT OIDS;
ALTER TABLE easy_folder_visibility_status OWNER TO tester;

-- Index: easy_folder_visibility_status_pid

-- DROP INDEX easy_folder_visibility_status_pid;

CREATE INDEX easy_folder_visibility_status_pid
  ON easy_folder_visibility_status
  USING btree
  (pid);
  
-- Table: easy_folder_accessibility_status

-- DROP TABLE easy_folder_accessibility_status;

CREATE TABLE easy_folder_accessibility_status
(
  id bigserial NOT NULL,
  pid character varying(64) NOT NULL,
  accessible_to character varying(32),
  CONSTRAINT easy_folder_accessibility_status_pkey PRIMARY KEY (id),
  CONSTRAINT easy_folder_accessibility_status_pid_fkey FOREIGN KEY (pid)
      REFERENCES easy_folders (pid) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE
)
WITHOUT OIDS;
ALTER TABLE easy_folder_accessibility_status OWNER TO tester;

-- Index: easy_folder_accessibility_status_pid

-- DROP INDEX easy_folder_accessibility_status_pid;

CREATE INDEX easy_folder_accessibility_status_pid
  ON easy_folder_accessibility_status
  USING btree
  (pid);



