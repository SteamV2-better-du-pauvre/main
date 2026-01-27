-- Database: editor_db
\c editor_db;

-- Enums
CREATE TYPE type_editor_enum AS ENUM ('particulier', 'entreprise');
CREATE TYPE platform_enum AS ENUM ('PC', 'XBOX', 'PS5', 'SWITCH'); -- Example values based on standard usage
CREATE TYPE genre_enum AS ENUM ('ACTION', 'RPG', 'STRATEGY', 'SPORTS'); -- Example values

-- Table: editor
CREATE TABLE editor (
    id UUID PRIMARY KEY,
    name VARCHAR(200),
    password VARCHAR(200),
    type type_editor_enum,
    description VARCHAR(2000)
);

-- Table: game
CREATE TABLE game (
    id UUID PRIMARY KEY,
    editor_id UUID,
    name VARCHAR(200),
    price FLOAT,
    num_version FLOAT,
    is_publish BOOLEAN,
    CONSTRAINT fk_editor FOREIGN KEY (editor_id) REFERENCES editor(id)
);

-- Table: dlc
CREATE TABLE dlc (
    id UUID PRIMARY KEY,
    game_id UUID,
    editor_id UUID,
    name VARCHAR(200),
    price FLOAT,
    num_version FLOAT,
    is_publish BOOLEAN,
    CONSTRAINT fk_dlc_game FOREIGN KEY (game_id) REFERENCES game(id)
);

-- Table: patch
CREATE TABLE patch (
    id UUID PRIMARY KEY,
    is_patch_of_game BOOLEAN,
    game_id UUID,
    platform platform_enum,
    old_version FLOAT,
    new_version FLOAT,
    comment VARCHAR(2000),
    modifications VARCHAR(10000),
    is_publish BOOLEAN,
    CONSTRAINT fk_patch_game FOREIGN KEY (game_id) REFERENCES game(id)
);

-- Table: bug_report (Synced from Platform)
CREATE TABLE bug_report (
    id_game UUID,
    id_patch UUID,
    description TEXT,
    plateforme platform_enum
    -- Note: No FK to game/patch enforced here strictly if data comes from external source
);

-- Table: evaluation (Synced from Platform)
CREATE TABLE evaluation (
    id_game UUID,
    description TEXT,
    plateforme platform_enum,
    note INT
);

-- Handling lists (genre/platform) as join tables for normalization
CREATE TABLE game_platforms (
    game_id UUID,
    platform platform_enum,
    PRIMARY KEY (game_id, platform),
    CONSTRAINT fk_game_plat FOREIGN KEY (game_id) REFERENCES game(id)
);

CREATE TABLE game_genres (
    game_id UUID,
    genre genre_enum,
    PRIMARY KEY (game_id, genre),
    CONSTRAINT fk_game_gen FOREIGN KEY (game_id) REFERENCES game(id)
);

