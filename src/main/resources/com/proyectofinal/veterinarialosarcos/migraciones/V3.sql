-- V3: cada usuario ve solo sus pacientes. Los pacientes registrados antes de este
-- cambio no tenían usuario asignado; se asignan al primer usuario registrado
-- (el de menor id) para que no queden invisibles.
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'paciente')
       AND EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'usuarios') THEN
        UPDATE paciente
        SET usuario_id = (SELECT id FROM usuarios ORDER BY id LIMIT 1)
        WHERE usuario_id IS NULL;
    END IF;
END $$;
