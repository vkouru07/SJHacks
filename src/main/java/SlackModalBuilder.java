import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SlackModalBuilder {
    public static String buildModalJson(
        String triggerId,
        String channelId,
        String responseUrl
    ) {
        // build the view
        JsonObject title = plainText("Plan Your Detour");
        JsonObject submit = plainText("Submit");

        JsonArray blocks = new JsonArray();
        // Origin block
        JsonObject originBlock = new JsonObject();
        originBlock.addProperty("type", "input");
        originBlock.addProperty("block_id", "origin_block");
        JsonObject originEl = new JsonObject();
        originEl.addProperty("type", "external_select");
        originEl.addProperty("data_source", "external");  // enable dynamic suggestions
        originEl.addProperty("action_id", "origin_select");
        originEl.add("placeholder", plainText("Type origin location"));
        originEl.addProperty("min_query_length", 3);
        originBlock.add("element", originEl);
        originBlock.add("label", plainText("Origin"));
        blocks.add(originBlock);

        // Destination block
        JsonObject destBlock = new JsonObject();
        destBlock.addProperty("type", "input");
        destBlock.addProperty("block_id", "destination_block");
        JsonObject destEl = new JsonObject();
        destEl.addProperty("type", "external_select");
        destEl.addProperty("data_source", "external");  // enable dynamic suggestions
        destEl.addProperty("action_id", "destination_select");
        destEl.add("placeholder", plainText("Type destination location"));
        destEl.addProperty("min_query_length", 3);
        destBlock.add("element", destEl);
        destBlock.add("label", plainText("Destination"));
        blocks.add(destBlock);

        JsonObject view = new JsonObject();
        view.addProperty("type", "modal");
        view.addProperty("callback_id", "route_submit");
        view.add("title", title);
        view.add("submit", submit);
        view.add("blocks", blocks);

        // package up metadata
        JsonObject meta = new JsonObject();
        meta.addProperty("channel_id", channelId);
        meta.addProperty("response_url", responseUrl);

        JsonObject payload = new JsonObject();
        payload.addProperty("trigger_id", triggerId);
        payload.add("view", view);
        payload.addProperty("private_metadata", meta.toString());

        return payload.toString();
    }

    private static JsonObject plainText(String txt) {
        JsonObject o = new JsonObject();
        o.addProperty("type", "plain_text");
        o.addProperty("text", txt);
        return o;
    }
}
