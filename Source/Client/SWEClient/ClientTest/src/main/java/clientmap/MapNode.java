package clientmap;

import data.EnumField;
import data.EnumFortState;
import data.EnumPlayerPosition;
import data.EnumTreasureState;

import java.util.Map;

public class MapNode {

    private EnumField enumField;
    private EnumFortState enumFortState;
    private EnumTreasureState enumTreasureState;
    private Coordinate coordinate;
    private EnumPlayerPosition enumPlayerPosition;

    private double gCost = Double.MAX_VALUE;
    private double hCost = 0;
    private MapNode parent;
    private int x,y;

    public MapNode(EnumField enumField, EnumFortState enumFortState, EnumTreasureState enumTreasureState, Coordinate coordinate) {
        this.enumField = enumField;
        this.enumFortState = enumFortState;
        this.enumTreasureState = enumTreasureState;
        this.coordinate = coordinate;
    }

    public MapNode(int x, int y){
        x = this.coordinate.getX();
        y= this.coordinate.getY();

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

    public void setEnumPlayerPosition(EnumPlayerPosition enumPlayerPosition) {
        this.enumPlayerPosition = enumPlayerPosition;
    }

    public double getGCost() {
        return gCost;
    }

    public double getHCost() {
        return hCost;
    }

    public double getFCost() {
        return gCost + hCost;
    }

    public MapNode getParent() {
        return parent;
    }

    public void setGCost(double gCost) {
        this.gCost = gCost;
    }

    public void setHCost(double hCost) {
        this.hCost = hCost;
    }

    public void setParent(MapNode parent) {
        this.parent = parent;
    }
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
