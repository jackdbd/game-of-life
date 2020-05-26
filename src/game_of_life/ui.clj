(ns game-of-life.ui
  "Animation logic and rendering for the Game of Life."
  (:require
   [clojure.spec.alpha :as s]
   [game-of-life.conway :refer [initial-cells new-board step]]
   [quil.core :as q]))

(s/def ::color number?)
(s/def ::generation pos-int?)

(s/def ::cell (s/coll-of int?))
(s/def ::cells (s/coll-of ::cell))
(s/def ::cell-height pos?)
(s/def ::cell-width pos?)

(s/def ::aspect-ratio pos?)
(s/def ::columns pos-int?)
(s/def ::rows pos-int?)
(s/def ::zoom pos?)
(s/def ::grid (s/keys :req [::aspect-ratio
                            ::columns
                            ::rows
                            ::zoom]))

(s/def ::state (s/keys :req [::cells
                             ::cell-height
                             ::cell-width
                             ::color
                             ::generation
                             ::grid]
                       :opt [::mouse-x
                             ::mouse-y]))

(def zoom-min 0.2)
(def zoom-max 4.0)

(defn reset-board
  [state]
  (assoc state ::cells (new-board) ::color 0 ::generation 1))

(defn setup-fn
  "Setup (pure function). It's called only once, during sketch initialization.
  Return the initial state."
  []
  (q/frame-rate 10)
  (q/color-mode :hsb)
  (let [aspect-ratio (/ (q/width) (q/height))
        rows 10
        columns (q/floor (* rows aspect-ratio))
        cell-width (quot (q/width) columns)
        cell-height (quot (q/height) rows)]
    {::cells initial-cells
     ::cell-height cell-height
     ::cell-width cell-width
     ::color 0
     ::generation 1
     ::grid {::aspect-ratio aspect-ratio
             ::columns columns
             ::rows rows
             ::zoom 1}}))

(defn calc-rows
  "Euristic to set a reasonable number of rows.
  Other ideas:
  - given a minimum row height (e.g. 20px), compute the number of rows for a
   particular screen height (only once), i.e.:
   rows-min = (q/screen-height) / row-min-height
   then use this rows-min to compute the number of rows for the sketch (recompute
   it every draw loop), i.e.:
   rows-min : (q/screen-height) = x : (q/height)
   Finally, apply the zoom to x.
  - define multiple steps, e.g. [0 100) -> 10, [100 200) -> 20, etc."
  [zoom]
  (if (> (q/height) 400)
    (q/floor (* zoom 20))
    (q/floor (* zoom 10))))

(defn update-fn
  "Update function. Called at each iteration. Receives the old state and
  produces the new state. It runs before the draw function."
  [state]
  (when-not (s/valid? ::state state)
    (throw (ex-info (s/explain-str ::state state)
                    (s/explain-data ::state state))))
  (let [cells (step (::cells state))
        color (mod (+ (::color state) 0.7) 255)
        generation (inc (::generation state))
        ;; The sketch might have been resized, so we recompute the aspect ratio
        aspect-ratio (/ (q/width) (q/height))
        zoom (get-in state [::grid ::zoom])
        rows (calc-rows zoom)
        ;; Whenever the aspect ratio changes, we need to recompute the grid
        columns (q/floor (* rows aspect-ratio))
        grid (assoc (::grid state) ::aspect-ratio aspect-ratio ::columns columns ::rows rows)
        cell-width (quot (q/width) columns)
        cell-height (quot (q/height) rows)]
    (assoc state ::cells cells ::cell-width cell-width ::cell-height cell-height ::color color ::generation generation ::grid grid)))

(defn draw-grid
  [{cw ::cell-width ch ::cell-height {:keys [::columns ::rows]} ::grid}]
  (q/background 240)
  (q/fill 240)
  (q/stroke 0)
  (q/stroke-weight 1)
  (doseq [x (range columns)
          y (range rows)]
    (q/rect (* x cw) (* y ch) cw ch)))

(defn draw-automata
  [{cells ::cells cw ::cell-width ch ::cell-height color ::color}]
  (q/fill color 255 255)
  (doseq [[x y] cells]
    (q/rect (* x cw) (* y ch) cw ch)))

(defn draw-label
  [{generation ::generation grid ::grid mx ::mouse-x my ::mouse-y}]
  (let [x (or mx (/ (q/width) 2))
        y (or my 10)
        w 400
        h 100
        x-text (+ 5 x)]
    (q/fill 255)
    (q/stroke-weight 1)
    (q/rect x y w h)
    (q/fill 0 0 0)
    (q/text-size 16)
    (q/text (format "Generation %d" generation) x-text (+ 5 y) w h)
    (q/text (format "Grid %d x %d (aspect=%.2f zoom=%.2f)" (q/width) (q/height) (float (::aspect-ratio grid)) (float (::zoom grid))) x-text (+ 25 y) w h)
    (q/text (format "%d rows" (::rows grid)) x-text (+ 45 y) w h)
    (q/text (format "%d columns" (::columns grid)) x-text (+ 65 y) w h)))

(defn draw-fn
  "Draw the Game of Life (function with side-effects)."
  [state]
  (draw-grid (select-keys state [::cell-height ::cell-width ::grid]))
  (draw-automata (select-keys state [::cells ::cell-height ::cell-width ::color]))
  (draw-label (select-keys state [::mouse-x ::mouse-y ::generation ::grid])))

(defn zoom-out
  [state]
  (let [old-zoom (get-in state [::grid ::zoom])
        zoom (min zoom-max (* 2 old-zoom))]
    (assoc-in state [::grid ::zoom] zoom)))

(defn zoom-in
  [state]
  (let [old-zoom (get-in state [::grid ::zoom])
        zoom (max zoom-min (* 0.2 old-zoom))]
    (assoc-in state [::grid ::zoom] zoom)))

(defn mouse-clicked
  [state {button :button}]
  (if (= :left button)
    (reset-board state)
    state))

(defn mouse-moved
  [state {x :x y :y}]
  (assoc state ::mouse-x x ::mouse-y y))

(defn mouse-wheel
  [state wheel-rotation]
  (if (= 1 wheel-rotation)
    (zoom-out state)
    (zoom-in state)))

(defn key-pressed
  [state event]
  (if (= :i (:key event))
    (reset-board state)
    state))