# Minesweeper
Client-server multiplayer variant of the classic computer game “Minesweeper”

## Description

Our variant works very similarly to standard Minesweeper, but with multiple players simultaneously playing on a single board. In both versions, players lose when they try to dig an untouched square that happens to contain a bomb. Whereas a standard Minesweeper game would end at this point, in our version, the game keeps going for the other players. In our version, when one player blows up a bomb, they still lose, and the game ends for them (the server ends their connection), but the other players may continue playing. The square where the bomb was blown up is now a dug square with no bomb. The player who lost may reconnect to the same game again via telnet to start playing again.

## Getting Started

### Prerequisites

Version numbers below indicate the versions used.

 * Java 1.8.0_131 (http://java.oracle.com)
 * PuTTY 0.68 (http://www.putty.org)
 
### Building Steps

 * ```git clone https://github.com/olya-chuchuk/minesweeper```
 
## Running

Firstly, run the server

 * ```cd minesweeper/bin```
 * ```java minesweeper.server.MinesweeperServer```
 
 By default the web server uses port 4444.
 
 Now, using your PuTTY client, you can connect to the server and play a game. 
 
 In your PuTTY client set next settings:

  * "Host Name (or IP address)" - "localhost"
  * "Port" - 4444
  * "Connection type" - "Telnet"
  
 Push "Open" button and follow the intstuction. Enjoy your game!

## Documentation

  * [Documentation](http://htmlpreview.github.io/?https://github.com/olya-chuchuk/minesweeper/blob/master/docs/index.html)
  * [Guide](https://github.com/olya-chuchuk/minesweeper/blob/master/guide.md)
 
 For the closer look on how the program works, please, check out the description
 
  * [Description of the Game](https://github.com/olya-chuchuk/minesweeper/blob/master/description.md)
