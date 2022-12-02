package model;

import model.ClassSource;
import view.ClassBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CodeGenerator {

    List<ClassBox> classBoxes;
    List<Connection> connections;
    Map<ClassBox, List<Connection>> adjacencyMap;
    private final String TAB = "    ";

    public String generateCode() {
        refresh();
        generateAdjacencyMap();
        final String RESIZE_DELAY = "                                                  \n";
        StringBuilder codeBuilder = new StringBuilder(RESIZE_DELAY);

        for (ClassBox classBox : classBoxes) {
            List<Connection> adjacentConnections = getAdjacentConnections(classBox);
            List<String> extensionList = getExtensionList(adjacentConnections);
            String extensions = getExtensions(extensionList);
            List<String> compositionList = getCompositionList(adjacentConnections);
            String compositions = getCompositions(compositionList);
            List<String> associationList = getAssociationList(adjacentConnections);
            String associations = getAssociations(associationList);

            codeBuilder.append("class ");
            codeBuilder.append(classBox.getClassName()).append(extensions).append(" {\n");
            codeBuilder.append(compositions);
            codeBuilder.append(associations);
            codeBuilder.append("}\n\n");
        }

        return codeBuilder.toString();
    }

    private void refresh() {
        ClassSource classSource = ClassSource.getInstance();
        classBoxes = classSource.getClassBoxes();
        connections = classSource.getConnections();
        adjacencyMap = new HashMap<>();
    }

    private void generateAdjacencyMap() {
        for (Connection connection : connections) {
            ClassBox fromClass = connection.getFromClass();

            if (!adjacencyMap.containsKey(fromClass)) {
                adjacencyMap.put(fromClass, new ArrayList<>());
            }

            adjacencyMap.get(fromClass).add(connection);
        }
    }

    private List<Connection> getAdjacentConnections(ClassBox classBox) {
        List<Connection> adjacentConnections = new ArrayList<>();

        if (adjacencyMap.containsKey(classBox)) {
            adjacentConnections = adjacencyMap.get(classBox);
        }

        return adjacentConnections;
    }

    private List<String> getExtensionList(List<Connection> adjacentConnections) {
        List<String> extensionList = new ArrayList<>();

        for (Connection connection : adjacentConnections) {
            if (connection instanceof Triangle) {
                extensionList.add(connection.getToClass().getClassName());
            }
        }

        return extensionList;
    }

    private String getExtensions(List<String> extensionList) {
        String extensions = "";

        if (!extensionList.isEmpty()) {
            StringBuilder extensionBuilder = new StringBuilder(" extends ");

            for (String extension : extensionList) {
                extensionBuilder.append(extension);
                extensionBuilder.append(", ");
            }

            extensionBuilder.setLength(extensionBuilder.length() - 2);
            extensions = extensionBuilder.toString();
        }

        return extensions;
    }

    private List<String> getCompositionList(List<Connection> adjacentConnections) {
        List<String> compositionList = new ArrayList<>();

        for (Connection connection : adjacentConnections) {
            if (connection instanceof Diamond) {
                compositionList.add(connection.getToClass().getClassName());
            }
        }

        return compositionList;
    }

    private String getCompositions(List<String> compositionList) {
        StringBuilder compositionBuilder = new StringBuilder();

        for (String composition : compositionList) {
            compositionBuilder.append(TAB).append(composition).append("\n");
        }

        return compositionBuilder.toString();
    }

    private List<String> getAssociationList(List<Connection> adjacentConnections) {
        List<String> associationList = new ArrayList<>();

        for (Connection connection : adjacentConnections) {
            if (connection instanceof Arrow) {
                associationList.add(connection.getToClass().getClassName());
            }
        }

        return associationList;
    }

    private String getAssociations(List<String> associationList) {
        if (associationList.isEmpty()) {
            return "";
        }

        StringBuilder associationBuilder = new StringBuilder(TAB + "method() {\n");

        for (String association : associationList) {
            associationBuilder.append(TAB + TAB).append(association).append("\n");
        }

        associationBuilder.append(TAB + "}\n");
        return associationBuilder.toString();
    }
}
