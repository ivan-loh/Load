package com.sstr.load.scenario;

import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Scene {

    public static final String TEMPLATE_FOLDER = "template";
    String name;
    Action pre;
    Action[] actions;

    /**
     * Loads all JS Template Files from the template folder
     *
     * @return List of scene objects
     * @throws FileNotFoundException
     */
    public static List<Scene> load() throws FileNotFoundException {

        File f = new File(TEMPLATE_FOLDER);
        String[] templates = f.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith("js");
            }
        });

        Gson parser = new Gson();
        List<Scene> scenes = new ArrayList<Scene>(templates.length);
        for (String t : templates) {
            String filename = TEMPLATE_FOLDER + File.separator + t;
            try {

                BufferedReader reader = new BufferedReader(
                        new FileReader(filename)
                );
                scenes.add(parser.fromJson(reader, Scene.class));

            } catch (Exception e) {
                System.out.println("Unable to load, " + filename + ", it will be ignored: " + e.getMessage());
            }
        }

        return scenes;
    }

    public static String deepToJSONString(String[] strings) {

        StringBuilder result = new StringBuilder("[ ");
        String seperator = "";
        for (String s : strings) {
            result.append(seperator)
                    .append("\"").append(s).append("\"");
            seperator = ",";
        }
        result.append(" ]");

        return result.toString();
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return new StringBuilder("{")
                .append("\"name\" : \"").append(name).append("\", ")
                .append("\"pre\" : ").append(pre.toString()).append(", ")
                .append("\"action\" : ").append(actions.toString()).append(" ")
                .append(" }").toString();
    }

    public class Action {
        String connect;
        String[] data;
        String[] cookie;
        String method;

        public String toString() {
            return new StringBuilder("{")
                    .append("\"connect\" : \"").append(connect).append("\", ")
                    .append("\"data\" : ").append(deepToJSONString(data)).append(", ")
                    .append("\"cookie\" : ").append(deepToJSONString(cookie)).append(", ")
                    .append("\"method\" : \"").append(method).append("\" ")
                    .append(" }").toString();
        }
    }

}
