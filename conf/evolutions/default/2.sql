# --- !Ups

CREATE TABLE "customer" (
    "id_customer" BIGINT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    "first_name" VARCHAR(100) NOT NULL,
    "last_name" VARCHAR(100) NOT NULL,
    "email" VARCHAR(100) NOT NULL,
    "phone" VARCHAR(100) NOT NULL
);

# --- !Downs

DROP TABLE "customer" IF EXISTS;