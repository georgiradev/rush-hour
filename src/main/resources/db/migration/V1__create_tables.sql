CREATE TABLE user (
    id INT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(15) NOT NULL,
    last_name VARCHAR(15) NOT NULL,
    email VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE role (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(10) NOT NULL,
    PRIMARY KEY(id)
);

ALTER TABLE user
    ADD (FOREIGN KEY (`role_id`) REFERENCES role(`id`));

CREATE TABLE activity (
    id INT NOT NULL AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL UNIQUE,
    duration BIGINT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY(id)
);

CREATE TABLE appointment (
    id INT NOT NULL AUTO_INCREMENT,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    user_id INT NOT NULL,
    PRIMARY KEY(id)
);

ALTER TABLE appointment
    ADD ( FOREIGN KEY (`user_id`) REFERENCES user(`id`));

CREATE TABLE activity_appointment (
    id INT NOT NULL AUTO_INCREMENT,
    activity_id INT NOT NULL,
    appointment_id INT NOT NULL,
    PRIMARY KEY(id)
);

ALTER TABLE activity_appointment
    ADD ( FOREIGN KEY (`activity_id`) REFERENCES activity(`id`), FOREIGN KEY (`appointment_id`) REFERENCES appointment(`id`));