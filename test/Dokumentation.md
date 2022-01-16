# Advanced IT - Testat 2
## Aufgabenstellung
Entwerfen Sie mit TCP einen Server, der Nachrichten speichert und zur Abfrage über das Netz bereit hält. Zum Ablegen 
einer Nachricht auf dem Server sendet ein Client einen String mit dem folgenden Format an den Server:

**SAVE** *beliebig langer String mit abschließendem Zeilenende*

Der Server generiert nach dem Empfang einen neuen geeigneten eindeutigen Schlüssel (als String) und speichert die 
Nachricht in einer Datei, wobei der Schlüssel als Dateiname verwendet wird. Danach sendet der Server den Schlüssel 
zurück an den Client:

**KEY** *schluessel*

Alle Dateien sollen auf dem Server auf dem Desktop im Verzeichnis *”Messages/”* abgespeichert werden, das Sie vorher schon
anlegen sollten.

Zum Abrufen einer Nachricht sendet ein Client einen String:

**GET** *schluessel*

an den Server, der daraufhin überprüft, ob eine entsprechende Datei existiert. Falls ja, sendet er den Inhalt der Datei 
an den Client:

**OK** *dateiinhalt*

Anderenfalls sendet er:

**FAILED**

Implementieren Sie den Server auf Port 7777 sowie einen Client zum Testen.

## Umsetzung
Im Folgenden wird kurz die Vorgehensweise innerhalb der beiden Java-Klassen erklärt. Weitere Erklärungen können 
innerhalb der Code-Dokumentation gefunden werden.

### Client.java
Der Aufbau des Clients orientiert sich an dem Aufbau, wie er in der Vorlesung behandelt und gelernt wurde. Lediglich der
Host sowie der Serverport unterscheiden sich. Des Weiteren erfolgt eine genauere Fehlerausgabe und -behandlung, also
ursprünglich kennengelernt. So ist das Programm benutzerfreundlicher.

```java
PrintWriter networkOut = null;
BufferedReader networkIn = null;
Socket s = null;

while (true) {
    try {
        s = new Socket(host, SERVER_PORT);
        BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
        networkIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
        networkOut = new PrintWriter(s.getOutputStream());
        String line = userIn.readLine();
        
        if (line.equalsIgnoreCase("EXIT")) {
            System.out.println("ATTENTION: The program will exit now.");
            break;
        }
        networkOut.println(line);
        networkOut.flush();
        System.out.println(networkIn.readLine());
        
    } catch (UnknownHostException e) {
        System.err.println("FAILED: " + e + "\nThe required host could not be found. Exiting program...");
        break;
    } catch (IOException e) {
        System.err.println("FAILED: " + e + "\nFailed to establish the connection. Exiting program...");
        break;
    } catch (Exception e) {
        System.err.println("FAILED: " + e + "\nSomething went wrong. Exiting program...");
        break;
        
    } finally {
        if (networkIn != null) {
            try {
                networkIn.close();
            } catch (IOException e) {
                System.err.println("FAILED: " + e + "\nFailed to exit the input stream accordingly.");
            }
        }
        if (networkOut != null) {
            networkOut.close();
        }
        if (s != null) {
            try {
                s.close();
            } catch (IOException e) {
                System.err.println("FAILED: " + e + "\nFailed to close the socket accordingly.");
            }
        }
    } 
} 
```

### Server.java
Auch der Server wurde nach den Maßstäben, wie in der Vorlesung behandelt, angelegt und implementiert. Jedoch war es nun
die Aufgabe hier die entsprechende Logik zu hinterlegen, mit welcher die Befehle entgegengenommen bzw. entsprechende
Inhalte ausgegeben werden. Des Weiteren war auch hier eine benutzerfreundliche und ausführliche Fehlerbehandlung und 
-ausgabe von Nöten, um den Benutzer darüber zu informieren, warum etwas nicht funktioniert oder was das Programm im 
Hintergrund für Aktionen ausführt.

Ausschlaggebend für die entsprechende Behandlung der Befehle ist die folgende Methode ``process``:
```java
public String process (String command) {
    String output = "FAILED";
    String key = "";
    if (command == null) {
        return "FAILED: Something failed on the client-side. Closing the server...";
    }
    String[] piece = command.split(" ", 2);
    if (piece.length < 2) {
        return "FAILED: A wrong command was used!";
    }
    if (piece[0].equalsIgnoreCase("SAVE")) {
        key = generateKey();
        File f = new File(PATH, key + ".txt");
        if(!f.exists()) {
            try {
                f.createNewFile();
                System.out.println("SUCCESS: A new file with the path " + f + " was created!");
            } catch (IOException e) {
                output = "FAILED: " + e + "\nProgram failed to create the corresponding file in the given directory!";
            }
        }
        try {
            PrintWriter pw = new PrintWriter(new FileWriter(f));
            pw.println(piece[1]);
            output = "KEY: "+ key + "\nSUCCESS: The key was successfully saved in the corresponding directory!";
            pw.flush();
            pw.close();
        } catch (Exception e){
            output = "FAILED: " + e + "\nProgram failed to save the content or its key!";
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
            output = "FAILED: " + e + "\nProgram failed to get the key or its content!";
        }
    } else {
        output = "FAILED: The given command is unknown! Please use the commands 'SAVE <Input>', 'GET <Key>' or 'EXIT'.";
    }
    return output;
}
```

Sie ist für die Dateierstellung, Speicherung, Inhaltsausgabe sowie Fehlerbehandlung zuständig. Hier werden die 
entsprechenden Reader und Writer für die jeweilige Aufgabe definiert sowie auch wieder geschlossen. Dabei wird immer mit
dem ``piece``-Array gearbeitet. Dieses enthält an erster Stelle den jeweiligen Befehl und an zweiter Stelle die 
jeweilige Nachricht, die mitgegeben wird. Diese kann wahlweise der Schlüssel oder aber eine Nachricht sein. Genauere
Erklärungen zum Code finden sich direkt in der Code-Dokumentation.

## Beispiele
Zur Darstellung der Anforderungen an den Code werden verschiedene Beispiele durchgespielt, um zu zeigen, dass das 
Programm in der Lage ist mit verschiedenen Situationen umzugehen.

### Beispiele: SAVE
In den folgenden Beispielen wird genauer auf den Befehl "SAVE" eingegangen.

#### "SAVE Hallo Welt, ein schöner Tage heute, nicht wahr?"
In diesem ersten Beispiel soll getestet werden, ob das Programm wie gefordert mittels des Kommandos "SAVE" den String in
einer Datei im entsprechenden Order speichert und mit einem eindeutigen Schlüssel verseht. Der dafür verwendete Text 
lautet: "Hallo Welt, ein schöner Tage heute, nicht wahr?". Es können aber auch Zahlen oder bestimmte Sonderzeichen
mitgegeben werden.

##### Ausgabe
Die Konsolenausgabe des **Clients** sieht für dieses Beispiel wie folgt aus:
```java
SUCCESS: Connected to server!
Please enter a command:
SAVE Hallo Welt, ein schöner Tage heute, nicht wahr?
KEY: 6d7ba594-1ff2-476b-ac38-b44d2c2a78b9
```

Die Konsolenausgabe des **Servers** sieht für dieses Beispiel wie folgt aus:
```java
SUCCESS: Connection was accepted by the client!
SUCCESS: A new file with the path C:\Users\{user}\Desktop\Messages\6d7ba594-1ff2-476b-ac38-b44d2c2a78b9.txt was created!
ATTENTION: Connection will be closed now!
```

##### Auswertung
Wie man an der Ausgabe sehen kann, wird eine neue Datei mit dem erzeugten Schlüssel im angegebenen Ordner angelegt. Der
Inhalt dieser Datei ist der eingegebene String. Dies lässt sich leicht beim manuellen Öffnen der Datei im Ordner 
überprüfen. Danach ist sowohl der Server als auch der Client bereit für eine weitere Eingabe.

#### "save Ich habe Hunger auf Eis!"
In diesem Beispiel soll getestet werden, ob das Programm wie gefordert mittels des Kommandos "SAVE" den String in
einer Datei im entsprechenden Order speichert und mit einem eindeutigen Schlüssel verseht. Hierbei ist nun die
Anforderung, dass der Befehl kleingeschrieben wird, um zu überprüfen, ob das Programm diesen Befehl trotzdem anerkennt.
Der dafür verwendete Text lautet: "Ich habe Hunger auf Eis!". 

##### Ausgabe
Die Konsolenausgabe des **Clients** sieht für dieses Beispiel wie folgt aus:
```java
SUCCESS: Connected to server!
Please enter a command:
save Ich habe Hunger auf Eis!
KEY: 277b85b9-7c6d-4cf7-87cb-0c1965811027
```

Die Konsolenausgabe des **Servers** sieht für dieses Beispiel wie folgt aus:
```java
SUCCESS: Connection was accepted by the client!
SUCCESS: A new file with the path C:\Users\{user}\Desktop\Messages\277b85b9-7c6d-4cf7-87cb-0c1965811027.txt was created!
ATTENTION: Connection will be closed now!
```

##### Auswertung
Wie man an der Ausgabe sehen kann, wird eine neue Datei mit dem erzeugten Schlüssel im angegebenen Ordner angelegt. Der
Inhalt dieser Datei ist der eingegebene String. Dies lässt sich leicht beim manuellen Öffnen der Datei im Ordner
überprüfen. Danach ist sowohl der Server als auch der Client bereit für eine weitere Eingabe. Der Befehl wird also 
anerkannt, obwohl er kleingeschrieben ist.

### Beispiele: GET
In den folgenden Beispielen wird genauer auf den Befehl "GET" eingegangen.

#### "GET 6d7ba594-1ff2-476b-ac38-b44d2c2a78b9"
In diesem Beispiel soll getestet werden, ob das Programm wie gefordert mittels des Kommandos "GET" den gespeicherten
String aus der entsprechenden Datei im Order ausliest und in der Konsole zurückgibt. Der dafür verwendete Schlüssel ist 
der aus dem Beispiel oben und lautet: "6d7ba594-1ff2-476b-ac38-b44d2c2a78b9".

##### Ausgabe
Die Konsolenausgabe des **Clients** sieht für dieses Beispiel wie folgt aus:
```java
SUCCESS: Connected to server!
Please enter a command:
GET 6d7ba594-1ff2-476b-ac38-b44d2c2a78b9
The command is OK! OUTPUT: Hallo Welt, ein schöner Tage heute, nicht wahr?
```

Die Konsolenausgabe des **Servers** sieht für dieses Beispiel wie folgt aus:
```java
SUCCESS: Connection was accepted by the client!
ATTENTION: Connection will be closed now!
```

##### Auswertung
Wie man oben erkennen kann, wir der Befehl anerkannt, woraufhin der Inhalt der vorher angelegten Datei ausgelesen und in
der Konsole ausgegeben wird. Danach ist sowohl der Server als auch der Client bereit für eine weitere Eingabe.

#### "get 277b85b9-7c6d-4cf7-87cb-0c1965811027"
In diesem Beispiel soll getestet werden, ob das Programm wie gefordert mittels des Kommandos "GET" den gespeicherten
String aus der entsprechenden Datei im Order ausliest und in der Konsole zurückgibt. Hierbei ist nun die Anforderung, 
dass der Befehl kleingeschrieben wird, um zu überprüfen, ob das Programm diesen Befehl trotzdem anerkennt. Der dafür 
verwendete Schlüssel ist der aus dem Beispiel oben und lautet: "277b85b9-7c6d-4cf7-87cb-0c1965811027".

##### Ausgabe
Die Konsolenausgabe des **Clients** sieht für dieses Beispiel wie folgt aus:
```java
SUCCESS: Connected to server!
Please enter a command:
get 277b85b9-7c6d-4cf7-87cb-0c1965811027
The command is OK! OUTPUT: Ich habe Hunger auf Eis!
```

Die Konsolenausgabe des **Servers** sieht für dieses Beispiel wie folgt aus:
```java
SUCCESS: Connection was accepted by the client!
ATTENTION: Connection will be closed now!
```

##### Auswertung
Wie man oben erkennen kann, wir der Befehl anerkannt (obwohl er kleingeschrieben ist), woraufhin der Inhalt der vorher 
angelegten Datei ausgelesen und in der Konsole ausgegeben wird. Danach ist sowohl der Server als auch der Client bereit 
für eine weitere Eingabe.

#### "get 1234"
In diesem Beispiel soll getestet werden, wie das Programm damit umgeht, wenn der mitgegebene Schlüssel für eine Datei
nicht im hinterlegten Ordner existiert. Der dafür verwendete Schlüssel ist ausgedacht und lautet: "1234".

##### Ausgabe
Die Konsolenausgabe des **Clients** sieht für dieses Beispiel wie folgt aus:
```java
SUCCESS: Connected to server!
Please enter a command:
GET 1234
FAILED: java.io.FileNotFoundException: C:\Users\Babett\Desktop\Messages\1234.txt (Das System kann die angegebene
Datei nicht finden)
```

Die Konsolenausgabe des **Servers** sieht für dieses Beispiel wie folgt aus:
```java
SUCCESS: Connection was accepted by the client!
ATTENTION: Connection will be closed now!
```

##### Auswertung
Wie man oben erkennen kann, wird dem Benutzer zu verstehen gegeben, dass die Datei nicht gefunden werden kann. Jedoch
stürzt auch hier das Programm nicht ab, sondern der Benutzer kann danach weiter Eingaben vornehmen, da dieser Befehler 
vom Programm behandelt wird und die entsprechende Fehlermeldung ausgibt.

### Beispiele: "EXIT"
In den folgenden Beispielen wird genauer auf den Befehl "EXIT" eingegangen.

#### "EXIT"
Zuerst soll überprüft werden, ob der Client mittels "EXIT"-Befehl geschlossen werden kann.

##### Ausgabe
Die Konsolenausgabe des **Clients** sieht für dieses Beispiel wie folgt aus:
```java
SUCCESS: Connected to server!
Please enter a command:
EXIT
ATTENTION: The program will exit now.
```

Die Konsolenausgabe des **Servers** sieht für dieses Beispiel wie folgt aus:
```java
SUCCESS: Connection was accepted by the client!
ATTENTION: Connection will be closed now!
```

##### Auswertung
Der Benutzer wird über das Schließen des Clients informiert. Der Server bleibt dabei jedoch erhalten und wartet auf 
weitere eingehende Connections von Clients. Er müsste manuell beendet werden.

#### "exit"
Nun soll, wie auch oben, überprüft werden, ob der Befehl auch kleingeschrieben angenommen wird.

##### Ausgabe
Die Konsolenausgabe des **Clients** sieht für dieses Beispiel wie folgt aus:
```java
SUCCESS: Connected to server!
Please enter a command:
exit
ATTENTION: The program will exit now.
```

Die Konsolenausgabe des **Servers** sieht für dieses Beispiel wie folgt aus:
```java
SUCCESS: Connection was accepted by the client!
ATTENTION: Connection will be closed now!
```

##### Auswertung
Der Benutzer wird über das Schließen des Clients informiert. Der Server bleibt dabei jedoch erhalten und wartet auf
weitere eingehende Connections von Clients. Er müsste manuell beendet werden. Somit wird der Befehl anerkannt, obwohl er
kleingeschrieben ist.

### Beispiele: andere Befehle
Alle implementierten Befehle wurden nun gezeigt. Als Nächstes soll gezeigt werden, wie das Programm mit Befehlen umgeht, 
die nicht definiert sind.

#### "SAFE Wir haben leider kein Eis mehr da... :("
In diesem Beispiel wird der Befehl "SAVE" mit einem Rechtschreibfehler eingegeben, sodass dieser für das Programm 
unbekannt sein sollte. Dafür wird folgender Text verwendet: "Wir haben leider kein Eis mehr da... :(". Für dieses 
Beispiel könnte man aber auch jeden anderen String verwenden, der nicht einem Befehl entspricht.

##### Ausgabe
Die Konsolenausgabe des **Clients** sieht für dieses Beispiel wie folgt aus:
```java
SUCCESS: Connected to server!
Please enter a command:
SAFE Wir haben leider kein Eis mehr da... :(
FAILED: The given command is unknown! Please use the commands 'SAVE <Input>', 'GET <Key>' or 'EXIT'.
```

Die Konsolenausgabe des **Servers** sieht für dieses Beispiel wie folgt aus:
```java
SUCCESS: Connection was accepted by the client!
ATTENTION: Connection will be closed now!
```

##### Auswertung
Wie erwartet erkennt das Programm den Befehl nicht an, speichert den String somit auch nicht ab und generiert keinen
Schlüssel dafür. Stattdessen erfolgt eine Fehlerausgabe und einen Hinweis an den Benutzer, welche Befehle existieren. 
Der Server bleibt trotz dieser fehlerhaften Eingabe offen für weitere Connection-Anfragen und auch der Client schließt
sich dabei nicht, sondern es kann daraufhin eine weitere Eingabe erfolgen.

#### " "
In diesem Beispiel soll ein leerer String übergeben werden, um zu schauen, wie das Programm auf diesen reagiert.

##### Ausgabe
Die Konsolenausgabe des **Clients** sieht für dieses Beispiel wie folgt aus:
```java
SUCCESS: Connected to server!
Please enter a command:
 
FAILED: The given command is unknown! Please use the commands 'SAVE <Input>', 'GET <Key>' or 'EXIT'.
```

Die Konsolenausgabe des **Servers** sieht für dieses Beispiel wie folgt aus:
```java
ATTENTION: Connection will be closed now!
SUCCESS: Connection was accepted by the client!
```

##### Auswertung
Das Verfahren entspricht dem gleichen wie im vorherigen Beispiel. Denn auch wenn der String leer ist, wird er wie ein
falscher Befehl behandelt.

### Beispiele: weitere Ausnahmen
Neben den Befehlseingaben gibt es jedoch noch andere Fälle, welche das Programm vorbereitet sein muss.

#### Beispiel ohne gestarteten Server
In diesem Beispiel soll nun dargestellt werden, was passiert, wenn man einen Client startet, ohne dass der entsprechende
Server dazu existiert.

##### Ausgabe
Die Konsolenausgabe des **Clients** sieht für dieses Beispiel wie folgt aus:
```java
FAILED: java.net.ConnectException: Connection refused: connect
Failed to establish the connection. Exiting program...
```

##### Auswertung
Wie man an der Ausgabe erkennt, wird dem Benutzer eine Fehlermeldung ausgegeben, die besagt, dass die Verbindungen 
fehlgeschlagen ist. Daraufhin beendet sich der Client von alleine, was auf eine entsprechende Fehlerbehandlung hinweist,
da er nicht abstürzt.

#### Beispiel ohne entsprechenden Order
Im letzten Beispiel soll nun ausprobiert werden, wie das Programm reagiert, wenn am implementierten Pfad kein 
entsprechender Ordner zur Ablage der Dateien vorgefunden werden kann.

##### Ausgabe
Die Konsolenausgabe des **Servers** sieht für dieses Beispiel wie folgt aus:
```java
SUCCESS: Server was started on port: 7777!
ATTENTION: Message Folder at the corresponding path does not exist.
Creating one...
```

##### Auswertung
Wie man an der Ausgabe und später auf dem Desktop erkennt, erstellt das Programm den benötigten Ordner von selbst und 
kann daraufhin die eingegebenen Befehle in diesen einlesen oder Ausgeben. Das Programm stürzt also nicht ab, sondern
kann nach Ordnererstellung sofort wieder Befehle entgegennehmen.

## Gesamtauswertung
Das Programm erfüllt alle Anforderungen an die Testataufgabe. Des Weiteren wird eine gute Nutzerfreundlichkeit mittels
genauer Konsolenausgaben gewährleistet, um den Benutzer über eventuelle Fehler zu informieren. Dadurch werden mögliche
Fehler behandelt und das Programm (also Server bzw. Client) können nach Auftritt des Fehlers meist weiterverwendet. 
Sollte das durch einen schwerwiegenderen Fehler nicht der Fall sein, so wird der Benutzer auch über das Beenden des 
Programms informiert und kann darauf dieses neustarten. Hierbei ist wichtig zu wissen, dass das Programm nicht abstürzt,
sondern sich in solchen Momenten selbst beendet, um weitere Fehler zu vermeiden und optimal weiterlaufen zu können.
