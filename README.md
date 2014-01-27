enlive-cljhtml
========
Template engin for Clojure that uses [enlive] 


Installation
----
Add following dependency to your `project.clj`

    [enlive-cljhtml "0.0.2"]


Usage
----
### Require

```clj
(require '[enlive.cljhtml :as template])
```




### Root Directory

Set root directory for template in src (default: `nil`)

```clj
(template/cljhtml-root "template") ;; -> "src/template"
```




### Mode

Set mode (default: `:development`)

__:production__ (use template-cache)

```clj
(template/cljhtml-mode :production)
```

__:development__ (read templates every time)

```clj
(template/cljhtml-mode :development)
```




### Views

HTML `src/template/test.html`

```html
<div>abcdefg</div>
```

__:partial__

```clj
(template/views :partial "test.html")
;; -> parse "<div>abcdefg</div>"
```

__:render__ (append tag. html,head,body,etc...)

```clj
(template/views :render "test.html")
;; -> parse "<html><head></head><body><div>abcdefg</div></body></html>"
```




### &lt;clojure&gt; Tag

HTML `src/template/app.html`

```html
<html>
    <head></head>
    <body>
        <clojure>(template/html [:h1 "Title"])</clojure>
        <clojure>(template/views :partial "test.html")</clojure>
    </body>
</html>
```




### Render

__defrender__ macro

```clj
(defrender test [data]
    ;; base template
    (template/views :render "app.html")
    ;; selector & command
    [:h1] (template/prepend "Enlive")
    [:div] (template/append "hijklmn") 
    [:div] (template/append
               (template/html [:span (:content data)]))))
```

__render__ macro

```clj
(def test
    (template/render [data]
        ;; base template
        (template/views :render "app.html")
        ;; selector & command
        [:h1] (template/prepend "Enlive")
        [:div] (template/append "hijklmn") 
        [:div] (template/append
                   (template/html [:span (:content data)]))))
```

use


```clj
(test {:content "opqrstu"})
```

output

```html
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
```




### Reload Cache

When template file is added

```clj
(template/cljhtml-cache)
```




License
----
Copyright © 2014 Daigo Kawasaki (@emanon_was)

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.

