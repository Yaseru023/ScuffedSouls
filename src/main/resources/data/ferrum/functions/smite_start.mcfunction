# runs as/at Ferrum on the ground smash: scatter strike markers across 30 blocks, then add strikes around each player
# (spreadplayers picks random ground spots; its range is a square, so the kill line trims the corners back to a 30 block circle)
summon marker ~ ~ ~ {Tags:["smite_mark","smite_new","ferrum_fx"]}
summon marker ~ ~ ~ {Tags:["smite_mark","smite_new","ferrum_fx"]}
summon marker ~ ~ ~ {Tags:["smite_mark","smite_new","ferrum_fx"]}
summon marker ~ ~ ~ {Tags:["smite_mark","smite_new","ferrum_fx"]}
summon marker ~ ~ ~ {Tags:["smite_mark","smite_new","ferrum_fx"]}
summon marker ~ ~ ~ {Tags:["smite_mark","smite_new","ferrum_fx"]}
summon marker ~ ~ ~ {Tags:["smite_mark","smite_new","ferrum_fx"]}
summon marker ~ ~ ~ {Tags:["smite_mark","smite_new","ferrum_fx"]}
summon marker ~ ~ ~ {Tags:["smite_mark","smite_new","ferrum_fx"]}
summon marker ~ ~ ~ {Tags:["smite_mark","smite_new","ferrum_fx"]}
summon marker ~ ~ ~ {Tags:["smite_mark","smite_new","ferrum_fx"]}
summon marker ~ ~ ~ {Tags:["smite_mark","smite_new","ferrum_fx"]}
spreadplayers ~ ~ 4 30 false @e[tag=smite_new]
kill @e[tag=smite_new,distance=30..]
tag @e[tag=smite_new] remove smite_new
execute as @a[distance=..30,gamemode=!spectator] at @s run function ferrum:smite_near
playsound minecraft:entity.generic.explosion hostile @a ~ ~ ~ 4 0.5
