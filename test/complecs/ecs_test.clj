(ns complecs.ecs-test
  (:use clojure.test
        complecs.core))


;;;; First, we define a normal component
(defcomponent RawPosition [x y])

;;;; Next, we define a vector protocol that
;;;; is implemented by 2 components
(defprotocol Vector
  (mag [this])
  (v+ [this] [this other] [this a b & c]))

;;; First, we have the map implementation
(defcomponent Vec2 [x y]

  Vector
  (mag [this]
       (Math/sqrt
        (+ (* x x) (* y y))))
  
  (v+ [this] this)
  (v+ [this other]
      (Vec2. (+ (:x this) (:x other)
                (:y this) (:y other))))
  (v+ [this a b & c]
      (reduce v+
              this
              (conj c b a))))