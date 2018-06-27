(ns glory-reframe.db)

(def default-db
  { :name "Sandbox"
    :game-state {
        :ac-deck [   :military-foresight
            :flank-speed
            :direct-hit
            :flank-speed
            :council-dissolved
            :rally-of-people
            :synchronicity
            :skilled-retreat
            :enhanced-armor
            :command-summit
            :experimental-weaponry
            :strategic-shift
            :master-of-trade
            :trade-stop
            :surprise-assault
            :successful-spy
            :ruinous-tariffs
            :temporary-stability
            :strategic-flexibility
            :rule-by-terror
            :morale-boost
            :armistice
            :skilled-retreat
            :courageous-to-end
            :tech-bubble
            :emergency-repairs
            :experimental-battlestation
            :recheck
            :in-silence-of-space
            :ghost-ship
            :voluntary-annexation
            :local-unrest
            :direct-hit
            :reparations
            :flanking-tactic
            :first-strike
            :direct-hit
            :chemical-warfare
            :first-strike
            :morale-boost
            :fantastic-rhetoric
            :determine-policy
            :experimental-weaponry
            :advanced-reinforcements
            :recheck
            :war-footing
            :emergency-repairs
            :sabotage
            :strategic-bombardment
            :diplomatic-immunity
            :local-unrest
            :touch-of-genius
            :productivity-spike
            :recheck
            :friendly-fire
            :friendly-fire
            :flank-speed
            :friendly-fire
            :faulty-targeting-systems
            :signal-jamming
            :sabotage
            :corporate-sponsorship
            :flanking-tactic
            :unexpected-action
            :signal-jamming
            :local-unrest
            :cultural-crisis
            :discredit
            :communications-breakdown
            :in-silence-of-space
            :transport
            :sabotage
            :friendly-fire
            :flawless-strategy
            :strategic-planning
            :courageous-to-end
            :lucky-shot
            :alien-technology
            :morale-boost
            :disclosure
            :target-their-flagship
            :sabotage
            :direct-hit
            :signal-jamming
            :signal-jamming
            :military-foresight
            :flank-speed
            :secret-industrial-agent
            :good-year
            :political-stability
            :recheck
            :influence-in-merchants-guild
            :skilled-retreat
            :grand-armada
            :advanced-reinforcements
            :into-breach
            :strategic-planning
            :dug-in
            :communications-breakdown
            :rare-mineral
            :faulty-targeting-systems
            :stellar-criminals
            :massive-transport
            :policy-paralysis
            :patrol
            :enhanced-armor
            :scientist-assassination
            :master-of-fate
            :sabotage
            :skilled-retreat
            :civil-defense
            :minelayers
            :usurper
            :local-unrest
            :morale-boost
            :public-disgrace
            :uprising
            :star-of-death
            :multiculturalism
            :fighter-prototype
            :rare-mineral
            :bribery
            :equipment-sabotage
            :equipment-sabotage
            :focused-research
            :rise-of-messiah
            :rare-mineral
            :veto
            :shields-holding
            :privateers
            :target-their-flagship
            :in-silence-of-space
            :morale-boost ]
        :counter 26
        :gm-password ""
        :history [   { :counter 1 }  ]
        :map {   :a3 { :controller :hacan :id :a3 :logical-pos [ -2 0 ] :planets #{ :kobol } :system :kobol }
            :a4 { :activated { :norr true } :id :a4 :logical-pos [ -2 1 ] :planets #{ :coorneeq :resculon } :system :coorneeq-resculon }
            :a5 { :id :a5 :logical-pos [ -2 2 ] :planets #{ :kazenoeki } :system :kazenoeki }
            :b2 { :activated { :norr true } :controller :hacan :id :b2 :logical-pos [ -1 -1 ] :planets #{ :sorkragh :machall } :system :machall-sorkragh }
            :b3 { :id :b3 :logical-pos [ -1 0 ] :planets #{ :torkan :tequran } :system :tequran-torkan }
            :b4 { :id :b4 :logical-pos [ -1 1 ] :planets #{ :suuth } :system :suuth }
            :b5 { :controller :norr :id :b5 :logical-pos [ -1 2 ] :planets #{ :cicerus } :system :cicerus }
            :c1 { :controller :hacan :id :c1 :logical-pos [ 0 -2 ] :planets #{  } :system :wormhole-b }
            :c2 { :id :c2 :logical-pos [ 0 -1 ] :planets #{ :ptah } :system :ptah }
            :c3 { :id :c3 :logical-pos [ 0 0 ] :planets #{ :bellatrix :tsion } :system :tsion-bellatrix }
            :c4 { :id :c4 :logical-pos [ 0 1 ] :planets #{ :chnum } :system :chnum }
            :c5 { :id :c5 :logical-pos [ 0 2 ] :planets #{  } :system :ion-storm }
            :d1 { :id :d1 :logical-pos [ 1 -2 ] :planets #{ :parzifal } :system :parzifal }
            :d2 { :id :d2 :logical-pos [ 1 -1 ] :planets #{  } :system :asteroid-field }
            :d3 { :activated { :hacan true :naalu true } :id :d3 :logical-pos [ 1 0 ] :planets #{  } :system :empty }
            :d4 { :activated { :hacan true :naalu true } :id :d4 :logical-pos [ 1 1 ] :planets #{  } :system :empty }
            :e1 { :id :e1 :logical-pos [ 2 -2 ] :planets #{  } :system :wormhole-a }
            :e2 { :controller :naalu :id :e2 :logical-pos [ 2 -1 ] :planets #{ :martinez :sulla } :system :sulla-martinez }
            :e3 { :id :e3 :logical-pos [ 2 0 ] :planets #{ :sem-lore } :system :sem-lore } }
        :planets {   :bellatrix { :controller nil :id :bellatrix }
            :chnum { :controller nil :id :chnum :fresh true }
            :cicerus { :controller :norr :id :cicerus :fresh true }
            :coorneeq { :controller nil :id :coorneeq :fresh true }
            :kazenoeki { :controller nil :id :kazenoeki :fresh true }
            :kobol { :controller :hacan :id :kobol :fresh true }
            :machall { :controller :hacan :id :machall :fresh true }
            :martinez { :controller :naalu :id :martinez :fresh true }
            :parzifal { :controller nil :id :parzifal :fresh true }
            :ptah { :controller nil :id :ptah }
            :resculon { :controller nil :id :resculon }
            :sem-lore { :controller nil :id :sem-lore }
            :sorkragh { :controller :hacan :id :sorkragh }
            :sulla { :controller :naalu :id :sulla }
            :suuth { :controller nil :id :suuth }
            :tequran { :controller nil :id :tequran }
            :torkan { :controller nil :id :torkan }
            :tsion { :controller nil :id :tsion } }
        :players {   :hacan { :ac [ :spacedock-accident ] :command-pool 0 :fleet-supply 3 :id :hacan :password "abc" :pc [  ] :strategy-alloc 2 }
            :naalu { :ac [ :plague :insubordination ] :command-pool 0 :fleet-supply 3 :id :naalu :password "123" :pc [  ] :strategy-alloc 2 }
            :norr { :ac [  ] :command-pool 1 :fleet-supply 4 :id :norr :password "xyz" :pc [  ] :strategy-alloc 3 } }
        :strategies #{
                      { :id :leadership4 :owner :norr :ready true :bonus 0 }
                      { :id :diplomacy4 :owner :norr :ready false :bonus 0 }
                      { :id :politics4  :owner :naalu :ready true :bonus 0 }
                      { :id :production :ready true :bonus 0 }
                      { :id :trade4 :owner :hacan :ready false :bonus 0 }
                      { :id :warfare4 :owner :hacan :ready true :bonus 0 }
                      { :id :technology4 :owner :naalu :ready true :bonus 0 }
                      { :id :bureaucracy :ready true :bonus 2 }
                      }
        :ship-counters { :ca 3 :cr 2 :de 2 :fi 2 :gf 10 }
        :units {   :ca1 { :id :ca1 :location :b2 :owner :hacan :type :ca }
            :ca2 { :id :ca2 :location :b2 :owner :hacan :type :ca }
            :ca3 { :id :ca3 :location :e2 :owner :naalu :type :ca }
            :cr1 { :id :cr1 :location :b2 :owner :hacan :type :cr }
            :cr2 { :id :cr2 :location :b5 :owner :norr :type :cr }
            :de1 { :id :de1 :location :b5 :owner :norr :type :de }
            :de2 { :id :de2 :location :b5 :owner :norr :type :de }
            :fi1 { :id :fi1 :location :e2 :owner :naalu :type :fi }
            :fi2 { :id :fi2 :location :e2 :owner :naalu :type :fi }
            :gf1 { :id :gf1 :location :a3 :owner :hacan :planet :kobol :type :gf }
            :gf10 { :id :gf10 :location :e2 :owner :naalu :planet :sulla :type :gf }
            :gf2 { :id :gf2 :location :a3 :owner :hacan :planet :kobol :type :gf }
            :gf3 { :id :gf3 :location :b4 :owner :hacan :planet :suuth :type :gf }
            :gf4 { :id :gf4 :location :b4 :owner :hacan :planet :suuth :type :gf }
            :gf5 { :id :gf5 :location :b5 :owner :norr :planet :cicerus :type :gf }
            :gf6 { :id :gf6 :location :b5 :owner :norr :planet :cicerus :type :gf }
            :gf7 { :id :gf7 :location :e2 :owner :naalu :planet :sulla :type :gf }
            :gf8 { :id :gf8 :location :e2 :owner :naalu :planet :sulla :type :gf }
            :gf9 { :id :gf9 :location :e2 :owner :naalu :planet :sulla :type :gf } } } }  )
