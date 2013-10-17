{
    name : "Login Info",
    pre : {
        "connect" : "http://192.168.200.195:8181/iDV/svltlogin",
        "data" : [ "form_name",    "loginform",
                   "txtUserID",    "chualk",
                   "txtUserPass",  "123",
                   "userTimeZone", "8" ],
        "cookie" : { 
            "session" : "JSESSIONID"
        },
        "method" : "POST"
    },
    action : {
        "connect" : "http://192.168.200.195:8181/iDV/svltaction",
        "data" : [ "tc",  "GetLoginInfo" ],
        "cookie" : { 
            "session" : "JSESSIONID"
        },
        "method" : "POST"
    }
}
