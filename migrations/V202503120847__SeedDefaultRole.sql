
WITH "default_role_id" AS (
  INSERT INTO "roles" VALUES (DEFAULT, 'default') RETURNING "role_id"
), "read_jwt_permission_id" AS (
  INSERT INTO "permissions" VALUES (DEFAULT, 'jwt::read::mine') RETURNING "permission_id"
), "read_user_permission_id" AS (
  INSERT INTO "permissions" VALUES (DEFAULT, 'user::read::me') RETURNING "permission_id"
), "write_user_permission_id" AS (
  INSERT INTO "permissions" VALUES (DEFAULT, 'user::write::me') RETURNING "permission_id"
)

INSERT INTO "role_permissions" VALUES (
  (SELECT "role_id" FROM "default_role_id"),
  (SELECT "permission_id" FROM "read_jwt_permission_id")
);

INSERT INTO "role_permissions" VALUES (
  (SELECT "role_id" FROM "default_role_id"),
  (SELECT "permission_id" FROM "read_user_permission_id")
);

INSERT INTO "role_permissions" VALUES (
  (SELECT "role_id" FROM "default_role_id"),
  (SELECT "permission_id" FROM "write_user_permission_id")
);
