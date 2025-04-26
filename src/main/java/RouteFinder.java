import java.util.List;

public class RouteFinder {
    private static final int MAX_EXTRA_TIME_MINUTES = 5;

    public static DetourOption findBestDetour(Waypoint origin, Waypoint destination, List<Waypoint> supplyPoints, List<Waypoint> shelters) {
        DetourOption bestOption = null;
        int bestDetourTime = Integer.MAX_VALUE;

        for (Waypoint supply : supplyPoints) {
            for (Waypoint shelter : shelters) {
                try {
                    int detourTime = estimateDetourTime(origin, supply, shelter, destination);

                    if (detourTime <= MAX_EXTRA_TIME_MINUTES && detourTime < bestDetourTime) {
                        bestOption = new DetourOption(supply, shelter, detourTime);
                        bestDetourTime = detourTime;
                    }
                } catch (Exception e) {
                    System.err.println("Error calculating detour time: " + e.getMessage());
                }
            }
        }
        return bestOption;
    }

    private static int estimateDetourTime(Waypoint start, Waypoint supply, Waypoint shelter, Waypoint end) throws Exception {
        int timeToSupply = WazeClient.getDriveTime(start, supply);
        int timeToShelter = WazeClient.getDriveTime(supply, shelter);
        int timeToEnd = WazeClient.getDriveTime(shelter, end);
        int directTime = WazeClient.getDriveTime(start, end);

        return timeToSupply + timeToShelter + timeToEnd - directTime;
    }
}
