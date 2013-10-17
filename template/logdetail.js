{
    name : "Log Detail",
    pre : {
        "connect" : "http://192.168.200.195:8181/iDV/svltlogin",
        "data" : [ "form_name",    "loginform",
                   "txtUserID",    "chualk",
                   "txtUserPass",  "123",
                   "userTimeZone", "8" ],
        "cookie" : [ "JSESSIONID" ],
        "method" : "POST"
    },
    action : {
        "connect" : "http://192.168.200.195:8181/iDV/svltview",
        "data" : [ "PageId",  "iDevMainEdit",
                   "AppID",   "iDV",
                   "LogID",   "CE919615-8B3E-4247-9C41-1BDBC8A77CAD",
                   "LOG_APP",   "IDV",
                   "status",   "WIP",
                   "piid",    "" ],
        "cookie" : [ "JSESSIONID" ],
        "method" : "GET"
    }
}
