package com.projektarbeit.sensormesh.models;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;

public class RootNode {

    private static String name;
    private static final ArrayList<Long> nodeList = new ArrayList<>();

    public static void update(JsonNode data){
        name = data.get("name").asText();

        if(data.get("message").isArray()){
            nodeList.clear();
            for(JsonNode element : data.get("message")){
                nodeList.add(element.asLong());
            }
        } else {
            System.out.println("Ungültige Json!");
        }

    }

    public static String getName() {
        return name;
    }

    public static ArrayList<Long> getNodeList() {
        return nodeList;
    }
}