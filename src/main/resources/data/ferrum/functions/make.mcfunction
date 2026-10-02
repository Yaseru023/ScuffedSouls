# runs at the ring position: an invisible pilot (does the flying) + the photon (just follows it)
summon marker ~ ~ ~ {Tags:["ferrum_pilot","ferrum_fx"]}
summon the_faint_radiance:the_photon ~ ~ ~ {Tags:["ferrum_photon","ferrum_fx"],NoGravity:1b,pickup:0b,damage:6.0d}
scoreboard players operation @e[tag=ferrum_pilot,tag=!ferrum_set] ferrum_slot = #slot ferrum_age
tag @e[tag=ferrum_pilot,tag=!ferrum_set] add ferrum_set
scale set pehkui:base 0.5 @e[tag=ferrum_photon,tag=!ferrum_scaled]
tag @e[tag=ferrum_photon,tag=!ferrum_scaled] add ferrum_scaled
