(ns game-of-life.conway)

(defn neighbours
  "Neighbours of a cell."
  [[x y]]
  (for [dx [-1 0 1]
        dy (if (zero? dx) ; because a cell is not a neighour of itself
             [-1 1]
             [-1 0 1])]
    [(+ x dx) (+ y dy)]))

(defn will-live?
  "Test whether the cell will live or not.
   The Conway's Game of Life rules state that a cell will live if:
   - it has 3 neighbours, or
   - it has 2 neighbours and is currently alive."
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
