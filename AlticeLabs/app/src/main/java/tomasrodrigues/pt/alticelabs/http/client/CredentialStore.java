package tomasrodrigues.pt.alticelabs.http.client;

import okhttp3.Credentials;

/**
 * Protected credentials for accessing a web server.
 * <p>
 * Created by Tomás Rodrigues on 21-02-2018.
 */
class CredentialStore {

    private static final int V01 = 0x31;

    private static final int V02 = 0x35;

    private static final int V03 = 0x23;

    private static final int V04 = 0x21;

    private static final int V05 = 0x66;

    private static final int V06 = 0x69;

    private static final int V07 = 0x6e;

    private static final int V08 = 0x70;

    private static final int V09 = 0x72;

    private static final int V10 = 0x73;

    private static final int V11 = 0x74;

    private static final char[] PART_1 = { V10, V05, V09 };

    private static final char[] PART_2 = { V03, V08, V11, V06, V07, V04, V01, V02 };

    /**
     * Creates a set of authentication credentials to be used with the ArQoS endpoints.
     *
     * @return the client credentials.
     */
    static String basic() {
        return Credentials.basic(new String(PART_1), new String(PART_2));
    }
}
