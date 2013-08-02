(ns complecs.entity-component-system
  "Defines operators from entities, components, and systems.

Entities:

Entitties are simply integer identifiers associated with a set of
components.

Components:

Components are implemented through defrecord and type-extension.
They may implement protocols.

Systems:

Two types of systems exist:
-Global Systems : These operate once per update cycle.
-Component Systems : These operate once per valid component.
They operate on a particular type or protocol of component,
and update the component as necessary.


ECS:

The ECS structure contains the state of the entities and components.
It is passed as a parameter to systems.

"
  (:use [clojure.algo.generic.functor]
        [complecs.util]))

(def ^:dynamic *component-deps*
  "This stores the mappings of component dependencies."
  (atom {}))


(defn entity
  "Create an entity map from a set of components."
  [& comps]
  (into {}
        (map #(-> [(class %) %])
             comps)))
  
  
;;;; Utility Functions
(defn- fn-form-type
  "Determines what type of function form is passed.
Returns either :single, :multi, or nil.  Works with
function forms as defined in the defcomponent macro"
  [frm]
  (let [frm (if (or (= (first frm) `fn)
                        (= (first frm) 'fn))
                  (nnext frm)
                  (next frm))]
    (cond
     (every? seq? frm)     :multi
     (vector? (first frm))     :single
     :else     nil)))

(defn s-extends?
  "A safe version of extends? that just returns false if it fails."
  [protocol atype]
  (let [protocol (if (var? protocol)
                   (var-get protocol)
                   protocol)]
    (and (:impls protocol)
         (:on-interface protocol)
         (extends? protocol atype))))

;;;; Components

(defmacro defcomponent
  "Define a component.  Components may be implemented as records
or as extensions of an existing type.  Works similar to
clojure.core/defrecord, but allows for type extension.

Also, multi-arity functions are allowed rather than multiple
definitions for single-arity functions.

Params:
-nme: A new name or an existing class.
-fields-or-extend : If the class currently exists, this param
should be :extend.  Otherwise, it is a set of field names for the
new record.
-specs: a set of methods delimited by protocols.  Use :deps followed
by a single list to denote dependencies on other components.x
"
  [nme fields-or-extend & specs]

  (let [specs (try
                (->>
                 specs
                 (partition-by #(or (symbol? %)
                                    (= :deps %)))
                 (map #(if (and (seq? %)
                                (or (symbol? (first %))
                                    (= :deps (first %))))
                         (first %)
                         %))
                 (apply hash-map))
                (catch Exception e
                  (throw
                     (Exception.
                      (str "defcomponent: specification format error: "
                           e)))))
        deps (:deps specs)
        
        _ (if (and deps
                   (not= (count deps) 1))
            (throw (Exception.
                    (str "Error in component definition: "
                         nme ".  Deps must be a single list."))))
        deps (set (first deps))
        
        ;; Re-assign specs
        specs (dissoc specs :deps)
        ]
    
    (if (= fields-or-extend :extend)
      
      
      ;; A class was provided
      ;; The extend form is weird and needs an associative
      ;; map instead of the regular forms.  So we need to
      ;; transform what we have.
      (let [methods (fmap
                     (fn [spec]
                       (reduce
                        (fn [acc m]
                          (assoc acc (keyword (first m))
                                 (cons `fn m)))
                        {}
                        spec))
                     specs)
            ]
        
        `(do
           (extend ~nme
             ~@(apply concat methods))
           (swap! *component-deps* assoc ~nme ~deps)))

      
      ;; A class was not provided
      ;; We just treat it as a normal defrecord
      ;; Todo: break up multi-implementation functions into
      ;; a sequence of single-implementation functions
      `(do
         (defrecord ~nme ~fields-or-extend
           ~@(apply concat
               (for [[k v] specs]
                 (cons
                  k
                  (apply concat
                         (for [f v]
                           (case (fn-form-type f)
                             :multi
                             (map (partial cons (first f))
                                  (rest f))
                             :single
                             (list f)
                             ;; Default..
                             (throw (Exception.
                                     (str "Error with provided function form: "
                                          f))))))))))
         (swap! *component-deps* assoc ~nme ~deps)))))


;;;; Systems
(defmacro defsystem
  []
  )


(defmacro defcomponentsystem
  "Define a system to operate on components.
Params:
 [name comp-type docstring? [&inst-params] [&fn-params] & fn-body]  
"
  [nme comp-type & r]
  (let [docstring-seq (if (string? (first r)) (list (first r)))
        
        [inst-params fn-params & fn-body]
        (if docstring-seq (next r) r)
        
        ]
    `(defn ~nme
       ~@docstring-seq
       ~inst-params
       (with-meta
         (fn ~fn-params
           ~@fn-body)
         {:comp-type ~(if (class? (eval comp-type))
                        comp-type
                        `(var ~comp-type))}))))


;;;; The ECS creator
(defn make-ecs
  "Make an entity component system."
  [&{:keys [entities systems]}]
  {:entities (apply int-table entities)
   ;; TODO: How will the systems be arranged?
   })