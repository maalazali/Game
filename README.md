#Client- Server Projekt
Dies ist ein Client-Projekt im Rahmen einer universitären Übung, bei dem eine KI automatisch eine gültige Spielfeldhälfte (`HalfMap`) generiert, an einen Server überträgt und später Spielfiguren durch das Spielfeld navigiert – komplett **ohne menschliches Eingreifen**. Der Server fungiert dabei als Spielleiter, Koordinator und Regelauswerter.


##Projektziel

- Regelkonforme Generierung einer Spielfeldhälfte (Kartenhälfte)
- Validierung der Karte (keine Inseln, Verteilung der Felder, Erreichbarkeit etc.)
- Kommunikation mit dem Server: Registrierung, Statusabfrage, Kartenaustausch
- Pfadfindung zum Schatz und zur gegnerischen Burg
- KI-basiertes Navigieren auf dem Spielfeld nach bestimmten Spielregeln
  

##Spielprinzip

- Zwei Clients (bzw. KIs) treten gegeneinander an
- Jeder Client generiert eine zufällige Kartenhälfte (5x10 Felder)
- Der Server kombiniert diese zu einer vollständigen Karte (z. B. 10x10)
- Ziel jeder KI ist es:
  1. Den eigenen **Schatz** zu finden
  2. Danach die **gegnerische Burg** zu erreichen
- Alle Spielaktionen (Bewegungen, Kartenübertragung) laufen über **Client/Server-Kommunikation**
- Spielende nach max. **320 Runden** oder wenn eine KI erfolgreich Schatz & Burg erreicht

  
##Regeln für die Kartengenerierung

- Die Karte ist 10 Felder hoch und 5 Felder breit (`10x5`)
- Es müssen mindestens enthalten sein:
  - 24 Wiesenfelder
  - 5 Bergfelder
  - 7 Wasserfelder
- Die Karte darf **keine Inseln** enthalten (alle betretbaren Felder müssen erreichbar sein)
- Die Burg (`Fort`) muss **auf einem Wiesenfeld** stehen
- An den Kartenrändern dürfen maximal erlaubt sein:
  - 2 Wasserfelder pro vertikaler Kante (links/rechts)
  - 4 Wasserfelder pro horizontaler Kante (oben/unten)
    
    
##KI-Logik: Wegfindung mit Dijkstra

Sobald das Spiel beginnt und die Spielfigur gespawnt ist:

1. Bewegt sich die KI mit Hilfe des Dijkstra-Algorithmus schrittweise über die Karte
2. Deckt dabei nach und nach Felder auf
3. Sobald der Schatz gefunden wurde, wird der kürzeste Weg zur gegnerischen Burg berechnet
4. Das Spielfeld berücksichtigt:  
   - Wasser ist unpassierbar
   - Berge benötigen 2 Schritte
   - nur direkte Nachbarn (oben/unten/links/rechts) sind erlaubt
