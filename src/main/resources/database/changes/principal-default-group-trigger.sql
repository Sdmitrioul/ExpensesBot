DROP TRIGGER IF EXISTS create_personal_group_trigger ON principals;

DROP FUNCTION IF EXISTS create_personal_group_function();

CREATE OR REPLACE FUNCTION create_personal_group_function()
    RETURNS TRIGGER AS
$$
DECLARE
    new_group_id BIGINT;
BEGIN
    INSERT INTO groups (name, owner_id)
    VALUES ('Personal', NEW.id)
    RETURNING id INTO new_group_id;

    UPDATE principals
    SET active_group_id = new_group_id
    WHERE id = NEW.id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER create_personal_group_trigger
    AFTER INSERT
    ON principals
    FOR EACH ROW
EXECUTE FUNCTION create_personal_group_function();