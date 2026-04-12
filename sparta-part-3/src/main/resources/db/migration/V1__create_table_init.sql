CREATE TABLE user
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '사용자 ID',
    name       VARCHAR(50)  NOT NULL COMMENT '사용자 이름',
    phone      VARCHAR(20)  NOT NULL COMMENT '전화번호',
    email      VARCHAR(100) NOT NULL UNIQUE COMMENT '이메일',
    password   VARCHAR(255) NOT NULL COMMENT '암호화된 비밀번호',
    gender     VARCHAR(10)  NOT NULL COMMENT '성별',
    created_at DATETIME     NOT NULL COMMENT '생성 일시',
    updated_at DATETIME     NOT NULL COMMENT '수정 일시',
    INDEX idx_user_email (email),
    INDEX idx_user_phone (phone)
);


CREATE TABLE category
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '카테고리 ID',
    name       VARCHAR(100) NOT NULL COMMENT '카테고리명',
    parent_id  BIGINT       NULL COMMENT '상위 카테고리 ID',
    created_at DATETIME     NOT NULL COMMENT '생성 일시',
    updated_at DATETIME     NOT NULL COMMENT '수정 일시',
    INDEX idx_category_parent_id (parent_id),
    INDEX idx_category_name (name)
);


CREATE TABLE product
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '상품 ID',
    category_id BIGINT         NOT NULL COMMENT '카테고리 ID',
    name        VARCHAR(200)   NOT NULL COMMENT '상품명',
    description TEXT COMMENT '상품 설명',
    price       DECIMAL(19, 2) NOT NULL COMMENT '가격',
    stock       INTEGER        NOT NULL COMMENT '재고',
    created_at  DATETIME       NOT NULL COMMENT '생성 일시',
    updated_at  DATETIME       NOT NULL COMMENT '수정 일시',
    INDEX idx_product_category_id (category_id)
);