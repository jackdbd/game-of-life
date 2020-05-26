# game-of-life

[![Build Status](https://travis-ci.com/jackdbd/game-of-life.svg?branch=master)](https://travis-ci.org/jackdbd/game-of-life)

Conway's Game of Life implemented in Clojure and animated with [Quil](https://github.com/quil/quil).

## Usage

This project uses [Leiningen](https://leiningen.org/).

Play with:

```sh
lein run
```

## Build

Build the [uberjar](https://imagej.net/Uber-JAR):

```sh
lein uberjar
```

Then run the executable:

```sh
java -jar target/uberjar/game-of-life-standalone.jar
```

## Credits

- [Conway's Game of Life by Christophe Grand](http://clj-me.cgrand.net/2011/08/19/conways-game-of-life/)
