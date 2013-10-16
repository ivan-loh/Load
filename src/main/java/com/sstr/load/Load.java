package com.sstr.load;

import com.sstr.load.manager.Manager;
import com.sstr.load.scenario.Scenario;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;
import java.util.concurrent.ConcurrentHashMap;

public class Load {

    public static void WebDisplay() throws IOException {

        /**
         * Create Essential Items
         */
        final ConcurrentHashMap<String, Manager> managers = new ConcurrentHashMap<String, Manager>(4, 0.75f, 4);

        /**
         * Spark
         */
        Spark.setPort(4444);
        Spark.externalStaticFileLocation(new File("static").getCanonicalPath());

        Spark.get(
                /**
                 * Scenario Listing
                 */
                new Route("scenarios") {
                    @Override
                    public Object handle(Request request, Response response) {

                        Class[] availableScenarios = Scenario.getAvailableScenarios();
                        if (availableScenarios.length == 0) {
                            return "";
                        }

                        StringBuilder scenarios = new StringBuilder("[");
                        String seperator = "";
                        for (Class c : availableScenarios) {
                            scenarios.append(seperator)
                                    .append("{")
                                    .append("\"name\" : ").append("\"").append(c.getSimpleName()).append("\",")
                                    .append("\"class\" : ").append("\"").append(c.getCanonicalName()).append("\"")
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
                new Route("start/:scenario") {
                    @Override
                    public Object handle(Request request, Response response) {


                        try {
                            String scenarioName = request.params(":scenario");
                            Class<?> scenario = Class.forName(scenarioName);
                            Manager scenarioManager = new Manager(scenario);
                            managers.putIfAbsent(scenarioName, scenarioManager);
                        } catch (Exception e) {
                            halt(500);
                            e.printStackTrace();
                        }

                        return "Ok";
                    }
                });

        Spark.get(
                /**
                 * Increase manager concurrency
                 */
                new Route("increase/:scenario") {
                    @Override
                    public Object handle(Request request, Response response) {

                        int jobs = 0;
                        try {
                            String scenarioName = request.params(":scenario");
                            Manager scenarioManager = managers.get(scenarioName);
                            jobs = scenarioManager.increaseJob();
                        } catch (Exception e) {
                            halt(500);
                            e.printStackTrace();
                        }

                        return jobs;
                    }
                });

        Spark.get(
                /**
                 * Decrease manager concurrency
                 */
                new Route("decrease/:scenario") {
                    @Override
                    public Object handle(Request request, Response response) {

                        int jobs = 0;
                        try {
                            String scenarioName = request.params(":scenario");
                            Manager scenarioManager = managers.get(scenarioName);
                            jobs = scenarioManager.decreaseJob();
                        } catch (Exception e) {
                            halt(500);
                            e.printStackTrace();
                        }

                        return jobs;
                    }
                });

        Spark.get(
                /**
                 * Get manager TPS
                 */
                new Route("tps/:scenario") {
                    @Override
                    public Object handle(Request request, Response response) {

                        double tps = 0;
                        try {
                            String scenarioName = request.params(":scenario");
                            Manager scenarioManager = managers.get(scenarioName);
                            return scenarioManager.getAggregateTPS();
                        } catch (Exception e) {
                            halt(500);
                            e.printStackTrace();
                        }

                        return tps;
                    }
                });


    }

    public static void SingleTest(Class scenario, int concurrencyLevel, long duration) throws InvalidClassException, InstantiationException, IllegalAccessException, InterruptedException {

        Manager m = new Manager(scenario);

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

//        SingleTest(ListingScenario.class, 3, 10 * 1000);
//        WebDisplay();

    }
}
