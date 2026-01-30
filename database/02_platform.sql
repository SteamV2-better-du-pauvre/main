-- Database: platform_db
\c platform_db;

-- Enums
CREATE TYPE type_editor_enum AS ENUM ('particulier', 'entreprise');
CREATE TYPE platform_enum AS ENUM ('PC', 'XBOX', 'PS5', 'SWITCH');
CREATE TYPE genre_enum AS ENUM ('ACTION', 'RPG', 'STRATEGY', 'SPORTS');

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
    CONSTRAINT fk_plat_editor FOREIGN KEY (editor_id) REFERENCES editor(id)
);

-- Game platforms list
CREATE TABLE game_platforms (
    game_id UUID,
    platform platform_enum,
    PRIMARY KEY (game_id, platform),
    CONSTRAINT fk_plat_game_p FOREIGN KEY (game_id) REFERENCES game(id)
);

-- Game genres list
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
    description VARCHAR(10000)
);

-- Table: player
CREATE TABLE player (
    id UUID PRIMARY KEY,
    pseudo VARCHAR(200) UNIQUE NOT NULL,
    password VARCHAR(200) NOT NULL,
    first_name VARCHAR(200),
    last_name VARCHAR(200),
    birthday DATE,
    email VARCHAR(200)
);

-- Table: possession_game
CREATE TABLE possession_game (
    player_id UUID NOT NULL,
    temp FLOAT,
    game_id UUID NOT NULL,
    id_patch UUID,
    platform platform_enum NOT NULL,
    PRIMARY KEY (player_id, game_id, platform),
    CONSTRAINT fk_possession_player FOREIGN KEY (player_id) REFERENCES player(id),
    CONSTRAINT fk_possession_game FOREIGN KEY (game_id) REFERENCES game(id),
    CONSTRAINT fk_possession_patch FOREIGN KEY (id_patch) REFERENCES patch(id)
);

-- Table: possession_dlc
CREATE TABLE possession_dlc (
    player_id UUID NOT NULL,
    dlc_id UUID NOT NULL,
    game_id UUID NOT NULL,
    name VARCHAR(200),
    num_version FLOAT,
    PRIMARY KEY (player_id, dlc_id),
    CONSTRAINT fk_possession_dlc_player FOREIGN KEY (player_id) REFERENCES player(id),
    CONSTRAINT fk_possession_dlc_dlc FOREIGN KEY (dlc_id) REFERENCES dlc(id),
    CONSTRAINT fk_possession_dlc_game FOREIGN KEY (game_id) REFERENCES game(id)
);

-- Table: player_follows
CREATE TABLE player_follows (
    id_player UUID NOT NULL,
    id_editor UUID NOT NULL,
    PRIMARY KEY (id_player, id_editor),
    CONSTRAINT fk_follows_player FOREIGN KEY (id_player) REFERENCES player(id),
    CONSTRAINT fk_follows_editor FOREIGN KEY (id_editor) REFERENCES editor(id)
);

-- Table: evaluation
CREATE TABLE evaluation (
    id_game UUID NOT NULL,
    player_id UUID NOT NULL,
    description TEXT,
    plateforme platform_enum,
    note INT CHECK (note >= 0 AND note <= 10),
    PRIMARY KEY (id_game, player_id, plateforme),
    CONSTRAINT fk_evaluation_game FOREIGN KEY (id_game) REFERENCES game(id),
    CONSTRAINT fk_evaluation_player FOREIGN KEY (player_id) REFERENCES player(id)
);

-- Table: bug_report
CREATE TABLE bug_report (
    id_game UUID NOT NULL,
    id_patch UUID,
    player_id UUID NOT NULL,
    description TEXT,
    plateforme platform_enum,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bug_game FOREIGN KEY (id_game) REFERENCES game(id),
    CONSTRAINT fk_bug_patch FOREIGN KEY (id_patch) REFERENCES patch(id),
    CONSTRAINT fk_bug_player FOREIGN KEY (player_id) REFERENCES player(id)
);

-- Table: publication_event_editeur
CREATE TABLE publication_event_editeur (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    titre TEXT,
    content TEXT,
    date_publication DATE DEFAULT CURRENT_DATE,
    id_editeur UUID NOT NULL,
    CONSTRAINT fk_publication_editor FOREIGN KEY (id_editeur) REFERENCES editor(id)
);
