package com.sstr.load.manager;

import com.sstr.load.scenario.Scenario;

import java.io.InvalidClassException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Manager {

    private ConcurrentLinkedQueue<Job> jobs = new ConcurrentLinkedQueue<Job>();
    private final Class scenarioType;

    public Manager(final Class scenarioType) throws InvalidClassException {
        if (!Scenario.class.isAssignableFrom(scenarioType)) {
            throw new InvalidClassException(scenarioType.getCanonicalName() + " is not a valid class");
        }
        this.scenarioType = scenarioType;
    }

    public int increaseJob() throws IllegalAccessException, InstantiationException {

        Scenario plan = (Scenario) scenarioType.newInstance();
        Job newJob = new Job(plan);

        Thread execution = new Thread(newJob);
        execution.setName(scenarioType.getCanonicalName() + " Job");
        execution.start();

        jobs.add(newJob);
        return jobs.size();
    }

    public int decreaseJob() {

        Job oldJob = jobs.poll();
        if (oldJob != null) {
            oldJob.stop();
        }
        return jobs.size();
    }

}
