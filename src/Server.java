import java.net.*;
import java.io.*;
import java.util.UUID;

public class Server {

    // Deklaration des Serversockets und Serverports
    public final static int DEFAULT_PORT = 7777;
    private ServerSocket serverS = null;

    // Definition des Pfades zur Ablage der generierten Dateien / Nachrichten
    public final String PATH = System.getProperty("user.home") + "/Desktop/Messages/";
    // Achtung: Die Deklaration dieses Pfades funktioniert in dieser Form nur in bestimmten Sprachen sowie Betriebs-
    //          systemen. Für andere Sprachen als Deutsch, Englisch sowie einem anderen System als Windows muss dies
    //          ggf. umgestellt werden.

    /**
     * Konstruktor der Klasse "Server.java"
     *
     * @param port - Mitgabe des Ports, auf welchem der Server / Serversocket gestartet werden soll
     * @return Server-Objekt - Erstellung eines Servers mit ein Socket auf dem entsprechenden Port
     */
    public Server (int port){
        try {

            // Zuweisung des Ports zum Serversocket
            serverS = new ServerSocket(port);
            System.out.println("SUCCESS: Server was started on port: " + port + "!");

            // Initialisierung eines Pointers auf den Ordner
            File folder = new File(PATH);
            // Überprüfung auf Exitenz eines entsprechenden Ordners
            if(!folder.exists())
                // existiert keiner, wird einer am entsprechenden Pfad erstellt
                System.out.println("ATTENTION: Message Folder at the corresponding path does not exist.\nCreating " +
                        "one...");
                folder.mkdirs();

        } catch (IOException e){
            // bei Auftreten einer IOException: Fehlerausgabe und Beenden des Programms
            System.err.println("ERROR: " + e + "\nCould not start the server! Exiting program...");
            System.exit(1);
        }
    }

    /**
     * Methode zur Generierung eines eindeutigen Schlüssels mittels UUID
     *
     * @return String - UUID als eindeutiger Schlüssel
     */
    public String generateKey (){

        // Initialisierung einer Variable "key" mit einer zufälligen UUID
        UUID key = UUID.randomUUID();
        // Rückgabe dieses "key" als String
        return key.toString();
    }

    /**
     * auszuführende Methode beim Start von "Server.java"
     *
     * @return void
     */
    public void start(){

        // Erstellung und Vorinitialisierung der benötigten Variablen
        Socket connection = null;
        PrintWriter out = null;
        BufferedReader in = null;
        String command = "";
        String result = "FAILURE";

        // Endlosschleife zum dauerhaften entgegennehmen der Connections
        while (true) {
            try {

                // Akzeptieren der Verbindung zum Server oder Warten bis eine Verbindung aufgebaut / angefragt wird
                connection = serverS.accept();
                System.out.println("SUCCESS: Connection was accepted by the client!");

                // Initialisierung des Readers für einkommende Nachrichten
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                // Initialisierung des Writers zum Schreiben des Outputs
                out = new PrintWriter(connection.getOutputStream());

                // Einlesen und Speicherung des Befehls vom Benutzer-Input in einer Variable
                command = in.readLine();
                // Überprüfung, ob Variable "beschrieben" ist
                if (command != "") {
                    // besitzt Variable einen String-Wert --> Weitergabe an process-Methode
                    result = process(command);
                    // Ausgabe des Rückgabewerts aus process-Methode durch Writer des Output-Streams
                    out.println(result);
                }

                // Schließen des Writers --> also des Output-Streams
                out.close();

            } catch (IOException e){
                // bei Auftritt einer IOException: Fehlerausgabe
                System.err.println("FAILED: " + e + "\nFailed to establish a connection!");

            // Ausführung von finally am Ende jeder Iteration zum Schließen der verwendeten Ressourcen
            } finally {
                try {

                    // Überprüfung auf Existenz einer Connection
                    if (connection != null ) {
                        // Schließen der Connection, wenn eine vorhanden ist und Ausgabe dieser Information an Benutzer
                        System.out.println("ATTENTION: Connection will be closed now!");
                        connection.close();
                    }

                } catch (IOException e) {
                    // bei Auftritt einer IOException: nur Fehlerausgabe --> Connection ist automatisch nicht mehr
                    //                                 verwendbar, sollte eine Exception auftreten
                    System.err.println("FAILED: " + e + "\nFailed to close the connection accordingly.");
                }
            }
        }
    }

    /**
     * Diese Methode dient der Verarbeitung der Benutzer-Eingabe. Dazu gehört die Eingabe und Speicherung des Benutzer-
     * Inputs, das Finden und Ausgeben von bereits Gespeichertem oder die Fehlerbehandlung bei falschen Befehlen.
     *
     * @param command - Input eines Befehls vom User aus der Standardeingabe
     * @return String - Rückgabe der Varaible "output"
     */
    public String process (String command) {

        // Erstellung und Vorinitialisierung der verwendeten Variablen
        String output = "FAILED";
        String key = "";

        // Wird kein Befehl auf Grund von "EXIT" übergeben, würde er sonst beim Aufrufen von "split" in einen Fehler
        // laufen. Durch vorherige Überprüfung soll dies vermieden werden.
        if (command == null) {
            return "FAILED: Something failed on the client-side. Closing the server...";
        }

        // Aufspaltung des vom Benutzer eingegebenen Befehls, sodass der erste Ausdruck im Array gespeichert wird
        String[] piece = command.split(" ", 2);

        // Überprüfung auf Länge des oben definierten Arrays
        if (piece.length < 2) {
            // Ist das Array kürzer als 2, wird ein Fehler geworfen.
            return "FAILED: A wrong command was used!";
        }

        // Ist der erste Ausdruck des Arrays "SAVE", führe folgenden Code aus:
        if (piece[0].equalsIgnoreCase("SAVE")) {

            // Generieren eines eindeutigen Schlüssels für jeden neue Nachricht
            key = generateKey();
            // Generieren eines File-Objekts mit dem vorher generierten Schlüssel als Dateiname, welches im oben
            // definierten Pfad abgelegt werden soll
            File f = new File(PATH, key + ".txt");

            // Überprüfung auf Existenz dieser Datei
            if(!f.exists()) {
                try {
                    // Ist diese Datei noch nicht vorhanden, wird sie im entsprechenden Order angelegt.
                    f.createNewFile();
                    System.out.println("SUCCESS: A new file with the path " + f + " was created!");

                } catch (IOException e) {
                    // bei Auftreten einer IOException bei der File-Erzeugung: Fehlerausgabe
                    output = "FAILED: " + e + "\nProgram failed to create the corresponding file in the given directory!";
                }
            }

            try {

                // Erstellung und Initialisierung eines FileWriters zum Speichern des Benutzer-Inputs in einer Datei
                PrintWriter pw = new PrintWriter(new FileWriter(f));
                // Schreiben des Benutzer-Inputs nach dem Befehl in die Datei
                pw.println(piece[1]);
                output = "KEY: "+ key + "\nSUCCESS: The key was successfully saved in the corresponding directory!";
                // Senden der Nachricht, sodass diese auch wirklich durchkommt
                pw.flush();
                pw.close();

            } catch (Exception e){
                // bei Auftreten einer beliebigen Exception: Fehlerausgabe
                output = "FAILED: " + e + "\nProgram failed to save the content or its key!";
            }

        // Ist der erste Ausdruck des Arrays "GET", führe folgenden Code aus:
        } else if (piece[0].equalsIgnoreCase("GET")) {

            // Speichern des eindeutigen Schlüssels einer Datei (eingegeben durch Benutzer) in der Variable "key"
            key = piece[1];

            try {

                // Erstellung und Initialisierung eines BufferedReaders für den angegebenen Schlüssel
                BufferedReader br = new BufferedReader(new FileReader(PATH + key + ".txt"));
                // Einlesen des Inhalts dieser Datei in die Variable "output"
                output = br.readLine();

                // Überprüfung, ob die Variable "output" befüllt ist
                if (output != null) {
                    // Besitzt diese einen String-Wert, so wird dieser dem Benutzer ausgegeben.
                    output = "The command is OK! OUTPUT: " + output;
                }

                // zum Ende: Schließen des Readers
                br.close();

            } catch (Exception e){
                // bei Auftreten einer beliebigen Exception: Fehlerausgabe
                output = "FAILED: " + e + "\nProgram failed to get the key or its content!";
            }

        } else {
            // Ist der erste Ausdruck keiner der definierten Befehle, teile dem Benutzer die möglichen Befehle mit.
            output = "FAILED: The given command is unknown! Please use the commands 'SAVE <Input>', 'GET <Key>' or 'EXIT'.";
        }

        // Ausgabe des Variable "output" mit dem jeweiligen belegten String
        return output;
    }

    /**
     * Einstiegspunkt des Programms "Server.java"
     *
     * @param args - Verwendung zur Überprüfung des Ports
     */
    public static void main(String[] args) {

        // Setzen des Ports auf den oben initialisierten Standardport
        int port = DEFAULT_PORT;

        // Überprüfung auf Länge des mitgegebenen Arrays "args", welches unter anderem die Portnummer beinhaltet
        if (args.length > 0) {
            try {

                // Überführung der Portnummer in eine Zahl im Integer-Format zur Validierung
                port = Integer.parseInt(args[0]);

                // Überprüfung, ob der angegebene Port nicht zwischen 0 und 65.536 liegt
                if (port < 0 || port >= 65536) {
                    // trifft dies zu: Ausgabe einer Fehlermeldung und Beenden des Programms
                    System.err.println("FAILED: The port must be between 0 and 65535. Exiting program...");
                    return;
                }

            } catch (NumberFormatException e) {
                // bei Auftreten einer NumberFormatException durch das Parsen: Fehlerausgabe und Beenden des Programms
                System.err.println("FAILED: " + e + "\nSomething failed to work. Exiting program...");
                return;
            }
        }

        // eigentliche Initialisierung eines Server-Objekts und Start von diesem
        Server server = new Server(port);
        server.start();
    }
}
