CREATE TABLE users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  bio VARCHAR(1024),
  image VARCHAR(255),
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  CONSTRAINT user_email_unique UNIQUE (email),
  CONSTRAINT user_username_unique UNIQUE (username)
);

CREATE TABLE followers (
  user_id INTEGER NOT NULL,
  followee_id INTEGER NOT NULL,
  inserted_at TIMESTAMP NOT NULL,
  FOREIGN KEY (followee_id) REFERENCES users(id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT follower_follower_follwed_unq UNIQUE (user_id, followee_id)
);

CREATE TABLE articles (
  id SERIAL PRIMARY KEY,
  slug VARCHAR(255) NOT NULL,
  title VARCHAR(300) NOT NULL,
  description VARCHAR(255) NOT NULL,
  body TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  author_id INTEGER NOT NULL,
  FOREIGN KEY (author_id) REFERENCES users(id),
  CONSTRAINT articles_slug_unique UNIQUE(slug)
);

CREATE TABLE tags (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  CONSTRAINT tag_name_unique UNIQUE(name)
);

CREATE TABLE articles_tags (
  id SERIAL PRIMARY KEY,
  article_id INTEGER NOT NULL,
  tag_id INTEGER NOT NULL,
  FOREIGN KEY (article_id) REFERENCES articles(id),
  FOREIGN KEY (tag_id) REFERENCES tags(id),
  CONSTRAINT article_tag_id_unique UNIQUE(article_id, tag_id)
);

CREATE TABLE comments (
  id SERIAL PRIMARY KEY,
  body VARCHAR (4096) NOT NULL,
  article_id INTEGER NOT NULL,
  author_id INTEGER NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  FOREIGN KEY (article_id) REFERENCES articles(id),
  FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE TABLE favorite (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL,
  favorited_id INTEGER NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (favorited_id) REFERENCES articles(id)
)
