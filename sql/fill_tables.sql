CREATE EXTENSION pgcrypto;

INSERT INTO transactions(transaction_id, transaction_name)
values (
           gen_random_uuid (),
           'print'
       ),
       (
           gen_random_uuid (),
           'queue'
       ),
       (
           gen_random_uuid (),
           'topQueue'
       ),
       (
           gen_random_uuid (),
           'start'
       ),
       (
           gen_random_uuid (),
           'stop'
       ),
       (
           gen_random_uuid (),
           'restart'
       ),
       (
           gen_random_uuid (),
           'status'
       ),
       (
           gen_random_uuid (),
           'readConfig'
       ),
       (
           gen_random_uuid (),
           'setConfig'
       );

INSERT INTO roles(role_id, role_name)
VALUES (
           gen_random_uuid (),
           'manager'
       ),
       (
           gen_random_uuid (),
           'technician'
       ),
       (
           gen_random_uuid (),
           'powerUser'
       ),
       (
           gen_random_uuid (),
           'user'
       );

