import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SlackAPI {
    private static final String WEBHOOK_URL = ConfigLoader.get("SLACK_WEBHOOK_URL");
    private static final String BOT_TOKEN   = ConfigLoader.get("SLACK_BOT_TOKEN");

    /**
     * Sends a simple text message to Slack via an incoming webhook.
     */
    public static void sendMessage(String text) {
        try {
            URL url = new URL(WEBHOOK_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String payload = String.format("{\"text\": \"%s\"}", text);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = payload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Slack Response Code: " + responseCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a modal in Slack using the views.open API.
     * @param triggerId the trigger_id from the slash command payload
     * @param modalJson full JSON payload defining the modal view
     */
    public static void openModal(String triggerId, String modalJson) {
        try {
            URL url = new URL("https://slack.com/api/views.open");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + BOT_TOKEN);

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = modalJson.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Slack Modal Response Code: " + responseCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void postMessageToChannel(String channel, String jsonPayload) {
    try {
        URL url = new URL("https://slack.com/api/chat.postMessage");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type",  "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + BOT_TOKEN);

        // ensure your jsonPayload includes the "text" field
        // here we inject channel:
        JsonObject body = JsonParser
          .parseString(jsonPayload)
          .getAsJsonObject();
        body.addProperty("channel", channel);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.toString().getBytes("utf-8"));
        }

        System.out.println("chat.postMessage response: " + conn.getResponseCode());
    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
