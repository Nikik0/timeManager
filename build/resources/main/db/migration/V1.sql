create table Users(
                      id SERIAL PRIMARY KEY ,
                      name varchar(32),
                      password varchar(32),
                      blocked boolean NOT NULL default false,
                      role varchar(32),
                      created_at TIMESTAMP,
                      updated_at TIMESTAMP
);
create table Events(
                       id serial primary key,
                       name varchar(32),
                       start_time timestamp,
                       duration bigint,
                       short_description varchar(256),
                       full_description varchar(1024),
                       created_at timestamp,
                       changed_at timestamp,
                       owner_id bigint references Users(id)
);