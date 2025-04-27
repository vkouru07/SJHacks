import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GooglePlacesClient {
    private static final String API_KEY;

    static {
        try {
            API_KEY = GoogleDistanceMatrixClient.readApiKeyFromFile("NOT_GOOGLE_MAPS_API_KEY");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<PlaceResult> autocomplete(String query) throws Exception {
        String encodedQuery = URLEncoder.encode(query, "UTF-8");
        String urlString = String.format(
            "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=%s&location=37.5,-119.5&radius=500000&components=country:us&key=%s",
            encodedQuery, API_KEY
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
        JsonArray predictions = json.getAsJsonArray("predictions");

        List<PlaceResult> results = new ArrayList<>();
        for (int i = 0; i < predictions.size(); i++) {
            JsonObject prediction = predictions.get(i).getAsJsonObject();
            String placeId = prediction.get("place_id").getAsString();
            String description = prediction.get("description").getAsString();
            results.add(new PlaceResult(placeId, description));
        }
        return results;
    }

    public static Waypoint getCoordinatesFromPlaceId(String placeId) throws Exception {
        String urlString = String.format(
                "https://maps.googleapis.com/maps/api/place/details/json?place_id=%s&fields=geometry&key=%s",
                placeId, API_KEY
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
        JsonObject location = json.getAsJsonObject("result")
                                   .getAsJsonObject("geometry")
                                   .getAsJsonObject("location");
    
        double lat = location.get("lat").getAsDouble();
        double lng = location.get("lng").getAsDouble();
    
        return new Waypoint(lat, lng);
    }
    
}
