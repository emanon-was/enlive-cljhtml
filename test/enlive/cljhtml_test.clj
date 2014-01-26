(ns enlive.cljhtml-test
  (:gen-class)
  (:use clojure.test)
  (:require [enlive.cljhtml :as cljhtml]))

(deftest test-all

  (require '[enlive.cljhtml :as cljhtml])
  (is
   (= cljhtml/template-ns
      :enlive.cljhtml))
  (is
   (= (deref cljhtml/template-mode)
      {:enlive.cljhtml :development}))
  (is
   (= (deref cljhtml/template-root)
      {:enlive.cljhtml nil}))

  ;; test/render.html
  (is
   (= (cljhtml/views :partial "test/render.html")
      '({:tag :div,
         :attrs nil,
         :content
         [{:tag :div,
           :attrs nil,
           :content ["abcdef"]}]})))
  (is
   (= (cljhtml/views :render "test/render.html")
      '({:tag :html,
         :attrs nil,
         :content
         [{:tag :body,
           :attrs nil,
           :content
           [{:tag :div,
             :attrs nil,
             :content
             [{:tag :div,
               :attrs nil,
               :content ["abcdef"]}]}
            "\n"]}]})))
  ;; test/partial.html
  (is
   (= (cljhtml/views :partial "test/partial.html")
      '({:tag :div,
         :attrs nil,
         :content ["abcdef"]})))
  (is
   (= (cljhtml/views :render "test/partial.html")
      '({:tag :html,
         :attrs nil,
         :content
         [{:tag :body,
           :attrs nil,
           :content
           [{:tag :div,
             :attrs nil,
             :content ["abcdef"]}
            "\n"]}]})))


  (cljhtml/cljhtml-mode :production)

  (is
   (= (deref cljhtml/template-mode)
      {:enlive.cljhtml :development,
       :user :production}))
  (is
   (= (deref cljhtml/template-root)
      {:enlive.cljhtml nil}))

  ;; test/mode-render.html
  (is
   (= (cljhtml/views :partial "test/mode-render.html")
      '({:tag :div,
         :attrs nil,
         :content
         [{:tag :div,
           :attrs nil,
           :content ["abcdef"]}]})))
  (is
   (= (cljhtml/views :render "test/mode-render.html")
      '({:tag :html,
         :attrs nil,
         :content
         [{:tag :body,
           :attrs nil,
           :content
           [{:tag :div,
             :attrs nil,
             :content
             [{:tag :div,
               :attrs nil,
               :content ["abcdef"]}]}
            "\n"]}]})))
  ;; test/mode-partial.html
  (is
   (= (cljhtml/views :partial "test/mode-partial.html")
      '({:tag :div,
         :attrs nil,
         :content ["abcdef"]})))
  (is
   (= (cljhtml/views :render "test/mode-partial.html")
      '({:tag :html,
         :attrs nil,
         :content
         [{:tag :body,
           :attrs nil,
           :content
           [{:tag :div,
             :attrs nil,
             :content ["abcdef"]}
            "\n"]}]})))


  (cljhtml/cljhtml-root "test")
  (is
   (= (deref cljhtml/template-mode)
      {:enlive.cljhtml :development,
       :user :production}))
  (is
   (= (deref cljhtml/template-root)
      {:enlive.cljhtml nil,
       :user "test"}))

  ;; test/root-render.html
  (is
   (= (cljhtml/views :partial "root-render.html")
      '({:tag :div,
         :attrs nil,
         :content
         [{:tag :div,
           :attrs nil,
           :content ["abcdef"]}]})))
  (is
   (= (cljhtml/views :render "root-render.html")
      '({:tag :html,
         :attrs nil,
         :content
         [{:tag :body,
           :attrs nil,
           :content
           [{:tag :div,
             :attrs nil,
             :content
             [{:tag :div,
               :attrs nil,
               :content ["abcdef"]}]}
            "\n"]}]})))
  ;; test/partial.html
  (is
   (= (cljhtml/views :partial "root-partial.html")
      '({:tag :div,
         :attrs nil,
         :content ["abcdef"]})))
  (is
   (= (cljhtml/views :render "root-partial.html")
      '({:tag :html,
         :attrs nil,
         :content
         [{:tag :body,
           :attrs nil,
           :content
           [{:tag :div,
             :attrs nil,
             :content ["abcdef"]}
            "\n"]}]}))))

