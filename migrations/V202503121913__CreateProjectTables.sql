create table "projects" (
  "project_id" serial primary key,
  "project_name" varchar(64) not null,
  "owner" int not null references users(user_id) on update cascade
);

create table "project_invites" (
  "project_invite_id" serial primary key,
  "project_id" integer not null references projects(project_id) on delete cascade on update cascade,
  "user_id" integer not null references users(user_id) on delete cascade on update cascade,
  "accepted" boolean not null,
  unique(project_id, user_id)
);

create table "f_ups" (
  "f_up_id" serial primary key,
  "project_id" integer not null references projects(project_id) on delete cascade on update cascade,
  "f_up_name" varchar(128) not null,
  "description" varchar(255) not null
);

create table "votes" (
  "vote_id" serial primary key,
  "reporter_id" integer not null references users(user_id) on delete cascade on update cascade,
  "accused_id" integer not null references users(user_id) on delete cascade on update cascade,
  "f_up_id" integer not null references f_ups(f_up_id) on delete cascade on update cascade,
  "score" integer not null check (score between 1 and 5),
  unique(f_up_id, reporter_id, accused_id)
);
