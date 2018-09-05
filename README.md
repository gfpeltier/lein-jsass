# lein-jsass

[![Build Status](https://travis-ci.org/grantpeltier/lein-jsass.svg?branch=master)](https://travis-ci.org/grantpeltier/lein-jsass)
[![Clojars Project](https://img.shields.io/clojars/v/lein-jsass.svg)](https://clojars.org/lein-jsass)

A Leiningen plugin that wraps [jsass](https://github.com/bit3/jsass)

## Usage

Put `[lein-jsass "0.2.0"]` into the `:plugins` vector of your project.clj.

Also add the following config information to your project.clj:

```clojure
:jsass {:source "path/to/scss/files"
        :target "path/to/css/output"}
```

lein-jsass may be used to either compile once or to run continuously
and recompile whenever file changes are detected under the source path.

Run once:

```
$ lein jsass once
```

Run continuously:

```
$ lein jsass auto
```

**NOTE:** Only public (i.e. does not start with '_') SASS files will be compiled
and placed in the target.

## License

Copyright © 2018 Grant Peltier

Distributed under the MIT License
