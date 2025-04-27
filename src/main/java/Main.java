import com.google.gson.Gson;

import java.util.List;

import static spark.Spark.port;
import static spark.Spark.post;

public class Main {
    public static void main(String[] args) {
        port(4568);

        post("/route", (req, res) -> {
            String body = req.body();
            RouteRequest routeRequest = new Gson().fromJson(body, RouteRequest.class);
            String text = req.queryParams("text"); // Slack sends command text as URL param
            // Expected format: "origin_lat,origin_lon;dest_lat,dest_lon"

            if (text == null || text.isEmpty()) {
                return "Please provide origin and destination.";
            }

            String[] parts = text.split(";");
            if (parts.length != 2) {
                return "Invalid format. Use: origin_lat,origin_lon;dest_lat,dest_lon";
            }

            String[] originParts = parts[0].split(",");
            String[] destParts = parts[1].split(",");

            Waypoint origin = new Waypoint(Double.parseDouble(originParts[0]), Double.parseDouble(originParts[1]));
            Waypoint destination = new Waypoint(Double.parseDouble(destParts[0]), Double.parseDouble(destParts[1]));

            List<Waypoint> supplyPoints = DataLoader.loadSupplyPoints();
            List<Waypoint> shelters = DataLoader.loadShelters();

            DetourOption bestDetour = RouteFinder.findBestDetour(origin, destination, supplyPoints, shelters);

            if (bestDetour != null) {
                // Send response back directly to Slack user
                res.type("application/json");
                return new Gson().toJson(new SlackMessage(bestDetour));
            } else {
                return "No suitable detour found.";
            }
        });
    }
}