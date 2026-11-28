package Battle;

public enum BattleEnvironment {
    FOREST("forest.png", "#2d4c2b"),
    ICE("ice.png", "#b3e5fc"),
    SAND("sand.png", "#e6c98a");

    private final String fileName;
    private final String fallbackHexColor;

    BattleEnvironment(String fileName, String fallbackHexColor) {
        this.fileName = fileName;
        this.fallbackHexColor = fallbackHexColor;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFallbackHexColor() {
        return fallbackHexColor;
    }
}