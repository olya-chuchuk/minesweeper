package minesweeper.server;

import java.io.*;
import java.net.*;
import java.util.*;

import minesweeper.Board;

/**
 * Multiplayer Minesweeper server.
 */
public class MinesweeperServer {

    // System thread safety argument
    // serverSocket is only used by main thread
    // debug is immutable
    // clientNumber is int - thread safe type
    // board is Board - thread safe type

    /** Default server port. */
    private static final int DEFAULT_PORT = 4444;
    /** Maximum port number as defined by ServerSocket. */
    private static final int MAXIMUM_PORT = 65535;
    /** Default square board size. */
    private static final int DEFAULT_SIZE = 10;

    /** Help message for clients */
    private static final String helpMessage = "Supported commands are:\r\n"
            + String.format("%-15s", "look")
            + "displays current state of a board\r\n"
            + String.format("%-15s", "dig [x] [y]")
            + "digs a cell in column x row yif it was untouched\r\n"
            + String.format("%-15s", "flag [x] [y]")
            + "flags a cell in column x row y if it was untouched\r\n"
            + String.format("%-15s", "deflag [x] [y]")
            + "deflags a cell in column x row y if it was flagged\r\n"
            + String.format("%-15s", "help")
            + "shows instructions\r\n"
            + String.format("%-15s", "bye")
            + "ends a connection";
    /** Boom message */
    private static final String boomMessage = "BOOM!";
    /** Pattern of welcome message */
    private static final String welcomeMessage = "Welcome to Minesweeper. Players: %d including you. Board: %d columns by %d rows. Type 'help' for help.\r\n";
    
    
    /** Socket for receiving incoming connections. */
    private final ServerSocket serverSocket;
    /** True if the server should *not* disconnect a client after a BOOM message. */
    private final boolean debug;
    /** Number of clients connected to the server. */
    private int clientNumber = 0;
    /** Minesweeper board */
    private final Board board;
    

    // Abstraction function
    // Represents a server running on localhost and serves a board board
    // Accepting messages with socket serverSocket and working in debug mode if debug is true
    // clientNumber is number of clients connected to this server
    
    // Rep invariant
    // serverSocket != null
    // clientNumber equals number of connected sockets
    // board != null
    
    // Rep exposure
    // all fields are private
    // all fields, except clientNumber, are final
    // clientNumber is int, debug is boolean, immutable types
    // serverSocket is final and reference is never shared
    // board is final and reference is never shared
    
    private void checkRep() {
        assert serverSocket != null;
        assert board != null;
    }
    

    /**
     * Make a MinesweeperServer that listens for connections on port.
     * 
     * @param port port number, requires 0 < port <= 65535
     * @param debug debug mode flag
     * @throws IOException if an error occurs opening the server socket
     */
    public MinesweeperServer(Board board, int port, boolean debug) throws IOException {
        this.board = board;
        serverSocket = new ServerSocket(port);
        System.out.println("Created on port " + port);
        this.debug = debug;
        checkRep();
    }

    /**
     * Run the server, listening for client connections and handling them.
     * Never returns unless an exception is thrown.
     * 
     * @throws IOException if the main server socket is broken
     *                     (IOExceptions from individual clients do *not* terminate serve())
     */
    public void serve() throws IOException { 
        System.out.println("Waiting on port " + serverSocket.getLocalPort());
        while (true) {
            // block until a client connects
            Socket socket = serverSocket.accept();
            System.out.println("Accepted client");
            clientNumber++;
            new Thread(() -> {
                try {
                    handleConnection(socket);
                } catch (IOException ioe) {
                    ioe.printStackTrace(); // but don't terminate serve()
                } finally {
                    try {
                        clientNumber--;
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            checkRep();
        }
    }

    /**
     * Handle a single client connection. Returns when client disconnects.
     * 
     * @param socket socket where the client is connected
     * @throws IOException if the connection encounters an error or terminates unexpectedly
     */
    private void handleConnection(Socket socket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        System.out.println("welcome " + Thread.activeCount());        
        out.format(String.format(welcomeMessage, clientNumber, board.columnsNumber(), board.rowsNumber()));
        try {
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                System.out.println(line);
                String output = handleRequest(line);
                if (output != "") {
                    out.println(output);
                }
                if(output == "" || (output == boomMessage && !debug)) break;
            }
        } finally {
            out.close();
            in.close();
            checkRep();
        }
    }

    /**
     * Handler for client input, performing requested operations and returning an output message.
     * 
     * @param input message from client
     * @return message to client, or empty string if none
     */
    private String handleRequest(String input) {
        String regex = "(look)|(help)|(bye)|"
                     + "(dig -?\\d+ -?\\d+)|(flag -?\\d+ -?\\d+)|(deflag -?\\d+ -?\\d+)";
        if ( ! input.matches(regex)) {
            // invalid input
            return helpMessage;
        }
        String[] tokens = input.split(" ");
        if (tokens[0].equals("look")) {
            // 'look' request
            return board.toString();
        } else if (tokens[0].equals("help")) {
            // 'help' request
            return helpMessage;
        } else if (tokens[0].equals("bye")) {
            // 'bye' request
            return "";
        } else {
            int x = Integer.parseInt(tokens[1]);
            int y = Integer.parseInt(tokens[2]);
            if (tokens[0].equals("dig")) {
                // 'dig x y' request
                if(x >= 0 && y >= 0 && x < board.columnsNumber() && y < board.rowsNumber()) {
                    boolean bomb = board.digIfUntouched(x, y);
                    if(bomb) return boomMessage;
                    return board.toString();
                }
            } else if (tokens[0].equals("flag")) {
                // 'flag x y' request
                if(x >= 0 && y >= 0 && x < board.columnsNumber() && y < board.rowsNumber()) {
                    board.flagIfUntouched(x, y);
                    return board.toString();
                }
            } else if (tokens[0].equals("deflag")) {
                // 'deflag x y' request
                if(x >= 0 && y >= 0 && x < board.columnsNumber() && y < board.rowsNumber()) {
                    board.deflagIfFlagged(x, y);
                    return board.toString();
                }
            }
        }
        throw new UnsupportedOperationException();
    }

    /**
     * Start a MinesweeperServer using the given arguments.
     * 
     * <br> Usage:
     *      MinesweeperServer [--debug | --no-debug] [--port PORT] [--size SIZE_X,SIZE_Y | --file FILE]
     * 
     * <br> The --debug argument means the server should run in debug mode. The server should disconnect a
     *      client after a BOOM message if and only if the --debug flag was NOT given.
     *      Using --no-debug is the same as using no flag at all.
     * <br> E.g. "MinesweeperServer --debug" starts the server in debug mode.
     * 
     * <br> PORT is an optional integer in the range 0 to 65535 inclusive, specifying the port the server
     *      should be listening on for incoming connections.
     * <br> E.g. "MinesweeperServer --port 1234" starts the server listening on port 1234.
     * 
     * <br> SIZE_X and SIZE_Y are optional positive integer arguments, specifying that a random board of size
     *      SIZE_X*SIZE_Y should be generated.
     * <br> E.g. "MinesweeperServer --size 42,58" starts the server initialized with a random board of size
     *      42*58.
     * 
     * <br> FILE is an optional argument specifying a file pathname where a board has been stored. If this
     *      argument is given, the stored board should be loaded as the starting board.
     * <br> E.g. "MinesweeperServer --file boardfile.txt" starts the server initialized with the board stored
     *      in boardfile.txt.
     * 
     * <br> The board file format, for use with the "--file" option, is specified by the following grammar:
     * <pre>
     *   FILE ::= BOARD LINE+
     *   BOARD ::= X SPACE Y NEWLINE
     *   LINE ::= (VAL SPACE)* VAL NEWLINE
     *   VAL ::= 0 | 1
     *   X ::= INT
     *   Y ::= INT
     *   SPACE ::= " "
     *   NEWLINE ::= "\n" | "\r" "\n"?
     *   INT ::= [0-9]+
     * </pre>
     * 
     * <br> If neither --file nor --size is given, generate a random board of size 10x10.
     * 
     * <br> Note that --file and --size may not be specified simultaneously.
     * 
     * @param args arguments as described
     */
    public static void main(String[] args) {
        boolean debug = false;
        int port = DEFAULT_PORT;
        int sizeX = DEFAULT_SIZE;
        int sizeY = DEFAULT_SIZE;
        Optional<File> file = Optional.empty();

        Queue<String> arguments = new LinkedList<String>(Arrays.asList(args));
        try {
            while ( ! arguments.isEmpty()) {
                String flag = arguments.remove();
                try {
                    if (flag.equals("--debug")) {
                        debug = true;
                    } else if (flag.equals("--no-debug")) {
                        debug = false;
                    } else if (flag.equals("--port")) {
                        port = Integer.parseInt(arguments.remove());
                        if (port < 0 || port > MAXIMUM_PORT) {
                            throw new IllegalArgumentException("port " + port + " out of range");
                        }
                    } else if (flag.equals("--size")) {
                        String[] sizes = arguments.remove().split(",");
                        sizeX = Integer.parseInt(sizes[0]);
                        sizeY = Integer.parseInt(sizes[1]);
                        file = Optional.empty();
                    } else if (flag.equals("--file")) {
                        sizeX = -1;
                        sizeY = -1;
                        file = Optional.of(new File(arguments.remove()));
                        if ( ! file.get().isFile()) {
                            throw new IllegalArgumentException("file not found: \"" + file.get() + "\"");
                        }
                    } else {
                        throw new IllegalArgumentException("unknown option: \"" + flag + "\"");
                    }
                } catch (NoSuchElementException nsee) {
                    throw new IllegalArgumentException("missing argument for " + flag);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("unable to parse number for " + flag);
                }
            }
        } catch (IllegalArgumentException iae) {
            System.err.println(iae.getMessage());
            System.err.println("usage: MinesweeperServer [--debug | --no-debug] [--port PORT] [--size SIZE_X,SIZE_Y | --file FILE]");
            return;
        }

        try {
            runMinesweeperServer(debug, file, sizeX, sizeY, port);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Start a MinesweeperServer running on the specified port, with either a random new board or a
     * board loaded from a file.
     * 
     * @param debug The server will disconnect a client after a BOOM message if and only if debug is false.
     * @param file If file.isPresent(), start with a board loaded from the specified file,
     *             according to the input file format defined in the documentation for main(..).
     * @param sizeX If (!file.isPresent()), start with a random board with width sizeX
     *              (and require sizeX > 0).
     * @param sizeY If (!file.isPresent()), start with a random board with height sizeY
     *              (and require sizeY > 0).
     * @param port The network port on which the server should listen, requires 0 <= port <= 65535.
     * @throws IOException if a network error occurs
     */
    public static void runMinesweeperServer(boolean debug, Optional<File> file, int sizeX, int sizeY, int port) throws IOException {
        Board board;
        if(file.isPresent()) {
                board = new Board(file.get());
            } else {
                board = new Board(sizeX, sizeY);
            }        
        MinesweeperServer server = new MinesweeperServer(board, port, debug);
        try {            
            server.serve();
        } finally {
            if(server != null) {
               // server.close();
            }
        }
    }
    
//    /**
//     * Method closes current server socket
//     * should be called at the end of using minesweeperServer
//     * @throws IOException 
//     */
//    public void close() throws IOException {
//        serverSocket.close();
//        System.out.println("Closed port" + serverSocket.getLocalPort());
//    }
}
