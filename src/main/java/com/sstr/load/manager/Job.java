package com.sstr.load.manager;

import com.sstr.load.scenario.Scenario;

import java.io.IOException;

public class Job implements Runnable {

    private final Scenario scenario;
    private boolean active = true;
    private double liveTPS = 0;


    public Job(Scenario scenario) {
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
            e.printStackTrace();
            active = false;
            return;
        }

        while (active) {
            try {
                liveTPS = scenario.execute();
            } catch (IOException e) {
                e.printStackTrace();
                liveTPS = 0;
                active = false;
                return;
            }
        }
    }
}
