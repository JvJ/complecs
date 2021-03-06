(ns complecs.ecs-test
  (:use clojure.test
        complecs.core))


;;;; First, we define a normal component
(defcomponent RawPosition [x y])

;;;; Next, we define a vector protocol that
;;;; is implemented by 2 components
(defprotocol Vector
  (mag [this])
  (v+ [this] [this other]))

;;; First, we have the map implementation
(defcomponent Vec2 [x y]
  
  Vector
  (mag
   [this]
   (Math/sqrt
    (+ (* x x) (* y y))))
  
  (v+
   ([this]
      this)
    ([this other]
       (Vec2. (+ x (:x other))
              (+ y (:y other))))))

(defcomponent String :extend

  :deps  (String Integer)
  
  Vector
  (mag
   [this]
   (count this))
  
  (v+
   ([this] this)
   ([this other] (str this other))))

(def test-entity
  (entity "Hello" (Vec2. 1 3)))

(defcomponentsystem
  vector-update
  Vector
  []
  [ecs ent cmp]
  "The function body!!")


(make-ecs
 :entities
 [ (entity (Vec2. 1 2)) ]
 )