(ns game-of-life.core
  "Conway's Game of Life implemented in Clojure and animated with Quil."
  (:gen-class)
  (:require
   [game-of-life.ui :as ui]
   [quil.core :as q]
   [quil.middleware :as m]))

; https://github.com/quil/quil/wiki/Dynamic-Workflow-(for-REPL)
; http://quil.info/api/environment#defsketch
;; (q/defsketch game-of-life
;;   :draw ui/draw-fn
;;   :features [:keep-on-top]
;;   :middleware [m/pause-on-error m/fun-mode]
;;   :mouse-clicked ui/mouse-clicked
;;   :mouse-moved ui/mouse-moved
;;   :mouse-wheel ui/mouse-wheel
;;   :settings #(q/smooth 2)
;;   :setup ui/setup-fn
;;   :size [800 600]
;;   :title "Conway's Game of Life"
;;   :update ui/update-fn)

; https://github.com/quil/quil/wiki/Runnable-jar
(defn -main []
  (q/sketch
   :draw ui/draw-fn
   :features [:resizable :exit-on-close]
   ;; :features [:present :exit-on-close]
   :key-pressed ui/key-pressed
   :middleware [m/fun-mode]
   :mouse-clicked ui/mouse-clicked
   :mouse-moved ui/mouse-moved
   :mouse-wheel ui/mouse-wheel
   :setup ui/setup-fn
   :size [800 600]
   ;; :size :fullscreen
   :title "Conway's Game of Life"
   :update ui/update-fn))
