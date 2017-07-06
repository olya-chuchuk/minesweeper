# Minesweeper

This is a client-server multiplayer variant of the classic computer game “Minesweeper”.

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
  * "Close window on exit" - "Never"
  
 Push "Open" button and follow the intstuction. Enjoy your game!
 
## Rules

 * You can review the [rules of traditional single-player Minesweeper on Wikipedia](https://en.wikipedia.org/wiki/Minesweeper_(video_game)).
 * You can also [play traditional single-player Minesweeper online](http://minesweeperonline.com/).
 
This variant works very similarly to standard Minesweeper, but with multiple players simultaneously playing on a single board. In both versions, players lose when they try to dig an untouched square that happens to contain a bomb. Whereas a standard Minesweeper game would end at this point, in this version, the game keeps going for the other players. In this version, when one player blows up a bomb, they still lose, and the game ends for them (the server ends their connection), but the other players may continue playing. The square where the bomb was blown up is now a dug square with no bomb. The player who lost may reconnect to the same game again via telnet to start playing again.

Please see the protocol of client-server communication
 * [Protocol](https://github.com/olya-chuchuk/minesweeper/blob/master/protocol.md)

## Documentation

  * [Initializing the Board](https://github.com/olya-chuchuk/minesweeper/blob/master/initialize_board.md)
  * [Documentation](http://htmlpreview.github.io/?https://github.com/olya-chuchuk/minesweeper/blob/master/docs/index.html)
  
