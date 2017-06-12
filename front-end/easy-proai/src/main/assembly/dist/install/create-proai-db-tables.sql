--
-- PostgreSQL database dump
--

SET client_encoding = 'UTF8';
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- Name: SCHEMA public; Type: COMMENT; Schema: -; Owner: postgres
--

COMMENT ON SCHEMA public IS 'Standard public schema';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: rcadmin; Type: TABLE; Schema: public; Owner: proai_db_admin; Tablespace: 
--

CREATE TABLE rcadmin (
    pollingenabled integer NOT NULL,
    identifypath character varying(28)
);


ALTER TABLE public.rcadmin OWNER TO {{ easy_proai_db_admin_username }};

--
-- Name: rcfailure; Type: TABLE; Schema: public; Owner: proai_db_admin; Tablespace: 
--

CREATE TABLE rcfailure (
    identifier character varying(255) NOT NULL,
    mdprefix character varying(255) NOT NULL,
    sourceinfo text NOT NULL,
    failcount integer NOT NULL,
    firstfaildate character varying(20) NOT NULL,
    lastfaildate character varying(20) NOT NULL,
    lastfailreason text NOT NULL
);


ALTER TABLE public.rcfailure OWNER TO {{ easy_proai_db_admin_username }};

--
-- Name: rcformat; Type: TABLE; Schema: public; Owner: proai_db_admin; Tablespace: 
--

CREATE TABLE rcformat (
    formatkey bigserial NOT NULL,
    mdprefix character varying(255) NOT NULL,
    namespaceuri character varying(255) NOT NULL,
    schemalocation character varying(255) NOT NULL,
    lastpolldate bigint DEFAULT 0 NOT NULL
);


ALTER TABLE public.rcformat OWNER TO {{ easy_proai_db_admin_username }};

--
-- Name: rcitem; Type: TABLE; Schema: public; Owner: proai_db_admin; Tablespace: 
--

CREATE TABLE rcitem (
    itemkey bigserial NOT NULL,
    identifier character varying(255) NOT NULL
);


ALTER TABLE public.rcitem OWNER TO {{ easy_proai_db_admin_username }};

--
-- Name: rcmembership; Type: TABLE; Schema: public; Owner: proai; Tablespace: 
--

CREATE TABLE rcmembership (
    setkey integer NOT NULL,
    recordkey integer NOT NULL
);


ALTER TABLE public.rcmembership OWNER TO {{ easy_proai_db_admin_username }};

--
-- Name: rcprunable; Type: TABLE; Schema: public; Owner: proai_db_admin; Tablespace: 
--

CREATE TABLE rcprunable (
    prunekey bigserial NOT NULL,
    xmlpath character varying(28) NOT NULL
);


ALTER TABLE public.rcprunable OWNER TO {{ easy_proai_db_admin_username }};

--
-- Name: rcqueue; Type: TABLE; Schema: public; Owner: proai_db_admin; Tablespace: 
--

CREATE TABLE rcqueue (
    queuekey bigserial NOT NULL,
    identifier character varying(255) NOT NULL,
    mdprefix character varying(255) NOT NULL,
    sourceinfo text NOT NULL,
    queuesource character varying(1) NOT NULL
);


ALTER TABLE public.rcqueue OWNER TO {{ easy_proai_db_admin_username }};

--
-- Name: rcrecord; Type: TABLE; Schema: public; Owner: proai_db_admin; Tablespace: 
--

CREATE TABLE rcrecord (
    recordkey bigserial NOT NULL,
    itemkey integer NOT NULL,
    formatkey integer NOT NULL,
    moddate bigint,
    xmlpath character varying(28) NOT NULL
);


ALTER TABLE public.rcrecord OWNER TO {{ easy_proai_db_admin_username }};

--
-- Name: rcset; Type: TABLE; Schema: public; Owner: proai_db_admin; Tablespace: 
--

CREATE TABLE rcset (
    setkey bigserial NOT NULL,
    setspec character varying(255) NOT NULL,
    xmlpath character varying(28) NOT NULL
);


ALTER TABLE public.rcset OWNER TO {{ easy_proai_db_admin_username }};

--
-- Name: rcfailure_pkey; Type: CONSTRAINT; Schema: public; Owner: proai_db_admin; Tablespace: 
--

ALTER TABLE ONLY rcfailure
    ADD CONSTRAINT rcfailure_pkey PRIMARY KEY (identifier, mdprefix);


--
-- Name: rcformat_pkey; Type: CONSTRAINT; Schema: public; Owner: proai_db_admin; Tablespace: 
--

ALTER TABLE ONLY rcformat
    ADD CONSTRAINT rcformat_pkey PRIMARY KEY (formatkey);


--
-- Name: rcitem_pkey; Type: CONSTRAINT; Schema: public; Owner: proai_db_admin; Tablespace: 
--

ALTER TABLE ONLY rcitem
    ADD CONSTRAINT rcitem_pkey PRIMARY KEY (itemkey);


--
-- Name: rcprunable_pkey; Type: CONSTRAINT; Schema: public; Owner: proai_db_admin; Tablespace: 
--

ALTER TABLE ONLY rcprunable
    ADD CONSTRAINT rcprunable_pkey PRIMARY KEY (prunekey);


--
-- Name: rcqueue_pkey; Type: CONSTRAINT; Schema: public; Owner: proai_db_admin; Tablespace: 
--

ALTER TABLE ONLY rcqueue
    ADD CONSTRAINT rcqueue_pkey PRIMARY KEY (queuekey);


--
-- Name: rcrecord_pkey; Type: CONSTRAINT; Schema: public; Owner: proai_db_admin; Tablespace: 
--

ALTER TABLE ONLY rcrecord
    ADD CONSTRAINT rcrecord_pkey PRIMARY KEY (recordkey);


--
-- Name: rcset_pkey; Type: CONSTRAINT; Schema: public; Owner: proai_db_admin; Tablespace: 
--

ALTER TABLE ONLY rcset
    ADD CONSTRAINT rcset_pkey PRIMARY KEY (setkey);


--
-- Name: rcfailure_failcount; Type: INDEX; Schema: public; Owner: proai_db_admin; Tablespace: 
--

CREATE INDEX rcfailure_failcount ON rcfailure USING btree (failcount);


--
-- Name: rcitem_identifier; Type: INDEX; Schema: public; Owner: proai_db_admin; Tablespace: 
--

CREATE INDEX rcitem_identifier ON rcitem USING btree (identifier);


--
-- Name: rcmembership_recordkey; Type: INDEX; Schema: public; Owner: proai_db_admin; Tablespace: 
--

CREATE INDEX rcmembership_recordkey ON rcmembership USING btree (recordkey);


--
-- Name: rcmembership_setkey; Type: INDEX; Schema: public; Owner: proai_db_admin; Tablespace: 
--

CREATE INDEX rcmembership_setkey ON rcmembership USING btree (setkey);


--
-- Name: rcrecord_formatkey; Type: INDEX; Schema: public; Owner: proai_db_admin; Tablespace: 
--

CREATE INDEX rcrecord_formatkey ON rcrecord USING btree (formatkey);


--
-- Name: rcrecord_itemkey; Type: INDEX; Schema: public; Owner: proai_db_admin; Tablespace: 
--

CREATE INDEX rcrecord_itemkey ON rcrecord USING btree (itemkey);


--
-- Name: rcrecord_moddate; Type: INDEX; Schema: public; Owner: proai_db_admin; Tablespace: 
--

CREATE INDEX rcrecord_moddate ON rcrecord USING btree (moddate);


--
-- Name: rcrecord_xmlpath; Type: INDEX; Schema: public; Owner: proai_db_admin; Tablespace: 
--

CREATE INDEX rcrecord_xmlpath ON rcrecord USING btree (xmlpath);


--
-- Name: rcset_xmlpath; Type: INDEX; Schema: public; Owner: proai_db_admin; Tablespace: 
--

CREATE INDEX rcset_xmlpath ON rcset USING btree (xmlpath);


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

