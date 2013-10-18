{
    name : "Listing",
    pre : {
        "connect" : "http://192.168.200.195:8181/iDV/svltlogin",
        "data" : [ "form_name",    "loginform",
                   "txtUserID",    "chualk",
                   "txtUserPass",  "123",
                   "userTimeZone", "8" ],
        "cookie" : [ "JSESSIONID" ],
        "method" : "POST"
    },
    actions : [ {
        "connect" : "http://192.168.200.195:8181/iDV/svltaction",
        "data" : [ "tc",       "iDevGetLogTracker_pagination",
                   "PageId",   "LogTrackerGet",
                   "GridId",   "Log_TABLE_0",
                   "AppID",    "IDV" ],
        "cookie" : [ "JSESSIONID" ],
        "method" : "GET"
    } ]
}
