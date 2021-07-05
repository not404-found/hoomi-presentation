insert into categories_tags (category_id, tag_id)
values (
    (select id from categories where category_name = 'PUBG'),
    (select id from tags where tag_name = 'GAME')
),
(
    (select id from categories where category_name = 'PUBG'),
    (select id from tags where tag_name = 'SHOOTER')
);

insert into categories_tags (category_id, tag_id)
values (
    (select id from categories where category_name = 'Vainglory'),
    (select id from tags where tag_name = 'GAME')
),
(
    (select id from categories where category_name = 'Vainglory'),
    (select id from tags where tag_name = 'MMORPG')
);

insert into categories_tags (category_id, tag_id)
values (
    (select id from categories where category_name = 'Minecraft'),
    (select id from tags where tag_name = 'GAME')
),
(
    (select id from categories where category_name = 'Minecraft'),
    (select id from tags where tag_name = 'ADVENTURE')
),
(
    (select id from categories where category_name = 'Minecraft'),
    (select id from tags where tag_name = 'MMORPG')
);

insert into categories_tags (category_id, tag_id)
values (
    (select id from categories where category_name = 'Hearthstone'),
    (select id from tags where tag_name = 'GAME')
),
(
    (select id from categories where category_name = 'Hearthstone'),
    (select id from tags where tag_name = 'CARD_AND_BOARD')
);

insert into categories_tags (category_id, tag_id)
values (
    (select id from categories where category_name = 'Call of Duty'),
    (select id from tags where tag_name = 'GAME')
),
(
    (select id from categories where category_name = 'Call of Duty'),
    (select id from tags where tag_name = 'SHOOTER')
),
(
     (select id from categories where category_name = 'Call of Duty'),
     (select id from tags where tag_name = 'ADVENTURE')
);

insert into categories_tags (category_id, tag_id)
values (
    (select id from categories where category_name = 'World of Tanks'),
    (select id from tags where tag_name = 'GAME')
),
(
    (select id from categories where category_name = 'World of Tanks'),
    (select id from tags where tag_name = 'SHOOTER')
);

