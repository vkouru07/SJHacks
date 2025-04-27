import com.google.gson.Gson;

import java.net.ServerSocket;
import java.util.List;

import static spark.Spark.port;
import static spark.Spark.post;

public class Main {
    public static void main(String[] args) {
        int port = findFreePort();
        port(port); // <-- Spark server binds to this free port
        System.out.println("Server starting on port: " + port);

        post("/route", (req, res) -> {
            String text = req.queryParams("text"); // Slack sends command text as URL param
            // Expected format: "origin_lat,origin_lon;dest_lat,dest_lon"
        
            if (text == null || text.isEmpty()) {
                res.type("application/json");
                return "{ \"text\": \"Please provide origin and destination.\" }";
            }
        
            String[] parts = text.split(";");
            if (parts.length != 2) {
                res.type("application/json");
                return "{ \"text\": \"Invalid format. Use: origin_lat,origin_lon;dest_lat,dest_lon\" }";
            }
        
            String[] originParts = parts[0].split(",");
            String[] destParts = parts[1].split(",");
        
            Waypoint origin = new Waypoint(Double.parseDouble(originParts[0]), Double.parseDouble(originParts[1]));
            Waypoint destination = new Waypoint(Double.parseDouble(destParts[0]), Double.parseDouble(destParts[1]));
        
            List<Waypoint> supplyPoints = DataLoader.loadSupplyPoints();
            List<Waypoint> shelters = DataLoader.loadShelters();
        
            DetourOption bestDetour = RouteFinder.findBestDetour(origin, destination, supplyPoints, shelters);
        
            res.type("application/json");
        
            if (bestDetour != null) {
                return new Gson().toJson(new SlackMessage(bestDetour));
            } else {
                return "{ \"text\": \"No suitable detour found.\" }";
            }
        });
    }
    
    private static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort(); // OS picks a free port
        } catch (Exception e) {
            throw new RuntimeException("Failed to find free port", e);
        }
    }
}