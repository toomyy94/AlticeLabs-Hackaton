package tomasrodrigues.pt.alticelabs.http.server;

import android.app.Application;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.common.base.Strings;

import java.util.Calendar;

import fi.iki.elonen.NanoHTTPD;

/**
 * Web server to receive requests from the management service.
 * <p>
 * Created by Tomás Rodrigues on 21-02-2018.
 */
public class HttpServer extends NanoHTTPD {

    private static final String APPLICATION_JSON = "application/json; charset=UTF-8";

    private static final int PORT = 8080;

    private static final String IP_ADDRESS = "10.112.209.186";

    private final String address;

    private final HttpRequestListener listener;

    public HttpServer(HttpRequestListener listener) {
        super(PORT);
        this.listener = listener;
        address = "http://" + IP_ADDRESS + ":" + PORT;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public Response serve(IHTTPSession session) {
        SessionWrapper wrapper = new SessionWrapper(session);

        if (wrapper.isOptions()) {
            return newPreFlightResponse();
        }

        if (wrapper.isPost()) {
            if (!wrapper.isJsonContent()) {
                return newBadRequest("Unsupported content type, only 'application/json' is allowed", 1);
            }
            if (wrapper.isRequestUri()) {
                String content = Strings.nullToEmpty(wrapper.getContent());
                int contentLength = content.length();
                Log.d("Received content : {}", content);

                if (contentLength == 0) {
                    return newBadRequest("Received empty body", 1);
                }

                try {
                    HttpRequestListener.ProcessedResponse response = null;
                    if (listener != null) {
                        response = listener.onRequestReceived(content);
                    }
                    if (response != null) {
                        if (response.isSuccess()) {
                            return newSuccessResponse(response.getBody());
                        } else {
                            return newBadRequest(response.getBody(), response.getErrorCode());
                        }
                    }
                } catch (Exception e) {
                    Log.e("Error on content", e.getMessage());

                    return newInternalServerError(e.getMessage());
                }

                return newSuccessResponse();
            }
        }

        return newNotFoundResponse();
    }

    private Response newInternalServerError(String message) {
        String error = getErrorResponseBody(message, 0);
        Response response = newFixedLengthResponse(Response.Status.INTERNAL_ERROR, APPLICATION_JSON, error);
        addCorsHeaders(response);
        return response;

    }

    private Response newBadRequest(String message, int errorCode) {
        String error = getErrorResponseBody(message, errorCode);
        Response response = newFixedLengthResponse(Response.Status.BAD_REQUEST, APPLICATION_JSON, error);
        addCorsHeaders(response);
        return response;
    }

    @NonNull
    private Response newSuccessResponse() {
        Response response = newFixedLengthResponse(Response.Status.OK, APPLICATION_JSON, getSuccessResponseBody());
        addCorsHeaders(response);
        return response;
    }

    @NonNull
    private Response newSuccessResponse(String body) {
        Response response = newFixedLengthResponse(Response.Status.OK, APPLICATION_JSON, body);
        addCorsHeaders(response);
        return response;
    }

    @NonNull
    private Response newPreFlightResponse() {
        Response response = newFixedLengthResponse(Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, null);
        addCorsHeaders(response, true);
        return response;
    }

    private Response newNotFoundResponse() {
        String error = getErrorResponseBody(null, 0);
        Response response = newFixedLengthResponse(Response.Status.NOT_FOUND, APPLICATION_JSON, error);
        addCorsHeaders(response);
        return response;
    }

    private void addCorsHeaders(Response response) {
        addCorsHeaders(response, false);
    }

    private void addCorsHeaders(Response response, boolean isPreFlight) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type");
        if (isPreFlight) {
            response.addHeader("Access-Control-Allow-Methods", "POST, OPTIONS");
            response.addHeader("Access-Control-Max-Age", "86400");
        }
    }

    private String getSuccessResponseBody() {
        return getResponseBody(true, null, -1);
    }

    private String getErrorResponseBody(String error, int errorCode) {
        return getResponseBody(false, Strings.emptyToNull(error), errorCode);
    }

    private String getResponseBody(boolean success, String error, int errorCode) {
        return "ola";
    }
}
