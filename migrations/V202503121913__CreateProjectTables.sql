create table "projects" (
  "project_id" serial primary key,
  "project_name" varchar(64) not null,
  "owner" int references users(user_id)
);

create table "project_invites" (
  "project_id" integer not null references projects(project_id),
  "user_id" integer not null references users(user_id),
  "accepted" boolean,
  primary key(project_id, user_id)
);

create table "f_ups" (
  "f_up_id" serial primary key,
  "project_id" integer not null references projects(project_id),
  "f_up_name" varchar(128) not null,
  "description" varchar(255) not null
);

create table "repositories" (
  "repository_id" serial primary key,
  "project_id" integer not null references projects(project_id),
  "repository_name" varchar(64) not null,
  "repository_url" varchar(64) not null
);

create table "commits" (
  "commit_id" serial primary key,
  "repository_id" integer not null references repositories(repository_id),
  "f_up_id" integer not null references f_ups(f_up_id),
  "commit_hash" varchar(128) not null
);

create table "votes" (
  "vote_id" serial primary key,
  "reporter_id" integer not null references users(user_id),
  "accused_id" integer not null references users(user_id),
  "f_up_id" integer not null references f_ups(f_up_id),
  "score" integer not null check (score between 1 and 5)
);
