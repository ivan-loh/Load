package com.sstr.load.scenario;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Hello world!
 */
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

    public static void main(String[] args) throws IOException {

        // Login Process
        Connection.Response response = Jsoup
                .connect("http://192.168.200.195:8181/iDV/svltlogin")
                .data("form_name", "loginform",
                        "txtUserID", "CHUALK",
                        "txtUserPass", "123",
                        "userTimeZone", "8")
                .method(Connection.Method.POST)
                .execute();

        String jsessionid = response.cookie("JSESSIONID");
        System.out.println(jsessionid);

        // Test Credentials
        Connection.Response response2 = Jsoup
                .connect("http://192.168.200.195:8181/iDV/svltaction?tc=GetLoginInfo")
                .cookie("JSESSIONID", jsessionid)
                .method(Connection.Method.POST)
                .execute();

//        System.out.println(response2.body());

        //
        Connection.Response response3 = Jsoup
                .connect("http://192.168.200.195:8181/iDV/svltview")
                .cookie("JSESSIONID", jsessionid)
                .data("PageId", "iDevMainEdit",
                        "AppID", "iDV",
                        "LogID", "CE919615-8B3E-4247-9C41-1BDBC8A77CAD",
                        "LOG_APP", "IDV",
                        "status", "WIP",
                        "piid", "")
                .method(Connection.Method.GET)
                .execute();
        System.out.println(response3.body());

        // Search Listing
        // http://192.168.200.195:8181/iDV/svltaction?tc=iDevGetLogTracker_pagination&PageId=LogTrackerGet&GridId=Log_TABLE_0&AppID=iDV
        // tc:iDevGetLogTracker_pagination
//        PageId:LogTrackerGet
//        GridId:Log_TABLE_0
//        AppID:iDV

    }
}
