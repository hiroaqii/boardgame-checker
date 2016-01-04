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

(defn- select
  [html selecter]
  (->> (html/select html selecter)
       first
       :content
       (html/texts)))

(defn scrape
  [url]
  (let [ret (html/html-resource (io/reader url))]
    (map->Event
     {:url     url
      :title   (last (select ret [:h1.largetext2]))
      :day     (apply str (select ret [:span.largetext]))
      :content (apply str (select ret [:div#desc]))})))
