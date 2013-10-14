package com.sstr.load;

import com.sstr.load.scenario.ListingScenario;
import com.sstr.load.scenario.LogDetailScenario;
import com.sstr.load.scenario.Scenario;

import java.io.IOException;

public class Runner {

    public static void main(String[] args) throws IOException {
        Scenario scenarioA = new LogDetailScenario();
        Scenario scenarioB = new ListingScenario();

        scenarioA.pre();
        scenarioB.pre();

        System.out.println("yea " + scenarioA.execute());
        System.out.println("Okay  " + scenarioB.execute());
    }


}
