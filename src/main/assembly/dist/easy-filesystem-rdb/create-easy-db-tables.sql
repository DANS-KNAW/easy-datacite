SET client_encoding = 'UTF8';
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Tables 
--

CREATE TABLE easy_files (
    pid character varying(64) NOT NULL,
    parent_sid character varying(64) NOT NULL,
    dataset_sid character varying(64) NOT NULL,
    path character varying(1024),
    filename character varying(256) NOT NULL,
    size integer NOT NULL,
    mimetype character varying(64) NOT NULL,
    creator_role character varying(32) NOT NULL,
    visible_to character varying(32) NOT NULL,
    accessible_to character varying(32) NOT NULL
);


ALTER TABLE public.easy_files OWNER TO easy_db_admin;

CREATE TABLE easy_folder_accessibility_status (
    id bigserial NOT NULL,
    pid character varying(64) NOT NULL,
    accessible_to character varying(32)
);

ALTER TABLE public.easy_folder_accessibility_status OWNER TO easy_db_admin;

CREATE TABLE easy_folder_creator (
    id bigserial NOT NULL,
    pid character varying(64) NOT NULL,
    creator character varying(32)
);


ALTER TABLE public.easy_folder_creator OWNER TO easy_db_admin;

CREATE TABLE easy_folder_visibility_status (
    id bigserial NOT NULL,
    pid character varying(64) NOT NULL,
    visible_to character varying(32)
);


ALTER TABLE public.easy_folder_visibility_status OWNER TO easy_db_admin;

CREATE TABLE easy_folders (
    pid character varying(64) NOT NULL,
    path character varying(1024),
    name character varying(256) NOT NULL,
    parent_sid character varying(64) NOT NULL,
    dataset_sid character varying(64) NOT NULL
);


ALTER TABLE public.easy_folders OWNER TO easy_db_admin;

--
-- Primary keys
--

ALTER TABLE ONLY easy_files
    ADD CONSTRAINT easy_files_pkey PRIMARY KEY (pid);

ALTER TABLE ONLY easy_folder_accessibility_status
    ADD CONSTRAINT easy_folder_accessibility_status_pkey PRIMARY KEY (id);

ALTER TABLE ONLY easy_folder_creator
    ADD CONSTRAINT easy_folder_creator_pkey PRIMARY KEY (id);

ALTER TABLE ONLY easy_folder_visibility_status
    ADD CONSTRAINT easy_folder_visibility_status_pkey PRIMARY KEY (id);

ALTER TABLE ONLY easy_folders
    ADD CONSTRAINT easy_folders_pkey PRIMARY KEY (pid);

-- 
-- Indices
--
    
CREATE INDEX easy_files_accessible_to ON easy_files USING btree (accessible_to);

CREATE INDEX easy_files_creator_role ON easy_files USING btree (creator_role);

CREATE INDEX easy_files_filename ON easy_files USING btree (filename);

CREATE INDEX easy_files_parent_sid ON easy_files USING btree (parent_sid);

CREATE INDEX easy_files_path ON easy_files USING btree (path);

CREATE INDEX easy_files_pid ON easy_files USING btree (pid);

CREATE INDEX easy_files_visible_to ON easy_files USING btree (visible_to);

CREATE INDEX easy_folder_accessibility_status_pid ON easy_folder_accessibility_status USING btree (pid);

CREATE INDEX easy_folder_creator_pid ON easy_folder_creator USING btree (pid);

CREATE INDEX easy_folder_visibility_status_pid ON easy_folder_visibility_status USING btree (pid);

CREATE INDEX easy_folders_name ON easy_folders USING btree (name);

CREATE INDEX easy_folders_path ON easy_folders USING btree (path);

CREATE INDEX easy_folders_pid ON easy_folders USING btree (pid);

--
-- Key constraints
--

ALTER TABLE ONLY easy_folder_accessibility_status
    ADD CONSTRAINT easy_folder_accessibility_status_pid_fkey FOREIGN KEY (pid) REFERENCES easy_folders(pid) ON DELETE CASCADE;

ALTER TABLE ONLY easy_folder_creator
    ADD CONSTRAINT easy_folder_creator_pid_fkey FOREIGN KEY (pid) REFERENCES easy_folders(pid) ON DELETE CASCADE;

ALTER TABLE ONLY easy_folder_visibility_status
    ADD CONSTRAINT easy_folder_visibility_status_pid_fkey FOREIGN KEY (pid) REFERENCES easy_folders(pid) ON DELETE CASCADE;

--
-- Users for client applications
--
    
CREATE ROLE easy_webui WITH LOGIN;
CREATE ROLE easy_sword WITH LOGIN;
CREATE ROLE easy_rest WITH LOGIN;
CREATE ROLE easy_ebiu WITH LOGIN;
    
GRANT INSERT,SELECT,UPDATE,DELETE ON TABLE 
    easy_files, 
    easy_folder_accessibility_status, 
    easy_folder_creator,
    easy_folder_visibility_status,
    easy_folders
    TO easy_webui, easy_sword, easy_rest, easy_ebiu;


