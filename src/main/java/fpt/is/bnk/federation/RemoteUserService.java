package fpt.is.bnk.federation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.keycloak.component.ComponentModel;
import org.jboss.logging.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Admin 12/1/2025
 *
 **/
public class RemoteUserService {

    private static final Logger logger = Logger.getLogger(RemoteUserService.class);

    private final String baseUrl;
    private final String secret;
    private final ObjectMapper mapper = new ObjectMapper();

    public RemoteUserService(ComponentModel model) {
        this.baseUrl = model.get("remoteUrl");
        this.secret = model.get("internalSecret");
        logger.infof("RemoteUserService initialized with baseUrl: %s", baseUrl);
    }

    public RemoteUser getUser(String username) {
        logger.infof("Getting user by username: %s", username);
        String url = baseUrl + "/internal/users/" + username;
        return doGet(url);
    }

    public RemoteUser getUserByEmail(String email) {
        logger.infof("Getting user by email: %s", email);
        String url = baseUrl + "/internal/users/email/" + email;
        return doGet(url);
    }

    public boolean verifyPassword(String username, String rawPassword) {
        logger.infof("Verifying password for username: %s", username);
        try {
            URL url = new URL(baseUrl + "/internal/users/auth/check");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("X-Internal-Secret", secret);

            String body = """
                        {
                            "username": "%s",
                            "password": "%s"
                        }
                    """.formatted(username, rawPassword);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
            }

            int responseCode = conn.getResponseCode();
            logger.infof("Password check response code: %d", responseCode);

            if (responseCode != 200) {
                return false;
            }

            InputStream is = conn.getInputStream();
            String response = new String(is.readAllBytes());

            if (response.isBlank()) {
                return false;
            }

            boolean isValid = mapper.readValue(response, Boolean.class);
            logger.infof("Password validation result: %s", isValid);
            return isValid;

        } catch (Exception e) {
            logger.errorf(e, "ERROR during password verification for user: %s", username);
            return false;
        }
    }

    private RemoteUser doGet(String urlStr) {
        logger.infof("HTTP GET: %s", urlStr);
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("X-Internal-Secret", secret);

            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            logger.infof("Response code: %d", responseCode);

            if (responseCode != 200) {
                return null;
            }

            InputStream is = conn.getInputStream();
            String body = new String(is.readAllBytes());
            logger.infof("Response body: %s", body);

            if (body.isBlank() || body.equals("null")) {
                return null;
            }

            RemoteUser user = mapper.readValue(body, RemoteUser.class);
            logger.infof("Parsed user: %s", user);
            return user;

        } catch (Exception e) {
            logger.errorf(e, "ERROR during GET request to: %s", urlStr);
            return null;
        }
    }
}
