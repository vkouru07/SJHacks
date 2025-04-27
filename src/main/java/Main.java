import static spark.Spark.post;
import static spark.Spark.port;

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

        // 1) Slash command → open modal
        post("/route", (req, res) -> {
            String trigId    = req.queryParams("trigger_id");
            String channelId = req.queryParams("channel_id");
            String respUrl   = req.queryParams("response_url");
        
            System.out.println("↪ /route invoked…");
        
            if (trigId == null || channelId == null || respUrl == null) {
              res.status(400);
              return "Missing trigger_id, channel_id or response_url";
            }
        
            // fire off the modal
            new Thread(() -> {
              try {
                String modalJson = SlackModalBuilder.buildModalJson(trigId, channelId, respUrl);
                SlackAPI.openModal(trigId, modalJson);
                System.out.println("→ views.open sent for trigger " + trigId);
              } catch (Exception e) {
                e.printStackTrace();
              }
            }).start();
        
            // **CRUCIAL**: Slack wants a non‐HTML, non‐empty 200 response here
            res.type("application/json");
            res.status(200);
            return "{}";
        });
        
        
        

        // 2) Handle both suggestions & submission
        post("/slack/interactions", (req, res) -> {
            String raw;
            String ct = req.contentType();
            if (ct != null && ct.startsWith("application/json")) {
                raw = req.body();
            } else {
                raw = req.queryParams("payload");
            }
            System.out.println("↪ /slack/interactions raw payload: " + raw);

            // 2) Parse it
            JsonObject body = new Gson().fromJson(raw, JsonObject.class);
            String type = body.get("type").getAsString();
            System.out.println("↪ interaction type: " + type);

            // 3a) Suggestions—as user types in external_select
            if (type.equals("block_suggestion") || type.equals("block_suggestions")) {
                String userInput = body.get("value").getAsString();
                System.out.println("↪ suggestion for input: " + userInput);

                List<PlaceResult> matches = GooglePlacesClient.autocomplete(userInput);

                JsonArray options = new JsonArray();
                for (PlaceResult p : matches) {
                    JsonObject opt = new JsonObject();
                    JsonObject text = new JsonObject();
                    text.addProperty("type", "plain_text");
                    text.addProperty("text", p.getDescription());
                    opt.add("text", text);
                    opt.addProperty("value", p.getPlaceId());
                    options.add(opt);
                }

                JsonObject reply = new JsonObject();
                reply.add("options", options);

                res.type("application/json");
                return reply.toString();
            }

            // Final form submit
            if ("view_submission".equals(type)) {
                JsonObject view  = body.getAsJsonObject("view");
                JsonObject state = view.getAsJsonObject("state").getAsJsonObject("values");

                String originPid = state
                  .getAsJsonObject("origin_block")
                  .getAsJsonObject("origin_select")
                  .getAsJsonObject("selected_option")
                  .get("value").getAsString();

                String destPid = state
                  .getAsJsonObject("destination_block")
                  .getAsJsonObject("destination_select")
                  .getAsJsonObject("selected_option")
                  .get("value").getAsString();

                Waypoint origin      = GooglePlacesClient.getCoordinatesFromPlaceId(originPid);
                Waypoint destination = GooglePlacesClient.getCoordinatesFromPlaceId(destPid);
                DetourOption best    = RouteFinder.findBestDetour(
                    origin,
                    destination,
                    DataLoader.loadSupplyPoints(),
                    DataLoader.loadShelters()
                );

                // pull channel from metadata
                String channel = new Gson()
                  .fromJson(view.get("private_metadata").getAsString(), JsonObject.class)
                  .get("channel_id").getAsString();

                if (best != null) {
                    SlackAPI.postMessageToChannel(
                        channel,
                        new Gson().toJson(new SlackMessage(best))
                    );
                } else {
                    SlackAPI.postMessageToChannel(
                        channel,
                        "{ \"text\": \"No suitable detour found.\" }"
                    );
                }

                return "{\"response_action\":\"clear\"}";
            }

            // otherwise, just ack
            res.status(200);
            return "";
        });
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
