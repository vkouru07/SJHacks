import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GoogleDistanceMatrixClient {
    private static final String API_KEY = "YOUR_API_KEY";

    public static int getDriveTimeMinutes(Waypoint origin, Waypoint destination) throws Exception {
        String urlString = String.format(
                "https://maps.googleapis.com/maps/api/directions/json?origin=%f,%f&destination=%f,%f&key=%s",
                origin.getLatitude(), origin.getLongitude(),
                destination.getLatitude(), destination.getLongitude(),
                API_KEY
        );

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        in.close();

        JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
        int seconds = json.getAsJsonArray("routes")
                .get(0).getAsJsonObject()
                .getAsJsonArray("legs")
                .get(0).getAsJsonObject()
                .getAsJsonObject("duration")
                .get("value").getAsInt();

        return seconds / 60; // convert to minutes
    }
}
