package Battle.Skills;

public class TypeChart {
    public static int getEffectiveness(String atkType, String defType) {
        atkType = atkType.toUpperCase(); defType = defType.toUpperCase();
        if ((atkType.equals("WATER") && defType.equals("FIRE")) ||
            (atkType.equals("FIRE") && defType.equals("GRASS")) ||
            (atkType.equals("GRASS") && defType.equals("WATER")) ||
            (atkType.equals("ELECTRIC") && defType.equals("WATER"))) return 1;

        if ((atkType.equals("FIRE") && defType.equals("WATER")) ||
            (atkType.equals("GRASS") && defType.equals("FIRE")) ||
            (atkType.equals("WATER") && defType.equals("GRASS")) ||
            (atkType.equals("ELECTRIC") && defType.equals("GRASS"))) return -1;
        return 0;
    }
}