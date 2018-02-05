# defns

A library that provides an interface for defining functions
with type signatures for which pre and post conditions are
generated using spec.

## Usage

``` clojure
user> (require '[defns.core :as defns])
nil

user> (defns/defns test-fn
        [defns/Fn [defns/Int defns/Int] defns/Int]
        [a b]
        (+ a b))
#'user/test-fn

user> (test-fn 1 2)
3

user> (test-fn "1" "2")
user> ;; a currently ugly assertion error

user> ;; override test-fn to return nil instead od Int
user> (defns/defns test-fn
        [defns/Fn [defns/Int defns/Int] defns/Int]
        [a b]
        nil)
#'user/test-fn

user> (test-fn 1 2)
user> ;; another currently ugly assertion error

user> (:pretty-signature (meta #'test-fn))
["Fn" ["Int" "Int"] "Int"]
```

## License

MIT License

Copyright (c) 2018 Dejan Josifović

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
