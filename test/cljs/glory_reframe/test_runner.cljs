(ns glory-reframe.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [glory-reframe.core-test]
   [glory-reframe.common-test]))

(enable-console-print!)

(doo-tests 'glory-reframe.core-test
           'glory-reframe.common-test)
