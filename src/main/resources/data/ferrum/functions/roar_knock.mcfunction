# runs as/at Ferrum: shove every player within 12 blocks away from him
execute at @s as @a[distance=..12] run damage @s 1 minecraft:mob_attack by @e[tag=ferrum_boss,limit=1,sort=nearest]
