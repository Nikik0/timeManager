drop table Users cascade ;
drop table Events;
create table Users(
                      id SERIAL PRIMARY KEY ,
                      name varchar(32) not null,
                      password varchar(32) NOT NULL,
                      blocked boolean NOT NULL default false,
                      role varchar(32) not null,
                      created_at TIMESTAMP,
                      updated_at TIMESTAMP
);
create table Events(
                       id serial primary key,
                       name varchar(32) not null ,
                       start_time timestamp not null ,
                       duration bigint not null ,
                       short_description varchar(256),
                       full_description varchar(1024),
                       created_at timestamp,
                       changed_at timestamp,
                       owner_id bigint references Users(id)
);