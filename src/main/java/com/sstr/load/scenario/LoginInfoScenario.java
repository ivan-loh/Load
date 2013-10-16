package com.sstr.load.scenario;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

public class LoginInfoScenario extends Scenario {
    @Override
    protected void action() throws IOException {
        String sessionID = getSessionID();

        Jsoup
                .connect("http://192.168.200.195:8181/iDV/svltaction?tc=GetLoginInfo")
                .cookie("JSESSIONID", sessionID)
                .method(Connection.Method.POST)
                .execute();
    }
}
