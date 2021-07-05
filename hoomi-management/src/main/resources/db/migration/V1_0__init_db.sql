create table if not exists users
(
    id            varchar(255) primary key default (uuid()),
    email         varchar(255) unique not null,
    phone_number  varchar(255) unique not null,
    username      varchar(255) unique not null,
    password      varchar(255)        not null,
    date_of_birth date                not null,
    status        varchar(255)        not null
);


create table if not exists tags
(
    id       varchar(255) primary key default (uuid()),
    tag_name varchar(255) not null unique
);

create table if not exists categories
(
    id                   varchar(255) primary key default (uuid()),
    category_name        varchar(255) not null unique,
    image_link           varchar(255),
    online_viewers_count int                      default 0
);

create table if not exists categories_tags
(
    category_id varchar(255) not null,
    tag_id      varchar(255) not null,
    primary key (category_id, tag_id),
    unique (category_id, tag_id),

    foreign key (category_id) references categories (id),
    foreign key (tag_id) references tags (id)
);

create table if not exists channels
(
    id                varchar(255) primary key default (uuid()),
    channel_name      varchar(255) unique not null,
    image_link        varchar(255),
    subscribers_count int                      default 0,
    is_live           boolean                  default false,
    user_id           varchar(255)        not null unique,
    foreign key (user_id) references users (id)

);

create table if not exists streams
(
    id                   varchar(255) primary key default (uuid()),
    stream_name          varchar(255) not null,
    preview_link         varchar(255) not null,
    online_viewers_count int                      default 0,
    views_count          int                      default 0,
    scheduled_time       datetime                 default now(),
    is_online            boolean                  default false,
    channel_id           varchar(255) not null,
    category_id             varchar(255) not null,
    foreign key (channel_id) references channels (id),
    foreign key (category_id) references categories (id)
);



create table if not exists channels_subscribers
(
    channel_id varchar(255) not null,
    user_id    varchar(255) not null,
    primary key (user_id, channel_id),
    unique (user_id, channel_id),
    foreign key (user_id) references users (id),
    foreign key (channel_id) references channels (id)
);

alter table users
    add
        viewing_stream_id varchar(255),
    add
        foreign key (viewing_stream_id) references streams (id);
