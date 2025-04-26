import java.util.List;

public class RouteFinder {
    private static final int MAX_EXTRA_TIME_MINUTES = 5;

    public static DetourOption findBestDetour(Waypoint origin, Waypoint destination, List<Waypoint> supplyPoints, List<Waypoint> shelters) {
        DetourOption bestOption = null;
        int bestDetourTime = Integer.MAX_VALUE;

        for (Waypoint supply : supplyPoints) {
            for (Waypoint shelter : shelters) {
                int detourTime = estimateDetourTime(origin, supply, shelter, destination);

                if (detourTime <= MAX_EXTRA_TIME_MINUTES && detourTime < bestDetourTime) {
                    bestOption = new DetourOption(supply, shelter, detourTime);
                    bestDetourTime = detourTime;
                }
            }
        }
        return bestOption;
    }

    private static int estimateDetourTime(Waypoint start, Waypoint supply, Waypoint shelter, Waypoint end) {
        // Here you'd call Google Maps Directions API to get real drive times.
        // For now, assume mock distances or simple haversine formula to get basic "time".
        return mockDriveTime(start, supply) + mockDriveTime(supply, shelter) + mockDriveTime(shelter, end) - mockDriveTime(start, end);
    }

    private static int mockDriveTime(Waypoint a, Waypoint b) {
        double distance = Math.sqrt(Math.pow(a.getLatitude() - b.getLatitude(), 2) + Math.pow(a.getLongitude() - b.getLongitude(), 2));
        return (int)(distance * 60); // mock: 1 degree ≈ 60 minutes of driving
    }
}
