package com.sstr.load.scenario;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public abstract class Scenario {

    private final String username;
    private final String password;
    private String sessionID;
    private long liveTPS;

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
    public long execute() throws IOException {

        long before = System.currentTimeMillis();
        action();
        long duration = System.currentTimeMillis() - before;

        if (liveTPS == 0) {
            liveTPS = 1 / duration * 1000;
        }

        return (liveTPS + duration) / 2;

    }

    public static Class[] getAvailableScenarios() {
        return new Class[]{
                ListingScenario.class,
                LogDetailScenario.class
        };
    }

}
