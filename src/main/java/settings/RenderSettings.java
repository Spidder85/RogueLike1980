package settings;

public class RenderSettings {
    private boolean debugRevealAll;

    public RenderSettings() {
        this.debugRevealAll = !GameSettings.ENABLE_FOG_OF_WAR;
    }

    public boolean isDebugRevealAll() {
        return debugRevealAll;
    }

    public void toggleDebugRevealAll() {
        debugRevealAll = !debugRevealAll;
    }
}
