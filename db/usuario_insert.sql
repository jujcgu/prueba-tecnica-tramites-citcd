CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Inserta múltiples usuarios con bcrypt (pgcrypto: crypt + gen_salt('bf'))
INSERT INTO public.usuario (email, password_hash, rol, es_activo, creado_en, actualizado_en, last_login_at)
VALUES
-- Admines
('admin1@miapp.gov',  crypt('Admin1!2025',  gen_salt('bf')), 'ROLE_ADMINISTRATIVO',       TRUE, now(), now(), now() - interval '1 day'),
('admin2@miapp.gov',  crypt('Admin2!2025',  gen_salt('bf')), 'ROLE_ADMINISTRATIVO',       TRUE, now(), now(), NULL),

-- Funcionarios / Agentes
('agente1@miapp.gov', crypt('Agente1#2025', gen_salt('bf')), 'ROLE_FUNCIONARIO', TRUE, now(), now(), now() - interval '3 days'),
('agente2@miapp.gov', crypt('Agente2#2025', gen_salt('bf')), 'ROLE_FUNCIONARIO', TRUE, now(), now(), NULL),
('agente3@miapp.gov', crypt('Agente3#2025', gen_salt('bf')), 'ROLE_FUNCIONARIO', TRUE, now(), now(), now() - interval '6 hours'),
('supervisor1@miapp.gov', crypt('Sup3rV!2025', gen_salt('bf')), 'ROLE_FUNCIONARIO', TRUE, now(), now(), NULL),

-- Ciudadanos
('ciudadano1@mail.com', crypt('Ciudadano1*', gen_salt('bf')), 'ROLE_CIUDADANO', TRUE, now(), now(), NULL),
('ciudadano2@mail.com', crypt('Ciudadano2*', gen_salt('bf')), 'ROLE_CIUDADANO', TRUE, now(), now(), now() - interval '10 days'),
('ciudadano3@mail.com', crypt('Ciudadano3*', gen_salt('bf')), 'ROLE_CIUDADANO', TRUE, now(), now(), NULL),
('ciudadano4@mail.com', crypt('Ciudadano4*', gen_salt('bf')), 'ROLE_CIUDADANO', TRUE, now(), now(), NULL),

-- Usuarios desactivados de ejemplo
('suspendido1@miapp.gov', crypt('Suspendido1?', gen_salt('bf')), 'ROLE_FUNCIONARIO', FALSE, now(), now(), NULL),
('inactivo1@mail.com',    crypt('Inactivo1?',   gen_salt('bf')), 'ROLE_CIUDADANO',  FALSE, now(), now(), NULL);