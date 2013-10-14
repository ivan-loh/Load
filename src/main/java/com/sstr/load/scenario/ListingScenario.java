package com.sstr.load.scenario;


import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class ListingScenario extends Scenario {

    @Override
    protected void action() throws IOException {

        String sessionID = getSessionID();

        // One Page Of Listing
        Jsoup
                .connect("http://192.168.200.195:8181/iDV/svltaction")
                .cookie("JSESSIONID", sessionID)
                .data("tc", "iDevGetLogTracker_pagination",
                        "PageId", "LogTrackerGet",
                        "GridId", "Log_TABLE_0",
                        "AppID", "IDV")
                .method(Connection.Method.GET)
                .execute();
    }

}
