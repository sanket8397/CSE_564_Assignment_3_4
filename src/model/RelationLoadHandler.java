package model;

import view.ClassBox;

public interface RelationLoadHandler {
    void loadConnection(String[] tokens, ClassBox fromClass, ClassBox toClass);
}