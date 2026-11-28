package Battle.BattleGUI;

import Battle.Skills.Skill;
import javafx.scene.paint.Color;

public class ElementStyleConfig {

    public static Color[] getElementColors(Skill skill) {
        String type = normalizeType(skill);

        if (type.equals("FIRE")) {
            return new Color[] { Color.web("#FFF59D"), Color.web("#FF3D00") }; // Lõi vàng sáng - Hào quang đỏ cam
        } else if (type.equals("WATER")) {
            return new Color[] { Color.web("#E0F7FA"), Color.web("#0091EA") }; // Lõi xanh băng - Hào quang xanh dương
        } else if (type.equals("GRASS")) {
            return new Color[] { Color.web("#F1F8E9"), Color.web("#2E7D32") }; // Lõi xanh non - Hào quang xanh lá
        } else if (type.equals("ICE")) {
            return new Color[] { Color.WHITE, Color.web("#00E5FF") };          // Lõi trắng - Hào quang xanh ngọc
        } else if (type.equals("ELECTRIC")) {
            return new Color[] { Color.web("#FFFDE7"), Color.web("#FFD600") }; // Lõi vàng nhạt - Hào quang vàng điện
        } else if (type.equals("POISON") || type.equals("PSYCHIC") || type.equals("GHOST")) {
            return new Color[] { Color.web("#F3E5F5"), Color.web("#AA00FF") }; // Lõi tím nhạt - Hào quang tím đậm
        } else if (type.equals("DRAGON")) {
            return new Color[] { Color.web("#E8EAF6"), Color.web("#3D5AFE") }; // Lõi trắng xanh - Hào quang chàm
        } else if (type.equals("GROUND") || type.equals("ROCK")) {
            return new Color[] { Color.web("#FFF8E1"), Color.web("#8D6E63") }; // Lõi kem - Hào quang nâu đất
        }

        return new Color[] { Color.WHITE, Color.web("#78909C") };
    }

    public static String getAttackSpriteFileName(Skill skill) {
        String type = normalizeType(skill);

        if (type.equals("FIRE")) {
            return "fire.png";
        } else if (type.equals("WATER")) {
            return "splash.png";
        } else if (type.equals("GRASS")) {
            return "green.png";
        } else if (type.equals("ICE")) {
            return "ice.png";
        }

        return "explosion.png";
    }

    public static double getElementHueShift(Skill skill) {
        String type = normalizeType(skill);

        if (type.equals("ELECTRIC")) {
            return 0.14;  // Dịch từ cam sang vàng điện rực rỡ
        } else if (type.equals("POISON") || type.equals("PSYCHIC") || type.equals("GHOST")) {
            return -0.32; // Dịch từ cam/vàng sang tím độc / siêu linh
        } else if (type.equals("DRAGON")) {
            return -0.55; // Dịch sang xanh chàm
        } else if (type.equals("GROUND") || type.equals("ROCK")) {
            return 0.06;  // Tông nâu vàng ấm
        }

        return 0.0;
    }

    private static String normalizeType(Skill skill) {
        if (skill != null && skill.getType() != null) {
            return skill.getType().trim().toUpperCase();
        }
        return "NORMAL";
    }
}