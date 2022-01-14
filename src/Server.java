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

                //
                connection = serverS.accept();
                System.out.println("SUCCESS: Connection was accepted by the client!");

                //
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                out = new PrintWriter(connection.getOutputStream());

                //
                command = in.readLine();
                //
                if (command != null) {
                    //
                    result = process(command);
                    //
                    out.println(result);
                }

                // Schließen des Writers --> also des Output-Streams
                out.close();

            } catch (IOException e){
                // bei Auftritt einer IOException: Fehlerausgabe
                System.err.println("ERROR: " + e + "\nFailed to establish a connection!");

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
                    System.err.println("ERROR: " + e + "\nFailed to close the connection accordingly.");
                }
            }
        }
    }

    /**
     * Einstiegspunkt des Programms "Server.java"
     *
     * @param command - Input eines Befehls vom User aus der Standardeingabe
     * @return String - Rückgabe der Varaible "output"
     */
    public String process (String command) {

        //
        String output = "FAILURE";
        String key = "";

        //
        String[] piece = command.split(" ", 2);

        //
        if (piece.length < 2) {
            return "FAILURE: A wrong command was used!";
        }

        //
        if (piece[0].equalsIgnoreCase("SAVE")) {

            //
            key = generateKey();
            //
            File f = new File(PATH, key + ".txt");

            //
            if(!f.exists()) {
                try {
                    //
                    f.createNewFile();
                    System.out.println("SUCCESS: A new file with the name " + f + " was created!");

                } catch (IOException e) {
                    //
                    output = "ERROR: " + e + "\nProgram failed to create the corresponding file in the given directory!";
                }
            }

            try {

                //
                PrintWriter pw = new PrintWriter(new FileWriter(f));
                pw.println(piece[1]);
                output = "KEY: "+ key + "\nSUCCESS: The key was successfully saved in the corresponding directory!";
                pw.flush();
                pw.close();

            } catch (Exception e){
                //
                output = "ERROR: " + e + "\nProgram failed to save the content or its key!";
            }

        //
        } else if (piece[0].equalsIgnoreCase("GET")) {

            //
            key = piece[1];

            try {

                //
                BufferedReader br = new BufferedReader(new FileReader(PATH + key + ".txt"));
                output = br.readLine();

                //
                if (output != null) {
                    output = "The command is OK! OUTPUT: " + output;
                }

                //
                br.close();

            } catch (Exception e){
                //
                output = "ERROR: " + e + "\nProgram failed to get the key or its content!";
            }
        } else {
            //
            output = "ERROR: The given command is unknown! Please use the commands 'SAVE <Input>', 'GET <Key>' or 'EXIT'.";
        }

        //
        return output;
    }

    /**
     * Einstiegspunkt des Programms "Server.java"
     *
     * @param args - Verwendung zur Überprüfung des Ports
     */
    public static void main(String[] args) {

        //
        int port = DEFAULT_PORT;

        //
        if (args.length > 0) {
            try {

                //
                port = Integer.parseInt(args[0]);

                //
                if (port < 0 || port >= 65536) {
                    //
                    System.err.println("ERROR: The port must be between 0 and 65535. Exiting program...");
                    return;
                }

            } catch (NumberFormatException e) {
                //
                System.err.println("ERROR: " + e + "\nSomething failed to work. Exiting program...");
                return;
            }
        }

        //
        Server server = new Server(port);
        server.start();
    }
}
