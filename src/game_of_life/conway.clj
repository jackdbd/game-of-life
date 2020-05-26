(ns game-of-life.conway
  "Rules for the Game of Life.")

(defn neighbours
  "Moore neighborhood of a cellular automata."
  [[x y]]
  (for [dx [-1 0 1]
        dy (if (zero? dx) ; because a cell is not a neighour of itself
             [-1 1]
             [-1 0 1])]
    [(+ x dx) (+ y dy)]))

(defn will-live?
  "Test whether the cell will live or not.
  The Game of Life rules state that a cell will live if:
  - it has 3 neighbours, or
  - it has 2 neighbours and is currently alive.
  In any other case the cell will die."
  [n cell]
  (let [alive? (not (nil? cell))]
    (or (= n 3)
        (and (= n 2) alive?))))

(defn step
  "Produce the next generation."
  [cells]
  (let [m (frequencies (mapcat neighbours cells))
        lzseq (for [[cell n] m :when (will-live? n (cells cell))]
                cell)]
    (set lzseq)))

(def initial-cells #{[6 2] [6 3] [6 4] ;; blinker
                     [2 0] [3 1] [1 2] [2 2] [3 2] ; glider
                     [10 1] [9 2] [9 3] [11 2] [11 3] [10 4] ; beehive
                     })

(defn new-board
  []
  #{[2 0] [3 1] [1 2] [2 2] [3 2]})
