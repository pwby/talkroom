-- auto-generated definition
create table article
(
  id      int auto_increment
    primary key,
  poster  varchar(32) null,
  time    varchar(19) null,
  content text        null
)
  engine = InnoDB
  collate = utf8_unicode_ci;
