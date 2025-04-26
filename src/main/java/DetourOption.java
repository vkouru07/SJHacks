public class DetourOption {
    private Waypoint supplyPoint;
    private Waypoint shelterPoint;
    private int extraTimeMinutes;

    public DetourOption(Waypoint supplyPoint, Waypoint shelterPoint, int extraTimeMinutes) {
        this.supplyPoint = supplyPoint;
        this.shelterPoint = shelterPoint;
        this.extraTimeMinutes = extraTimeMinutes;
    }

    public Waypoint getSupplyPoint() { return supplyPoint; }
    public Waypoint getShelterPoint() { return shelterPoint; }
    public int getExtraTimeMinutes() { return extraTimeMinutes; }
}
