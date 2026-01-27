-- Database: platform_db
\c platform_db;

-- Enums
CREATE TYPE type_editor_enum AS ENUM ('particulier', 'entreprise');
CREATE TYPE platform_enum AS ENUM ('PC', 'XBOX', 'PS5', 'SWITCH');
CREATE TYPE genre_enum AS ENUM ('ACTION', 'RPG', 'STRATEGY', 'SPORTS');

-- Table: editor (Replica/Account info for the platform)
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
    CONSTRAINT fk_plat_editor FOREIGN KEY (editor_id) REFERENCES editor(id)
);

-- Lists for Game
CREATE TABLE game_platforms (
    game_id UUID,
    platform platform_enum,
    PRIMARY KEY (game_id, platform),
    CONSTRAINT fk_plat_game_p FOREIGN KEY (game_id) REFERENCES game(id)
);

CREATE TABLE game_genres (
    game_id UUID,
    genre genre_enum,
    PRIMARY KEY (game_id, genre),
    CONSTRAINT fk_plat_game_g FOREIGN KEY (game_id) REFERENCES game(id)
);

-- Table: dlc
CREATE TABLE dlc (
    id UUID PRIMARY KEY,
    game_id UUID,
    editor_id UUID,
    name VARCHAR(200),
    price FLOAT,
    num_version FLOAT,
    CONSTRAINT fk_plat_dlc_game FOREIGN KEY (game_id) REFERENCES game(id)
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
    description VARCHAR(10000) -- Note: Called 'description' in Platform UML
);
