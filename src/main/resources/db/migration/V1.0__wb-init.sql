CREATE SCHEMA IF NOT EXISTS wb_list;

create table wb_list.raws
(
    id CHARACTER VARYING PRIMARY KEY,
    value CHARACTER VARYING NOT NULL
);
