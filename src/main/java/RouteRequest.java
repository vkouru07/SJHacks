public class RouteRequest {
    private Waypoint origin;
    private Waypoint destination;

    public RouteRequest(Waypoint origin, Waypoint destination) {
        this.origin = origin;
        this.destination = destination;
    }
    public Waypoint getOrigin() {
        return origin;
    }
    public Waypoint getDestination() {
        return destination;
    }
}
