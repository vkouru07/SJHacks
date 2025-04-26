import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        port(4567);

        post("/route", (req, res) -> {
            // Parse input from Slack bot
            String body = req.body(); // Expecting JSON
            // Parse it into origin & destination waypoints (later step)
            return "Received!";
        });
    }
}
