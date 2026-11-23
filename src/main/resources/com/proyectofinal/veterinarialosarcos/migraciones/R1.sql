-- R1 (se ejecuta en cada arranque): quita las restricciones CHECK que Hibernate genera
-- para las columnas enum (por ejemplo paciente_especie_check). Hibernate no las
-- actualiza cuando se agrega un valor nuevo al enum (p. ej. CONEJO en Especie),
-- y entonces el INSERT fallaría. Java ya garantiza que solo se guarden valores válidos.
DO $$
DECLARE
    r record;
BEGIN
    FOR r IN
        SELECT c.conrelid::regclass AS tabla, c.conname
        FROM pg_constraint c
        JOIN pg_namespace n ON n.oid = c.connamespace
        WHERE c.contype = 'c'
          AND n.nspname = current_schema()
          AND c.conname LIKE '%\_check'
          AND (pg_get_constraintdef(c.oid) LIKE '%= ANY (%ARRAY[%'
               OR pg_get_constraintdef(c.oid) ~ '>= 0\) AND \(.* <= [0-9]+')
    LOOP
        EXECUTE format('ALTER TABLE %s DROP CONSTRAINT %I', r.tabla, r.conname);
    END LOOP;
END $$;
