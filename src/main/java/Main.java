import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.get;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.net.ServerSocket;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        int port = findFreePort();
        port(port);
        System.out.println("Server starting on port: " + port);

        // 1️⃣ Slash command now opens the modal instead of parsing coords
        post("/route", (req, res) -> {
            String trigId     = req.queryParams("trigger_id");
            String channelId  = req.queryParams("channel_id");
            String responseUrl= req.queryParams("response_url");
        
            String modalJson = SlackModalBuilder.buildModalJson(
                trigId,
                channelId,
                responseUrl
            );
            SlackAPI.openModal(trigId, modalJson);
        
            res.status(200);
            return "";
        });
        

        // 2️⃣ Your autocomplete endpoint (unchanged)
        get("/autocomplete", (req, res) -> {
            String query = req.queryParams("query");
            if (query == null || query.isEmpty()) {
                res.status(400);
                return "Missing query parameter.";
            }
            List<PlaceResult> results = GooglePlacesClient.autocomplete(query);

            JsonArray options = new JsonArray();
            for (PlaceResult r : results) {
                JsonObject opt = new JsonObject();
                opt.addProperty("text", r.getDescription());
                opt.addProperty("value", r.getPlaceId());
                options.add(opt);
            }
            JsonObject resp = new JsonObject();
            resp.add("options", options);

            res.type("application/json");
            return resp.toString();
        });

        post("/slack/interactions", (req, res) -> {
            // Slack sends body as form-urlencoded: payload=<json>
            String payload = req.queryParams("payload");
            JsonObject root = new Gson().fromJson(payload, JsonObject.class);
        
            // Only care about modal submissions:
            if (!"view_submission".equals(root.get("type").getAsString())) {
                res.status(200);
                return "";
            }
        
            // 1) unpack metadata
            JsonObject view = root.getAsJsonObject("view");
            JsonObject meta = new Gson()
              .fromJson(view.get("private_metadata").getAsString(), JsonObject.class);
            String channelId   = meta.get("channel_id").getAsString();
        
            // 2) grab the place_ids from the state
            JsonObject vals = view
              .getAsJsonObject("state")
              .getAsJsonObject("values");
        
            String originPlaceId = vals
              .getAsJsonObject("origin_block")
              .getAsJsonObject("origin_select")
              .getAsJsonObject("selected_option")
              .get("value").getAsString();
        
            String destPlaceId = vals
              .getAsJsonObject("destination_block")
              .getAsJsonObject("destination_select")
              .getAsJsonObject("selected_option")
              .get("value").getAsString();
        
            // 3) translate to coords, compute detour
            Waypoint origin      = GooglePlacesClient.getCoordinatesFromPlaceId(originPlaceId);
            Waypoint destination = GooglePlacesClient.getCoordinatesFromPlaceId(destPlaceId);
            DetourOption best    = RouteFinder.findBestDetour(
                origin,
                destination,
                DataLoader.loadSupplyPoints(),
                DataLoader.loadShelters()
            );
        
            // 4) reply back into the channel
            if (best != null) {
                SlackAPI.postMessageToChannel(
                  channelId,
                  new Gson().toJson(new SlackMessage(best))
                );
            } else {
                SlackAPI.postMessageToChannel(
                  channelId,
                  "{ \"text\": \"No suitable detour found.\" }"
                );
            }
        
            // 5) Acknowledge & clear the modal
            res.type("application/json");
            return "{\"response_action\":\"clear\"}";
        });
        

        // 3️⃣ Later: handle the modal submit (route_submit) via /slack/interactions
        //    — parse the selected place_ids,
        //    — use GooglePlacesClient.getCoordinatesFromPlaceId(),
        //    — then run your existing DetourOption logic &
        //    — reply with SlackAPI.sendMessage(...)
    }

    private static int findFreePort() {
        try (ServerSocket socket = new ServerSocket(0)) {
            socket.setReuseAddress(true);
            return socket.getLocalPort();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find free port", e);
        }
    }
}
