import java.net.*;
import java.io.*;

public class Client {

    // Deklaration des Hosts und Serverports
    public static final int SERVER_PORT = 7777;
    public static String host = "localhost";

    /**
     * Die main-Methode des Clients.
     *
     * @param args - Standardübergabeparameter der main-Methode
     */
    public static void main(String[] args) {

        // Initialisierung der benötigten Variablen als "null pointer"
        PrintWriter networkOut = null;
        BufferedReader networkIn = null;
        Socket s = null;

        // Start der Endlosschleife für das dauerhafte entgegennehmen von Befehlen
        while (true) {
            try {

                // Erstellung des (neuen) Client-Sockets auf dem entsprechenden Serverport
                // Muss auf Grund der Schließung am Ende der Iteration zum Neustart der Schleife immer erneut geöffnet
                // werden.
                s = new Socket(host, SERVER_PORT);
                System.out.println("SUCCESS: Connected to server!\nPlease enter a command:");

                // Erstellung eines Buffered Readers zur Entgegennahme des Benutzer-Inputs
                BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
                // Deklaration des Buffered Readers zur Entgegennahme von einkommenden Befehlen / Nachrichten
                networkIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
                // Deklaration des Writers für ausgehende Meldungen
                networkOut = new PrintWriter(s.getOutputStream());
                // Speicherung des Benutzer-Inputs in einer Variable
                String line = userIn.readLine();

                // Überprüfung der Variable auf den Inhalt des Befehls: "EXIT"
                if (line.equalsIgnoreCase("EXIT")) {
                    // Beenden des Programms, wenn der oben genannte Befehl vom Benutzer eingegeben wird
                    System.out.println("ATTENTION: The program will exit now.");
                    break;
                }

                // Übergabe des Benutzer-Inputs / Benutzer-Nachricht an den Ausgabe-Stream
                networkOut.println(line);
                // Senden der Nachricht
                networkOut.flush();
                // Ausgabe der Nachricht in der Client-Konsole
                System.out.println(networkIn.readLine());

            } catch (UnknownHostException e) {
                // bei Auftritt einer UnknownHostException: Fehlerausgabe und Beenden des Programms
                System.err.println("ERROR: " + e + "\nThe required host could not be found. Exiting program...");
                break;

            } catch (IOException e) {
                // bei Auftritt einer IOException: Fehlerausgabe und Beenden des Programms
                System.err.println("ERROR: " + e + "\nFailed to establish the connection. Exiting program...");
                break;

            } catch (Exception e) {
                // bei Auftritt einer unbekannten Exception: Fehlerausgabe und Beenden des Programms
                System.err.println("ERROR: " + e + "\nSomething went wrong. Exiting program...");
                break;

            // Ausführung von finally am Ende jeder Iteration zum Schließen der verwendeten Ressourcen
            } finally {

                // Überprüfung auf Existenz eines Input-Streams
                if (networkIn != null) {
                    try {
                        // Schließen des Input-Streams, wenn einer vorhanden ist
                        networkIn.close();

                    } catch (IOException e) {
                        // bei Auftritt einer IOException: nur Fehlerausgabe --> Connection wird sowieso geschlossen
                        System.err.println("ERROR: " + e + "\nFailed to exit the input stream.");
                    }
                }

                // Überprüfung auf Existenz eines Ausgabe-Streams
                if (networkOut != null) {
                    // Schließen des Output-Streams, wenn einer vorhanden ist
                    networkOut.close();
                }

                // Überprüfung auf Existenz einer Socket-Connection
                if (s != null) {
                    try {
                        // Schließen der Socket-Connection, wenn eine vorhanden ist
                        s.close();

                    } catch (IOException e) {
                        // bei Auftritt einer IOException: nur Fehlerausgabe --> Socket-Connection ist automatisch nicht
                        //                                 mehr verwendbar, sollte eine Exception auftreten
                        System.err.println("ERROR: " + e + "\nFailed to close the socket accordingly.");
                    }
                }
            } // Ende des try-catch-Blocks
        } // Ende der while-Schleife
    }
}

