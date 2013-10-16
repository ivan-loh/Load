package com.sstr.load;

import com.sstr.load.manager.Manager;
import com.sstr.load.scenario.ListingScenario;
import com.sstr.load.scenario.LogDetailScenario;
import com.sstr.load.scenario.Scenario;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import java.io.InvalidClassException;
import java.util.concurrent.ConcurrentHashMap;

public class Load {

    public static void WebDisplay() {

        /**
         * Create Essential Items
         */
        ConcurrentHashMap<String, Manager> managers = new ConcurrentHashMap<String, Manager>(2, 0.75f, 4);


        /**
         * Spark
         */
        Spark.setPort(4444);

        Spark.get(
                /**
                 * Scenario Listing
                 * ================
                 * Returns a list of scenario supported
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

    }

    public static void singleTest(Class scenario, int concurrencyLevel, long duration) throws InvalidClassException, InstantiationException, IllegalAccessException, InterruptedException {

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

        singleTest(ListingScenario.class, 2, 10 * 1000);

    }
}
