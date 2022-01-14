import java.net.*;
import java.io.*;
import java.util.UUID;

public class Server {

    public final static int DEFAULT_PORT = 7777;
    private ServerSocket serverS = null;

    public final String PATH = System.getProperty("user.home") + "/Desktop/Messages/";

    public Server (int port){
        try {

            serverS = new ServerSocket(port);
            System.out.println("SUCCESS: Server was started on port: " + port + "!");

            File file = new File(PATH);
            if(!file.exists())
                file.mkdirs();

        } catch (IOException e){
            System.err.println("ERROR: " + e + "\nCould not start the server! Exit program...");
            System.exit(1);
        }
    }

    public String generateKey (){

        UUID key = UUID.randomUUID();
        return key.toString();
    }

    public void start(){

        Socket connection = null;
        PrintWriter out = null;
        BufferedReader in = null;
        String command = "";
        String result = "FAILURE";

        while (true) {
            try {

                connection = serverS.accept();
                System.out.println("SUCCESS: Connection was accepted by the client!");

                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                out = new PrintWriter(connection.getOutputStream());

                command = in.readLine();
                if (command != null) {
                    result = process(command);
                    out.println(result);
                }

                out.close();

            } catch (IOException e){
                System.err.println("ERROR: " + e + "\nBuilt of connection failed!");

            } finally {
                try {

                    if (connection != null ) {
                        System.out.println("ATTENTION: Connection will be closed now!");
                        connection.close();
                    }

                } catch (IOException e) { }
            }
        }
    }

    public String process (String command) {

        String output = "FAILURE";
        String key = "";

        String[] piece = command.split(" ", 2);

        if (piece.length < 2) {
            return "FAILURE: A wrong command was used!";
        }

        if (piece[0].equalsIgnoreCase("SAVE")) {

            key = generateKey();
            File f = new File(PATH, key + ".txt");

            if(!f.exists()) {
                try {
                    f.createNewFile();
                    System.out.println("SUCCESS: A new file with the name " + f + " was created!");

                } catch (IOException e) {
                    output = "ERROR: " + e + "\nProgram failed to create the corresponding file in the given directory!";
                }
            }

            try {

                PrintWriter pw = new PrintWriter(new FileWriter(f));
                pw.println(piece[1]);
                output = "KEY: "+ key + "\nSUCCESS: The key was successfully saved in the corresponding directory!";
                pw.flush();
                pw.close();

            } catch (Exception e){
                output = "ERROR: " + e + "\nProgram failed to save the content or its key!";
            }
        } else if (piece[0].equalsIgnoreCase("GET")) {

            key = piece[1];

            try {

                BufferedReader br = new BufferedReader(new FileReader(PATH + key + ".txt"));
                output = br.readLine();

                if (output != null) {
                    output = "The command is OK! OUTPUT: " + output;
                }

                br.close();

            } catch (Exception e){
                output = "ERROR: " + e + "\nProgram failed to get the key or its content!";
            }
        } else {
            output = "ERROR: The given command is unknown! Please use the commands 'SAVE <Input>', 'GET <Key>' or 'EXIT'.";
        }

        return output;
    }

    public static void main(String[] args) {

        int port = DEFAULT_PORT;

        if (args.length > 0) {
            try {

                port = Integer.parseInt(args[0]);

                if (port < 0 || port >= 65536) {

                    System.err.println("ERROR: The port must be between 0 and 65535");
                    return;
                }

            } catch (NumberFormatException e) {
                System.err.println("ERROR: " + e + "\nSomething failed to work. Please try again!");
                return;
            }
        }

        Server server = new Server(port);
        server.start();
    }
}
