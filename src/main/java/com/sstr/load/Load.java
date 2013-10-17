package com.sstr.load;

import com.sstr.load.manager.Manager;
import com.sstr.load.scenario.Scene;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Load {

    public static void webDisplay(int port) throws IOException {

        /**
         * Create Essential Items
         */
        final ConcurrentHashMap<String, Manager> managers = new ConcurrentHashMap<String, Manager>(4, 0.75f, 4);

        final List<Scene> scenes = Scene.load();
        final ConcurrentHashMap<String, Scene> sceneMap = new ConcurrentHashMap<String, Scene>(scenes.size(), 0.75f, 4);
        for (Scene s : scenes) {
            sceneMap.put(s.getName(), s);
        }

        /**
         * Spark
         */
        Spark.setPort(port);
        Spark.externalStaticFileLocation(new File("static").getCanonicalPath());

        Spark.get(
                /**
                 * Scenario Listing
                 */
                new Route("scenarios") {
                    @Override
                    public Object handle(Request request, Response response) {

                        StringBuilder scenarios = new StringBuilder("[");
                        String seperator = "";
                        for (Scene s : scenes) {
                            scenarios.append(seperator)
                                    .append("{")
                                    .append("\"name\" : ").append("\"").append(s.getName()).append("\",")
                                    .append("\"scene\" : ").append("\"").append(s.getName()).append("\"")
                                    .append("}");
                            seperator = ",";
                        }
                        scenarios.append("]");

                        return scenarios.toString();

                    }
                });

        Spark.get(
                /**
                 * Starts Manager
                 */
                new Route("start/:scene") {
                    @Override
                    public Object handle(Request request, Response response) {
                        try {
                            String sceneName = request.params(":scene");
                            sceneName = sceneName.replaceAll("%20", " ");

                            Manager m = managers.get(sceneName);
                            if (m == null) {
                                Scene s = sceneMap.get(sceneName);
                                Manager scenarioManager = new Manager(s);
                                managers.putIfAbsent(sceneName, scenarioManager);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            halt(500);
                        }

                        return "Ok";
                    }
                });

        Spark.get(
                /**
                 * Increase manager concurrency
                 */
                new Route("increase/:scene") {
                    @Override
                    public Object handle(Request request, Response response) {

                        int jobs = 0;
                        try {
                            String sceneName = request.params(":scene");
                            sceneName = sceneName.replaceAll("%20", " ");

                            Manager scenarioManager = managers.get(sceneName);
                            jobs = scenarioManager.increaseJob();
                        } catch (Exception e) {
                            e.printStackTrace();
                            halt(500);
                        }

                        return jobs;
                    }
                });

        Spark.get(
                /**
                 * Decrease manager concurrency
                 */
                new Route("decrease/:scene") {
                    @Override
                    public Object handle(Request request, Response response) {

                        int jobs = 0;
                        try {
                            String sceneName = request.params(":scene");
                            sceneName = sceneName.replaceAll("%20", " ");

                            Manager scenarioManager = managers.get(sceneName);
                            jobs = scenarioManager.decreaseJob();
                        } catch (Exception e) {
                            e.printStackTrace();
                            halt(500);
                        }

                        return jobs;
                    }
                });

        Spark.get(
                /**
                 * Get manager TPS
                 */
                new Route("tps/:scene") {
                    @Override
                    public Object handle(Request request, Response response) {

                        int tps = 0;
                        try {
                            String sceneName = request.params(":scene");
                            sceneName = sceneName.replaceAll("%20", " ");

                            Manager scenarioManager = managers.get(sceneName);
                            tps = (int) scenarioManager.getAggregateTPS();
                        } catch (Exception e) {
                            e.printStackTrace();
                            halt(500);
                        }

                        return tps;
                    }
                });

        Spark.get(
                /**
                 * Get Concurrency Level
                 */
                new Route("concurrency/:scene") {

                    @Override
                    public Object handle(Request request, Response response) {
                        int tps = 0;
                        try {
                            String sceneName = request.params(":scene");
                            sceneName = sceneName.replaceAll("%20", " ");

                            Manager scenarioManager = managers.get(sceneName);
                            tps = scenarioManager.getConcurrencyLevel();
                        } catch (Exception e) {
                            e.printStackTrace();
                            halt(500);
                        }

                        return tps;
                    }
                });


    }

    public static void singleTest(String sceneName, int concurrencyLevel, long duration) throws InvalidClassException, InstantiationException, IllegalAccessException, InterruptedException, FileNotFoundException {

        final List<Scene> scenes = Scene.load();
        Scene activeScene = null;
        for (Scene s : scenes) {
            if (s.getName().toLowerCase().equals(sceneName.toLowerCase())) {
                activeScene = s;
            }
        }

        Manager m = new Manager(activeScene);

        for (int i = 0; i < concurrencyLevel; i++) {
            m.increaseJob();
        }


        long start = System.currentTimeMillis();
        long activeDuration = 0;
        while (activeDuration < duration) {

            Thread.sleep(100);

            int tps = (int) m.getAggregateTPS();
            if (tps > 0) {
                System.out.println(tps);
            }

            activeDuration = System.currentTimeMillis() - start;
        }

        for (int i = 0; i < concurrencyLevel; i++) {
            m.decreaseJob();
        }
    }

    public static void main(String[] args) throws Exception {

        int port = 4444;
        if (args.length > 1) {
            for (int i = 0; i < args.length; i++) {
                String s = args[i].toLowerCase();
                if (s.equals("-p") || s.equals("--port")) {
                    port = Integer.parseInt(args[++i]);
                }
            }
        }

        webDisplay(port);

    }

}
