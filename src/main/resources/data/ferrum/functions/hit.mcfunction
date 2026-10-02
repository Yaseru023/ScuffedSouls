# runs as a photon, at the photon, when it reaches the target
damage @p[tag=ferrum_target,distance=..2] 8 minecraft:magic
kill @e[tag=ferrum_pilot,distance=..0.5,sort=nearest,limit=1]
kill @s
