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

(defn int-table
  "Create a table where the rows are integers "
  [& r]
  (into {}
        (map vector (range) r)))