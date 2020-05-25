(defproject game-of-life "0.1.0-SNAPSHOT"
  :description "Conway's Game of Life with Clojure and Quil."
  :url "https://github.com/jackdbd/game-of-life"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [quil "3.1.0"]]
  :main game-of-life.core
  :target-path "target/%s"
  :uberjar-name "game-of-life-standalone.jar"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[io.aviso/pretty "0.1.37"]
                                  [pjstadig/humane-test-output "0.10.0"]]
                   :injections [(require 'pjstadig.humane-test-output)
                                (pjstadig.humane-test-output/activate!)]
                   :middleware [io.aviso.lein-pretty/inject]
                   :plugins [[com.jakemccrary/lein-test-refresh "0.24.1"]
                             [io.aviso/pretty "0.1.37"]
                             [jonase/eastwood "0.3.10"]
                             [lein-cljfmt "0.6.7" :exclusions [org.clojure/clojure]]]}})
