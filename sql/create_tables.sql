CREATE TABLE IF NOT EXISTS users
(
    user_id
    varchar
(
    250
) NOT NULL,
    password
    varchar
(
    250
) NOT NULL,
    PRIMARY KEY
(
    user_id
)
    );

CREATE TABLE IF NOT EXISTS transactions
(
    transaction_id INT NOT NULL,
    transaction_name varchar
(
    250
) NOT NULL,
    PRIMARY KEY
(
    transaction_id
)
);

CREATE TABLE IF NOT EXISTS user_role
(
    user_role_id INT NOT NULL,
    role_id INT NOT NULL,
    user_id varchar
(
    250
) NOT NULL,
    PRIMARY KEY
(
    user_role_id
)
    CONSTRAINT fk_role
    FOREIGN KEY(role_id)
    REFERENCES roles(role_id),
    CONSTRAINT fk_user
    FOREIGN KEY(user_id)
    REFERENCES users(user_id),
);

CREATE TABLE IF NOT EXISTS role_transaction
(
    role_action_id INT NOT NULL,
    role_id INT NOT NULL,
    transaction_id INT NOT NULL,
    PRIMARY KEY
(
    role_transaction_id
)
    CONSTRAINT fk_role
    FOREIGN KEY(role_id)
    REFERENCES roles(role_id),
    CONSTRAINT fk_transaction
    FOREIGN KEY(transaction_id)
    REFERENCES transactions(transaction_id),
);

CREATE TABLE IF NOT EXISTS role_transaction
(
    role_action_id INT NOT NULL,
    role_id INT NOT NULL,
    transaction_id INT NOT NULL,
    PRIMARY KEY
(
    role_transaction_id
)
    CONSTRAINT fk_role
    FOREIGN KEY(role_id)
    REFERENCES roles(role_id),
    CONSTRAINT fk_transaction
    FOREIGN KEY(transaction_id)
    REFERENCES transactions(transaction_id),
);




CREATE TABLE IF NOT EXISTS acl (
    acl_id INT NOT NULL,
    user_id
    varchar
(
    250
) NOT NULL,
    print BOOLEAN NOT NULL,
    queue BOOLEAN NOT NULL,
    topQueue BOOLEAN NOT NULL,
    start BOOLEAN NOT NULL,
    stop BOOLEAN NOT NULL,
    restart BOOLEAN NOT NULL,
    status BOOLEAN NOT NULL,
    readConfig BOOLEAN NOT NULL,
    setConfig BOOLEAN NOT NULL,
    PRIMARY KEY (acl_id),
    CONSTRAINT fk_user
    FOREIGN KEY(user_id)
    REFERENCES users(user_id),
);

CREATE TABLE IF NOT EXISTS rbac (

);