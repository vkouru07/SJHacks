import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class DataLoader {
    public static List<Waypoint> loadSupplyPoints() {
        return new Gson().fromJson(
                new InputStreamReader(DataLoader.class.getResourceAsStream("/supply_points.json")),
                new TypeToken<List<Waypoint>>() {}.getType()
        );
    }

    public static List<Waypoint> loadShelters() {
        InputStreamReader reader = new InputStreamReader(DataLoader.class.getResourceAsStream("/shelters.json"));
        List<Waypoint> shelters = new Gson().fromJson(reader, new TypeToken<List<Waypoint>>() {}.getType());
        return shelters != null ? shelters : Collections.emptyList();
    }
}
