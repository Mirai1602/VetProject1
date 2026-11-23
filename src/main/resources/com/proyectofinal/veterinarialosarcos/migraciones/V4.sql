-- V4: el sistema ya no maneja roles; se elimina la columna "rol" de usuarios.
ALTER TABLE IF EXISTS usuarios DROP COLUMN IF EXISTS rol;
