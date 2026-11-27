-- R2 (se ejecuta en cada arranque, después de Hibernate).
--
-- 1) Catálogo de vacunas: agrega las vacunas básicas por especie si todavía no existen.
--    Se pueden agregar más filas aquí; las existentes no se duplican.
INSERT INTO vacuna (nombre, especie)
SELECT v.nombre, v.especie
FROM (VALUES
    ('Parvovirus', 'PERRO'),
    ('Moquillo', 'PERRO'),
    ('Puppy (Parvovirus + Moquillo)', 'PERRO'),
    ('Múltiple (DHPPi + L)', 'PERRO'),
    ('Leptospirosis', 'PERRO'),
    ('Bordetella (tos de las perreras)', 'PERRO'),
    ('Rabia', 'PERRO'),
    ('Triple felina', 'GATO'),
    ('Leucemia felina', 'GATO'),
    ('Rabia', 'GATO')
) AS v (nombre, especie)
WHERE NOT EXISTS (SELECT 1 FROM vacuna x WHERE x.nombre = v.nombre AND x.especie = v.especie);

-- 2) Los textos clínicos que antes vivían en "expediente" (vacunas, desparasitación,
--    parásitos y observaciones) pasan a una consulta del paciente para no perderlos.
--    Después se borran esas columnas, así que esto solo ocurre una vez.
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_schema = current_schema() AND table_name = 'expediente'
                 AND column_name = 'historial_vacunas') THEN

        INSERT INTO consulta (paciente_id, fecha, motivo_consulta, comentarios)
        SELECT e.paciente_id,
               current_date,
               'Historial previo (migrado del expediente anterior)',
               concat_ws(E'\n\n',
                   CASE WHEN nullif(trim(e.historial_vacunas), '') IS NOT NULL
                        THEN 'Historial de vacunación:' || E'\n' || e.historial_vacunas END,
                   CASE WHEN nullif(trim(e.historial_desparasitacion), '') IS NOT NULL
                        THEN 'Historial de desparasitación:' || E'\n' || e.historial_desparasitacion END,
                   CASE WHEN nullif(trim(e.control_parasitos), '') IS NOT NULL
                        THEN 'Control de parásitos:' || E'\n' || e.control_parasitos END,
                   CASE WHEN nullif(trim(e.observaciones), '') IS NOT NULL
                        THEN 'Observaciones:' || E'\n' || e.observaciones END)
        FROM expediente e
        WHERE nullif(trim(e.historial_vacunas), '') IS NOT NULL
           OR nullif(trim(e.historial_desparasitacion), '') IS NOT NULL
           OR nullif(trim(e.control_parasitos), '') IS NOT NULL
           OR nullif(trim(e.observaciones), '') IS NOT NULL;

        ALTER TABLE expediente
            DROP COLUMN historial_vacunas,
            DROP COLUMN historial_desparasitacion,
            DROP COLUMN control_parasitos,
            DROP COLUMN observaciones;
    END IF;
END $$;
