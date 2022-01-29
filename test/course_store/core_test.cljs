(ns course-store.core-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [course-store.core :as core]))

(deftest fake-test
  (testing "fake description"
    (is (= 1 2))))
