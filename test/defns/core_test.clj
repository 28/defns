(ns defns.core-test
  (:require [clojure.test :refer :all]
            [defns.core :refer :all]
            [clojure.spec.alpha :as s]))


;; Test functions

(defns addition
  [Fn [Int Int] Int]
  [a b]
  (+ a b))

(defns negate
  [Fn [Bool] Bool]
  [b]
  (not b))

(defns nilify
  [Fn [T] Nil]
  [_]
  nil)

(deftest base-specs-test
  (testing "Fn spec conforms on Fn."
    (is (s/valid? :defns.core/fn Fn)))
  (testing "Fn spec will fail for non Fn."
    (is (false? (s/valid? :defns.core/fn Int))))
  (testing "Type spec conforms on all predefined types."
    (is (s/valid? :defns.core/type Str))
    (is (s/valid? :defns.core/type Int))
    (is (s/valid? :defns.core/type Bool))
    (is (s/valid? :defns.core/type Nil))
    (is (s/valid? :defns.core/type GenericType))
    (is (s/valid? :defns.core/type T))
    (is (s/valid? :defns.core/type P)))
  (testing "Parameters spec will conform to parameter vector."
    (is (s/valid? :defns.core/parameters []))
    (is (s/valid? :defns.core/parameters [Int]))
    (is (s/valid? :defns.core/parameters [Int Int]))
    (is (s/valid? :defns.core/parameters [Int Str]))
    (is (false? (s/valid? :defns.core/parameters [Fn Fn]))))
  (testing "Function signature spec will conform on valid signatures."
    (is (s/valid? :defns.core/signature [Fn [Int] Int]))
    (is (s/valid? :defns.core/signature [Fn [Int] Bool]))
    (is (s/valid? :defns.core/signature [Fn [] Nil]))
    (is (s/valid? :defns.core/signature [Fn [Nil] Int]))
    (is (s/valid? :defns.core/signature [Fn [Nil] Nil])))
  (testing "Function signature spec will not conform on invalid signatures."
    (is (false? (s/valid? :defns.core/signature [])))
    (is (false? (s/valid? :defns.core/signature [Fn])))
    (is (false? (s/valid? :defns.core/signature [Fn []])))
    (is (false? (s/valid? :defns.core/signature [Fn [Int]])))))

(deftest prettiffy-signature-test
  (testing "prettifying"
    (is (= (prettify-sig [Fn [Int Int] Int])
           ["Fn" ["Int" "Int"] "Int"]))
    (is (= (prettify-sig [Fn [] Int])
           ["Fn" [] "Int"]))))

(deftest metadata-test
  (testing "signature"
    (is (= (:signature (meta #'addition)) [Fn [Int Int] Int]))
    (is (= (:signature (meta #'negate)) [Fn [Bool] Bool]))
    (is (= (:signature (meta #'nilify)) [Fn [T] Nil])))
  (testing "params"
    (is (= (:p (meta #'addition)) [Int Int]))
    (is (= (:p (meta #'negate)) [Bool]))
    (is (= (:p (meta #'nilify)) [T])))
  (testing "result"
    (is (= (:r (meta #'addition)) Int))
    (is (= (:r (meta #'negate)) Bool))
    (is (= (:r (meta #'nilify)) Nil)))
  (testing "pretty"
    (is (= (:pretty-signature (meta #'addition)) ["Fn" ["Int" "Int"] "Int"]))
    (is (= (:pretty-signature (meta #'negate)) ["Fn" ["Bool"] "Bool"]))
    (is (= (:pretty-signature (meta #'nilify)) ["Fn" ["GenericType"] "Nil"]))))

(deftest argument-validity-test
  (testing "addition"
    (is (true? (applies? #'addition [1 2])))
    (is (false? (applies? #'addition [1 "a"])))
    (is (false? (applies? #'addition [1])))
    (is (false? (applies? #'addition ["a" 'a]))))
  (testing "negate"
    (is (true? (applies? #'negate [true])))
    (is (true? (applies? #'negate [false])))
    (is (false? (applies? #'negate [1])))
    (is (false? (applies? #'negate ["a"]))))
  (testing "nilify"
    (is (true? (applies? #'nilify [true])))
    (is (true? (applies? #'nilify [1])))
    (is (true? (applies? #'nilify ["a"])))
    (is (true? (applies? #'nilify ['a])))
    (is (true? (applies? #'nilify [[]])))
    (is (false? (applies? #'nilify [nil])))))

(deftest result-validity-test
  (testing "addition"
    (is (true? (valid-result? #'addition 1)))
    (is (false? (valid-result? #'addition [1])))
    (is (false? (valid-result? #'addition "a"))))
  (testing "negate"
    (is (true? (valid-result? #'negate true)))
    (is (true? (valid-result? #'negate false)))
    (is (false? (valid-result? #'negate 1)))
    (is (false? (valid-result? #'negate "a"))))
  (testing "nilify"
    (is (true? (valid-result? #'nilify nil)))
    (is (false? (valid-result? #'nilify 1)))
    (is (false? (valid-result? #'nilify "a")))
    (is (false? (valid-result? #'nilify 'a)))))
