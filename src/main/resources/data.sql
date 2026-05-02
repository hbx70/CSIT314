DELETE FROM user_account;
DELETE FROM user_profile;

-- =========================
-- User Profile
-- =========================

INSERT INTO user_profile (name, description, status)
VALUES
    ('ADMIN', 'Admin role', 'ACTIVE'),
    ('DONEE', 'Donee role', 'ACTIVE'),
    ('FUND_RAISER', 'Fund Raiser role', 'ACTIVE'),
    ('PLATFORM_MANAGER', 'Platform manager role', 'SUSPENDED');

-- =========================
-- User Account
-- =========================

INSERT INTO user_account
(username, password, email, address, status, user_profile_name)
VALUES
    (
        'validUser',
        '$2a$10$INJyYC5lI86fLbMgq/gtjeX7sAhbDHmAhiZJ3vg7.LPLcXNJoHvva',
        'valid@test.com',
        'Singapore',
        'ACTIVE',
        'DONEE'
    );


INSERT INTO user_account
(username, password, email, address, status, user_profile_name)
VALUES
    (
        'validUser2',
        '$2a$10$3K6DdPKk92Znkwr3aHFA2uyW/vsedymwLmPVCgYyKJylRM2/QJhs6',
        'valid2@test.com',
        'Singapore',
        'ACTIVE',
        'DONEE'
    );

INSERT INTO user_account
(username, password, email, address, status, user_profile_name)
VALUES
    (
        'suspendedUser',
        '$2a$10$INJyYC5lI86fLbMgq/gtjeX7sAhbDHmAhiZJ3vg7.LPLcXNJoHvva',
        'suspend@test.com',
        'Singapore',
        'SUSPENDED',
        'DONEE'
    );

INSERT INTO user_account
(username, password, email, address, status, user_profile_name)
VALUES
    (
        'roleSuspendedUser',
        '$2a$10$INJyYC5lI86fLbMgq/gtjeX7sAhbDHmAhiZJ3vg7.LPLcXNJoHvva',
        'role@test.com',
        'Singapore',
        'ACTIVE',
        'PLATFORM_MANAGER'
    );