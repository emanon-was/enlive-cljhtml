;;
;; in project.clj
;;
;; [enlive "1.1.5"]
;;

;;
;; 1. <clojure> tag
;;    Example:
;;      <clojure>
;;        (html [:h1 "Hello, world!"])
;;      </clojure>
;;
;; 2. render(partial) in <clojure> tag
;;    Example:
;;      <clojure>
;;        (views :partial "test.html")
;;      </clojure>
;;
;; 3. render macro
;;    Example:
;;      (def test
;;        (render [req]
;;           (views :render "test.html")
;;           [:#content] (prepend (html [:div.ip (req :remote-addr)]))
;;           [:div.info] (append  (html [:div.params (req :params)]))))
;;      in handler
;;      (GET "/" req (test req))
;;
;; 4. set mode to load cljhtml
;;    [:production]  => use *cljhtml-cache*
;;    [:development] => read cljhtml every time
;;    Default:
;;      :development
;;    Example:
;;      (cljhtml-mode :production)
;;
;; 5. set ns to run cljhtml
;;    Default:
;;      'enlive.cljhtml
;;    Example:
;;      (cljhtml-ns 'application.controller)
;;
;; 6. set root directory (in "src") to cljhtml
;;    Default:
;;      nil
;;    Example:
;;      (cljhtml-root "application/templates")
;;
;; 7. set use-ns in ns to run cljhtml
;;    Example:
;;      (cljhtml-use 'application.helper)
;;
;; 8. set require-ns in ns to run cljhtml
;;    Example:
;;      (cljhtml-require 'application.helper)
;;

(ns enlive.cljhtml
  (:gen-class)
  (:require net.cgrand.enlive-html))

;;
;; inherit net.cgrand.enlive-html
;;

(defn- inherit-var [var]
  (eval
   (let [meta-data (meta var)
         name      (meta-data :name)
         macro     (meta-data :macro)
         dynamic   (meta-data :dynamic)]
     `(def ~(with-meta name
              (assoc (meta name)
                :macro   macro
                :dynamic dynamic)) ~var))))

(defn- inherit-ns [ns]
  (let [vars (vals (ns-publics ns))]
    (doseq [v vars] (inherit-var v))))

(inherit-ns 'net.cgrand.enlive-html)

;;
;; parameters
;;

(def template-mode  (ref {}))
(def template-root  (ref {}))
(def template-cache (ref {}))

;;
;; utilities
;;

(defn- ns-keyword []
  (keyword (ns-name *ns*)))

(defmacro ref-set! [name & body]
  `(dosync (ref-set ~name ~@body)))

(def template-ns (ns-keyword))
(ref-set! template-mode  (assoc @template-mode  template-ns :development))
(ref-set! template-root  (assoc @template-root  template-ns nil))
(ref-set! template-cache (assoc @template-cache template-ns nil))

(defn- use-mode []
  (let [nskey (ns-keyword)]
    (or (@template-mode nskey)
        (@template-mode template-ns))))

(defn- use-root []
  (let [nskey (ns-keyword)]
    (or (@template-root nskey)
        (@template-root template-ns))))

(defn- use-cache []
  (let [nskey (ns-keyword)]
    (or (@template-cache nskey)
        (@template-cache template-ns))))

(defn- html-partial [resource]
  (let [nodes (html-resource resource)]
    (concat (select nodes [:head :> :*])
            (select nodes [:body :> :*]))))

(defn- html-files []
  (filter #(re-matches #".*\.html|.*\.cljhtml" (.getName %))
          (file-seq (clojure.java.io/file (str "src/" (use-root))))))

(defn html-expand [nodes]
  (apply str (emit* nodes)))

;;
;; macros
;;

(defmacro template-snippet [nodes & body]
  (let [nodesym (gensym "nodes")]
    `(let [~nodesym (map annotate ~nodes)]
       (doall (flatmap (transformation ~@body) ~nodesym)))))

(defmacro render* [args nodes & body]
  `(fn ~args
     (template-snippet ~nodes ~@body)))

(defmacro render [args nodes & body]
  `(fn ~args
     (html-expand
      (template-snippet ~nodes ~@body))))

(defmacro defrender [name args nodes & body]
  `(def ~name (render ~args ~nodes ~@body)))

;;
;; private functions
;;

(defn- cljhtml-render [nodes]
  (render* [] nodes
   [:clojure] #(load-string (first (% :content)))))

(defn- cljhtml-name [path]
  (->> path
       (re-find (re-pattern (str "src/" (use-root) "/?(.*)")))
       second))

(defn- resource-name [path]
  (->> path (re-find #"src/(.*)") second))

(defn- cljhtml-map [f coll]
  (reduce (fn [map path]
             (assoc map
               (cljhtml-name path)
               (cljhtml-render (f (resource-name path)))))
           {} coll))

(defn- make-cljhtml-cache []
  (let [paths (map #(.getPath %) (html-files))]
    {:render  (cljhtml-map html-resource paths)
     :partial (cljhtml-map html-partial  paths)}))

(defn- cljhtml-cache []
  (let [nskey (ns-keyword)]
    (ref-set! template-cache
              (assoc @template-cache nskey
                     (make-cljhtml-cache)))
    (@template-cache nskey)))

(defn- cljhtml-views []
  (let [func {:render html-resource :partial html-partial}
        mode (use-mode)
        root (use-root)]
    (cond (= mode :development)
          (defn views [option path]
            ((cljhtml-render
              ((func option)
               (if root (str root "/" path) path)))))
          (= mode :production)
          (defn views [option path]
            ((((or (use-cache) (cljhtml-cache)) option) path))))))

;;
;; accessor
;;

(defn cljhtml-mode [development-or-production]
  (let [mode (if (or (= :production  development-or-production)
                     (= :development development-or-production))
               development-or-production nil)]
    (ref-set! template-mode
              (assoc @template-mode (ns-keyword) mode))
    (cljhtml-cache)
    (cljhtml-views)
    mode))

(defn cljhtml-root [directory-in-src]
  (ref-set! template-root
            (assoc @template-root (ns-keyword) directory-in-src))
  (cljhtml-cache)
  (cljhtml-views)
  directory-in-src)


;;
;; etc
;;

(cljhtml-cache)
(cljhtml-views)

