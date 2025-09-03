INSERT INTO members (email, password, name, role)
VALUES ('user@email.com', 'user', 'user', 'USER'),
       ('admin@email.com', 'admin', 'admin', 'ADMIN');

INSERT INTO time_slots (time)
VALUES ('10:00'), ('11:00'), ('12:00'), ('13:00'), ('14:00'), ('15:00'), ('16:00'), ('17:00'), ('18:00');

INSERT INTO reservations (date, time_slot_id, member_id)
VALUES (CURRENT_DATE + 3, 1, 1),
    (CURRENT_DATE + 3, 2, 1),
    (CURRENT_DATE + 2, 5, 1),
    (CURRENT_DATE + 2, 6, 1),
    (CURRENT_DATE + 3, 4, 2),
    (CURRENT_DATE + 3, 3, 2),
    (CURRENT_DATE + 2, 1, 2),
    (CURRENT_DATE + 2, 2, 2),
    (CURRENT_DATE + 1, 5, 2);




