![SerialKey](https://media.forgecdn.net/attachments/119/978/SerialKey.png)

_This plugin allows you to lock your doors with a craftable key. It has a master key too._

## Features
* Lock your doors, chests, trapdoors, ...
* A craftable key, master key, key clone, bunch of keys and even a padlock finder !
* Every craft has its own permissions.
* Every item has its own permissions.
* Auto updates.
* Everything is configurable.
* Compatible with the new doors (1.8).
* Open-source (licensed under GNU GPL v3).

## How to use it ?
Left click on a door with an empty key in your hand will create a padlock on the door and will format the key. You can open, close or destroy the door only if you have the corresponding key in your hand. Also works for chests and trapdoors.

## Documentation
**This is the documentation for SerialKey v1.0.**

### How to clone a key ?
As of v0.2, you are able to clone a key. It is easy, all you have to do is to put a blank key next to a key, like that :

![Key clone craft](https://media.forgecdn.net/attachments/119/981/key-clone-craft.png)

That's all ! You get two keys and they open the same padlock.

### How to use the bunch of keys ?
As of v0.2, you are able to create a bunch of keys (see below for the default craft). 
Right click with it will open an inventory in which you can put keys in. It is limited to one key per slot.

![Bunch of keys craft](https://media.forgecdn.net/attachments/119/982/bunch-of-keys-craft-default.png)

You can open padlocks with a bunch of keys if it contains the correct key.

### How to use the padlock finder ?
As of v0.2, you are able to create a padlock finder with the following craft (a key next to a compass) :

![Padlock finder craft](https://media.forgecdn.net/attachments/119/983/padlock-finder-craft.png)

When you make a right click with it, every compass in your inventory will point to the selected padlock. 
Right click again with a padlock finder to disable it.

### Commands
/serialkey getkey - See below for permission. - Allows you to get the key corresponding to your facing block.

### Permissions
* **serialkey.craft.key** - Allows you to craft a key. - _Default :_ Everyone.
* **serialkey.craft.masterkey** - Allows you to craft a master key. - _Default :_ Operators.
* **serialkey.craft.keyclone** - Allows you to craft a key clone. - _Default :_ Everyone.
* **serialkey.craft.bunchofkeys** - Allows you to craft a bunch of keys. - _Default :_ Everyone.
* **serialkey.craft.padlockfinder** - Allows you to craft a padlock finder. - _Default :_ Everyone.
* **serialkey.use.key** - Allows you to use a key. - _Default :_ Everyone.
* **serialkey.use.masterkey** - Allows you to use a master key. - _Default :_ Operators.
* **serialkey.use.bunchofkeys** - Allows you to use a bunch of keys. - _Default :_ Everyone.
* **serialkey.use.padlockfinder** - Allows you to use a padlock finder. - _Default :_ Everyone.
* **serialkey.command.getkey** - Allows you to use /serialkey getkey. - _Default :_ Operators.

### Configuration
**Please stop your server before each modification !**    
_NOTE :_ ``.yml`` can be replaced by `.conf` for Sponge.

#### config.yml

This file allows to configure the plugin.

**enable** - Contains the third party services options.    
— **updater** - If you want to automatically check for updates (or update the plugin for Bukkit).    
— **metrics** - If you want to enable bStats.

**options** - Contains the plugin options.   
— **reusable-keys** - When you destroy a door, if this option is set to true, the plugin will pop a blank key in front of yourself. Otherwise, the key will be broken.    
— **disable-hoppers** - If you want to disable the ability to place hoppers next to locked chests.    
— **encrypt-lore** - This will encrypt keys' lore with the ROT47 algorithm.    
— **can-rename-items** - Whether items can be renamed (be warned : any item of the same kind can be used as a replacement).
— **allow-lost-chests** - Whether players can place keys that unlock chests in them.

**key** - Contains the key item options.   
— **material** - The key item material. _A list of materials is available [here](https://minecraft-ids.grahamedgecombe.com/)._    
— **name** - The key item name. Supports colors.    
— **shape** - The key item craft's shape. Max. three lines and three characters by line. Example : 'ABA', 'BAB', 'ABA'.

The default craft is :    
![Key default craft](https://media.forgecdn.net/attachments/119/979/key-craft-default.png)

**master-key** - Contains the master key item options.    
— **material** - The master key item material. _A list of materials is available [here](https://minecraft-ids.grahamedgecombe.com/)._    
— **name** - The master key item name. Supports colors.    
— **shape** - The master key item craft's shape. Max. three lines and three characters by line. Example : 'ABA', 'BAB', 'ABA'.

The default craft is :    
![Master key default craft](https://media.forgecdn.net/attachments/119/977/masterkey-craft-default-new.png)

**bunch-of-keys** - Contains the bunch of keys item options.    
— **material** - The bunch of keys item material. _A list of materials is available [here](https://minecraft-ids.grahamedgecombe.com/)._    
— **name** - The bunch of keys item name. Supports colors.    
— **shape** - The bunch of keys item craft shape. Max. three lines and three characters by line. Example : 'ABA', 'BAB', 'ABA'.

The default craft is :    
![Bunch of keys default craft](https://media.forgecdn.net/attachments/119/982/bunch-of-keys-craft-default.png)

— **shape-materials-vX** - Contains data about the shape. Example : A: GRASS, B: STONE, C: SPONGE. **Do not take care about vX.** _A list of materials is available [here](https://minecraft-ids.grahamedgecombe.com/)._

#### messages.yml
_This file contains the plugin messages. You can translate it in your language._

#### data.yml
This file contains the plugin data. Please, ignore this (and do not edit it manually)._

## Videos
[Here](https://www.youtube.com/watch?v=R7MYarBGLgs) is the official video (but it is a bit outdated). If you have a YouTube channel and have made a video, do not hesitate to send it to me. I will post it with pleasure !

## Donations
_Development costs time and time is the money of life. Please feel free to donate._

[![Donate](https://www.paypal.com/en_US/i/btn/btn_donate_SM.gif)](https://www.paypal.com/cgi-bin/webscr?hosted_button_id=XLEBVBMQNTXMY&item_name=SerialKey+(from+Ore)&cmd=_s-xclick)
