SET schema 'notifygatewaysvc';

CREATE SEQUENCE messageseq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    MAXVALUE 999999999999
    CACHE 1;


CREATE TABLE "message" (
    messagepk bigint NOT NULL,
    id uuid NOT NULL,
    notificationid character varying(128),
    optlockversion integer DEFAULT 0
);

ALTER TABLE ONLY "message"
    ADD CONSTRAINT message_pkey PRIMARY KEY (messagepk);

CREATE UNIQUE INDEX message_id_idx ON message (id);
