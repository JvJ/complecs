(ns complecs.entity-component-system
  (:use [clojure.algo.generic.functor]))


;;;; TODO: Use defprotocol, defrecord, and extend to
;;;; create components with interfaces!!

(defmacro defcomponent
  "Define a component.  Components may be implemented as records
or as extensions of an existing type."
  [nme fields-or-type & orig-specs]


  (if (and (symbol? fields-or-type)
           (class? (eval fields-or-type)))

    ;; A class was provided
    ;; The extend form is weird and needs an associative
    ;; map instead of the regular forms.  So we need to
    ;; transform what we have.
    ;; TODO: How do we know when a type implements a protocol?
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
                           e)))))

          methods (fmap
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
    `(defrecord ~nme ~fields-or-type
       ~@orig-specs)))