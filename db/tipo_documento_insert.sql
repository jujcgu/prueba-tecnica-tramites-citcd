INSERT INTO public.tipo_documento (codigo, nombre, descripcion, es_activo, creado_en) VALUES
('TD-001','Documento de identidad','Cédula/ti o documento equivalente.', true, CURRENT_DATE),
('TD-002','Certificado académico','Historial o certificado expedido por la institución.', true, CURRENT_DATE),
('TD-003','Formulario de cancelación','Formato diligenciado para cancelar semestre.', true, CURRENT_DATE),
('TD-004','Carta de solicitud','Carta explicando el motivo de la solicitud.', true, CURRENT_DATE),
('TD-005','Recibo de pago','Soporte de pago de derechos/servicios.', true, CURRENT_DATE),
('TD-006','Paz y salvo','Paz y salvo académico/financiero.', true, CURRENT_DATE),
('TD-007','Soporte médico','Incapacidad o certificado médico (si aplica).', true, CURRENT_DATE),
('TD-008','Soporte de traslado','Admisión/aceptación u otros soportes de traslado.', true, CURRENT_DATE)
ON CONFLICT (codigo) DO NOTHING;
