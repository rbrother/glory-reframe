(ns glory-reframe.strategies)

(def all-strategies-arr
  [ { :id :initiative :rules "TI3" :order 1
     :special "After selecting this strategy card, claim the Speaker Token.
     During Action Phase, you do not have to pay Command-Counters from your
     Strategy Allocation area in order to execute the secondary ability of Strategy Cards.
     You may not select the Initiative Strategy the next Strategy Phase.
     You do not take a Strategic Action during the Action Phase this round." }
   { :id :leadership :rules "TI3-SE" :order 1
    :primary "Gain 3 Command tokens. You may then immediately use this cards
     secondary ability."
    :secondary "Spend influence to purchase up to 3 Command Counters from your reinforcements.
     You receive 1 Command Counter for every 2 influence spent." }
   { :id :leadership4 :rules "TI4" :order 1
    :primary "Gain 3 Command tokens. Spend any amount of influence to gain 1 Command token
     for every 3 influence spent."
    :secondary "Spend any amount of influence to gain 1 Command token
     for every 3 influence spent."}

   { :id :diplomacy :rules "TI3" :order 2
    :primary "Diplomatic Envoy. Name an opponent. For the remainder of this phase,
     neither you nor that opponent may activate a system containing units of the other
     player (including Ground Forces and PDS)"
    :secondary "Economic Stimulus. Spend one Command Counter from your Strategy Allocation
     area to refresh up to two of your exhausted (non Home System) Planet Cards" }
   { :id :diplomacy2 :rules "TI3-SE" :order 2
    :primary "Demilitarized zone. Choose either a) or b).
     a) Choose one system containing a planet you control. Each opponen must place one
     Command Counter into the system from his reinforcements.
     b) Execute the secondary ability of this card without paying Command Counter or influence."
    :secondary "Peaceful Annexation. Spend 1 Command Counter from Strategy Allocation and
     3 influence to claim an empty planet adjacent to a system you control. Place your
     Command Marker on the planet." }
   { :id :diplomacy4 :rules "TI4" :order 2
    :primary "Choose 1 system other than the Mecatol Rex system that contains a planet
    you control. Each other player places a Command token from his reinforcements to the
    chosen system. Then, ready each exhausted planet you control in that system."
    :secondary "Spend one token from your Strategy pool to ready up to two exhausted planes." }

   { :id :political :rules "TI3" :order 3
    :primary "The Galactic Council. Draw three action cards and receive one Command Counter
     from your reinforcements. Then draw the top card of the political agenda and resolve
     its agenda. After completing the agenda, draw the top three cards of the Political Deck,
     secretly read them, and then place one card face down on top of the deck and the rest
     to the bottom of the deck. "
    :secondary "Seek Destiny. Spend one Command Counter from your Strategy Allocation area
     to draw one action card."}
   { :id :assembly :rules "TI3-SE" :order 3
    :primary "Draw 1 Political and 2 Action cards. Then choose either a) or b). You may
     not choose a) if your are the Speaker.
     a) Claim the Speaker token and choose one other player to play a Political Card and
     resolve its agenda.
     b) Choose one other player to claim the Speaker Token. Play a Political Card from your
     hand and resolve its agenda."
    :secondary "Morale Boost. Spend 1 Command Counter from Strategy pool to refresh and
     number of your planet cards with total combined resource and influence of 6 or less." }
   { :id :politics4 :rules "TI4" :order 3
    :primary "Choose a player other than the speaker. That player gains the speaker token.
     Draw 2 Action Cards. Look at the top 2 cards of the agenda deck. Place each card on the
     top or bottom of the deck in any order."
    :secondary "Spend 1 token from your strategy pool to draw 2 Action Cards." }

   { :id :logistics :rules "TI3" :order 4
    :primary "Receive 4 Command Counters from your reinforcements."
    :secondary "Yoy may spend influence to receive Command Counters from your reinforcements.
     You receive 1 Command Counter for every 3 influence you spend."}
   { :id :production :rules "TI3-SE" :order 4
    :primary "Tight Deadlines. Immediately build units in one of your systems containing
     one or more friendly Space Docks, receiving 2 additional resources with which to build,
     even if you have activated that system already. Building units here does not activate
     the system"
    :secondary "Double Efforts. Spend 1 Command Counter from your Strategy pool to
     immediately build up to 3 units in one of your systems containing one or more
     friendly Space Docks, even if you have activated that system already.
     Building units here does not activate the system." }
   { :id :construction :rules "TI4" :order 4
    :primary "Place 1 PDS or 1 Space Dock on a planet you control.
     Place 1 PDS on a planet you control."
    :secondary "Place 1 token from your strategy pool in any system. You may place
     1 Space Dock or 1 PDS on a planet you control in that system." }

   { :id :trade :rules "TI3" :order 5
    :primary "Influence on the Merchands Guild. Choose a) or b).
     a) Immediately receive 3 Trade Goods. Then receive Trade Goods from your active
     trade agreements. Finally open Trade Negotiations among all players. You must
     approve all trade agreements.
     b) All trade agreements are cancelled." }
   { :id :trade2 :rules "TI3-SE" :order 5
    :special "Free Trade. Receive 3 Trade Goods or cancel up to 2 trade agreements
     (you may not cancel Hacan trade agreements).
     Then all players receive Trade Goods from their active trade agreements.
     Players who are not the active player receive 1 fewer total Trade Goods.
     Finally, open trade negotiations among all players. You must approve all new
     trade agreements." }
   { :id :trade4 :rules "TI4" :order 5
    :primary "Gain 3 Trade Goods. Replenish commodities. Choose any number of players.
     Those players use the secondary ability of this strategy card without spending
     a command token."
    :secondary "Spend 1 token from your strategy allocation pool to replenish commodities." }

   { :id :warfare2 :rules "TI3-SE" :order 6
    :primary "High Alert. Place the High Alert token in a system. Your ships in the system
     with the token gain +1 movement and +1 on all combat rolls. If you move any ships from
     this system, you may move the token with them. Remove the token from the board at the
     start of next status phase."
    :secondary "Reinforce. Spend 1 Command token from your Strategy pool to move up to two
     of your ships from unactivated systems into any adjacent systems you control. This
     does not activate the destination system(s)."}
   { :id :warfare4 :rules "TI4" :order 6
    :primary "Remove 1 of your command tokens from the game board. Gain 1 command token.
     Redistribute any number of command tokens on your command sheet."
    :secondary "Spend 1 token from your strategy pool to use the PRODUCTION ability of
     1 of your space docks in your home system." }

   { :id :technology2 :rules "TI3-SE" :order 7
    :primary "Receive one Technology advance. You may then buy a second Technology
     at the cost of 8 resources. You must have the necessary prerequisites for each
     technology."
    :secondary "Spend 1 Command token from your Strategy pool and 6 resources to
     receive one Technology advance. You must have the necessary prerequisites for
     each technology." }
   { :id :technology4 :rules "TI4" :order 7
    :primary "Research 1 technology. Spend 6 resources to research 1 technology."
    :secondary "Spend 1 token from your strategy pool and 4 resources to research
     1 technology." }

   { :id :bureaucracy :rules "TI3-SE" :order 8
    :special "After selecting this Strategy Card, reveal cards from the Objective
     deck equal to the number of Bonus Counters on this card."
    :primary "Receive 1 Command token from your reinforcements. Then draw the
     top two cards from the Objective deck. Place one face up in the common play area
     and the other on the top of the deck. You may then immediately claim one
     public objective you qualify for." }
   { :id :imperial2 :rules "TI3-SE" :order 8
    :primary "Choose either a) or b)
     a) During the upcoming Status Phase, you may qualify for any number of
     Public Objectives. Also, if you control Mecatol Rex, immediately gain 1 Victory Point." }
   { :id :imperial4 :rules "TI4" :order 8
    :primary "Immediately score 1 public objective if you fulfill its requirements.
     Gain 1 victory point if you control Mecatol Rex; otherwise, draw 1 secret objective."
    :secondary "Spend 1 token from your strategy pool to draw 1 secret objective." }] )
