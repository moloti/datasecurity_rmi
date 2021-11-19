CREATE TABLE IF NOT EXISTS users (
    user_id INT NOT NULL primary key,
    user_name VARCHAR (250) NOT NULL,
    password VARCHAR (250) NOT NULL
);
CREATE TABLE IF NOT EXISTS transactions (
    transaction_id INT NOT NULL primary key,
    transaction_name VARCHAR (
        250
    ) NOT NULL
);
CREATE TABLE IF NOT EXISTS roles (
    role_id INT NOT NULL primary key,
    role_name VARCHAR (
        250
    ) NOT NULL
);
CREATE TABLE IF NOT EXISTS user_role (
    user_role_id INT NOT NULL primary key,
    role_id INT NOT NULL,
    user_id INT NOT NULL,
    constraint fk_role foreign key(role_id) references roles(role_id),
    constraint fk_user foreign key(user_id) references users(user_id)
);
CREATE TABLE IF NOT EXISTS role_transaction (
    role_action_id INT NOT NULL primary key,
    role_id INT NOT NULL,
    transaction_id INT NOT NULL,
    constraint fk_role foreign key(role_id) references roles(role_id),
    constraint fk_transaction foreign key(transaction_id) references transactions(transaction_id)
);
CREATE TABLE IF NOT EXISTS acl (
    acl_id INT NOT NULL primary key,
    user_id INT NOT NULL,
    print BOOLEAN NOT NULL,
    queue BOOLEAN NOT NULL,
    topQueue BOOLEAN NOT NULL,
    START BOOLEAN NOT NULL,
    stop BOOLEAN NOT NULL,
    restart BOOLEAN NOT NULL,
    status BOOLEAN NOT NULL,
    readConfig BOOLEAN NOT NULL,
    setConfig BOOLEAN NOT NULL,
    manageEmployee BOOLEAN NOT NULL,
    constraint fk_user foreign key(user_id) references users(user_id)
);
