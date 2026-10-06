scoreboard players add @e[tag=ferrum_fx] ferrum_age 1

# hold in the ring (first 2s)
execute as @e[tag=ferrum_boss,limit=1] at @s run tp @e[tag=ferrum_pilot,scores={ferrum_slot=1,ferrum_age=..39}] ^1 ^0.47 ^-1.5
execute as @e[tag=ferrum_boss,limit=1] at @s run tp @e[tag=ferrum_pilot,scores={ferrum_slot=2,ferrum_age=..39}] ^2 ^2.2 ^-1.5
execute as @e[tag=ferrum_boss,limit=1] at @s run tp @e[tag=ferrum_pilot,scores={ferrum_slot=3,ferrum_age=..39}] ^1 ^3.93 ^-1.5
execute as @e[tag=ferrum_boss,limit=1] at @s run tp @e[tag=ferrum_pilot,scores={ferrum_slot=4,ferrum_age=..39}] ^-1 ^3.93 ^-1.5
execute as @e[tag=ferrum_boss,limit=1] at @s run tp @e[tag=ferrum_pilot,scores={ferrum_slot=5,ferrum_age=..39}] ^-2 ^2.2 ^-1.5
execute as @e[tag=ferrum_boss,limit=1] at @s run tp @e[tag=ferrum_pilot,scores={ferrum_slot=6,ferrum_age=..39}] ^-1 ^0.47 ^-1.5

# aim once at launch; the marker keeps that rotation
execute as @e[tag=ferrum_pilot,scores={ferrum_age=40}] at @s run tp @s ~ ~ ~ facing entity @p[tag=ferrum_target] eyes
# fly straight along that line
execute as @e[tag=ferrum_pilot,scores={ferrum_age=40..}] at @s run tp @s ^ ^ ^0.6

# photons follow their pilot, no gravity or sticking
execute as @e[tag=ferrum_pilot] at @s run tp @e[tag=ferrum_photon,sort=nearest,limit=1] ~ ~ ~
execute as @e[tag=ferrum_photon] run data merge entity @s {Motion:[0.0,0.0,0.0],inGround:0b}

# hit check (radius 2 so it counts from eye height)
execute as @e[tag=ferrum_photon,scores={ferrum_age=40..}] at @s if entity @p[tag=ferrum_target,distance=..2.2] run function ferrum:hit

# ---- RADIANCE STRIKES: absolute_lux pulses mark each spot, the radiance lands at 20 ticks (1s)
execute as @e[tag=smite_mark,scores={ferrum_age=1}] at @s if entity @e[tag=ferrum_boss,distance=..6] run kill @s
execute as @e[tag=smite_mark,scores={ferrum_age=19}] run effect give @e[tag=ferrum_boss] minecraft:resistance 1 4 true
execute as @e[tag=smite_mark,scores={ferrum_age=1}] at @s run particle the_faint_radiance:absolute_lux ~ ~0.1 ~ 0 0 0 0 1
execute as @e[tag=smite_mark,scores={ferrum_age=8}] at @s run particle the_faint_radiance:absolute_lux ~ ~0.1 ~ 0 0 0 0 1
execute as @e[tag=smite_mark,scores={ferrum_age=15}] at @s run particle the_faint_radiance:absolute_lux ~ ~0.1 ~ 0 0 0 0 1
execute as @e[tag=smite_mark,scores={ferrum_age=20}] at @s run summon the_faint_radiance:radiance ~ ~ ~
execute as @e[tag=smite_mark,scores={ferrum_age=20}] at @s run summon the_faint_radiance:holy ~ ~ ~
kill @e[tag=smite_mark,scores={ferrum_age=20..}]

# cleanup (6s max life, covers all ferrum_fx entities)
kill @e[tag=ferrum_fx,scores={ferrum_age=120..}]
