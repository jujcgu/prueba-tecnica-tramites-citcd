-- Homologación -> Documento de identidad + Certificado académico
INSERT INTO public.tipo_tramite_documento (tipo_tramite_id, tipo_documento_id, orden, es_obligatorio)
SELECT tt.id, td.id, v.orden, v.es_obligatorio
FROM (VALUES
  ('TT-001','TD-001',1,true),
  ('TT-001','TD-002',2,true),
  ('TT-001','TD-004',3,false)
) AS v(tramite_codigo, doc_codigo, orden, es_obligatorio)
JOIN public.tipo_tramite tt ON tt.codigo = v.tramite_codigo
JOIN public.tipo_documento td ON td.codigo = v.doc_codigo
ON CONFLICT (tipo_tramite_id, tipo_documento_id) DO NOTHING;

-- Cancelación de semestre -> Documento de identidad + Formulario de cancelación
INSERT INTO public.tipo_tramite_documento (tipo_tramite_id, tipo_documento_id, orden, es_obligatorio)
SELECT tt.id, td.id, v.orden, v.es_obligatorio
FROM (VALUES
  ('TT-002','TD-001',1,true),
  ('TT-002','TD-003',2,true),
  ('TT-002','TD-007',3,false)
) AS v(tramite_codigo, doc_codigo, orden, es_obligatorio)
JOIN public.tipo_tramite tt ON tt.codigo = v.tramite_codigo
JOIN public.tipo_documento td ON td.codigo = v.doc_codigo
ON CONFLICT (tipo_tramite_id, tipo_documento_id) DO NOTHING;
