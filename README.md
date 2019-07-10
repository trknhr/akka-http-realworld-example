# ![RealWorld Example Applications](logo.png)

[![Build Status](https://travis-ci.org/1984weed/akka-http-realworld-example.svg?branch=master)](https://travis-ci.org/1984weed/akka-http-realworld-example)

PRs and issues welcome!

# Getting started

1. Clone this repo
    ```
    git clone https://github.com/1984weed/akka-http-realworld-example
    ```
2. Launch local Database using dokcer-compose
    ```
    docker-compose up
    ```
3. `sbt run` to start the local server

It launches on `localhost:9000`

# Application Structure

Each directory under the realworld.com/ has a responsibility for each Domail. (e.g `articles` has only a responsibility for article router, logic and Database

# Main Dependencies

- Slick3
- circe
- Flyway

# Development

Hot reloading 

```
sbt ~reStart
```

## Code Format

```
sbt scalafmt
```

# Licence
Copyright
Copyright (C) 2019 Teruo Kunihiro.
Distributed under the MIT License.


