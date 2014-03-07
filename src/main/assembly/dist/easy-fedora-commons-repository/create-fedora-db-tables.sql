--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: datastreampaths; Type: TABLE; Schema: public; Owner: fedora_db_admin; Tablespace: 
--

CREATE TABLE datastreampaths (
    tokendbid bigint NOT NULL,
    token character varying(199) DEFAULT ''::character varying NOT NULL,
    path character varying(255) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.datastreampaths OWNER TO fedora_db_admin;

--
-- Name: datastreampaths_tokendbid_seq; Type: SEQUENCE; Schema: public; Owner: fedora_db_admin
--

CREATE SEQUENCE datastreampaths_tokendbid_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.datastreampaths_tokendbid_seq OWNER TO fedora_db_admin;

--
-- Name: datastreampaths_tokendbid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: fedora_db_admin
--

ALTER SEQUENCE datastreampaths_tokendbid_seq OWNED BY datastreampaths.tokendbid;


--
-- Name: dcdates; Type: TABLE; Schema: public; Owner: fedora_db_admin; Tablespace: 
--

CREATE TABLE dcdates (
    pid character varying(64) NOT NULL,
    dcdate bigint NOT NULL
);


ALTER TABLE public.dcdates OWNER TO fedora_db_admin;

--
-- Name: dofields; Type: TABLE; Schema: public; Owner: fedora_db_admin; Tablespace: 
--

CREATE TABLE dofields (
    pid character varying(64) NOT NULL,
    label character varying(255),
    state character varying(1) DEFAULT 'A'::character varying NOT NULL,
    ownerid character varying(64),
    cdate bigint NOT NULL,
    mdate bigint NOT NULL,
    dcmdate bigint,
    dctitle text,
    dccreator text,
    dcsubject text,
    dcdescription text,
    dcpublisher text,
    dccontributor text,
    dcdate text,
    dctype text,
    dcformat text,
    dcidentifier text,
    dcsource text,
    dclanguage text,
    dcrelation text,
    dccoverage text,
    dcrights text
);


ALTER TABLE public.dofields OWNER TO fedora_db_admin;

--
-- Name: doregistry; Type: TABLE; Schema: public; Owner: fedora_db_admin; Tablespace: 
--

CREATE TABLE doregistry (
    dopid character varying(64) NOT NULL,
    systemversion integer DEFAULT 0 NOT NULL,
    ownerid character varying(64),
    objectstate character varying(1) DEFAULT 'A'::character varying NOT NULL,
    label character varying(255) DEFAULT ''::character varying
);


ALTER TABLE public.doregistry OWNER TO fedora_db_admin;

--
-- Name: modeldeploymentmap; Type: TABLE; Schema: public; Owner: fedora_db_admin; Tablespace: 
--

CREATE TABLE modeldeploymentmap (
    cmodel character varying(64) NOT NULL,
    sdef character varying(64) NOT NULL,
    sdep character varying(64) NOT NULL
);


ALTER TABLE public.modeldeploymentmap OWNER TO fedora_db_admin;

--
-- Name: objectpaths; Type: TABLE; Schema: public; Owner: fedora_db_admin; Tablespace: 
--

CREATE TABLE objectpaths (
    tokendbid bigint NOT NULL,
    token character varying(64) DEFAULT ''::character varying NOT NULL,
    path character varying(255) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE public.objectpaths OWNER TO fedora_db_admin;

--
-- Name: objectpaths_tokendbid_seq; Type: SEQUENCE; Schema: public; Owner: fedora_db_admin
--

CREATE SEQUENCE objectpaths_tokendbid_seq
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;


ALTER TABLE public.objectpaths_tokendbid_seq OWNER TO fedora_db_admin;

--
-- Name: objectpaths_tokendbid_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: fedora_db_admin
--

ALTER SEQUENCE objectpaths_tokendbid_seq OWNED BY objectpaths.tokendbid;


--
-- Name: pidgen; Type: TABLE; Schema: public; Owner: fedora_db_admin; Tablespace: 
--

CREATE TABLE pidgen (
    namespace character varying(255) NOT NULL,
    highestid integer NOT NULL
);


ALTER TABLE public.pidgen OWNER TO fedora_db_admin;

--
-- Name: tokendbid; Type: DEFAULT; Schema: public; Owner: fedora_db_admin
--

ALTER TABLE ONLY datastreampaths ALTER COLUMN tokendbid SET DEFAULT nextval('datastreampaths_tokendbid_seq'::regclass);


--
-- Name: tokendbid; Type: DEFAULT; Schema: public; Owner: fedora_db_admin
--

ALTER TABLE ONLY objectpaths ALTER COLUMN tokendbid SET DEFAULT nextval('objectpaths_tokendbid_seq'::regclass);


--
-- Name: datastreampaths_pkey; Type: CONSTRAINT; Schema: public; Owner: fedora_db_admin; Tablespace: 
--

ALTER TABLE ONLY datastreampaths
    ADD CONSTRAINT datastreampaths_pkey PRIMARY KEY (tokendbid);


--
-- Name: datastreampaths_token_key; Type: CONSTRAINT; Schema: public; Owner: fedora_db_admin; Tablespace: 
--

ALTER TABLE ONLY datastreampaths
    ADD CONSTRAINT datastreampaths_token_key UNIQUE (token);


--
-- Name: doregistry_pkey; Type: CONSTRAINT; Schema: public; Owner: fedora_db_admin; Tablespace: 
--

ALTER TABLE ONLY doregistry
    ADD CONSTRAINT doregistry_pkey PRIMARY KEY (dopid);


--
-- Name: objectpaths_pkey; Type: CONSTRAINT; Schema: public; Owner: fedora_db_admin; Tablespace: 
--

ALTER TABLE ONLY objectpaths
    ADD CONSTRAINT objectpaths_pkey PRIMARY KEY (tokendbid);


--
-- Name: objectpaths_token_key; Type: CONSTRAINT; Schema: public; Owner: fedora_db_admin; Tablespace: 
--

ALTER TABLE ONLY objectpaths
    ADD CONSTRAINT objectpaths_token_key UNIQUE (token);


--
-- Name: dcdates_pid; Type: INDEX; Schema: public; Owner: fedora_db_admin; Tablespace: 
--

CREATE INDEX dcdates_pid ON dcdates USING btree (pid);


--
-- Name: dofields_pid; Type: INDEX; Schema: public; Owner: fedora_db_admin; Tablespace: 
--

CREATE INDEX dofields_pid ON dofields USING btree (pid);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

