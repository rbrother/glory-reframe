(ns glory-reframe.ac
  (:require [glory-reframe.utils :as utils]))

; http://www.preeminent.org/steve/games/ti3/ti3demo/reference/action-mine/

; remember to modity from SA rules!

(def all-ac-array [
                   { :id :armistice :count 1 :play :action
                    :description "Select an opponent and a planet, which is under your control.
                This player can not attack on the selected planet in this round." }
                   { :id :chemical-warfare :count 1 :play "Immediately before the invasion on the planet"
                    :description "Play this card if you have a dreadnought in a system containing a planet,
              which is under opponent control. Consume half the ground units on the planet (rounded up)." }
                   { :id :civil-defense :count 1  :play :action
                    :description "Place two PDS units on a planet you control." }
                   { :id :command-summit :count 1 :play "During the strategic phase"
                    :description "Get two command counters"  }
                   { :id :corporate-sponsorship :count 1 :play "Immediately before the acquisition of a technology"
                    :description "You can buy a green technology with 4 resource discount."  }
                   { :id :council-dissolved :count 1 :play "When political strategy is played"
                    :description "The player who chose the Political Strategy does not draw a Political Card." }
                   { :id :cultural-crisis :count 1 :play "During the strategic phase"
                    :description "Select a player (can be you). This player loses in this round all the features of his race."  }
                   { :id :determine-policy :count 1 :play "Before voting for policy"
                    :description "Choose one of the discarded political cards.
       The Senate must vote again for this law, and not take the law from the political deck"  }
                   { :id :diplomatic-immunity :count 1 :play :action
                    :description "Select a system in which you have at least
  one ship. Only you can activate this system in this round."  }
                   { :id :disclosure :count 1 :play :action
                    :description "Have a look at the action cards of one opponent. Select and discard one card."  }
                   { :id :discredit :count 1 :play "Immediately after all players have voted in the council"
                    :description "Choose a player. Voices of the selected player does not count." }
                   { :id :dug-in :count 1 :play "Immediately prior to bombardment of a planet you have troops on"
                    :description "Choose a planet. Your ground forces at this planet are not subject to the bombing at this round."  }
                   { :id :emergency-repairs :count 2 :play :any-time
                    :description "Select your system. Immediately repair all your dreadnoughts and war-suns there" }
                   { :id :experimental-battlestation :count 1
                    :play "After opponent acticates a system with your spacedock and moves in fleet"
                    :description "Choose your spacedock. The dock may immediately attack
    three times in a row, similar to your PDS."  }
                   { :id :fantastic-rhetoric :count 1 :play "When casting your vote for council"
                    :description "You get an extra 10 votes." }
                   { :id :fighter-prototype :count 1 :play "Immediately before spacebattle."
                    :description "Select a system. All your fighters in this system receive the
    modifier +2 in all throws on one round."  }
                   { :id :flank-speed :count 4 :play "Immediately after activating a system."
                    :description "Increase the speed of all ships traveling into the system by 1." }
                   { :id :focused-research :count 1 :play "When aquiring technology"
                    :description "Spend 6 Resources for ignoring one technological prerequisite for the technology tree." }
                   { :id :ghost-ship :count 1 :play :action
                    :description "Add one free destroyer to a system containing worm hole and no enemy ships." }
                   { :id :good-year :count 1 :play :action
                    :description "Get one trade-good for each planet you control, and which is not in your home system." }
                   { :id :grand-armada :count 1 :play "During the strategic phase."
                    :description "During this game round until the beginning of status phase,
    you can have any number of ships in your home system, not limited by your fleet supply." }
                   { :id :in-silence-of-space :count 3
                    :description "Select one of your fleets. All the ships of the fleet
   at a rate of 2 or higher can pass through (but not stop) at opponent fleets." }
                   { :id :influence-in-merchants-guild :count 1 :play :action
                    :description "Break a trade agreement." }
                   { :id :insubordination :count 1 :play :action
                    :description "Remove 1 command-counter from command-pool of one opponent" }
                   { :id :into-breach :count 1 :play "before space battle"
                    :description "Choose your Dreadnought. All the other ships in your fleet
    receive +1 to attack rolls until the end of the battle. The first two
    Damage received by your fleet you must apply to the selected dreadnought." }
                   { :id :local-unrest :count 4 :play :action
                    :description "Exhaust any 1 planet in a non-home system and destroy 1 Ground Force on that planet.
    It reverts to neutral status if emptied." }
                   { :id :lucky-shot :count 1 :play :action
                    :description "Destroy 1 enemy Dreadnought, Carrier or Destroyer in system with a planet you control." }
                   { :id :massive-transport :count 1 :play :action
                    :description "You may immediately move one of your existing Space Docks to another friendly planet.
    A route that does not contain enemy ships must exist between the two planets." }
                   { :id :master-of-trade :count 1 :play :action
                    :description "Play only if you have two trade agreements. Choose and refresh up to two of your exhausted planets." }
                   { :id :minelayers :count 1 :play "Before space battle where you are defending"
                    :description "Choose a system with 1 of your fleets: each of your cruisers
    gets 1 automatic hit before the first round of a Space Battle." }
                   { :id :morale-boost :count 5
                    :description "For 1 round in battle, all of your units receive +1 on all combat rolls." }
                   { :id :multiculturalism :count 1 :play :strategy-phase
                    :description "Gain 1 of the racial special abilities of 1 opponent for this round only." }
                   { :id :patrol :count 1 :play :action
                    :description "Move up to 2 Carriers or 2 Destroyers from an activated system to empty adjacent system.
    Then place a Command Counter from your reinforcements there." }
                   { :id :plague :count 1 :play :action
                    :description "Roll 1 die for each Ground Force on 1 chosen opponent's planet. Even numbers kill.
    Control of the planet does not change." }
                   { :id :policy-paralysis :count 1 :play "When strategy card secondaries are played"
                    :description "Opponent of your choice cannot perform the Secondary Ability of current Strategy card." }
                   { :id :political-stability :count 1 :play "end of game round"
                    :description "Keep your current Strategy Card at the end of the game round. You do not select a new Strategy Card
    during the Strategy Phase of the upcoming round. You cannot play this card if you chose the Imperial
    or the Initiative Strategy card (or similar) this round. For games where players may have more than one Strategy Card,
    this only applies to one of the Strategy Cards owned by the player." }
                   { :id :privateers :count 1 :play :action
                    :description "Remove all Trade Goods from 1 opponent's card. You get half of them (rounded down)." }
                   { :id :productivity-spike :count 1
                    :description "Choose 1 space dock; its production capacity increases by the value of its influence this round only." }
                   { :id :public-disgrace :count 1 :play :strategy-phase
                    :description "After a player has chosen a Strategy Card, play this card to force the player
    to return that Strategy Card and choose a different one." }
                   { :id :rally-of-people :count 1 :play :action
                    :description "Place free Dreadnought in your home system." }
                   { :id :rare-mineral :count 3
                    :description "You get 3 free Trade Good when you successfully invade one neutral planet." }
                   { :id :recheck :count 4
                    :description "Force reroll of any 1 combat die (your or opponents)" }
                   { :id :reparations :count 1
                    :description "Exhaust a chosen planet of opponent who has just taken 1 from you.
    Then refresh 1 of your own planets of equal or lesser resource value." }
                   { :id :rise-of-messiah :count 1 :play :action
                    :description "You receive 1 Ground Force on each planet that you control." }
                   { :id :sabotage :count 5
                    :description "Cancel any other action card (can't be used versus another Sabotage)." }
                   { :id :scientist-assassination :count 1 :play :strategy-phase
                    :description "Choose a player; that player can't acquire or purchase Technology this turn." }
                   { :id :secret-industrial-agent :count 1 :play :action
                    :description "Destroy any one Space Dock that is not in a Home System." }
                   { :id :shields-holding :count 1
                    :description "Cancel up to 2 hits in 1 round of Space Battle in which you participate." }
                   { :id :signal-jamming :count 4 :play :strategy-phase
                    :description "Place a Command Counter from opponent's reinforcements on any non-home system." }
                   { :id :skilled-retreat :count 4 :play "before any round of space battle"
                    :description "Choose 1 of your fleets that is in a space battle: put a Command Counter from
    reinforcements in an adjacent system and move your fleet there." }
                   { :id :spacedock-accident :count 1 :play :action
                    :description "Choose a Space Dock in a non-Home system. The owner of the Space Dock may not build
    units at the chosen Space Dock this round." }
                   { :id :star-of-death :count 1
                    :description "Destroy all Ground Forces, PDS, Space Docks on 1 planet in system with 1 of your War Suns.
    Then place Radiation Domain counter there. Radiation counter: The planet contains unexpected high
    levels of radiation. Kill all Ground Forces that initially land here, then remove this counter from the planet." }
                   { :id :stellar-criminals :count 1 :play :action
                    :description "Choose opponent; he must choose and exhaust half of his unexhausted planets (round down)." }
                   { :id :strategic-bombardment :count 1 :play :action
                    :description "Play in system in which you have a War Sun and enemy planets: exhaust all planets. Then roll die:
            1-5 = discard this card. -10 = return this card to your hand." }
                   { :id :strategic-flexibility :count 1
                    :description "After all Strategy cards chosen, swap yours for one of those not chosen." }
                   { :id :strategic-shift :count 1
                    :description "Before Strategy Cards selected, choose 1 that may not be taken this round." }
                   { :id :successful-spy :count 1 :play :action
                    :description "Steal 2 random action cards from 1 opponent." }
                   { :id :synchronicity :count 1 :play :strategy-phase
                    :description "Search Action card deck (not discards) and put one in your hand, then shuffle." }
                   { :id :tech-bubble :count 1
                    :description "Play only if you chose the Tech Strategy this turn. You may spend a Command Counter to do
    the Secondary Strategy in addition to executing the Primary." }
                   { :id :touch-of-genius :count 1
                    :description "Spend 3 influence: Duplicate effects of any Action Card in discard pile." }
                   { :id :trade-stop :count 1 :play :strategy-phase
                    :description "Break all trade agreements in play." }
                   { :id :transport :count 1 :play :action
                    :description "Choose a planet you control. You may move up to three Ground Forces from the chosen
    planet to another planet you control. A route that does not contain enemy ships must exist between these two planets." }
                   { :id :unexpected-action :count 1 :play :action
                    :description "Remove 1 of your Command Counter from board and put in your reinforcements." }
                   { :id :uprising :count 1 :play :action
                    :description "Exhaust all planets in any 1 non-home system." }
                   { :id :usurper :count 1 :play :strategy-phase
                    :description "Place this card in Mecatol Rex system. While it's there, you get double
    influence from Mecatol Rex, even if it's exhausted." }
                   { :id :veto :count 1
                    :description "Discard current political agenda, a new agenda must be drawn." }
                   { :id :voluntary-annexation :count 1 :play :action
                    :description "Gain control of neutral planet and place 3 Ground Force on it.
    Must be in system adjacent to 1 containing a planet you control." }
                   { :id :war-footing :count 1 :play :action
                    :description "In 1 system of your choice, your fleet supply limit is increased by 2.
    At the end of the Status Phase, you must remove ships exceeding the Fleet supply limit." }
                   { :id :advanced-reinforcements :count 2 :set :shattered-empire :play :action
                    :description "Add 2 ground units or 1 shock-troop at one of the planets under your control." }
                   { :id :alien-technology :count 1 :set :shattered-empire :play :action
                    :description "You can immediately spend 4 resources to
        buy one technology (for which you have prerequisites).
        You can not reduce the cost of the technology." }
                   { :id :bribery :count 1 :set :shattered-empire :play "Immediately after all the players voted for the law"
                    :description "You can spend trade goods to add an equal number of votes." }
                   { :id :communications-breakdown :count 2 :set :shattered-empire
                    :play "Immediately before battle which involves your units."
                    :description "None of the players can play any action cards (except Sabotage) until the end of the battle" }
                   { :id :courageous-to-end :count 2 :set :shattered-empire :play "Immediately after your one of the ships destroyed."
                    :description "Throw two dice. For each result equal to or superior to the firepower of your ship
      opponent immediately takes losses. You can use this card, even if your ship cannot fire back." }
                   { :id :direct-hit  :count 4 :set :shattered-empire
                    :play " Immediately after a ship was damaged during a battle in which you participate."
                    :description "Destroy an enemy damaged ship. (revised in SE)" }
                   { :id :enhanced-armor :count 2 :set :shattered-empire :play :action
                    :description "Until the end of the round your cruisers can sustain two hits before destroyed." }
                   { :id :equipment-sabotage :count 2 :set :shattered-empire :play :action
                    :description "Destroy up to two PDS on a chosen planet." }
                   { :id :experimental-weaponry :count 2 :set :shattered-empire
                    :play "Before start of a space-battle round"
                    :description "Until the end of the space-battle round, all damade caused by
    your warsuns and dreadnoughts cannot be applied to fighters." }
                   { :id :faulty-targeting-systems :count 2 :set :shattered-empire :play "any time"
                    :description "Prevents the action card Minelayers Action, or removes
    one minefield from a system or force opponent to reroll one PDS roll" }
                   { :id :first-strike :count 2 :set :shattered-empire :play "Immediately after the strategic phase."
                    :description "You can take action first, before player of lowest initiative.
    Continue to play in the usual playing order after that." }
                   { :id :flanking-tactic :count 2 :set :shattered-empire
                    :play "Immediately before the battle in which you are involved"
                    :description "Your opponent can not retreat from the battle
    regardless of special properties and action cards." }
                   { :id :flawless-strategy :count 1 :set :shattered-empire :play :action
                    :description "You can perform a primary property of a strategic card which was
    not selected during strategic phase. Each player can then perform a secondary
    feature of this card." }
                   { :id :friendly-fire :count 4 :set :shattered-empire
                    :play "Immediately before spacebattle, in which you are involved."
                    :description "If your opponent has in the system more fighters than other types of ships,
    he must immediately lose half of the fighters (Rounded up)." }
                   { :id :master-of-fate :count 1 :set :shattered-empire
                    :description "Draw a number of Action Cards equal to the number of players.
    You may look at them and distribute one of them to each player.
    Players will receive 1 fewer Action Card during this Status Phase." }
                   { :id :military-foresight :count 2 :set :shattered-empire
                    :description "When you lose a ship in space-battle, instead of discarding it, you can at next
    status phase place it at any of your system with a space-dock" }
                   { :id :ruinous-tariffs :count 1 :set :shattered-empire
                    :play "Immediately after selected player received TGs from its trading contracts."
                    :description "Choose a player with whom you have entered into trade
    agreement. This player must give you half of their TGs (rounded up). ( revised in SE)" }
                   { :id :rule-by-terror :count 1 :set :shattered-empire
                    :play "Before spacebattle, in which you are involved"
                    :description "If you have a warsun or 2 dreadnoughts, then
    Your opponent must either retreat (if possible) or immediately suffer two hits." }
                   { :id :strategic-planning :count 2 :set :shattered-empire :play :strategy-phase
                    :description "You do not have to pay the action-counter from strategy allocation
    for implementation of secondary strategy actions during this game round." }
                   { :id :surprise-assault :count 1 :set :shattered-empire
                    :play "After activating the system containing your dock."
                    :description "Immediately after the movement of ships in
    system you can produce units in your blocked dock. Then start the battle." }
                   { :id :target-their-flagship :count 2 :set :shattered-empire :play "before space battle"
                    :description "Choose one of your ships and one of the enemy ships.
      Throw the dice. If you succeed in hit with your ship, the ship of the enemy
      immediately receives damage (without the right to return fire). You will not throw for your chosen
      ship in the first round of battle." }
                   { :id :temporary-stability :count 1 :set :shattered-empire
                    :description "Players can not play action cards up to  the next status phase (except sabotage).
    Any player may throw away 3 action cards to cancel the effect of this card. " }  ] )

(def all-ac-types (utils/index-by-id all-ac-array))

(defn multiply-card [ { count :count :as ac } ] (take count (repeat (dissoc ac :count))) )

(defn create-ac-deck [] (shuffle (map :id (mapcat multiply-card all-ac-array))))


; PC cards

; http://www.preeminent.org/steve/games/ti3/ti3demo/reference/political-mine/

(def all-pc-array
  [ { :id :aggressive-strategy
     :for "Each player may (in order of play) execute the Secondary Ability of his chosen
      Strategy without spending a Command Counter."
     :against "No player may execute the Secondary Ability of any Strategy Card this round." }
   { :id :ancient-artifact
    :for "Roll 1 die with following results: 1-5: all ships in Mecatol Rex are destroyed;
     then roll 3 dice for each fleet in adjacent systems; rolls greater than 4 cause 1 hit each,
     greater than 5: all players gain 2 technologies of their choice."
    :against "Discard this card" }
   { :id :arms-reduction
    :for "Each player destroys all but 2 Dreadnoughts and all but 4 Carriers, other types unaffected."
    :against "Players exhaust all planets with Red Technology bonus." }
   { :id :checks-and-balances :type :law
    :for "When a player chooses a Strategy card, he gives it to another player who does
     not have one."
    :against "Each player passes current Strategy Card to the player on his left." }
   { :id :closing-the-wormholes :type :law
    :for "No fleets can move through wormholes."
    :against "Any player with a fleet in a wormhole hex must discard 1 non-Ftr from that fleet." }
   { :id :code-of-honor :type :law
    :for "Fleets may not withdraw or retreat from Space Battles."
    :against "Discard this card." }
   { :id :colonial-redistribution
    :for "Player with most Victory Points (roll die if tie) destroys all Ground Force
     Units on 1 of his planets, then chooses a player with least Victory Points.
     That player gets 3 Ground Force Units on that planet and controls it."
    :against "Discard this card." }
   { :id :colonial-licensing :type :law
    :for "Before landing on neutral planet, players must spend resources at least equal to
     resource value of the planet."
    :against "All players exhaust 1 planet for every 3 they control outside home system (round down)" }
   { :id :compensated-disarmament
    :elect "Elect planet. All Ground Force Units on it are destroyed, but the owner
     receives 1 Trade Good for each Ground Force Unit lost and retains control of the planet."}
   { :id :conventions-of-war :type :law
    :for "Dreadnoughts & War Suns may not bombard planets with space docks."
    :against "Each player that voted against this law must return 1 of his space docks
     outside his home system and return it to his reinforcements."}
   { :id :core-stability :type :law
    :for "During each Status Phase Action Cards are drawn and placed face-up on the table.
      Players choose their cards in order of unexhausted influence from highest to lowest."
    :against "No Action cards are drawn this round."}
   { :id :cost-overruns
    :for "Each player must discard 1 Trade Good for each planet he controls."
    :against "Discard this card." }
   { :id :council-censure :type :law
    :elect "Elect Player. Elected player may only choose the Imperial Strategy by
      discarding 2 Command Counter from his Strategy pool during the Strategy Phase."}
   { :id :council-elder :type :law
    :elect "Elect Player. Chosen player may discard this card whenever another \"Elect\"
      agenda comes up; only his votes will count to determine the result of the election." }
   { :id :crown-of-thalnos :type :law
    :elect "Elect Player. Elected player's Ground Force Units receive +1 on all rolls."}
   { :id :dispute-resolution
    :elect "Elect 2 planets controlled by 2 different players; destroy all Ground Force
      Units and Planetary Defense Systems on both planets. Planets revert to neutral.
      Mecatol Rex and Home System planets may not be chosen." }
   { :id :economic-revitalization
    :elect "Elect Player. He receives 1 Trade Good from each opponent, if possible."}
   { :id :fleet-regulations :type :law
    :for "Command Counter in Fleet area limited to 5."
    :against "Each player puts a Command Counter from reinforcements into Fleet Supply area." }
   { :id :forced-economic-independence :type :law
    :elect "Elect 2 Players. Place control markers of the 2 players on this card.
      Any trade agreements between them are broken and they may not have trade agreements
      between them as long as this law is in play." }
   { :id :free-trade :type :law
    :for "Each time a player received Trade Goods from his agreements,
      he received 1 additional Trade Good."
    :against "All players must discard 2 Trade Goods if possible." }
   { :id :holder-of-mecatol-rex
    :for "Player controlling Mecatol Rex may choose and discard any 1 law."
    :against "Player controlling Mecatol Rex adds 2 free Ground Force Units to Mecatol Rex." }
   ])


" original:




Holy Planet of Ixth
Humane Labor
Imperial Academy
Imperial Containment
Imperial Mandate
Imperial Peace
Labor Force Politics
Limits to Individual Power
Mass Mobilization
Minister of Commerce
Minister of Exploration
Minister of Internal Security
Minister of Policy
Minister of War
New Constitution
Official Sanction
Open The Trade Routes (original)
Planetary Conscription
Planetary Security
Prophecy of Ixth
Public Execution
Regressive Rhetoric
Regulated Conscription
Repeal
Research Grant
Resource Management
Revote
Science Community Speaker
Short Term Truce
Subsidized Industry
Subsidized Studies
Technology Buy-back
Technology Investigation Committee
Technology Tariffs
Trade Embargo
Trade War
Traffic Tariffs
Unconventional Weapons
Wormhole Reconstruction


in SE:


Class Struggle
Defend the Jewel
Diversified Income
Emperor
Enemy of the Throne
Fighter Tax
Fleet Restrictions
Forbidden Research
Glory of the Empire
Hope’s End Training Ground
Incentive Program
Intergalactic Commerce
Interstellar Arms Dealers
Investigate Spatial Anomalies
Minister of Peace
Mutiny
Neutrality Pact
Non-Aggression Pact
Open the Trade Routes (revised)
Political Focus
Recognize Accomplishments
Redefining War Crimes
Seed of an Empire
Sharing of Technology
Technological Jihad
The Crown of Emphidia
Unconventional Measures
Veto Power
Vorhal Peace Prize
War Funding
Warship Commission
Wormhole Research


secret onjectives:

Conqueror
Diversified
Expansionist [SHATTERED EMPIRE]
Focused
Forceful
Industrial
Keeper of Gates
Master of Ships
Merciless
Regulator [SHATTERED EMPIRE]
Technocrat
Threatening [SHATTERED EMPIRE]
Usurper

"
