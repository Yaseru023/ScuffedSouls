# runs as a roar photon, at the photon, when a player is in range
damage @a[distance=..1.5,gamemode=!spectator] 6 minecraft:magic
kill @e[tag=ferrum_orbit_pilot,distance=..0.6,sort=nearest,limit=1]
kill @s
