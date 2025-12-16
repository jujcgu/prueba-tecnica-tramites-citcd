-- Table: public.tipo_tramite_documento

-- DROP TABLE IF EXISTS public.tipo_tramite_documento;

CREATE TABLE IF NOT EXISTS public.tipo_tramite_documento
(
    tipo_tramite_id bigint NOT NULL,
    tipo_documento_id bigint NOT NULL,
    es_obligatorio boolean NOT NULL,
    orden integer,
    CONSTRAINT tipo_tramite_documento_pkey PRIMARY KEY (tipo_tramite_id, tipo_documento_id),
    CONSTRAINT tipo_tramite_documento_tipo_documento_id_fkey FOREIGN KEY (tipo_documento_id)
        REFERENCES public.tipo_documento (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT tipo_tramite_documento_tipo_tramite_id_fkey FOREIGN KEY (tipo_tramite_id)
        REFERENCES public.tipo_tramite (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.tipo_tramite_documento
    OWNER to demo;