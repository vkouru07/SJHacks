public class PlaceResult {
    private String placeId;
    private String description;

    public PlaceResult(String placeId, String description) {
        this.placeId = placeId;
        this.description = description;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getDescription() {
        return description;
    }
}
