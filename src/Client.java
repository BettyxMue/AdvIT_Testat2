import java.net.*;
import java.io.*;

public class Client {

    // Deklaration des Hosts und Serverports
    public static final int SERVER_PORT = 7777;
    public static String host = "localhost";

    /**
     * Die main-Methode des Clients.
     *
     * @param args - Startparameter des Programms
     */
    public static void main(String[] args) {

        // Überprüfung auf
        if (args.length > 0) {
            host = args[0];
        }

        // Initialisierung der benötigten Variablen als "null pointer"
        PrintWriter networkOut = null;
        BufferedReader networkIn = null;
        Socket s = null;

        // Start der Endlosschleife für das dauerhafte entgegennehmen von Befehlen
        while (true) {
            try {

                s = new Socket(host, SERVER_PORT);


                System.out.println("SUCCESS: Connected to server!\nPlease enter a command:");


                BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));


                networkIn = new BufferedReader(new InputStreamReader(s.getInputStream()));


                networkOut = new PrintWriter(s.getOutputStream());

                String line = userIn.readLine();
                if (line.equals(".")) {
                    break;
                }

                networkOut.println(line);
                networkOut.flush();
                System.out.println(networkIn.readLine());

            } catch (UnknownHostException e) {
                System.err.println("ERROR: " + e + "\nThe required host could not be found.");
                break;

            } catch (IOException e) {
                System.err.println("ERROR: " + e + "\nFailed to establish the connection.");
                break;

            } catch (Exception e) {
                System.err.println("ERROR: " + e + "\nSomething went wrong. Please try again!");
                break;

            } finally {

                if (networkIn != null) {
                    try {
                        networkIn.close();
                    } catch (IOException e) {
                        System.err.println("ERROR: " + e + "\nFailed to exit the connection.");
                    }
                }

                if (networkOut != null) {
                    networkOut.close();
                }

                if (s != null) {
                    try {
                        s.close();
                    } catch (IOException e) {
                        System.err.println("ERROR: " + e + "\nFailed to close the socket accordingly.");
                    }
                }
            } // Ende des try-catch-Blocks
        } // Ende der while-Schleife
    }
}

