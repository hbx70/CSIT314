DROP TABLE IF EXISTS user_account;
DROP TABLE IF EXISTS user_profile;

-- =========================
-- User Profile (角色表)
-- =========================
CREATE TABLE user_profile (
    enum ('ADMIN', 'DONEE', 'FUND_RAISER', 'PLATFORM_MANAGER') not null primary key,
    description text not null,
    status enum ('ACTIVE', 'SUSPENDED') default 'ACTIVE' not null,
    created_at  datetime default CURRENT_TIMESTAMP not null
);

-- =========================
-- User Account (用户表)
-- =========================
CREATE TABLE user_account (
                              id                int auto_increment
        primary key,
                              username          varchar(100)                                                                         not null,
                              password          varchar(255)                                                                         not null,
                              email             varchar(255)                                                                         not null,
                              address           varchar(255)                                                                         not null,
                              status            enum ('ACTIVE', 'SUSPENDED')                               default 'ACTIVE'          not null,
                              user_profile_name enum ('ADMIN', 'DONEE', 'FUND_RAISER', 'PLATFORM_MANAGER') default 'DONEE'           not null,
                              created_at        datetime                                                   default CURRENT_TIMESTAMP not null,
                              constraint username
                                  unique (username),
                              constraint fk_user_profile
                                  foreign key (user_profile_name) references assignment.user_profile (name)
);