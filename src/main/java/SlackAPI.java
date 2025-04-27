import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SlackAPI {
    private static final String WEBHOOK_URL = "https://hooks.slack.com/services/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";

    public static void sendMessage(String text) {
        try {
            URL url = new URL(WEBHOOK_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json");

            String payload = "{\"text\": \"" + text + "\"}";

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

    public static void main(String args[]){
        SlackAPI.sendMessage("Hello Driver! You can help!");
    }
}
