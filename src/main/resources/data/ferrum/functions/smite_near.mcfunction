# runs as/at a player: one strike right on them, three more within 5 blocks, so every player has to move
summon marker ~ ~ ~ {Tags:["smite_mark","ferrum_fx"]}
summon marker ~ ~ ~ {Tags:["smite_mark","smite_new","ferrum_fx"]}
summon marker ~ ~ ~ {Tags:["smite_mark","smite_new","ferrum_fx"]}
summon marker ~ ~ ~ {Tags:["smite_mark","smite_new","ferrum_fx"]}
spreadplayers ~ ~ 2 5 false @e[tag=smite_new]
tag @e[tag=smite_new] remove smite_new
