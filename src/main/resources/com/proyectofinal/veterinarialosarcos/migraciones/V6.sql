-- V6: se eliminan columnas y tablas que ya no corresponden a ninguna entidad.
--
-- paciente: los textos clínicos ahora viven en cada consulta (ver V5 y R2), y "genero"
--           quedó duplicado por "sexo".
-- usuarios: datos de perfil que la entidad Usuario ya no maneja.
-- citas / vacunas: el sistema no gestiona citas, y el catálogo de vacunas ahora está en
--           la tabla "vacuna" (singular).
ALTER TABLE IF EXISTS paciente
    DROP COLUMN IF EXISTS genero,
    DROP COLUMN IF EXISTS historial_vacunas,
    DROP COLUMN IF EXISTS historial_desparasitacion,
    DROP COLUMN IF EXISTS control_parasitos,
    DROP COLUMN IF EXISTS observaciones;

ALTER TABLE IF EXISTS usuarios
    DROP COLUMN IF EXISTS sexo,
    DROP COLUMN IF EXISTS telefono,
    DROP COLUMN IF EXISTS cedula,
    DROP COLUMN IF EXISTS especialidad,
    DROP COLUMN IF EXISTS num_consultorio;

DROP TABLE IF EXISTS citas;
DROP TABLE IF EXISTS vacunas;
