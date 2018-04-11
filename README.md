
Calcit three.js Workflow
----

> Personal project template based on ClojureScript, Respo, shadow-cljs, Cirru.

### Usage

Make sure you have Node.js and JVM installed. Then install dependencies:

```bash
yarn

yarn repl # to start shadow-cljs Clojure REPL
```

All build script are running in

```clojure
(require '[build.main :as b])

(b/watch) ; to start watch server
(b/html) ; to generate target/index.html file
(b/build-local) ; to build release bundles, with local assets links
(b/build) ; to build release bundles, with assets link to CDN
(b/upload) ; to upload assets to my server
```

Edit Clojure with [calcit-editor](https://github.com/Cirru/calcit-editor):

```bash
npm i -g calcit-editor
calcit-editor # watching server...

op=compile calcit-editor # generate code.
```

### Assets

Teapot file https://graphics.stanford.edu/courses/cs148-10-summer/as3/code/as3/teapot.obj

### Workflow

https://github.com/mvc-works/calcit-threejs-workflow

### License

MIT
