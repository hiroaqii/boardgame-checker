(defproject boardgame-checker "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.taoensso/timbre "4.2.0"]
                 [prismatic/schema "1.0.4"]
                 [twitter4clojure "0.2.1"]
                 [enlive "1.1.6"]]
  :profiles {:uberjar {:aot :all}}
  :main boardgame-checker.core)
