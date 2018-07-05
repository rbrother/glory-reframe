(ns glory-reframe.races
  (:require [glory-reframe.utils :as utils]
            [clojure.spec.alpha :as spec]))

; see http://gameknight.com/?page_id=474
; Colors from https://community.fantasyflightgames.com/topic/58489-appropriate-colors/
(def all-races-arr
  [ { :id :letnev :name "The Barony of Letnev" :unit-color "Grey"
     :home-system :letnev
     :starting-tech [ :antimass :hylar-v ]
     :leaders [ :admiral :general :diplomat ]
     :trade-agreements [ 1 1 ]
     :staring-units [ :dreadnought :destroyer :carrier :gf :gf :gf ]
     :abilities [
                 "+1 to Fleet Supply"
                 "May spend 2 Trade Goods to get +1 in actual round of space combat or +2 in ground combat" ]
     :racial-techs [
                    { :id :l4-disruptors, :cost 6  }
                    { :id :noneuclidian-shielding, :cost 4 } ]  }
   { :id :saar    :name "The Clan of Saar " :unit-color "Brown" }
   { :id :hacan   :name "The Emirates of Hacan" :unit-color "Yellow" }
   { :id :sol     :name "The Federation of Sol" :unit-color "SteelBlue" }
   { :id :mentak  :name "The Mentak Coalition" :unit-color "Orange" }
   { :id :naalu   :name "The Naalu Collective" :unit-color "Tan" } ; was: Beige
   { :id :norr    :name "The Sardakk N'orr" :unit-color "Red"  }
   { :id :jolnar  :name "The Universities of Jol-Nar" :unit-color "Pink" }
   { :id :winnu   :name "The Winnu" :unit-color "Purple" }
   { :id :xxcha   :name "The Xxcha Kingdom" :unit-color "Olive" }
   { :id :yssaril :name "The Yssaril Tribes" :unit-color "Green" }
   { :id :yin     :name "The Brotherhood of Yin" :unit-color "White" }
   { :id :muaat   :name "The Embers of Muaat" :unit-color "Black" }
   { :id :l1s1x   :name "The L1z1x Mindnet" :unit-color "Blue" }
   { :id :creauss :name "The Ghosts of Creuss" :unit-color "Lilac" }
   { :id :arborec :name "The Arborec" :unit-color "YellowGreen" }
   { :id :nekro   :name "The Nekro Virus" :unit-color "Coral" }
   ] )

(def all-races (utils/index-by-id all-races-arr))

(spec/def ::id #(contains? all-races %))