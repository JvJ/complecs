(ns complecs.util
  (:require [clojure.set :as set]))

(defn f
  "Floating point literal."
  [x]
  (cond
   (seq? x) `(float ~@x)
   :else `(float ~x)))

(defn v
  "Vector literal."
  [[& r]]
  (vec r))


;;;; Table data structure.
;;;; For now, the table data structure is a map of maps.

(defn row
  "Get a row of the table.
 If only the key is passed, a row
lookup function is returned."
  ([r]
     #(row r %))
  ([r m]
     (get m r)))

(defn col
  "Get a column of the table."
  ([c]
     #(col c %))
  ([c m]
     (for [[rk r] m
           :let [x (get r c)]
           :when x]
       [rk x])))

(defn t-get
  "Get values in a table."
  [m &{:keys [r c]}]
  (cond
   (and r c) (get-in m [r c])
   r (row r m)
   c (col c m)
   :else m))

(defn t-update
  "Update a value or values in the map."
  [m f &{:keys [r c]}]
  (with-meta
    (cond
     (and r c) (update-in m [r c] f)
     r (update-in m [r] f)
     
     ;; This O(n) column update is making me angry!
     c (let [res (f (into {} (t-get m :c c)))]
         (println "f result: " res)
         (reduce
          (fn [acc k]
            (if-let [v (get res k)]
              (update-in acc [k] assoc c v)
              (if (contains? (get m k) c)
                (update-in acc [k] dissoc c)
                acc)))
          m
          (set/union (set (keys m))
                             (set (keys res))))))
    ;; The meta
    ;; TODO: This doesn't necessarily work!
    ;; What about removing rows/colums??
    ;; Actually... we'll just keep a full record of column keys!!
    (-> (meta m)
        (update-in [:row-keys]
                   set/union
                   (if r (sorted-set r)))
        (update-in [:col-keys]
                   set/union
                   (if c (sorted-set c))))))

(defn t-keys
  "Get the sets representing the keys for the table."
  [m]
  {:row-keys (:row-keys (meta m))
   :col-keys (:col-keys (meta m))})

(defn t-set
  "Set a value or values in the map."
  [m v & r]
  (apply t-update m (constantly v) r))

(defn int-table
  "Create a table where the rows are integers "
  [& r]
  (into (sorted-map)
        (map vector (range) r)))
