DROP TABLE comment IF EXISTS;

CREATE TABLE comment  (
         id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
         postId BIGINT,
         name VARCHAR(100),
         email VARCHAR(100),
         body VARCHAR(512)
);