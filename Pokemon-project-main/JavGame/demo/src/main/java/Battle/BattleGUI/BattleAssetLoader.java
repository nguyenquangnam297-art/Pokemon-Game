package Battle.BattleGUI;

import java.io.File;
import java.io.InputStream;
import javafx.scene.image.Image;

public class BattleAssetLoader {

    public static Image loadImage(String... candidatePaths) {
        if (candidatePaths == null) {
            return null;
        }

        for (int i = 0; i < candidatePaths.length; i++) {
            String path = candidatePaths[i];
            if (path == null) {
                continue;
            }

            try {
                InputStream stream = BattleAssetLoader.class.getResourceAsStream(path);
                if (stream != null) {
                    Image loaded = new Image(stream);
                    if (!loaded.isError() && loaded.getWidth() > 0) {
                        return loaded;
                    }
                }
            } catch (Exception e) {
            }

            try {
                String cleanPath = path;
                if (cleanPath.startsWith("/")) {
                    cleanPath = cleanPath.substring(1);
                }
                File localFile = new File(cleanPath);
                if (!localFile.exists()) {
                    localFile = new File("graphics/attacks/" + new File(cleanPath).getName());
                }
                if (localFile.exists()) {
                    Image loadedFile = new Image(localFile.toURI().toString());
                    if (!loadedFile.isError() && loadedFile.getWidth() > 0) {
                        return loadedFile;
                    }
                }
            } catch (Exception e) {

            }
        }

        return null;
    }

    public static String[] buildAttackCandidatePaths(String fileName) {
        return new String[] {
            "/AttackSprites/" + fileName,
            "/attacks/" + fileName,
            "/" + fileName,
            "graphics/attacks/" + fileName
        };
    }

    public static Image loadAttackSpriteByName(String fileName) {
        Image img = loadImage(buildAttackCandidatePaths(fileName));
        if (img != null) {
            return img;
        }
        if (!"fire.png".equals(fileName)) {
            return loadImage(buildAttackCandidatePaths("fire.png"));
        }
        return null;
    }
}