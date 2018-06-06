(ns glory-reframe.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub :name (fn [db] (:name db)))

(re-frame/reg-sub :game (fn [db] (-> db :game-state )))

(re-frame/reg-sub :players (fn [ { { players :players} :game-state } ] players ))

(re-frame/reg-sub :units (fn [ { { units :units} :game-state } ] units ))

(re-frame/reg-sub :planets (fn [ { { planets :planets} :game-state } ] planets ))
