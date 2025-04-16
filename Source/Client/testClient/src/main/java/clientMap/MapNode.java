package clientMap;

import data.EnumField;
import data.EnumFortState;
import data.EnumPlayerPosition;
import data.EnumTreasureState;

public class MapNode {
    private EnumField enumField;
    private EnumFortState enumFortState;
    private EnumTreasureState enumTreasureState;
    private Coordinate coordinate;
    private EnumPlayerPosition enumPlayerPosition;
   private  int x, y;
   private boolean revealed = false;




    public MapNode(EnumField enumField, EnumFortState enumFortState, EnumTreasureState enumTreasureState, Coordinate coordinate) {
        this.enumField = enumField;
        this.enumFortState = enumFortState;
        this.enumTreasureState = enumTreasureState;
        this.coordinate = coordinate;
    }
    public MapNode(int x, int y){
        this.x = x;
        this.y = y;
    }

    public EnumField getEnumField() {
        return enumField;
    }

    public EnumFortState getEnumFortState() {
        return enumFortState;
    }

    public EnumTreasureState getEnumTreasureState() {
        return enumTreasureState;
    }


    public Coordinate getCoordinate() {
        return coordinate;
    }

    public EnumPlayerPosition getEnumPlayerPosition() {
        return enumPlayerPosition;
    }

    public void setEnumPlayerPosition(EnumPlayerPosition enumPlayerPosition) {
        this.enumPlayerPosition = enumPlayerPosition;
    }

    public void setEnumField(EnumField enumField) {
        this.enumField = enumField;
    }

    public void setEnumFortState(EnumFortState enumFortState) {
        this.enumFortState = enumFortState;
    }

    public void setEnumTreasureState(EnumTreasureState enumTreasureState) {
        this.enumTreasureState = enumTreasureState;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }



    public boolean isRevealed() {
        return revealed;
    }

    public void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }
}
