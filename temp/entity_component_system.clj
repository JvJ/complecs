(ns complecs.entity-component-system
  (:use [clojure.algo.generic.functor]
        [complecs.util]))



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
   
           
   

(defmacro defcomponent
  "Define a component.  Components may be implemented as records
or as extensions of an existing type."
  [nme fields-or-type & orig-specs]

  (let [specs (try
                (->>
                 orig-specs
                 (partition-by symbol?)
                 (map #(if (and (seq? %)
                                  (symbol? (first %)))
                         (first %)
                         %))
                 (apply hash-map))
                (catch Exception e
                  (throw
                     (Exception.
                      (str "defcomponent: specification format error: "
                           e)))))]
    
    (if (symbol? fields-or-type)
             
      
      ;; A class was provided
      ;; The extend form is weird and needs an associative
      ;; map instead of the regular forms.  So we need to
      ;; transform what we have.
      ;; TODO: How do we know when a type implements a protocol?
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
           (def ~nme ~fields-or-type)
           (extend ~fields-or-type
             ~@(apply concat methods))))
      
      ;; A class was not provided
      ;; We just treat it as a normal defrecord
      ;; Todo: break up multi-implementation functions into
      ;; a sequence of single-implementation functions

      `(defrecord ~nme ~fields-or-type
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
                                        f)))))))))))))