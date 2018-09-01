# lein-jsass

A Leiningen plugin that wraps [jsass](https://github.com/bit3/jsass)

## Usage

Put `[lein-jsass "0.1.0-SNAPSHOT"]` into the `:plugins` vector of your project.clj.

Also add the following config information to your project.clj:

```clojure
:jsass {:source "path/to/scss/files"
        :target "some/relative/target/path"}
```

Use the following command to compile your SASS files to CSS.

    $ lein jsass

## License

Copyright © 2018 Grant Peltier

Distributed under the MIT License
