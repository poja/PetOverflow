connect 'jdbc:derby:PetOvDb';

CREATE TABLE petOwner (username VARCHAR(10), password VARCHAR(8), nickname VARCHAR(20), description LONG VARCHAR, photo LONG VARCHAR, phone VARCHAR(20));

INSERT INTO petOwner VALUES ('Yishai', 'Gronich', 'poja', 'cool guy writing code', '', '+972527567813');

SELECT username,password FROM petOwner;

