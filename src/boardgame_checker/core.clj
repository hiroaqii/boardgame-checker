(ns boardgame-checker.core
  (:require [net.cgrand.enlive-html :as html]
            [schema.core :as s]
            [taoensso.timbre :as log]
            [taoensso.timbre.appenders.core :as appenders]
            [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [twitter4j TwitterFactory])
  (:gen-class))

(log/merge-config!
 {:appenders
  {:spit (appenders/spit-appender {:fname "logs/info.log"})}})

(def twippa_url "https://twipla.jp")
(def twitter (. (TwitterFactory.) getInstance))
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
      :tags    (apply str (select html [:div.arrow_box]))
      :content (apply str (select html [:div#desc]))})))

(defn analog_game_event?
  [^Event event]
  (let [s (str (:title event) (:content event) (:tags event))]
    (some #(.contains s %) check_wards)))

(defn tweet [msg]
 (.updateStatus twitter msg))

(defn crawl
  [event_id]
  (try
    (let [event (scrape (str twippa_url "/events/" event_id))
          url (:url event)
          title (:title event)]
      (if (analog_game_event? event)
        (do
          (tweet (str title " " url))
          (log/info "[ok]" url title))
        (log/info "[skip]" url title)))
    (catch Exception e
      (log/info "[error]" e))))

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
    (- (latest_event_id) 10)))

(defn- save_last_check_id
  [event_id]
  (with-open [f (io/writer "logs/last_check_id.txt")]
    (.write f (str event_id))))

(defn -main
  [& args]
  (let [start_id (last_check_event_id "logs/last_check_id.txt")
        end_id (inc (latest_event_id))]
    (log/info "start_id:"start_id " end_id:"end_id)
    (doseq [event_id (range start_id end_id)]
      (crawl event_id)
      (Thread/sleep 5000))
    (save_last_check_id end_id)
    (log/info"finish")))

