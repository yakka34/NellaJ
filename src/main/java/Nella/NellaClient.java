package Nella;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import Exceptions.*;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;

public class NellaClient {

    private URL baseUrl;
    private URL authUrl;
    private URL backendURL;

    private Boolean debug = false;
    private String lang = "en";
    
    private Session session;

    public NellaClient(Boolean debug) {
        try {
            baseUrl = new URL("https://nella.tampere.fi/mobiili/");
            authUrl = new URL(baseUrl, "oauth/token");
            backendURL = new URL(baseUrl, "api/v1/");
            this.debug = debug;
        } catch (MalformedURLException ex) {
            Logger.getLogger(NellaClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (debug) {
            System.out.println(String.format(" BaseUrl: %s", baseUrl));
            System.out.println(String.format(" AuthUrl: %s", authUrl));
            System.out.println(String.format(" BackendUrl: %s", backendURL));
            System.out.println(String.format(" Lang code: %s", lang));
        }
    }

    public Boolean auth(String username, String password) throws NellaAuthFailedError{
        String payload = String.format("grant_type=password&username=%s&password=%s", username, password);
        HttpsURLConnection con = sendPost(authUrl, payload.getBytes(StandardCharsets.UTF_8));
        JsonObject json = new Gson().fromJson(readInput(con), JsonObject.class);
        try {
            if(con.getResponseCode() == HttpsURLConnection.HTTP_OK){
                session = new Session(json.get("access_token").getAsString(), json.get("token_type").getAsString(), json.get("expires_in").getAsInt());
                return true;
            }
            else{
                throw new NellaAuthFailedError(String.format("Authentication failed: %s",json.get("error_description").getAsString()));
            }
        } catch (IOException ex) {
            Logger.getLogger(NellaClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private String readInput(HttpsURLConnection con) {
        try {
            //Sori hei tästä
            BufferedReader in = new BufferedReader(
                    (con.getResponseCode() == HttpsURLConnection.HTTP_OK)
                            ? new InputStreamReader(con.getInputStream())
                            : new InputStreamReader(con.getErrorStream()));
            //</sori>
            StringBuilder sb = new StringBuilder();
            String input;
            while ((input = in.readLine()) != null) {
                sb.append(input);
            }
            return sb.toString();
        } catch (IOException e) {
            Logger.getLogger(NellaClient.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
    }

    private HttpsURLConnection sendPost(URL url, byte[] payload) {
        try {
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            OutputStream out = con.getOutputStream();
            out.write(payload);
            out.flush();
            out.close();
            return con;
        } catch (IOException ex) {
            Logger.getLogger(NellaClient.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public String doRequest(String url) throws NellaNotAuthenticatedError, NellaRequestFailedError{
        if(session == null || session.isExpired()){
            throw new NellaNotAuthenticatedError("Not authenticated");
        }
        try {
            URL target = new URL(backendURL,String.format("%s?lang=%s", url,lang));
            HttpsURLConnection con = (HttpsURLConnection) target.openConnection();
            con.setRequestProperty("Authorization", String.format("bearer %s", session.getAccessToken()));
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Cache-Control", "no-cache");
            con.setRequestProperty("Pragma", "no-cache");
            if(con.getResponseCode() != HttpsURLConnection.HTTP_OK){
                throw new NellaRequestFailedError("Request failed");
            }
            session.updateExpiryDate();
            return readInput(con);
        } catch (IOException ex) {
            Logger.getLogger(NellaClient.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
