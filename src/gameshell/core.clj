(ns gameshell.core
  (:gen-class))
;-------------------------------------------------------;
; meta-state information

(def commandlist (ref()))

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

;someone else's code really I don't really understand it. Here until I do.
(defmacro add-action [command subj obj place & args]
  `(defmacro ~command [subject# object#]
    `(println (cond (and (= location '~'~place)
                         (= '~subject# '~'~subj)
                         (= '~object# '~'~obj)
                         (have? '~'~subj))
                    ~@'~args
                    :else '(I cannot ~'~command like that -)))))

; print the commandlist
(defn printcommands [] (map println @commandlist))

(defn commandlist-bump [command]
  (println "Adding" command)
  (dosync (alter commandlist conj command)))

; Thread safe now, just doesn't work :P
(defn add-inaction [command & args]
  (cond 
    (not (some #{command} @commandlist))
         (do
           (commandlist-bump command))
    :else (println "Already in command list")))


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
