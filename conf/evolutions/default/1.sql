# --- !Ups

CREATE TABLE "room" (
    "id_room" BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    "title" VARCHAR(255) NOT NULL,
    "description" VARCHAR(255) NOT NULL,
    "adult_capacity" INT,
    "children_capacity" INT,
    "private_bathroom" BOOLEAN
);

# --- !Downs

DROP TABLE "room" IF EXISTS;