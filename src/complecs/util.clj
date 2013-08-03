(ns complecs.util)

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
        (clojure.set/union (set (keys m))
                           (set (keys res)))))))

(defn t-set
  "Set a value or values in the map."
  [m v & r]
  (apply t-update m (constantly v) r))

(defn int-table
  "Create a table where the rows are integers "
  [& r]
  (into (sorted-map)
        (map vector (range) r)))
