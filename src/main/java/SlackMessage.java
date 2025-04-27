public class SlackMessage {
    private String text;

    public SlackMessage(DetourOption option) {
        this.text = String.format(
            "Pickup available! Stop by (%.4f, %.4f) and drop at (%.4f, %.4f). Adds about %d minutes.",
            option.getSupplyPoint().getLatitude(), option.getSupplyPoint().getLongitude(),
            option.getShelterPoint().getLatitude(), option.getShelterPoint().getLongitude(),
            option.getExtraTimeMinutes()
        );
    }

    public String getText() {
        return text;
    }
}
