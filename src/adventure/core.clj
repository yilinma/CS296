;; This project was auto formatted by cljfmt
(ns adventure.core
  (:require [clojure.core.match :refer [match]]
            [clojure.string :as str]
            [clojure.set :as set])
  (:gen-class))

(def the-map
  {:basement {:desc " The room is very dark and there is only a lit candle. There is also an entrance to a dry well beside you. Type search to search around the room. Directions: up, down. "
              :title "in the basement"
              :dir {:down :well :up :hallway}
              :contents #{:mail}}
   :well {:desc "Surrounded by 3 crocodiles in the dry well. Bad End. Enter ^C to exit the game. "
          :title "died"
          :dir {}
          :contents #{}}
   :hallway {:desc "The hallway has 2 doors on your left numbered with 1,2 and 2 doors on your right numbered with 3,4 and a locked door in the end of the hallway. Type down to go to the basement, room1 to enter the first room, room2 to enter the second room, room3 to enter the third room, room4 to enter the fourth room, unlock to open the locked door and south to enter that door. "
             :title "in the hallway"
             :dir {:down :basement :room1 :bedroom1 :room2 :bedroom2 :room3 :bedroom3 :room4 :bedroom4 :south :mainroom}
             :contents #{}}
   :bedroom1 {:desc "There is an old television on the desk. However, the TV is turned off and you couldn't find the switch to turn it on. Type back to leave this room. "
              :title "in room1"
              :dir {:back :hallway}
              :contents #{}}
   :bedroom2 {:desc "This room is the Karaoke room. Enter search to search around this room and back to leave the room. "
              :title "in the Karaoke room"
              :dir {:search :search :back :hallway}
              :contents #{:remote}}
   :bedroom3 {:desc "This room has a drawing of grassland on the wall and a safe box locked by a number lock on the floor. Type openbox to try open the box. "
              :title "in the exhibition room"
              :dir {:back :hallway}
              :contents #{:silverkey}}
   :bedroom4 {:desc "This room is a normal bedroom with a large bed that looks very comfortable. Type sleep to get some rest or back to leave the room. "
              :title "in the bedroom"
              :dir {:sleep :sleep :back :hallway}
              :contents #{}}
   :mainroom {:desc "This room is quite large. Enter up to go up the stairs, south to enter the dining room, north to enter the hallway, east to walk to the front entrance, and search to search the room. "
              :title "in the main room"
              :dir {:up :secondfloor :south :dinning :north :hallway :east :frontentrance :search :search}
              :contents #{:laptop}}
   :frontentrance {:desc "The door to the outside is locked and you need to find the diamond key to open it. Type search to search the room, west to leave the room. "
                   :title "in the front entrance"
                   :dir {:west :mainroom}
                   :contents #{}}
   :dinning {:desc "This room has a long table and some chairs, the table seems new since it is not covered by dust. Directions: north, west, east, search. "
             :title "in the dinning room"
             :dir {:north :mainroom :east :restroom :west :kitchen :search :search}
             :contents #{:flashlight}}
   :restroom {:desc "This room has a tub and a sink. The tub and the sink are very clean. Directions: west, search. "
              :title "in the first floor restroom"
              :dir {:west :dinning :search :search}
              :contents #{:bottle_water}}
   :kitchen {:desc "This room has a stove and a refridgerator. Is it possible that there are something in the fridge? Directions: east, search. "
             :title "in the kitchen"
             :dir {:east :dinning}
             :contents #{:apple}}
   :secondfloor {:desc "This is a hallway that connects serveral different rooms, there is also a dark brown wardrobe on your west side. Directions: north, east, south, down. "
                 :title "in the hallway on secondfloor"
                 :dir {:north :shop :east :library :south :restroom2 :down :mainroom}
                 :contents #{:diamondkey}}
   :shop {:desc "There is a merchandise in this room. Commands: south, search, talk. "
          :title "in the shop"
          :dir {:south :secondfloor}
          :contents #{:goldkey}}
   :library {:desc "This room is very retro, with a chandelier and many wooden bookshelves on the wall. Direction: west. "
             :title "in the library"
             :dir {:west :secondfloor :search :search}
             :contents #{:book}}
   :restroom2 {:desc "This restroom room is very dark and you can't see anything. Direction: north. "
               :title "in the second floor restroom"
               :dir {:north :secondfloor :search :search}
               :contents #{:goldbar}}})

(def adventurer
  {:location :basement
   :inventory #{}
   :tick 0
   :silverdoor 0
   :openedfridge 0
   :silverkey 0
   :boxopened 0
   :strength 0
   :water 0
   :sleep 0
   :seen #{}})

(defn status [player]
  (let [location (player :location)]
    (print (str "You are " (-> the-map location :title) ". "))
    (when-not ((player :seen) location)
      (print (-> the-map location :desc)))
    (update-in player [:seen]
               #(conj % location))))

(defn to-keywords [commands]
  (mapv keyword (str/split commands #"[.,?! ]+")))

(defn go [dir player]
  (let [location (player :location)
        dest (->> the-map location :dir dir)]
    (if (nil? dest)
      (do (println "You can't go that way.")
          player)
      (assoc-in player [:location] dest))))

(defn tock [player]
  (update-in player [:tick] inc))

(defn readl [player]
  (if (contains? (player :inventory) :mail)
    (println "Welcome. All the windows in this building are sealed and the exit door is locked. Type command to view all commands, inventory to check what you have, search to search around the room. Good luck. ")
    (println "Can't do that right now. "))
  player)

(defn cmdbase [player]
  (println " ******************** Command Cheat Sheet ******************** ")
  (println "note: these are all the commands avaliable in this game and there might be secret commands")
  (println "north west south east up down back room1 room2 room3 room4")
  (println "command inventory")
  (println "search readl unlock eat drink sleep turnontv solvepuzzle findbook read talk openbox flashlight")
  (println " ")
    ;;(println "END")
  player)

(defn inven [player]
  (print "Your currently have ")
  (if (empty? (player :inventory))
    (println "nothing in your inventory")
    (apply println (player :inventory)))
  player)

(defn searchroom [player]
  ;;(def loc (player :location))
  ;;(println loc)
  ;;(println ((the-map (player :location)) :contents))
  ;;(println (contains? ((the-map (player :location)) :contents) :mail))
  ;;(def retval 10)
  ;; Need to debug this part!!!
  (if (= (and (and (contains? ((the-map (player :location)) :contents) :mail) (not (contains? (player :inventory) :mail))) (= (player :location) :basement)) true)
    (do
      (def retval 20)
      (println "You found a mail on the floor. This mail has been added to your inventory and you can use command readl to read the mail. ")
      ;;(update-in the-map [(player :location) :contents] nil)
      ;;(disj ((the-map (player :location)) :contents) :mail) ;; remove the mail in this room
      (update-in player [:inventory] #(conj % :mail)) ;; adding the mail
      ;;(def retval 20)
)
    (do
      (if (= (and (= (player :location) :bedroom2) (not (contains? (player :inventory) :remote))) true)
        (do
          (println "You found a TV remote behind the sofa. This remote has been added to your inventory and you can use command turnontv to use it. ")
          (update-in player [:inventory] #(conj % :remote)))
        (do
          (if (= (and (= (player :location) :dinning) (not (contains? (player :inventory) :flashlight))) true)
            (do
              (println "You found a working flashlight under the chair. This flashlight has been added to your inventory and you can use command flashlight to use it. ")
              (update-in player [:inventory] #(conj % :flashlight)))
            (do
              (if (= (and (= (player :location) :restroom) (not (contains? (player :inventory) :bottle_water))) true)
                (do
                  (println "You found a bottle of water in one of the cabinet in this room. This bottle of water has been added to your inventory and you can use command drink to drink it. ")
                  (update-in player [:inventory] #(conj % :bottle_water)))
                (do
                  (if (= (and (= (player :location) :kitchen) (= (player :openedfridge) 0)) true)
                    (do
                      (println "You found an apple in the fridge. It has been added to your inventory and you can use eat command to eat it. ")
                      (update-in player [:inventory] #(conj % :apple)))
                    (do
                      (if (= (and (= (player :location) :mainroom) (not (contains? (player :inventory) :laptop))) true)
                        (do
                          (println "You found a laptop in the main room. It has been added to your inventory and you can use solvepuzzle to solve the puzzle on the laptop")
                          (update-in player [:inventory] #(conj % :laptop)))
                        (do
                          (if (= (and (= (player :location) :library) (not (contains? (player :inventory) :book))) true)
                            (do
                              (println "You found a book on the book shelf named Clojure Programming. It has been added to your inventory and you can can read to read the book")
                              (update-in player [:inventory] #(conj % :book)))
                            (do

                              (println "This room is empty and you can't find anything.")
                              player)))))))))))))))

(defn unlock [player]
  (if (= (and (= (player :location) :hallway) (= (player :silverdoor) 0) (contains? (player :inventory) :silverkey)) true)
    (do
      (println "You inserted your silver key into the locked door and turned the door knob. The door slowly opened with some squeak sounds")
      (update-in player [:silverdoor] inc))
    (do
      (if (= (and (= (player :location) :hallway) (= (player :silverdoor) 0) (= (player :silverkey) 0)) true)
        (do (println "You don't have the silverkey to open that door right now. ")
            player)
      (do
        (if (= (player :location) :frontentrance)
          (do
            (if (contains? (player :inventory) :diamondkey)
              (do
                (println "You inserted the diamond key into the key slot. You turned the key and the door opened seamlessly.")
                (println "You finally escaped from this house. Congrats!!")
                (System/exit 0))
              (do
                (println "You don't have the diamond key to open this door")
                player)))
          (do
            (println "You can't do that right now")
            player)))))))

(defn turnontv [player]
  (if (= (and (= (player :location) :bedroom1) (contains? (player :inventory) :remote)) true)
    (println "You turned on the TV using the remote. After a short pause, many white noises appeared on the TV screen. You can hear a distorted voice played by the TV. [1.....0.....2......4........I REPEAT.......1.....0.....2......4........] The TV then turned off by itself and silence dominated the room again.")
    (do
      (if (= (and (= (player :location) :bedroom2) (contains? (player :inventory) :remote)) true)
        (do
          (println "You pressed the power button on the TV remote and the Karaoke begins playing All The Lonely People by Beatles")
          player)
        (do
          (if (contains? (player :inventory) :remote)
            (println "You pressed the power button on the TV remote but nothing happened")
            (println "You can't do that right now"))
          player)))) player)

(defn flashlight [player]
  ;; TODO
  (if (= (and (= (player :location) :restroom2) (contains? (player :inventory) :flashlight)) true)
    (do
      (if (not (contains? (player :inventory) :goldbar))
        (do
          (println "You turned on the flashlight and this room is not dark anymore")
          (println "With the light, you found a gold bar in the restroom and it was added to your inventory")
          (update-in player [:inventory] #(conj % :goldbar)))
        (do
          (println "You turned on the flashlight and this room is not dark anymore")
          player)))
    (do
      (if (contains? (player :inventory) :flashlight)
        (do
          (println "This room is already luminous and flashlight is useless right now")
          player)
        (do
          (println "You can't do that right now")
          player))
      player)))

(defn openbox [player]
  (if (= (and (= (player :location) :bedroom3) (not (contains? (player :inventory) :silverkey))) true)
    (do
      (println "Enter a 4 digit passcode without space to open the number lock")
      (let [passwordin (read-line)]
        (if (= passwordin "1024")
          (do
            (println "You entered the right password for the number lock and the box is now opened. You found a silver key in the box and it is now avaliable in your inventory")
            (update-in player [:inventory] #(conj % :silverkey)))
          (do (println "Seems that that digit combination is not quite right")
              player))))
    (do
      (println "You can't do that right now")
      player)))

(defn sleep [player]
  (if (= (player :location) :bedroom4)
    (do

      (if (not (= (player :sleep) 0))
        (do
          (println "You already got enough sleep")
          player)
        (do
      (println "You got onto the bed and fell asleep quickly")
      (println "zzzzzzzzzzzz")
      (println "8 hours later")
      (update-in player [:sleep] inc)


      )))
    (do
      (println "It is very uncomfortable to try to sleep without a bed and you couldn't fall asleep")
     ;; (update-in player [:tick] inc)
      player)))

(defn eat [player]
  (if (contains? (player :inventory) :apple)
    (do
      (println "You ate half of the apple in your inventory and you have more strength now")
      (update-in player [:strength] inc))
    (do
      (println "Hungry? Unfortunatly you have nothing to eat.")
      player)))

(defn drink [player]
  (if (contains? (player :inventory) :bottle_water)
    (do
      (println "You drank half of the bottle water in your inventory and you are not thirsty now")
      (update-in player [:water] inc))
    (do
      (println "Thirsty? Unfortunatly you have nothing to drink.")
      player)))

(defn readb [player]
  (if (contains? (player :inventory) :book)
    (do
      (if (< (rand-int 10) 5)
        (println "Clojure is dynamically and strongly typed programming language on JVM")
        (do
          (if (< (rand-int 10) 5)
            (println "Clojure is a dialect of Lisp")
            (println "The page 42 on this book is missing")))))
    (do
      (println "You can't do that right now")
      player)) player)

(defn solvepuzzle [player]
  (if (contains? (player :inventory) :laptop)
    (do
      (println "You turned the laptop on and prairielearn automatically showed up on screen, asking you to solve a puzzle")
      (println "If Planck's age = 2 * John's age and Planck is 16 years older than John, how old is John? Please input your answer(real number only)")
      (let [agein (read-line)]
        (if (= agein "16")
          (do
            (println "Good job!!! Hint:enter `move` in room with wardrobe to esc"))
          (println "Too bad... Try harder next time please")))

      player)
    (do
      (println "You are puzzled that right now you can't find the puzzle to solve")
      player)))

(defn move [player]
  (if (= (player :location) :secondfloor)
    (do
      (if (contains? (player :inventory) :diamondkey)
        (do
          (println "you already obtained the diamondkey so there is no need to move the wardrobe again. ")
          player)
      (do

      (if (or (not= (player :strength) 0) (not= (player :sleep) 0))
        (do
          (println "cong you have found the secret command!!")
          (println "you moved the wardrobe and found a safe behind it")

          (if (contains? (player :inventory) :goldkey)
            (do
              (println "You used your gold key and opened the box")
              (println "With a surprise, you found a diamond key in the safe")
              (println "After a short pause, the wardrobe moved back to it's original position automatically")
              (update-in player [:inventory] #(conj % :diamondkey)))
            (do
              (println "Unfortunatly you don't have a gold key in your inventory to open this safe")
              (println "With a pause, the wardrobe moved back to it's original position automatically")
              player)))
        (do
          (println "The wardrobe is so heavy, get some sleep and eat something please")
          player)))))
    (do
      (println "You can't do that right now")
      player)))

(defn talk [player]
  (if (not (contains?  (player :inventory) :goldkey))
    (do
      (println "Merchandise: Hello, I can sell you a gold key for half of your goldbar. ")
      (println "Do you want to take the deal? You can say yes or no. It's your own choice. ")
      (let [reply (read-line)]
        (do
          (if (= reply "yes")
            (do
              (if (contains? (player :inventory) :goldbar)
                (do
                  (println "Deal!! Here is your gold key. Take care and good luck!")
                  (update-in player [:inventory] #(conj % :goldkey)))
                (do
                  (println "Sorry man, you can't afford this key. Come back later please. ")
                  player)))
            (if (= reply "no")
              (do
                (println "Alright, you can come back any time later")
                player)
              (do
                (println "I don't understand you.")
                player))))))

    (do
      (println "You alread got all you need. Good luck man  :)")
      player)))

(defn respond [player command]
  (match command
    [:look] (update-in player [:seen] #(disj % (-> player :location)))
    (:or [:n] [:north]) (go :north player)
    [:south]
    (if (and (= (player :location) :hallway) (= (player :silverdoor) 0))
      (do (println "That door is locked and you need to find the silver key to unlock it. ") player)
      (go :south player))
    [:north] (go :north player)
    [:west] (go :west player)
    [:east] (go :east player)
    [:gosouth] (go :south player)
    [:gonorth] (go :north player)
    [:gowest] (go :west player)
    [:goeast] (go :east player)
    [:down] (go :down player)
    [:up] (go :up player)
    [:goup] (go :up player)
    [:godown] (go :down player)
    [:readl] (readl player)
    [:command] (cmdbase player)
    [:cmd] (cmdbase player)
    [:back] (go :back player)
    [:room1] (go :room1 player)
    [:room2] (go :room2 player)
    [:room3] (go :room3 player)
    [:room4] (go :room4 player)
    [:inventory] (inven player)
    [:search] (searchroom player)
    [:unlock] (unlock player)
    [:openbox] (openbox player)
    [:turnontv] (turnontv player)
    [:sleep] (sleep player)
    [:flashlight] (flashlight player)
    [:eat] (eat player)
    [:drink] (drink player)
    [:solvepuzzle] (solvepuzzle player)
    [:move] (move player)
    [:read] (readb player)
    [:talk] (talk player)

    _ (do (println "I don't understand you.")
          player)))

(defn -main
  [& args]
  (loop [local-map the-map
         local-player adventurer]
    (let [pl (status local-player)
          _  (println "What do you want to do?")
          command (read-line)]
      (recur local-map (respond pl (to-keywords command))))))
