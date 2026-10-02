# spawns one ring slot, then reschedules itself every 4 ticks (0.2s) until 6 are out
scoreboard players add #slot ferrum_age 1
execute if score #slot ferrum_age matches 1 as @e[tag=ferrum_boss,limit=1] at @s positioned ^1 ^0.47 ^-1.5 run function ferrum:make
execute if score #slot ferrum_age matches 2 as @e[tag=ferrum_boss,limit=1] at @s positioned ^2 ^2.2 ^-1.5 run function ferrum:make
execute if score #slot ferrum_age matches 3 as @e[tag=ferrum_boss,limit=1] at @s positioned ^1 ^3.93 ^-1.5 run function ferrum:make
execute if score #slot ferrum_age matches 4 as @e[tag=ferrum_boss,limit=1] at @s positioned ^-1 ^3.93 ^-1.5 run function ferrum:make
execute if score #slot ferrum_age matches 5 as @e[tag=ferrum_boss,limit=1] at @s positioned ^-2 ^2.2 ^-1.5 run function ferrum:make
execute if score #slot ferrum_age matches 6 as @e[tag=ferrum_boss,limit=1] at @s positioned ^-1 ^0.47 ^-1.5 run function ferrum:make
execute if score #slot ferrum_age matches ..5 run schedule function ferrum:spawn 12t
