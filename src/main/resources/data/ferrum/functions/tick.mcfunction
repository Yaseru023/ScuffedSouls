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

# ---- roar orbit photons: spiral outward from Ferrum (gentle turn that eases off, so each arm sweeps away)
execute as @e[tag=ferrum_orbit_pilot,scores={ferrum_age=1..40}] at @s run tp @s ^ ^ ^0.4 ~3 ~
execute as @e[tag=ferrum_orbit_pilot,scores={ferrum_age=41..}] at @s run tp @s ^ ^ ^0.45 ~1.5 ~
execute as @e[tag=ferrum_orbit_pilot] at @s run tp @e[tag=ferrum_orbit_photon,sort=nearest,limit=1] ~ ~ ~
execute as @e[tag=ferrum_orbit_photon] run data merge entity @s {Motion:[0.0,0.0,0.0],inGround:0b}
execute as @e[tag=ferrum_orbit_photon,scores={ferrum_age=1..}] at @s if entity @a[distance=..1.5,gamemode=!spectator] run function ferrum:orbit_hit

# cleanup (6s max life, covers all ferrum_fx entities)
kill @e[tag=ferrum_fx,scores={ferrum_age=120..}]