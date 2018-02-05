(ns defns.core-test
  (:require [clojure.test :refer :all]
            [defns.core :refer :all]
            [clojure.spec.alpha :as s]))

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
    (is (s/valid? :defns.core/type Fun))
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
