package tomasrodrigues.pt.alticelabs.http.server;

import android.support.annotation.NonNull;

/**
 * Interface for a HTTP request listener.
 * <p>
 * Created by Tomás Rodrigues on 21-02-2018.
 */
public interface HttpRequestListener {

    /**
     * Is called when a valid HTTP request is made with some supposed JSOn content.
     *
     * @param content the received content.
     */
    ProcessedResponse onRequestReceived(@NonNull String content);

    class ProcessedResponse {

        private final int errorCode;

        private final String body;

        public ProcessedResponse(String body) {
            this(-1, body);
        }

        public ProcessedResponse(int errorCode, String body) {
            this.errorCode = errorCode;
            this.body = body;
        }

        public int getErrorCode() {
            return errorCode;
        }

        String getBody() {
            return body;
        }

        boolean isSuccess() {
            return errorCode < 0;
        }
    }
}
