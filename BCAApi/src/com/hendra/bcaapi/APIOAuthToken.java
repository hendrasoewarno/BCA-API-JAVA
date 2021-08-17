package com.hendra.bcaapi;

//import android.util.Base64;
import com.hendra.middleware.inet.HTTPRequest;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Created by Hendra Soewarno(0119067505) on 12/10/2020.
 */
/*
POST /api/oauth/token HTTP/1.1
Host: sandbox.bca.co.id
Authorization: Basic Base64(client_id:client_secret)
Content-Type: application/x-www-form-urlencoded

grant_type=client_credentials

Response:
{
"access_token":"2YotnFZFEjr1zCsicMWpAA",
"token_type":"Bearer",
"expires_in":3600,
"scope":"resource.WRITE resource.READ"
}
 */
public class APIOAuthToken {
    private static String endpoint = "/api/oauth/token";
    private String access_token;
    private String token_type;
    private long expires_in;
    private String scope;
    private long timestamp;

    private static APIOAuthToken instance;

    //Membuat Header Authorization Basic Authentication
    public HashMap<String, String> BasicHeader() {
        HashMap<String, String> header = new HashMap<>();
        String auth = Auth.clientId + ":" + Auth.clientSecret;
        //String basicAuth = "Basic " + Base64.encodeToString(auth.getBytes(), Base64.NO_WRAP);
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
        header.put("Authorization", basicAuth);
        header.put("Content-Type", "application/x-www-form-urlencoded");
        return header;
    }

    //hide Constructor
    private APIOAuthToken() throws Exception {
        refreshToken();
    }

    public static APIOAuthToken getInstance() throws Exception {
        if (instance==null)
            instance = new APIOAuthToken();
        return instance;
    }

    public void refreshToken() throws Exception {
        HTTPRequest httpRequest = new HTTPRequest() {
            @Override
            protected void publish(int bytesread) {
            }
        };
        //{"access_token":"qk4Jr6AKwDf5uYrDZk9q0QZDV4zU0gagzNRn6b7hO4hMY6oBVeEM72","token_type":"Bearer","expires_in":3600,"scope":"resource.WRITE resource.READ"}
        String rawResponse = httpRequest.post(Auth.URL + endpoint, "grant_type=client_credentials", BasicHeader());
        if (httpRequest.getResponseCode() == 200) {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(rawResponse);  
            this.access_token = (String) obj.get("access_token");
            this.token_type = (String) obj.get("token_type");
            this.expires_in = (Long) obj.get("expires_in");
            this.scope = (String) obj.get("scope");
            this.timestamp = (new Date()).getTime();
        }
        else
            throw new Exception(rawResponse);
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public long getExpires_in() {
        return expires_in;
    }

    public String getScope() {
        return scope;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isExpired() {
        return (timestamp+this.expires_in-20)*1000 < (new Date()).getTime();
    }
}
