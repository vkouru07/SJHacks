import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SlackModalBuilder {
    public static String buildModalJson(
        String triggerId,
        String channelId,
        String responseUrl
        )  {
        JsonObject view = new JsonObject();
        view.addProperty("type", "modal");
        view.addProperty("callback_id", "route_submit");

        JsonObject title = new JsonObject();
        title.addProperty("type", "plain_text");
        title.addProperty("text", "Plan Your Detour");

        JsonObject submit = new JsonObject();
        submit.addProperty("type", "plain_text");
        submit.addProperty("text", "Submit");

        JsonArray blocks = new JsonArray();

        JsonObject originBlock = new JsonObject();
        originBlock.addProperty("type", "input");
        originBlock.addProperty("block_id", "origin_block");
        JsonObject originElement = new JsonObject();
        originElement.addProperty("type", "external_select");
        originElement.addProperty("action_id", "origin_select");
        originElement.add("placeholder", plainTextObject("Type origin location"));
        originElement.addProperty("min_query_length", 3);
        originBlock.add("element", originElement);
        originBlock.add("label", plainTextObject("Origin"));

        JsonObject destinationBlock = new JsonObject();
        destinationBlock.addProperty("type", "input");
        destinationBlock.addProperty("block_id", "destination_block");
        JsonObject destinationElement = new JsonObject();
        destinationElement.addProperty("type", "external_select");
        destinationElement.addProperty("action_id", "destination_select");
        destinationElement.add("placeholder", plainTextObject("Type destination location"));
        destinationElement.addProperty("min_query_length", 3);
        destinationBlock.add("element", destinationElement);
        destinationBlock.add("label", plainTextObject("Destination"));

        blocks.add(originBlock);
        blocks.add(destinationBlock);

        JsonObject viewWrapper = new JsonObject();
        viewWrapper.add("type", new com.google.gson.JsonPrimitive("modal"));
        viewWrapper.add("callback_id", new com.google.gson.JsonPrimitive("route_submit"));
        viewWrapper.add("title", title);
        viewWrapper.add("submit", submit);
        viewWrapper.add("blocks", blocks);

        JsonObject payload = new JsonObject();
        payload.addProperty("trigger_id", triggerId);
        payload.add("view", viewWrapper);

        // stash channel + response_url so we can use them later
        JsonObject meta = new JsonObject();
        meta.addProperty("channel_id",  channelId);
        meta.addProperty("response_url", responseUrl);
        payload.addProperty(
        "private_metadata",
        meta.toString()
        );

        return payload.toString();
    }

    private static JsonObject plainTextObject(String text) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "plain_text");
        obj.addProperty("text", text);
        return obj;
    }
}
