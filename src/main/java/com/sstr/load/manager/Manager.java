package com.sstr.load.manager;

import com.sstr.load.scenario.ScenarioV2;
import com.sstr.load.scenario.Scene;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Manager {

    private final Scene scene;
    private ConcurrentLinkedQueue<Job> jobs = new ConcurrentLinkedQueue<Job>();

    public Manager(final Scene scene) {
        if (scene == null) {
            throw new RuntimeException(" Scene cannot be null");
        }
        this.scene = scene;
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

        ScenarioV2 plan = new ScenarioV2(scene);
        Job newJob = new Job(plan);

        Thread execution = new Thread(newJob);
        execution.setName(scene.getName() + " Job");
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
