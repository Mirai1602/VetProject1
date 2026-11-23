-- V1: "especie" pasó de guardarse como número (0/1) a texto (PERRO/GATO)
-- y se agregó la columna "fechanacimientoconocida".
-- Solo hace cambios si la base tiene la estructura antigua.
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'paciente' AND column_name = 'especie'
                 AND data_type = 'smallint') THEN
        ALTER TABLE paciente DROP CONSTRAINT IF EXISTS paciente_especie_check;
        ALTER TABLE paciente ALTER COLUMN especie TYPE varchar(255)
            USING CASE especie WHEN 0 THEN 'PERRO' WHEN 1 THEN 'GATO' END;
    END IF;
END $$;

ALTER TABLE IF EXISTS paciente ADD COLUMN IF NOT EXISTS fechanacimientoconocida boolean;
