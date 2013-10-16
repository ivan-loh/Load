package com.sstr.load;

import com.sstr.load.scenario.Scenario;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class Load {
    public static void main(String[] args) {

        Spark.setPort(4444);

        /**
         * Scenario Listing
         */
        Spark.get(new Route("scenarios") {
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
                            .append(c.getSimpleName());
                    seperator = ",";
                }
                scenarios.append("]");

                return scenarios.toString();

            }
        });

    }
}
