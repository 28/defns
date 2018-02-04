(ns defns.core
  [:require [clojure.spec.alpha :as s]])


;; Signature type definitions

(defrecord TypeSpecification
    [predicate type-name])

(defmacro defs
  [type-name predicate]
  `(def ~type-name
     (->TypeSpecification ~predicate ~(name type-name))))

(defs Fn nil)
(defs Str string?)
(defs Int int?)
(defs Bool boolean?)
(defs Fun fn?)
(defs Nil nil?)

(defs GenericType (comp not nil?))
(def T GenericType)
(def P GenericType)


;; Specs

(s/def ::type #(and (instance? TypeSpecification %) (not= Fn %)))
(s/def ::fn #(= Fn %))
(s/def ::parameters (s/coll-of ::type))
(s/def ::signature (s/tuple ::fn ::parameters ::type))

(defn prettify-sig
  [signature]
  (into [] (map (fn [t] (if (vector? t)
                         (into [] (map :type-name t))
                         (:type-name t))) signature)))

(defn signature-valid?
  [signature]
  (if-not (s/valid? ::signature signature)
    (throw (new Exception ""))
    true))

(defmacro defns
  [name signature params body]
  `(when (signature-valid? ~signature)
     (let [[fn# p# r#] ~signature]
       (defn ~name
         {:signature ~signature
          :p p#
          :r r#
          :pretty-signature (prettify-sig ~signature)}
         ~params
         {:pre [(every? true?
                        (map (fn [f# a#] (f# a#)) (map :predicate p#) ~params))]
          :post [((:predicate r#) ~'%)]}
         ~body))))
