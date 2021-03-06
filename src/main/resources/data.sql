/**
  Spring Boot 서버가 시작될때 마다 Table 들이 새로 Create 되기 때문에 편의를 위해 초기 데이터를 자동으로 Database 에 넣어주는 기능을 활용
  Spring Boot 서버가 새로 시작될때 data.sql 의 쿼리들이 자동으로 실행
 */

INSERT INTO USER (USER_ID, USERNAME, PASSWORD, NICKNAME, ACTIVATED)
VALUES (1, 'admin', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'admin', 1);

INSERT INTO AUTHORITY (AUTHORITY_NAME) VALUES ('ROLE_USER');
INSERT INTO AUTHORITY (AUTHORITY_NAME) VALUES ('ROLE_ADMIN');

INSERT INTO USER_AUTHORITY (USER_ID, AUTHORITY_NAME) VALUES (1, 'ROLE_USER');
INSERT INTO USER_AUTHORITY (USER_ID, AUTHORITY_NAME) VALUES (1, 'ROLE_ADMIN');
