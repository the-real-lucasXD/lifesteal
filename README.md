# Lifesteal

A multipurpose Minecraft Fabric mod that recreates the hearts system in the Lifesteal SMP, alongside other features like custom moderation commands, crafting recipes, and a combat system.

## Disclaimer

This mod is mainly designed not for public purposes, but for use in a private Minecraft server. Please do not expect frequent updates, new features or more config options, as they will only be added when required.

## Features

#### Hearts system
- Configurable minimum and maximum health
- When a player is killed by another player, their heart is transferred unless at minimum health
- If the killer is at maximum health, a heart object drops instead at the player's death position
- `/withdraw <amount>` command to withdraw hearts from a player and convert them into heart items
- `/hearts <player> set/add/remove/get` moderator command to get/update player heart data
- Custom heart crafting recipe

#### Combat system
- Automatically enter combat upon attacking another player
- Block all commands during combat except `/combat` (moderator only) and when in creative mode
- Limit ender pearls (limit editable in config)
- Block mace/spear attacks on first hit of combat
- Blocks fire aspect in combat
- Combat log system (Instantly dies to last player attacker if disconnecting during combat)
- `/combat <player> clear` moderator command to clear combat of selected player
- `/combat <player> enter (<opponent>)` moderator command to make player enter combat

#### Others
- Custom mace crafting recipe
- `/votestop` command to vote for server shutdown in the event of a hacker/greifer
- Unenchantable maces
- Chat name gets redacted if player is invisible while sending message

## License

This project is licensed under the MIT license - see [LICENSE](License) for more details.
