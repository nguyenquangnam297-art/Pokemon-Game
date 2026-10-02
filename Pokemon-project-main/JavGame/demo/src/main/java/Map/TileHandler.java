package Map;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import Main.GamePanel;

public class TileHandler {
    GamePanel gp;
    Tile[] tile;
    public int[][] mapTileNum;

    public TileHandler(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[10];
        getTiles();
        loadMap();
    }

    public void getTiles() {
        try {
            BufferedImage bigCanvas = ImageIO.read(getClass().getResourceAsStream("/TileSprite/OverWorld.png"));
            tile[0] = new Tile();
            tile[0].image = bigCanvas.getSubimage(0, 0, gp.tileSize /3 , gp.tileSize / 3);
            tile[1] = new Tile();
            tile[1].image = bigCanvas.getSubimage(0, 1 * gp.tileSize / 3, gp.tileSize / 3, gp.tileSize / 3);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMap() {
        try (InputStream is = getClass().getResourceAsStream("/MapMatrix");
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            List<String> lines = new ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line.trim());
                }
            }

            int rows = lines.size();
            int cols = lines.get(0).split("\\s+").length;
            mapTileNum = new int[rows][cols];

            for (int row = 0; row < rows; row++) {
                String[] values = lines.get(row).split("\\s+");
                for (int col = 0; col < values.length; col++) {
                    mapTileNum[row][col] = Integer.parseInt(values[col]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics2D g2) {
        if (mapTileNum == null) {
            return;
        }

        for (int row = 0; row < mapTileNum.length; row++) {
            for (int col = 0; col < mapTileNum[row].length; col++) {
                int tileNum = mapTileNum[row][col];
                if (tileNum >= 0 && tileNum < tile.length && tile[tileNum] != null) {
                    g2.drawImage(tile[tileNum].image, col * gp.tileSize, row * gp.tileSize, gp.tileSize, gp.tileSize, null);
                }
            }
        }
    }
}
