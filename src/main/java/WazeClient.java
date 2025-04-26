public class WazeClient {
    private static final String WAZE_API_KEY = "your-waze-api-key";
    private static final String WAZE_BASE_URL = "https://www.waze.com/row-RoutingManager/routingRequest";

    public static int getDriveTime(Waypoint origin, Waypoint destination) throws Exception {
        return 5;
    }
//        String urlString = String.format(
//                "%s?from=x:%f+y:%f&to=x:%f+y:%f&key=%s",
//                WAZE_BASE_URL,
//                origin.getLongitude(), origin.getLatitude(),
//                destination.getLongitude(), destination.getLatitude(),
//                WAZE_API_KEY
//        );
//
//        URL url = new URL(urlString);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("GET");
//
//        int responseCode = connection.getResponseCode();
//        if (responseCode == 200) {
//            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//            StringBuilder response = new StringBuilder();
//            String inputLine;
//            response.append(inputLine);
//        }
//        in.close();
//
//        String responseBody = response.toString();
//        return parseDriveTime(responseBody);
//    } else
//
//    {
//        throw new RuntimeException("Failed to fetch data from Waze API. Response code: " + responseCode);
//    }
//
//}
//    private static int parseDriveTime(String responseBody) {
//        int driveTime = 0;
//        if (responseBody.contains("crossTime")) {
//            String[] parts = responseBody.split("\"crossTime\":");
//            driveTime = Integer.parseInt(parts[1].split(",")[0]);
//        }
//        return driveTime;
//    }
}
