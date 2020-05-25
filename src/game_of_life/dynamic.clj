(ns game-of-life.dynamic
  (:require
   [clojure.spec.alpha :as s]
   [game-of-life.conway :refer [step]]
   [quil.core :as q]))

(s/def ::generation pos-int?)

(s/def ::color number?)

(s/def ::cell (s/coll-of int?))
(s/def ::cells (s/coll-of ::cell))

(s/def ::num-cells-per-row pos-int?)
(s/def ::num-cells-per-column pos-int?)
(s/def ::grid (s/keys :req [::num-cells-per-row
                            ::num-cells-per-column]))

(s/def ::state (s/keys :req [::cells
                             ::color
                             ::generation
                             ::grid]))

(def zoom-factor 2)
(def min-rows 8)
(def min-columns 6)
(def max-rows 200)
(def max-columns 100)

;; TODO:
;; - random automata on mouse click
;; - double check num rows and num columns
;; - improve zoom
;; - use cljc and make a version for clojurescript
;; - save PDF when a button or a location on the screen is pressed
;; - use with-graphics macro
;; - use nicer font family: create-font? load-font?

(def initial-cells #{[6 2] [6 3] [6 4] ;; blinker
                     [2 0] [3 1] [1 2] [2 2] [3 2] ; glider
                     [10 1] [9 2] [9 3] [11 2] [11 3] [10 4] ; beehive
                     })

(defn setup-fn
  "setup function called only once, during sketch initialization."
  []
  (q/frame-rate 10)
  (q/color-mode :hsb)
  {::cells initial-cells
   ::color 0
   ::generation 1
   ::grid {::num-cells-per-row 10 ::num-cells-per-column 8}})

(defn update-fn
  "update function called on each iteration before the draw function."
  [state]
  (when-not (s/valid? ::state state)
    (throw (ex-info (s/explain-str ::state state)
                    (s/explain-data ::state state))))
  ;; (prn "STATE" state)
  (let [cells (step (::cells state))
        color (mod (+ (::color state) 0.7) 255)
        generation (inc (::generation state))]
    (-> state
        (assoc ::cells cells ::color color ::generation generation))))

(defn draw-grid
  [state]
  (when-not (s/valid? ::state state)
    (throw (ex-info (s/explain-str ::state state)
                    (s/explain-data ::state state))))
  (let [grid (::grid state)
        n-cw (::num-cells-per-column grid)
        n-ch (::num-cells-per-row grid)
        cw (quot (q/width) n-cw)
        ch (quot (q/height) n-ch)]
    (q/background 240)
    (q/fill 240)
    (q/stroke 0)
    (q/stroke-weight 1)

    (doseq [x (range n-cw)
            y (range n-ch)]
      (q/rect (* x cw) (* y ch) cw ch))))

(defn draw-automata
  [state]
  (let [grid (::grid state)
        n-cw (::num-cells-per-column grid)
        n-ch (::num-cells-per-row grid)
        cw (quot (q/width) n-cw)
        ch (quot (q/height) n-ch)]
    (q/fill (::color state) 255 255)
    (doseq [[x y] (::cells state)]
      (q/rect (* x cw) (* y ch) cw ch))))

(defn draw-label
  [state]
  (let [x (or (:x state) (/ (q/width) 2))
        y (or (:y state) 10)
        width 300
        height 100]
    (q/fill 255)
    (q/stroke-weight 1)
    (q/rect x y width height)
    (q/fill 0 0 0)
    (q/text-size 16)
    (q/text (format "Generation %d" (::generation state)) (+ 5 x) (+ 5 y) width height)
    (q/text (format "%d rows" (get-in state [::grid ::num-cells-per-row])) (+ 5 x) (+ 35 y) width height)
    (q/text (format "%d columns" (get-in state [::grid ::num-cells-per-column])) (+ 35 x) (+ 55 y) width height)))

(defn draw-fn
  "Draw function, i.e. the render loop."
  [state]
  (draw-grid state)
  (draw-automata state)
  (draw-label state))

(defn zoom-out [state]
  (let [nrows-old (get-in state [::grid ::num-cells-per-row])
        ncols-old (get-in state [::grid ::num-cells-per-column])
        nrows (min max-rows (int (Math/floor (* zoom-factor nrows-old))))
        ncols (min max-columns (int (Math/floor (* zoom-factor ncols-old))))]
    ;; (prn "zooming OUT" nrows-old nrows ncols-old ncols)
    (-> state
        (assoc-in [::grid ::num-cells-per-row] nrows)
        (assoc-in [::grid ::num-cells-per-column] ncols))))

(defn zoom-in [state]
  (let [nrows-old (get-in state [::grid ::num-cells-per-row])
        ncols-old (get-in state [::grid ::num-cells-per-column])
        nrows (max min-rows (int (Math/ceil (/ nrows-old zoom-factor))))
        ncols (max min-columns (int (Math/ceil (/ ncols-old zoom-factor))))]
    ;; (prn "zooming IN" nrows-old nrows ncols-old ncols)
    (-> state
        (assoc-in [::grid ::num-cells-per-row] nrows)
        (assoc-in [::grid ::num-cells-per-column] ncols))))

(defn mouse-moved [state event]
  (assoc state :x (:x event) :y (:y event)))

(defn mouse-wheel [state wheel-rotation]
  (if (= 1 wheel-rotation)
    (zoom-out state)
    (zoom-in state)))
