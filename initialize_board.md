# Initializing the board based on command-line options

The server is able to accept some command-line options.

## Usage

```
MinesweeperServer [--debug | --no-debug] [--port PORT]
                  [--size SIZE_X,SIZE_Y | --file FILE]
```
The --debug argument means the server will run in debug mode. The server will disconnect a client after a BOOM message if and only if the --debug flag was NOT given. Using --no-debug is the same as using no flag at all.

  * E.g. MinesweeperServer --debug starts the server in debug mode.

PORT is an optional integer in the range 0 to 65535 inclusive, specifying the port the server will be listening on for incoming connections.

  * E.g. MinesweeperServer --port 1234 starts the server listening on port 1234.

SIZE_X and SIZE_Y are optional positive integer arguments specifying that a random board of size SIZE_X × SIZE_Y will be generated.

  * E.g. MinesweeperServer --size 42,58 starts the server initialized with a random board of size 42 × 58.

FILE is an optional argument specifying a file pathname where a board has been stored. If this argument is given, the stored board should be loaded as the starting board.

  * E.g. MinesweeperServer --file boardfile.txt starts the server initialized with the board stored in boardfile.txt.
  
For a --size argument: if the passed-in size X,Y > 0, the server’s Board instance will be randomly generated and will have size equal to X by Y. 

For a --file argument: If a file exists at the given path, the file will be read, and if it is properly formatted the server will use this board. The file format for input is:

```
FILE ::= BOARD LINE+
BOARD := X SPACE Y NEWLINE
LINE ::= (VAL SPACE)* VAL NEWLINE
VAL ::= 0 | 1
X ::= INT
Y ::= INT
SPACE ::= " "
NEWLINE ::= "\n" | "\r" "\n"?
INT ::= [0-9]+
```

If neither --file nor --size is given, the server will generate a random board of size 10 × 10.

Note that --file and --size may not be specified simultaneously.

## Running the server on the command line

This is easiest to do from the bin directory. Open a command prompt, cd to directory, where you uploaded this project, then cd minesweeper/bin. Here are some examples:

```
cd minesweeper/bin
java minesweeper.server.MinesweeperServer --debug
java minesweeper.server.MinesweeperServer --port 1234
java minesweeper.server.MinesweeperServer --size 123,234
java minesweeper.server.MinesweeperServer --file ../testBoard
java minesweeper.server.MinesweeperServer --debug --port 1234 --size 20,14
```

In the boards folder we provided you with some examples of board files. You can use them like this:

```java minesweeper.server.MinesweeperServer --file ../boards/board_file_1```
