/*package pathFinder;

import clientMap.ClientHalfMap;
import clientMap.MapNode;
import data.EnumField;

import java.util.*;

public class PathFinder {

        //Taken from : https://youtu.be/BuvKtCh0SKk?si=c5djIiR0M4o4TXJH

        public List<MapNode> findShortestPath(ClientHalfMap clientHalfMap, String startKey, String targetKey) {                  //gibt eine Liste von Strings zurück die, die Reihenfolge der besuchten Felder als Koordinaten-String

            Map<String, Integer> distance = new HashMap<>();                                                                    //Integer repräsentiert den kürzesten Abstand, zu Beginn ist er 0
            Map<String, String> previousNode = new HashMap<>();                                                                 //frist String, Start-Feld, und String, Ziel-Feld
            PriorityQueue<String> unsettledNodes = new PriorityQueue<>(Comparator.comparingInt(k -> distance.getOrDefault(k, Integer.MAX_VALUE)));                 //priorisiert in der distance-Map den kürzesten Weg
            Set<String> visited = new HashSet<>();

            clientHalfMap.getClientHalfMap().keySet().forEach(key -> distance.put(key, Integer.MAX_VALUE));
            distance.put(startKey, 0);
            unsettledNodes.add(startKey);                                                                                         //Erkundigung beginnt bei startKEy


            while (!unsettledNodes.isEmpty()) {
                String currentKey = unsettledNodes.poll();                                                                      //entfernt Node mit der kleinsten Distanz & bereits besucht
                if (visited.contains(currentKey)) continue;
                visited.add(currentKey);
                if(currentKey.equals(targetKey)){
                    break;
                }
                MapNode currentNode = clientHalfMap.getClientHalfMap().get(currentKey);                                         // Abruf von current MapNode

                for (String neighborKey : getValidNeighbors(clientHalfMap, currentKey)) {
                    int stepCost = currentNode.getEnumField() == EnumField.MOUNTAIN ? 5 : 2;        //Berg hat Gewichtung von 5 und alle anderen Fields eine Gewichtung von 2
                    int newDist = distance.get(currentKey) + stepCost;                                                              //+1 repräsentiert den Schritt, den man machen muss, um zum nächsten Feld zu gelangen

                    if (newDist < distance.get(neighborKey)) {                                                                  //Prüft, ob newDist kleiner ist als die aktuelle Distanz zum neighborKey.
                        distance.put(neighborKey, newDist);                                                                     //Aktualisiert die kürzeste Distanz zum neighborKey.
                        previousNode.put(neighborKey, currentKey);
                        unsettledNodes.add(neighborKey);
                    }
                }
            }

           return constructPath(clientHalfMap,previousNode, targetKey);
        }

        private List<MapNode> constructPath(ClientHalfMap clientHalfMap,Map<String,String> previousNode, String targetKey) {
            List<MapNode> path = new LinkedList<>();
            for (String at = targetKey; at != null; at = previousNode.get(at)) {
                path.add(0, clientHalfMap.getClientHalfMap().get(at)); // Pfad in umgekehrter Reihenfolge hinzufügen
            }
            return path;
        }

        private static List<String> getValidNeighbors(ClientHalfMap map, String key) {
            int x = Integer.parseInt(key.substring(0, 1));
            int y = Integer.parseInt(key.substring(1, 2));

            List<String> neighbors = new ArrayList<>();
            addNeighborIfValid(map, neighbors, x + 1, y);
            addNeighborIfValid(map, neighbors, x - 1, y);
            addNeighborIfValid(map, neighbors, x, y + 1);
            addNeighborIfValid(map, neighbors, x, y - 1);

            return neighbors;
        }

        private static void addNeighborIfValid(ClientHalfMap map, List<String> neighbors, int x, int y) {
            if(x<0 || x >= 5 || y<0 || y >= 10){
                return;                                 //Wenn Koordinaten außerhalb der Karte ist, nicht hinzufügen
            }
            String neighborKey = String.valueOf(x) + String.valueOf(y);
            MapNode node = map.getClientHalfMap().get(neighborKey);

            if (node != null &&(node.getEnumField()== EnumField.GRAS || node.getEnumField()== EnumField.MOUNTAIN)&& node.getEnumField() != EnumField.WATER) {
                neighbors.add(neighborKey);
            }
        }
    }
*/