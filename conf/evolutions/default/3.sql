# --- !Ups

CREATE TABLE "booking" (
    "id_booking" BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    "check_in_date" DATE NOT NULL,
    "check_out_date" DATE NOT NULL,
    "check_in_time" VARCHAR NOT NULL,
    "check_out_time" VARCHAR NOT NULL,
    "id_customer" BIGINT NOT NULL,
    "id_room" BIGINT NOT NULL,
    "created" TIMESTAMP NOT NULL,

    CONSTRAINT "fk_customer" FOREIGN KEY ("id_customer") REFERENCES "customer" ("id_customer") ON DELETE CASCADE,
    CONSTRAINT "fk_room" FOREIGN KEY ("id_room") REFERENCES "room" ("id_room") ON DELETE CASCADE
);

# --- !Downs

DROP TABLE "booking" IF EXISTS;