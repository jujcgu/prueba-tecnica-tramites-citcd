-- Table: public.tipo_tramite_documento

-- DROP TABLE IF EXISTS public.tipo_tramite_documento;

CREATE TABLE IF NOT EXISTS public.tipo_tramite_documento
(
    tipo_tramite_id bigint NOT NULL,
    tipo_documento_id bigint NOT NULL,
    es_obligatorio boolean NOT NULL DEFAULT false,
    orden integer,
    cantidad_minima integer NOT NULL DEFAULT 0,
    cantidad_maxima integer NOT NULL DEFAULT 1,
    mime_permitidos jsonb,
    tamano_max_mb integer,
    CONSTRAINT tipo_tramite_documento_pkey PRIMARY KEY (tipo_tramite_id, tipo_documento_id),
    CONSTRAINT tipo_tramite_documento_tipo_documento_id_fkey FOREIGN KEY (tipo_documento_id)
        REFERENCES public.tipo_documento (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT tipo_tramite_documento_tipo_tramite_id_fkey FOREIGN KEY (tipo_tramite_id)
        REFERENCES public.tipo_tramite (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT ttd_cardinalidad_chk CHECK (cantidad_minima >= 0 AND cantidad_maxima >= 1 AND cantidad_maxima >= cantidad_minima AND (NOT es_obligatorio OR cantidad_minima >= 1) AND (tamano_max_mb IS NULL OR tamano_max_mb > 0)),
    CONSTRAINT ttd_orden_pos_chk CHECK (orden IS NULL OR orden >= 1),
    CONSTRAINT ttd_mime_json_chk CHECK (mime_permitidos IS NULL OR jsonb_typeof(mime_permitidos) = 'array'::text)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.tipo_tramite_documento
    OWNER to demo;
-- Index: ix_ttd_tipo_documento

-- DROP INDEX IF EXISTS public.ix_ttd_tipo_documento;

CREATE INDEX IF NOT EXISTS ix_ttd_tipo_documento
    ON public.tipo_tramite_documento USING btree
    (tipo_documento_id ASC NULLS LAST)
    WITH (fillfactor=100, deduplicate_items=True)
    TABLESPACE pg_default;
-- Index: ix_ttd_tram_obl_orden

-- DROP INDEX IF EXISTS public.ix_ttd_tram_obl_orden;

CREATE INDEX IF NOT EXISTS ix_ttd_tram_obl_orden
    ON public.tipo_tramite_documento USING btree
    (tipo_tramite_id ASC NULLS LAST, es_obligatorio ASC NULLS LAST, orden ASC NULLS LAST)
    WITH (fillfactor=100, deduplicate_items=True)
    TABLESPACE pg_default;
-- Index: tipo_tramite_documento_tipo_tramite_id_index

-- DROP INDEX IF EXISTS public.tipo_tramite_documento_tipo_tramite_id_index;

CREATE INDEX IF NOT EXISTS tipo_tramite_documento_tipo_tramite_id_index
    ON public.tipo_tramite_documento USING btree
    (tipo_tramite_id ASC NULLS LAST)
    WITH (fillfactor=100, deduplicate_items=True)
    TABLESPACE pg_default;
-- Index: ux_ttd_tram_orden

-- DROP INDEX IF EXISTS public.ux_ttd_tram_orden;

CREATE UNIQUE INDEX IF NOT EXISTS ux_ttd_tram_orden
    ON public.tipo_tramite_documento USING btree
    (tipo_tramite_id ASC NULLS LAST, orden ASC NULLS LAST)
    WITH (fillfactor=100, deduplicate_items=True)
    TABLESPACE pg_default
    WHERE orden IS NOT NULL;