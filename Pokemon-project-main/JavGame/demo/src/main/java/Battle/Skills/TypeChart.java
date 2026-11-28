package Battle.Skills;

public class TypeChart {
    public static int getEffectiveness(String atkType, String defType) {
        if (atkType == null || defType == null) {
            return 0;
        }

        atkType = atkType.trim().toUpperCase();
        defType = defType.trim().toUpperCase();

        // Siêu hiệu quả
        if ((atkType.equals("WATER") && (defType.equals("FIRE") || defType.equals("GROUND") || defType.equals("ROCK"))) ||
            (atkType.equals("FIRE") && (defType.equals("GRASS") || defType.equals("ICE"))) ||
            (atkType.equals("GRASS") && (defType.equals("WATER") || defType.equals("GROUND") || defType.equals("ROCK"))) ||
            (atkType.equals("ELECTRIC") && defType.equals("WATER")) ||
            (atkType.equals("POISON") && defType.equals("GRASS")) ||
            (atkType.equals("GROUND") && (defType.equals("FIRE") || defType.equals("ELECTRIC") || defType.equals("POISON") || defType.equals("ROCK"))) ||
            (atkType.equals("ICE") && (defType.equals("GRASS") || defType.equals("GROUND") || defType.equals("DRAGON"))) ||
            (atkType.equals("DRAGON") && defType.equals("DRAGON")) ||
            (atkType.equals("PSYCHIC") && defType.equals("POISON"))) {
            return 1;
        }

        // Không mấy hiệu quả
        if ((atkType.equals("FIRE") && (defType.equals("WATER") || defType.equals("FIRE") || defType.equals("ROCK") || defType.equals("DRAGON"))) ||
            (atkType.equals("WATER") && (defType.equals("GRASS") || defType.equals("WATER") || defType.equals("DRAGON"))) ||
            (atkType.equals("GRASS") && (defType.equals("FIRE") || defType.equals("GRASS") || defType.equals("POISON") || defType.equals("DRAGON"))) ||
            (atkType.equals("ELECTRIC") && (defType.equals("GRASS") || defType.equals("ELECTRIC") || defType.equals("GROUND") || defType.equals("DRAGON"))) ||
            (atkType.equals("POISON") && (defType.equals("POISON") || defType.equals("GROUND") || defType.equals("ROCK") || defType.equals("GHOST"))) ||
            (atkType.equals("GROUND") && defType.equals("GRASS")) ||
            (atkType.equals("ICE") && (defType.equals("FIRE") || defType.equals("WATER") || defType.equals("ICE"))) ||
            (atkType.equals("NORMAL") && defType.equals("ROCK")) ||
            (atkType.equals("PSYCHIC") && (defType.equals("PSYCHIC") || defType.equals("DARK")))) {
            return -1;
        }

        return 0;
    }
}