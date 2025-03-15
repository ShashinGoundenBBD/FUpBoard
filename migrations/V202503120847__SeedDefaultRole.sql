
do $$
declare
    admin_id integer;
    projects_read_all integer;
    projects_write_all integer;
    projects_delete_all integer;
begin
    INSERT INTO "roles" VALUES (DEFAULT, 'admin') RETURNING "role_id" INTO admin_id;
    INSERT INTO "permissions" VALUES (DEFAULT, 'projects::read::all') RETURNING "permission_id" INTO projects_read_all;
    INSERT INTO "permissions" VALUES (DEFAULT, 'projects::write::all') RETURNING "permission_id" INTO projects_write_all;
    INSERT INTO "permissions" VALUES (DEFAULT, 'projects::delete::all') RETURNING "permission_id" INTO projects_delete_all;
end;
$$;
