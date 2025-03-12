create table "users" (
  "user_id" serial primary key,
  "google_id" varchar(255) unique,
  "email" varchar(255) unique not null,
  "email_verified" boolean not null,
  "username" varchar(64) unique not null,
  "created_at" timestamp with time zone not null default now()
);

create table "roles" (
  "role_id" serial primary key,
  "role_name" varchar(32) unique not null
);

create table "permissions" (
  "permission_id" serial primary key,
  "permission_name" varchar(32) unique not null
);

create table "user_roles" (
  "user_id" integer not null references users(user_id) on delete cascade on update cascade,
  "role_id" integer not null references roles(role_id) on delete cascade on update cascade,
  primary key (user_id, role_id)
);

create table "role_permissions" (
  "role_id" integer not null references roles(role_id) on delete cascade on update cascade,
  "permission_id" integer not null references permissions(permission_id) on delete cascade on update cascade,
  primary key (user_id, role_id)
);
