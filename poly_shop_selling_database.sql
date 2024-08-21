CREATE DATABASE poly_selling;
USE poly_selling;

CREATE TABLE categories (
    id        VARCHAR(100) PRIMARY KEY NOT NULL,
    name      VARCHAR(100) NOT NULL
);

CREATE TABLE products (
    id             INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    name           VARCHAR(100) NOT NULL,
    image          VARCHAR(100),
    price          FLOAT,
    Categoryid     VARCHAR(100) NOT NULL,
    Createdate     DATE NOT NULL,
    available      TINYINT,
    FOREIGN KEY (categoryid) REFERENCES categories(id)
);

CREATE TABLE accounts(
    username     VARCHAR(100) PRIMARY KEY NOT NULL,
    password     VARCHAR(100) NOT NULL,
    fullname     NVARCHAR(100) NOT NULL,
    email        VARCHAR(100) NOT NULL,
    photo        VARCHAR(500),
    activated    TINYINT,
    admin        TINYINT
);

CREATE TABLE orders(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    address       VARCHAR(100),
    Createdate    DATE NOT NULL,
    Username      VARCHAR(100),
    FOREIGN KEY (username) REFERENCES accounts(username)
);

CREATE TABLE orderdetails(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
    price       FLOAT,
    quantity    INT,
    Productid   INT NOT NULL,
    Orderid     BIGINT NOT NULL,
    FOREIGN KEY (productid) REFERENCES products(id),
    FOREIGN KEY (orderid) REFERENCES orders(id)
);
