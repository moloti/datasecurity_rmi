CREATE SCHEMA IF NOT EXISTS rightsmanagement;
CREATE EXTENSION pgcrypto;

CREATE TABLE IF NOT EXISTS users (user_id VARCHAR(255) DEFAULT gen_random_uuid (), user_name VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, primary key (user_id));
CREATE TABLE IF NOT EXISTS transactions (transaction_id VARCHAR(255) DEFAULT gen_random_uuid (), transaction_name VARCHAR(255) NOT NULL, primary key (transaction_id));
CREATE TABLE IF NOT EXISTS roles (role_id VARCHAR(255) DEFAULT gen_random_uuid (), role_name VARCHAR(255) NOT NULL, primary key (role_id));
CREATE TABLE IF NOT EXISTS user_role (user_role_id VARCHAR(255) DEFAULT gen_random_uuid (), role_id VARCHAR(255) NOT NULL, user_id VARCHAR(255) NOT NULL, primary key (user_role_id), constraint fk_role foreign key(role_id) references roles(role_id), constraint fk_user foreign key(user_id) references users(user_id));
CREATE TABLE IF NOT EXISTS role_transaction (role_transaction_id VARCHAR(255) DEFAULT gen_random_uuid (), role_id VARCHAR(255) NOT NULL, transaction_id VARCHAR(255) NOT NULL, primary key (role_transaction_id), constraint fk_role foreign key(role_id) references roles(role_id), constraint fk_transaction foreign key(transaction_id) references transactions(transaction_id));
CREATE TABLE IF NOT EXISTS transaction_user ( transaction_user_id VARCHAR(255) DEFAULT gen_random_uuid (), transaction_id VARCHAR(255) NOT NULL, user_id VARCHAR(255) NOT NULL, primary key (transaction_user_id), constraint fk_transaction foreign key(transaction_id) references transactions(transaction_id), constraint fk_user foreign key(user_id) references users(user_id));

