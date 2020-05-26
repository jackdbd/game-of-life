(ns game-of-life.conway-test
  (:require [clojure.test :refer [deftest is testing]]
            [game-of-life.conway :refer [neighbours step will-live?]]))

(deftest will-live?-alive-cell-test
  (testing "An alive cell will die if it has 1 neighbour"
    (is (= false (will-live? 1 [0 0]))))
  (testing "An alive cell will live if it has 2 neighbours"
    (is (= true (will-live? 2 [0 0]))))
  (testing "An alive cell will live if it has 3 neighbours"
    (is (= true (will-live? 2 [0 0]))))
  (testing "An alive cell will die if it has 4 neighbours"
    (is (= false (will-live? 4 [0 0])))))

(deftest will-live?-dead-cell-test
  (testing "A dead cell will stay dead if it has 1 neighbour"
    (is (= false (will-live? 1 nil))))
  (testing "A dead cell will stay dead if it has 2 neighbours"
    (is (= false (will-live? 2 nil))))
  (testing "A dead cell will come to life if it has 3 neighbours"
    (is (= true (will-live? 3 nil))))
  (testing "An alive cell will stay dead if it has 4 neighbours"
    (is (= false (will-live? 4 nil)))))

(deftest neighbours-test
  (testing "Any cell has 8 neighbours."
    (is (= 8 (count (neighbours [0 0]))))
    (is (= 8 (count (neighbours [1 3]))))
    (is (= 8 (count (neighbours [123 456]))))))

; https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life#Examples_of_patterns
(deftest blinker-test
  (let [blinker #{[2 1] [2 2] [2 3]}]
    (testing "A blinker alternates between 2 states."
      (is (= blinker (step (step blinker)))))))

(deftest beehive-test
  (let [beehive #{[2 1] [1 2] [1 3] [2 4] [3 2] [3 3]}]
    (testing "A beehive never changes state."
      (is (= beehive (step beehive)))
      (is (= beehive (step (step beehive))))
      (is (= beehive (step (step (step beehive))))))))

(defn sorted-cells-by-xy-sum
  [cells]
  (sort (map (fn [cell] [(apply + cell) cell]) cells)))

; https://www.conwaylife.com/wiki/Glider
(deftest glider-test
  (let [glider #{[1 2] [2 3] [3 1] [3 2] [3 3]}
        n (count glider)
        generations (take 5 (iterate step glider))]
    (testing "A glider keeps the same number of alive cells across generations."
      (is (= 5 (count generations)))
      (is (= n (count (nth generations 2))))
      (is (= n (count (nth generations 3))))
      (is (= n (count (nth generations 4))))
      (is (= n (count (last generations)))))
    (testing "A glider travels 1 cell diagonally downward from generation 0 to 4."
      (let [[xy-sum-0 cell-0] (last (sorted-cells-by-xy-sum glider))
            glider-4 (last generations)
            [xy-sum-4 cell-4] (last (sorted-cells-by-xy-sum glider-4))]
        (is (= 6 xy-sum-0))
        (is (= [3 3] cell-0))
        (is (= 8 xy-sum-4))
        (is (= [4 4] cell-4))))))
