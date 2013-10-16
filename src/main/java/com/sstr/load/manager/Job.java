package com.sstr.load.manager;

import com.sstr.load.scenario.Scenario;

import java.io.IOException;

public class Job implements Runnable {

    private final Scenario scenario;
    private boolean active = true;


    public Job(Scenario scenario) {
        this.scenario = scenario;
    }

    public void stop() {
        active = false;
    }

    @Override
    public void run() {

        try {
            scenario.pre();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        while (active) {
            try {
                scenario.execute();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
