(ns defns.core
  [:require [clojure.spec.alpha :as s]])


;; Signature type definitions

(defrecord TypeSpecification [type-name predicate])

(defmacro deft
  "Defines a new TypeSpecification instance i.e. a new type."
  [type-name predicate]
  `(def ~type-name
     (->TypeSpecification ~(name type-name) ~predicate)))

;; built in types
(deft Fn fn?)
(deft Str string?)
(deft Int int?)
(deft Bool boolean?)
(deft Nil nil?)
(deft Key keyword?)
(deft GenericType (comp not nil?)) ; TODO - Find a better solution
(def T GenericType) ; generics like sugar
(def P GenericType) ; generics like sugar


;; Specs

(s/def ::type #(instance? TypeSpecification %))
(s/def ::fn #(= Fn %))
(s/def ::parameters (s/coll-of ::type))
(s/def ::signature (s/tuple ::fn ::parameters ::type))


;; defns

(defn prettify-sig
  "Converts a vector of TypeSpecifications to a vector of
   Strings representing their names."
  [signature]
  (into [] (map (fn [t] (if (vector? t)
                         (prettify-sig t)
                         (:type-name t))) signature)))

(defn signature-valid?
  "Check if type signature vector conforms to the signature spec. Also
   checks if number of parameters in the signature is equal to the passed
   number. Returns true if both are true, otherwise throws Exception."
  [signature params-count]
  (cond
    (not (s/valid? ::signature signature))
    (throw (new Exception (str (s/explain-data ::signature signature))))
    (not= (count (second signature)) params-count)
    (throw (new Exception (str "Signature and function arrities don't match!")))
    :else true))

(defmacro defns
  "Takes a name, type signature vector, parameter vector and a body and
   defines a new function using them. Behaves like defn, but adding additional
   metadata and adding :pre and :post conditions.
  
   Adds(merges) the following map to metadata:
   {:signature - the type signature, vector if TypeSpecifications
    :p - parameter part of the type signature
    :r - result type
    :pretty-signature - the string representation of the type signature
   }

   Creates the :pre condition such that applies the type predicate to the
   corresponding parameter and checks if all conformed. Creates :post 
   condition that applies the result type predicate to the result.

   Throws Exception if signature is not valid and/or function arity
   doesn't match with the signature."
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
  "Returns the prettified signature from the passed var's metadata if any."
  [fn-var]
  (:pretty-signature (meta fn-var)))

(defn sig
  "Returns the type signature from the passed var's metadata if any."
  [fn-var]
  (:signature (meta fn-var)))

(defn applies?
  "Checks if the passed parameter vector conforms to the type signature from
   the passed var's metadata. Also checks arity. Returns true if satisfied,
   otherwise false. Does not invoke the function."
  [fn-var args]
  (when-let [p (:p (meta fn-var))]
    (and
     (= (count p) (count args))
     (every? true? (map (fn [pred arg] (pred arg)) (map :predicate p) args)))))

(defn valid-result?
  "Checks if passed value conforms to the return type signature from the
   passed var's metadata. Returns true if satisfied, otherwise false.
   Does not invoke the function."
  [fn-var arg]
  (when-let [r (:r (meta fn-var))]
    ((:predicate r) arg)))
