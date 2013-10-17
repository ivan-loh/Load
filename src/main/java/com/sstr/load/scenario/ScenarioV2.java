package com.sstr.load.scenario;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScenarioV2 {

    private final AveragingTPS durations = new AveragingTPS(200);
    private final Map<String, String> cookie = new HashMap<String, String>(2);
    private final Scene scene;

    public ScenarioV2(Scene scene) {
        this.scene = scene;
    }

    public final void pre() throws IOException {

        Scene.Action pre = scene.pre;
        Connection.Method method =
                pre.method.toLowerCase().equals("post") ?
                        Connection.Method.POST
                        : Connection.Method.GET;

        // Establish Connection
        Connection.Response response = Jsoup
                .connect(pre.connect)
                .userAgent("SSTR Load Job/1.0 (JSoup 1.7.2)")
                .data(pre.data)
                .method(method)
                .execute();

        // Get Response Cookie
        for (String c : pre.cookie) {
            cookie.put(c, response.cookie(c));
        }

    }

    protected void action() throws IOException {

        // 1 . Default Variables
        Scene.Action action = scene.action;
        Connection.Method method =
                action.method.toLowerCase().equals("post") ?
                        Connection.Method.POST
                        : Connection.Method.GET;

        // 2. Connection Object
        Connection connect = Jsoup.connect(action.connect);

        // 3. Set Cookies
        for (String s : action.cookie) {
            String value = cookie.get(s);
            if (value != null) {
                connect.cookie(s, value);
            }
        }

        // 4. Connect
        connect
                .data(action.data)
                .method(method)
                .execute();

    }

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
}
