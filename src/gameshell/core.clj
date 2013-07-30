(ns gameshell.core
  (:gen-class))
;-------------------------------------------------------;
; meta-state information

(def commandlist nil)

;-------------------------------------------------------;
; game loop and validation

(defn valid? [line] false)

; This isn't good enough yet
(defn input []
  (print "> ")
  (if-let [v (valid? (read-line))]
    v
    (do
      (println "That is not valid")
      (recur))))

(defmacro add-action [command subj obj place & args]
  `(defmacro ~command [subject# object#]
    `(println (cond (and (= location '~'~place)
                         (= '~subject# '~'~subj)
                         (= '~object# '~'~obj)
                         (have? '~'~subj))
                    ~@'~args
                    :else '(I cannot ~'~command like that -)))))

; Currently not thread-safe FIXME: decide if I care
(defmacro add-inaction [command & args]
  `(defmacro ~command []
     `println((def commandlist (conj commandlist '~'~command)))
     `(~@'~args)))

;------------------------------------------------------;
; functions

(defn describe-path [path]
  `(there is a ~(second path) going ~(first path) from here -))

; Depends heavily on game-map data ordering. i.e. FIXME
(defn describe-paths [location game-map]
  (apply concat (map describe-path (rest (get game-map location)))))
;------------------------------------------------------;
; immutables

(def game-map (hash-map
                'room '("you are in a room"
                          (west door anotherroom)
                          (upstairs ladder attic))))

;------------------------------------------------------;
; state information

(def object-locations (hash-map
                        'turd 'room))

(def location 'living-room)

;------------------------------------------------------;
; main
(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  ;; work around dangerous default behaviour in Clojure
  (alter-var-root #'*read-eval* (constantly false))
  (println "Hello, World!")
  (input))
