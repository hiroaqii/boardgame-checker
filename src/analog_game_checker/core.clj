(ns analog-game-checker.core
  (:require [net.cgrand.enlive-html :as html]
            [schema.core :as s]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def twippa_url "http://twipla.jp/")

(def check_text
  ["ボードゲーム"
   "ボドゲ"
   "アナログゲーム"
   "カードゲーム"
   "非電源ゲーム"
   "非電源系ゲーム"
   "テーブルトークRPG"
   "TRPG"])

(s/defrecord Event
    [url     :- s/Str
     day     :- s/Str
     title   :- s/Str
     content :- s/Str])
