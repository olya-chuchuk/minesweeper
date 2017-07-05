package minesweeper.server;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.junit.Test;

import minesweeper.Board;

/**
 * Tests for MinesweeperServer data type
 */
public class MinesweeperServerTest {
    
     /**
      * Testing strategy:
      * 
      * mode
      *      debug, no debug
      * board
      *      from file, random
      *      x = 1, x = 2,3, x >= 4
      *      y = 1, y = 2,3, y >= 4
      * port
      *      no port, port < 0, port = 0, 0 < port < 65535, port = 65535, port > 65535
      * clients
      *      1 client, > 1, many clients
      *      
      * Cover-each-part
      */
    
    //Test MinesweeperServer listening on port
    @Test
    public void testMinesweeperServerPort() {
        try {
            MinesweeperServer ms = new MinesweeperServer(new Board(2,3), 4444, false);
            Socket socket = new Socket("localhost", 4444);
        } catch (ConnectException e) {  // if there is no server to connect
            assertTrue("There is nobody waiting on that port", false);
        } catch (IOException e) {   // if constructor throws an exception
            e.printStackTrace();
            assert false;
        }
    }  

    //Test MinesweeperServer port=65535
    @Test
    public void testMinesweeperServer65535Port() {
        try {
            MinesweeperServer ms = new MinesweeperServer(new Board(2,3), 65535, false);
            Socket socket = new Socket("localhost", 65535);
        } catch (UnknownHostException e) {  // if there is no server to connect
            assertTrue("There is nobody waiting on 65535 port", false);
        } catch (IOException e) {   // if constructor throws an exception
            assert false;
        }
    }    

    
    /**
     * Testing strategy
     * 
     * Starting server:
     * 
     * 1. Debug mode: enabled, disabled
     * 2. From file or not.
     * 3. X,Y
     * 4. Port (listens on specified port, on standard port)
     * 
     * Communicating with client:
     * 
     * 1. Multiple clients can connect
     * 2. Communication
     *      hello - welcoming message with proper X,Y and clients number
     *      look - returns a board
     *      help - some message
     *      bye - ends connection
     *      flag - proper board message
     *      deflag - proper board message
     *      dig - no bomb or with bomb(debug mode or not); empty neighbors
     */
    
    /**
     * starts MinesweeperServer with board from specified file on port 4444
     * @param boardName file with board stored
     * @return Thread with server running
     */
    private static Thread startMinesweeperServer(String boardName) {
        String filePath = new File("test\\boards\\" + boardName).getAbsolutePath();
        final String[] args = new String[] {"--file", filePath};
        Thread server = new Thread(() -> MinesweeperServer.main(args));
        server.start();
        return server;
    }
    
    /**
     * starts MinesweeperServer with board from specified file on specified port
     * @param port to connect
     * @param boardName file with board stored
     * @return Thread with server running
     */
    private static Thread startMinesweeperServer(String boardName, int port) {
        String filePath = new File("test\\boards\\" + boardName).getAbsolutePath();
        final String[] args = new String[] {"--port", String.valueOf(port), "--file", filePath};
        Thread server = new Thread(() -> MinesweeperServer.main(args));
        server.start();
        return server;
    }
    
    /**
     * starts MinesweeperServer with board from specified file in debug mode on port 4444
     * @param boardName file with board stored
     * @return Thread with server running
     */
    private static Thread startDebugMinesweeperServer(String boardName) {
        String filePath = new File("test\\boards\\" + boardName).getAbsolutePath();
        final String[] args = new String[] {"--debug", "--file", filePath};
        Thread server = new Thread(() -> MinesweeperServer.main(args));
        server.start();
        return server;
    }

    /**
     * starts MinesweeperServer with board x * y
     * @param x number of columns
     * @param y number of rows
     * @return Thread with server running
     */
    private static Thread startMinesweeperServer(int x, int y) {
        
        final String[] args = new String[] {"--debug", "--size", x + "," + y};
        Thread server = new Thread(() -> MinesweeperServer.main(args));
        server.start();
        return server;
    }
    
    /**
     * creates a connection to a server and sets timeout to 3 sec
     * @return a socket connected to a local server port 4444
     * @throws IOException if occurs
     */
    private static Socket connectToServer() throws IOException{
        Socket socket = new Socket("localhost", 4444);
        //socket.setSoTimeout(3000);
        return socket;
    }

    /**
     * creates a connection to a server and sets timeout to 3 sec
     * @param port port to connect
     * @return a socket connected to a local server specified port
     * @throws IOException if occurs
     */
    private static Socket connectToServer(int port) throws IOException{
        Socket socket = new Socket("localhost", port);
        //socket.setSoTimeout(3000);
        return socket;
    }
    
    //Test board_3_1
    @Test//(timeout=10000)
    public void testBoard_3_1() {
        startDebugMinesweeperServer("board_3_1.txt");
        try {
            Socket clientSocket = connectToServer();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            String helloMessage = in.readLine();
            assertEquals("Welcome to Minesweeper. Players: 1 including you. Board: 3 columns by 1 rows. Type 'help' for help.", helloMessage);
            
            out.println("look");
            assertEquals("- - -", in.readLine());
            
            out.println("dig 1 0");
            assertEquals("- 2 -", in.readLine());
            
            out.println("dig 0 0");
            assertEquals("BOOM!", in.readLine());
            
            out.println("look");
            assertEquals("  1 -", in.readLine());
            
            out.println("bye");
            out.println("look");
            try {
                in.readLine();
                assertTrue("Server must disconnect after bye", false);
            } catch (SocketTimeoutException e) {}
            clientSocket.close();
        } catch (IOException e) {
            assertTrue(false);
        }
        
    }
    
    //Test board_1_2
    @Test//(timeout=10000)
    public void testBoard_1_2() {
        startMinesweeperServer("board_1_2.txt");
        try {
            Socket clientSocket = connectToServer();
            Socket anotherClientSocket = connectToServer();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
            in.readLine(); //hello message
            
            out.println("help");
            in.readLine();  // help message
            
            out.println("flag 0 1");
            assertEquals("-", in.readLine());
            assertEquals("F", in.readLine());
            
            out.println("deflag 0 1");
            assertEquals("-", in.readLine());
            assertEquals("-", in.readLine());
            
            out.println("dig 0 1");
            assertEquals("BOOM!", in.readLine());
            
            out.println("look");
            try {
                in.readLine();
                assertTrue("Server must disconnect after boom", false);
            } catch (SocketTimeoutException e) {}
            

            BufferedReader anotherIn = new BufferedReader(new InputStreamReader(anotherClientSocket.getInputStream()));
            PrintWriter anotherOut = new PrintWriter(anotherClientSocket.getOutputStream());
            anotherIn.readLine();
            anotherOut.println("look");
            assertEquals("-", anotherIn.readLine());
            assertEquals("1", anotherIn.readLine());
            
            clientSocket.close();
            anotherClientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            assertTrue(false);
        }
        
    }
    
    //Test for multiple players
    @Test(timeout=10000)
    public void testMultiplePlayers() {
        startMinesweeperServer(6,7);
        Socket socket;
        try{
            Socket[] sockets = new Socket[10];
            for(int i = 0; i<10; ++i) {
                sockets[i] = connectToServer();
            }
            socket = connectToServer();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            assertEquals("Welcome to Minesweeper. Players: 11 including you. Board: 6 columns by 7 rows. Type 'help' for help.", in.readLine());
            socket.close();
            for(int i = 0; i<10; ++i) {
                sockets[i].close();
            }            
        } catch(IOException e) {
            assertTrue(false);
        }
    }
    
    //Test standard board
    @Test(timeout=10000)
    public void testStandardBoard() throws IOException {
        MinesweeperServer.main(new String[]{});
        Socket socket = connectToServer();
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        assertEquals("Welcome to Minesweeper. Players: 1 including you. Board: 10 columns by 10 rows. Type 'help' for help.", in.readLine());
        socket.close();
    }
    
    //Test entered port and empty neighbors
    @Test(timeout=10000)
    public void testBoard_5_4() throws IOException {
        startMinesweeperServer("board_5_4.txt", 1234);
        Socket socket = connectToServer();
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        in.readLine();
        out.println("dig 3 0");
        assertEquals("- - - - -", in.readLine());
        assertEquals("- - - - -", in.readLine());
        assertEquals("1 2 - - -", in.readLine());
        assertEquals("  1 - - -", in.readLine());
        socket.close();
    }
}
