package com.sstr.load.manager;

import com.sstr.load.scenario.ScenarioV2;

import java.io.IOException;

public class Job implements Runnable {

    private final ScenarioV2 scenario;
    private boolean active = true;
    private double liveTPS = 0;


    public Job(ScenarioV2 scenario) {
        this.scenario = scenario;
    }

    public void stop() {
        active = false;
    }

    public boolean isActive() {
        return active;
    }

    public double getLiveTPS() {
        return liveTPS;
    }

    @Override
    public void run() {

        try {
            scenario.pre();
        } catch (IOException e) {
            active = false;
            return;
        }

        while (active) {
            try {
                liveTPS = scenario.execute();
            } catch (IOException e) {
                liveTPS = 0;
                active = false;
                return;
            }
        }
    }
}
