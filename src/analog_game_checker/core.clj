(ns analog-game-checker.core
  (:require [net.cgrand.enlive-html :as html]
            [schema.core :as s]
            [clojure.java.io :as io]
            [clojure.string :as str]))

(def twippa_url "http://twipla.jp")

(def check_wards
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
     date    :- s/Str
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
  (let [html (html/html-resource (io/reader url))]
    (map->Event
     {:url     url
      :title   (last (select html [:h1.largetext2]))
      :date    (apply str (select html [:span.largetext]))
      :content (apply str (select html [:div#desc]))})))

(defn analog_game_event?
  [^Event event]
  (let [s (str (:title event) (:content event))]
    (some #(.contains s %) check_wards)))

(defn crawl
  [event_id]
  (try
    (let [event (scrape (str twippa_url "/events/" event_id))
          url (:url event)
          title (:title event)]
      (if (analog_game_event? event)
        (println "[ok]" url title)
        (println "[skip]" url title)))
    (catch Exception e
      (println "[error]" event_id))))

(defn latest_event_id []
  (let [ret (html/html-resource (io/reader twippa_url))]
    (-> ret
        (html/select #{[:ol.links :li html/first-child]})
        first
        (get-in [:attrs :href])
        (str/split #"/")
        last
        Integer/parseInt)))

(defn last_check_event_id [s]
  (println s)
  (if (.exists (io/file s))
    (with-open [f (io/reader s)]
      (Integer/parseInt (first (line-seq f))))
    (- (latest_event_id) 100)))
