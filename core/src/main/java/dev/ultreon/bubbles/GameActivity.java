package dev.ultreon.bubbles;

public class GameActivity {
    public static final GameActivity MENUS = new GameActivity("MENUS");
    public static final GameActivity LOADING = new GameActivity("LOADING");
    public static final GameActivity NOWHERE = new GameActivity("NOWHERE");
    public static final GameActivity PLAYING = new GameActivity("PLAYING");

    private final String name;

    private GameActivity(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
