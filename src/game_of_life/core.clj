(ns game-of-life.core
  (:gen-class)
  (:require
   [game-of-life.dynamic :as dynamic]
   [quil.core :as q]
   [quil.middleware :as m]))

; https://github.com/quil/quil/wiki/Dynamic-Workflow-(for-REPL)
(q/defsketch game-of-life
  :title "Conway's Game of Life"
  :size [800 600]
  :setup dynamic/setup-fn
  :update dynamic/update-fn
  :draw dynamic/draw-fn
  :features [:keep-on-top]
  :mouse-moved dynamic/mouse-moved
  :mouse-wheel dynamic/mouse-wheel
  :middleware [m/fun-mode])

; https://github.com/quil/quil/wiki/Runnable-jar
(defn -main []
  (q/sketch
   :title "Conway's Game of Life"
   :size [800 600]
   :setup dynamic/setup-fn
   :update dynamic/update-fn
   :draw dynamic/draw-fn
   :features [:exit-on-close]
   :mouse-moved dynamic/mouse-moved
   :mouse-wheel dynamic/mouse-wheel
   :middleware [m/fun-mode]))
