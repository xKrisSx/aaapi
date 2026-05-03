package pl.notkris.example.utils;

import org.bukkit.event.Listener;

import java.util.HashMap;

public class WorldUtils {

    private static final HashMap<Listener, Boolean> physicListeners = new HashMap<>();
    public static HashMap<Listener, Boolean> getPhysicListeners() {
        return physicListeners;
    }
    public static boolean checkPhysicListener(Listener listener) {
        return physicListeners.getOrDefault(listener, false);
    }
    public static void addPhysicListener(Listener listener, boolean value) {
        physicListeners.put(listener, value);
    }
    public static void togglePhysicListener(Listener listener) {
        physicListeners.put(listener, !physicListeners.getOrDefault(listener, false));
    }
}
