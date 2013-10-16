package com.sstr.load.scenario;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public abstract class Scenario {

    private final AveragingTPS durations = new AveragingTPS(300);
    private final String username;
    private final String password;
    private String sessionID;

    public Scenario() {
        this("CHUALK", "123");
    }

    public Scenario(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public final void pre() throws IOException {

        if (sessionID != null) {
            return;
        }

        sessionID = Jsoup
                .connect("http://192.168.200.195:8181/iDV/svltlogin")
                .userAgent("SSTR Load Job/1.0 (JSoup 1.7.2)")
                .data("form_name", "loginform",
                        "txtUserID", username,
                        "txtUserPass", password,
                        "userTimeZone", "8")
                .method(Connection.Method.POST)
                .execute().cookie("JSESSIONID");
    }

    protected String getSessionID() {
        return sessionID;
    }

    protected abstract void action() throws IOException;

    /**
     * Perform Execution of Scenario.
     *
     * @return Live TPS calculated from single transaction
     * @throws IOException
     */
    public double execute() throws IOException {

        long before = System.currentTimeMillis();
        action();
        return durations
                .add(System.currentTimeMillis() - before)
                .getTPS();

    }

    public static Class[] getAvailableScenarios() {
        return new Class[]{
                ListingScenario.class,
                LogDetailScenario.class,
                LoginInfoScenario.class
        };
    }

    private class AveragingTPS {

        private final double[] hold;
        private int index;

        public AveragingTPS(int size) {
            hold = new double[size];
        }

        public AveragingTPS add(double duration) {

            if (index == hold.length) {
                index = 0;
            }

            hold[index++] = 1000 / duration;

            return this;
        }

        public double getTPS() {

            int count = 0;
            double total = 0;
            for (int i = 0; i < hold.length; i++) {
                if (hold[i] != 0) {
                    total = total + hold[i];
                    count++;
                }
            }

            return total / count;
        }
    }

}
