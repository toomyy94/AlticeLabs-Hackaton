package tomasrodrigues.pt.alticelabs.http.client;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.common.base.Strings;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Authenticator;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;


/**
 * HTTP client to execute HTTP requests.
 * <p>
 * Created by Tomás Rodrigues on 21-02-2018.
 */
public class HttpClient {

    private static final String HTTP_CLIENT = "HttpClient";

    private static final MediaType JSON_CONTENT_TYPE = MediaType.parse("application/json; charset=utf-8");

    private static final String AUTHORIZATION_HEADER = "Authorization";

    /**
     * id Probe ArQoS Pocket
     */
    public final static String EQUIPMENT_TYPE = "13";

    private OkHttpClient client;

    private String ipAddress;

    private String imei;

    public HttpClient() {
        client = new OkHttpClient.Builder().authenticator(new BasicAuthenticator()).build();
    }

    public Response post(String url, String body) throws IOException {
        RequestBody requestBody = RequestBody.create(JSON_CONTENT_TYPE, body);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return client.newCall(request).execute();
    }

    private Response get(String url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        return client.newCall(request).execute();
    }

    public String getRequest(String url){
        try {
            Response response = get(url);
            if (response.isSuccessful()) {
                Log.d("Sent with success", "Sent with success");
                return response.body().string();
            }
            else{
                return "Error";
            }
        }
        catch (Exception e) {
            Log.e("Error executing request", e.toString());
            return "Error";
        }

    }

    private class BasicAuthenticator implements Authenticator {

        @Override
        public Request authenticate(Route route, Response response) throws IOException {
            return authenticateWithStoredCredentials(response);
        }

        /**
         * Responds the the server authentication challenge. We must first check if the 'Authentication' header is
         * already present. If the header is already there then it means that we already responded to the challenge once
         * and the server rejected the credentials. We must return {@code null} to stop the request or we will enter an
         * infinite loop.
         *
         * @param response the response from the server containing the challenge.
         * @return a new request filled with the client credentials.
         */
        @Nullable
        private Request authenticateWithStoredCredentials(Response response) {
            String credential = CredentialStore.basic();
            if (credential.equals(response.request().header(AUTHORIZATION_HEADER))) {
                return null;
            }
            return response.request().newBuilder().header(AUTHORIZATION_HEADER, credential).build();
        }
    }
}
