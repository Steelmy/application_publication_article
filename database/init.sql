-- ============================================
-- Base de données : Application Publication Article
-- ============================================

CREATE DATABASE IF NOT EXISTS publication_article;
USE publication_article;

-- =====================
-- Table : roles
-- =====================
CREATE TABLE roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom_role VARCHAR(50) NOT NULL UNIQUE
);

-- =====================
-- Table : user
-- =====================
CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    nom VARCHAR(100) NOT NULL,
    role_id INT NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- =====================
-- Table : categories
-- =====================
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom_categorie VARCHAR(100) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =====================
-- Table : categories_suivies
-- =====================
CREATE TABLE categories_suivies (
    utilisateur_id INT NOT NULL,
    categorie_id INT NOT NULL,
    PRIMARY KEY (utilisateur_id, categorie_id),
    FOREIGN KEY (utilisateur_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (categorie_id) REFERENCES categories(id) ON DELETE CASCADE
);

-- =====================
-- Table : articles
-- =====================
CREATE TABLE articles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(255) NOT NULL,
    resume TEXT NOT NULL,
    contenu TEXT NOT NULL,
    categorie_id INT NOT NULL,
    auteur_id INT NOT NULL,
    nombre_de_vues INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (categorie_id) REFERENCES categories(id),
    FOREIGN KEY (auteur_id) REFERENCES user(id)
);

-- =====================
-- Table : article_likes
-- =====================
CREATE TABLE article_likes (
    utilisateur_id INT NOT NULL,
    article_id INT NOT NULL,
    PRIMARY KEY (utilisateur_id, article_id),
    FOREIGN KEY (utilisateur_id) REFERENCES user(id) ON DELETE CASCADE,
    FOREIGN KEY (article_id) REFERENCES articles(id) ON DELETE CASCADE
);

-- =====================
-- Données initiales : rôles
-- =====================
INSERT INTO roles (nom_role) VALUES
    ('ADMIN'),
    ('USER');
