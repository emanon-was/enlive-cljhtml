enlive-cljhtml
========
Template engin for Clojure that uses [enlive] 

Installation
-----
Add following dependency to your __project.clj__    

    [enlive-cljhtml "0.0.1"]

Usage
----
1. Require

        (require [enlive.cljhtml :as template])

2. Set root directory for template in src (default: nil)

        (template/cljhtml-root "template") ;; -> "src/template"

3. Set mode (default: :development)   

    __:production__ (use template-cache)

        (template/cljhtml-mode :production)

    __:development__ (read templates every time)

         (template/cljhtml-mode :development)

4. Parsed views

    "src/template/test.html"

        <div>abcdefg</div>

    __:partial__ (append tag. html,head,body,etc...)

        (template/views :partial "test.html")
        ;; -> parse "<div>abcdefg</div>"

    __:render__ (append tag. html,head,body,etc...)

        (template/views :render "test.html")
        ;; -> parse "<html><head></head><body><div>abcdefg</div></body></html>"

5. Embed __&lt;clojure&gt;__ tag

    "src/template/app.html"

        <html>
            <head></head>
            <body>
                <clojure>(template/html [:h1 "Title"])</clojure>
                <clojure>(template/views :partial "test.html")</clojure>
            </body>
        </html>

6. Render macro

    __defrender__ macro

        (defrender test [data]
            ;; base template
            (views :render "app.html")
            ;; selector & command
            [:h1] (template/prepend "Enlive")
            [:div] (template/append "hijklmn") 
            [:div] (template/append
                       (template/html [:span (:content data)]))))

    __render__ macro

        (def test
            (template/render [data]
                ;; base template
                (views :render "app.html")
                ;; selector & command
                [:h1] (template/prepend "Enlive")
                [:div] (template/append "hijklmn") 
                [:div] (template/append
                           (template/html [:span (:content data)]))))
   use

        (test {:content "opqrstu"})

    output

        <html>
            <head></head>
            <body>
                <h1>EnliveTitle</h1>
                <div>
                    abcdefghijklmn
                    <span>opqrstu</span>
                </div>
            </body>
        </html>



License
-----
Copyright © 2014 Daigo Kawasaki (@emanon_was)

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.