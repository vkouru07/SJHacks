import com.google.gson.Gson;

import java.util.List;

import static spark.Spark.port;
import static spark.Spark.post;

public class Main {
    public static void main(String[] args) {
        port(4567);

        post("/route", (req, res) -> {
            String body = req.body();
            RouteRequest routeRequest = new Gson().fromJson(body, RouteRequest.class);

            Waypoint origin = routeRequest.getOrigin();
            Waypoint destination = routeRequest.getDestination();

            List<Waypoint> supplyPoints = DataLoader.loadSupplyPoints();
            List<Waypoint> shelters = DataLoader.loadShelters();

            DetourOption bestDetour = RouteFinder.findBestDetour(origin, destination, supplyPoints, shelters);

            if (bestDetour != null) {
                res.type("application/json");
                return new Gson().toJson(bestDetour);
            } else {
                return "No suitable detour found.";
            }
        });
    }
}
