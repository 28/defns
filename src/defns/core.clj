(ns defns.core
  [:require [clojure.spec.alpha :as s]])


;; Signature type definitions

(defrecord TypeSpecification [type-name predicate])

(defmacro defs
  [type-name predicate]
  `(def ~type-name
     (->TypeSpecification ~(name type-name) ~predicate)))

(defs Fn fn?)
(defs Str string?)
(defs Int int?)
(defs Bool boolean?)
(defs Nil nil?)
(defs Key keyword?)

(defs GenericType (comp not nil?)) ; TODO - Find a better solution
(def T GenericType)
(def P GenericType)


;; Specs

(s/def ::type #(instance? TypeSpecification %))
(s/def ::fn #(= Fn %))
(s/def ::parameters (s/coll-of ::type))
(s/def ::signature (s/tuple ::fn ::parameters ::type))


;; defns

(defn prettify-sig
  [signature]
  (into [] (map (fn [t] (if (vector? t)
                         (prettify-sig t)
                         (:type-name t))) signature)))

(defn signature-valid?
  [signature params-count]
  (cond
    (not (s/valid? ::signature signature))
    (throw (new Exception (str (s/explain-data ::signature signature))))
    (not= (count (second signature)) params-count)
    (throw (new Exception (str "Signature and function arrities don't match!")))
    :else true))

(defmacro defns
  [name signature params body]
  (let [params-count (count params)]
    `(when (signature-valid? ~signature ~params-count)
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
           ~body)))))


;; Util

(defn pretty-sig
  [fn-var]
  (:pretty-signature (meta fn-var)))

(defn sig
  [fn-var]
  (:signature (meta fn-var)))

(defn applies?
  [fn-var args]
  (when-let [p (:p (meta fn-var))]
    (and
     (= (count p) (count args))
     (every? true? (map (fn [pred arg] (pred arg)) (map :predicate p) args)))))

(defn valid-result?
  [fn-var arg]
  (when-let [r (:r (meta fn-var))]
    ((:predicate r) arg)))
