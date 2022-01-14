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


## Beispiele


### Beispiele: SAVE


### Beispiele: GET


### Beispiele: andere Befehle


## Auswertung