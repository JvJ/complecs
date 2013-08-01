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