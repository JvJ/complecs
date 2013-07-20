(defproject complecs "0.1.0-SNAPSHOT"
  :description "FIXME: write description"

  :url "http://example.com/FIXME"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :repositories [["libgdx" "http://libgdx.badlogicgames.com/nightlies/maven/"]]
  
  :plugins [[lein-swank "1.4.5"]]

  :java-source-paths ["src/java"]

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [org.clojure/algo.generic "0.1.1"]
                 [org.clojure/algo.monads "0.1.4"]
                 [org.clojure/core.incubator "0.1.2"]
                 [com.badlogic.gdx/gdx "0.9.9-SNAPSHOT"]
                 [com.badlogic.gdx/gdx-backend-lwjgl "0.9.9-SNAPSHOT"]
                 ])
