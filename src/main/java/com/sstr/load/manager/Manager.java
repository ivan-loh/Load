package com.sstr.load.manager;

import com.sstr.load.scenario.Scenario;

import java.io.InvalidClassException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Manager {

    private final Class scenarioType;
    private ConcurrentLinkedQueue<Job> jobs = new ConcurrentLinkedQueue<Job>();

    public Manager(final Class scenarioType) throws InvalidClassException {
        if (!Scenario.class.isAssignableFrom(scenarioType)) {
            throw new InvalidClassException(scenarioType.getCanonicalName() + " is not a valid class");
        }
        this.scenarioType = scenarioType;
    }

    public int getConcurrencyLevel() {
        return jobs.size();
    }

    public double getAggregateTPS() {

        double count = 0;
        double total = 0;

        Iterator<Job> jobIter = jobs.iterator();
        while (jobIter.hasNext()) {
            Job j = jobIter.next();
            double jobTPS = j.getLiveTPS();

            if (!j.isActive()) {
                jobs.remove(j);
                continue;
            }

            if (jobTPS != 0) {
                total = total + jobTPS;
                count++;
            }
        }

        return (count > 0 ? total / count : 0);
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
