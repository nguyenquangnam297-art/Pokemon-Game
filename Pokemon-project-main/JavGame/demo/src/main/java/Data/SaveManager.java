package Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SaveManager {
    private static final String SAVE_PATH = "save1.dat";

    public static void save(PlayerData data) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_PATH))) {
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PlayerData load() {
        File file = new File(SAVE_PATH);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (PlayerData) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}