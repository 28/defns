# defns

A library that provides a way for defining functions with type
signatures for which pre and post conditions are generated using spec.
This is an experimental idea.

## Usage

``` clojure
user> (require '[defns.core :as d])
=> nil

user> (d/defns test-fn
        [d/Fn [d/Int d/Int] d/Int]
        [a b]
        (+ a b))
=> #'user/test-fn

user> (test-fn 1 2)
=> 3

user> (test-fn "1" "2")
=>  ;; a currently ugly assertion error

user> ;; override test-fn to return nil instead of Int
user> (d/defns test-fn
        [d/Fn [d/Int d/Int] d/Int]
        [a b]
        nil)
=> #'user/test-fn

user> (test-fn 1 2)
=>  ;; another currently ugly assertion error
```

You can define your own types:
```clojure
user> (require '[clojure.spec.alpha :as s])
=> nil

user> (s/def ::first-name string?) 
=> :user/first-name

user> (s/def ::last-name string?)
=> :user/last-name

user> (d/deft Person (partial s/valid? (s/keys :req [::first-name ::last-name])))
=> #'user/Person

user> (d/defns update-first-name
       [d/Fn [Person d/Str] Person]
       [p name]
       (assoc-in p [::first-name] name))
=> #'user/update-first-name

user> (update-first-name {::first-name "John" ::last-name "Doe"} "Jane")
=> #:user{:first-name "Jane", :last-name "Doe"}

user> (update-first-name {:a 1} "Jane")
=> ;; another currently ugly assertion error
```

A couple of useful things that are possible with defns:
``` clojure
user> (d/pretty-sig #'test-fn)
=> ["Fn" ["Int" "Int"] "Int"]

user> (d/applies? #'test-fn [1 2])
=> true

user> (d/applies? #'test-fn ["a" "b"])
=> false

user> (d/valid-result? #'test-fn 1)
=> true

user> (d/valid-result? #'test-fn "1")
=> false
```

## License

MIT License

Copyright (c) 2018 Dejan Josifović

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
