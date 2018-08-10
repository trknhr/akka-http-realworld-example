-- CREATE TABLE "users" (
--   id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY
-- );
CREATE TABLE users (
  id INTEGER NOT NULL PRIMARY KEY,
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
)
