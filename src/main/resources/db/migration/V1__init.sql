CREATE TABLE user(
                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                     email VARCHAR(255) NOT NULL,
                     nickname VARCHAR(20) NOT NULL,
                     password VARCHAR(255) NOT NULL,
                     base_address VARCHAR(255) NOT NULL,
                     detail_address VARCHAR(255) NOT NULL,
                     created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                     updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);
CREATE INDEX idx__email ON user (email);

CREATE TABLE product_brand(
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              name VARCHAR(80) NOT NULL,
                              created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                              updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE product_category(
                                 id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                 name VARCHAR(80) NOT NULL,
                                 parent_category_id BIGINT,
                                 created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                                 updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE product(
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(80) NOT NULL,
                        release_price INT NOT NULL,
                        model_number VARCHAR(50) NOT NULL,
                        size_type VARCHAR(20) NOT NULL,
                        product_brand_id BIGINT NOT NULL,
                        product_category_id BIGINT NOT NULL,
                        created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                        updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE bidding(
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        product_id BIGINT NOT NULL,
                        user_id BIGINT NOT NULL,
                        price INT NOT NULL,
                        size VARCHAR(5) NOT NULL,
                        status VARCHAR(20) NOT NULL,
                        bidding_type VARCHAR(10) NOT NULL,
                        created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                        updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);
CREATE INDEX idx__product_id_bidding_type ON bidding (product_id, bidding_type);

CREATE TABLE sale_history(
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             bidding_id BIGINT NOT NULL,
                             user_id BIGINT NOT NULL,
                             price INT NOT NULL,
                             size VARCHAR(5) NOT NULL,
                             created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                             updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE orders(
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       payment_id VARCHAR(255) NOT NULL,
                       bidding_id BIGINT NOT NULL,
                       user_id BIGINT NOT NULL,
                       status VARCHAR(10) NOT NULL,
                       created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE delivery(
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         bidding_id BIGINT NOT NULL,
                         status VARCHAR(20) NOT NULL,
                         created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                         updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);
CREATE INDEX idx__status ON delivery (status);

CREATE TABLE outbox(
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       aggregate_type VARCHAR(255) NOT NULL,
                       payload TEXT,
                       completed_at DATETIME(6),
                       created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE coupon_group(
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             identifier VARCHAR(32) NOT NULL,
                             discount_type VARCHAR(20) NOT NULL,
                             discount_value INT NOT NULL,
                             status VARCHAR(20) NOT NULL,
                             quantity INT NOT NULL,
                             remain_quantity INT NOT NULL,
                             period_type VARCHAR(20) NOT NULL,
                             period INT NOT NULL,
                             created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                             updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE coupon(
                       id BIGINT PRIMARY KEY AUTO_INCREMENT,
                       coupon_group_id BIGINT NOT NULL,
                       user_id BIGINT NOT NULL,
                       status VARCHAR(20) NOT NULL,
                       expired_at DATETIME(6) NOT NULL,
                       created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                       updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);

CREATE TABLE coupon_history(
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               coupon_id BIGINT NOT NULL,
                               user_id BIGINT NOT NULL,
                               status VARCHAR(20) NOT NULL,
                               created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
                               updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6)
);
