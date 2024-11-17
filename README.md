# Claustrophobia
 
Claustrophobia is a cooped up Minecraft gamemode designed to make you hate your friends. The entire server will be 
limited to a *very* small border. If you die, you are out. After waiting some time, you will be put 
up for vote, which will let you respawn once passed.
  
This experience works best with chaotic friend groups.

## How to play
1. Don't die
2. Make alliances
3. Cause mischief
4. Use `/votemenu` to revive your friends
5. Don't use `/votemenu` to revive your enemies
6. If you die, hopefully have friends that will revive you

## Installation  
To install, add the latest release of the plugin from the 
[downloads page](https://github.com/Pm7-dev/Claustrophobia/releases) to the `plugins` folder in your server and launch 
it. Now look for the `Claustrophobia` folder in the plugin folder, and open `config.yml` to edit the following values:
- `deathTime`: How long (in minutes) players should be dead before being put up for vote. Default is 720 minutes (12 
Hours)
- `reviveVotePercentage`: What percentage of living players will have to have voted for a player for them to be 
revived. Default is 66 percent (~2/3rds) 
- Do not change `borderSize` yet. This is for if you want to change the border size after starting the game
  
Relaunch the server if you made any changes, and proceed to the **Setup** category.

## Setup  
Once you have installed the plugin, join the server on an account with operator permissions and find the spot you want 
to play the game in. Once you have found the spot, run `/startgame <border size>` to set the center of the border at
your player's location and set the border size to the specified value. If no border size is specified, the border
will default to be 100x100. After this, your game will be ready to play.

## Usage
Once a player is up for vote, enter the command `/votemenu` to open the voting menu. Hover over a player's head to see 
their current votes, and click the slimeball beneath the head to vote for that player. To remove your vote from a 
player, click the fire charge icon. 

#### Notes for server managers:
- If for whatever reason you would like to revive a dead player that either does not have enough votes or is still 
waiting to be up for vote, you can use `/reviveplayer <player name>` to revive that player.  
- If a player has become inactive or if you have banned a player, be sure to remove their player data in the `data.yml`
file in the plugin folder. This will make sure they aren't still counted when determining the needed number of votes for 
a player to be revived
- If you would like to end the server, you can run `/endgame`. This command will disable the revive system, and you can
determine a winner based on whatever criteria you want. This isn't necessary, but if you want to do
something fun to end the server, it's there.
- If you want to change the size of the border (or any data that was set in the **Installation** section), close the 
server and change the desired value in the plugin's `config.yml` file.
- If you want to change the center of the border, close the server and edit the plugin's `data.yml` file. Change the 
x and z values in the `gameLocation` to change the center of the border.

## Other bits and/or bobs
There is currently one singular bit or bob. Because the overworld does not use a normal minecraft border, Exiting a 
nether portal will teleport the player to a random safe location on the map (using the same method that determines where 
revived players spawn). If I didn't do this, players would be able to generate a nether portal outside the border and
would immediately die.

# Feature Requests and Bugs
If you have an idea for a feature to add to this game, or have found a bug that needs to be fixed, feel free to make an
issue on the [issues page](https://github.com/Pm7-dev/Claustrophobia/issues)!
