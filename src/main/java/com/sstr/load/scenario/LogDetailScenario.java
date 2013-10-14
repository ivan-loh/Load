package com.sstr.load.scenario;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;


public class LogDetailScenario extends Scenario {

    @Override
    protected void action() throws IOException {

        String sessionID = getSessionID();

        Jsoup
                .connect("http://192.168.200.195:8181/iDV/svltview")
                .cookie("JSESSIONID", sessionID)
                .data("PageId", "iDevMainEdit",
                        "AppID", "iDV",
                        "LogID", "CE919615-8B3E-4247-9C41-1BDBC8A77CAD",
                        "LOG_APP", "IDV",
                        "status", "WIP",
                        "piid", "")
                .method(Connection.Method.GET)
                .execute();
    }
}
