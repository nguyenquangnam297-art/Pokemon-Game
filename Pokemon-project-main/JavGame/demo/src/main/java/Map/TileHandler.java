package Map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import Main.GamePanel;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class TileHandler {
    private static final int SOURCE_TILE_SIZE = 64;
    private static final int WORLD_COLUMNS = 10;
    private static final int WORLD_ROWS = 21;
    private static final int INDOOR_COLUMNS = 10;
    private static final int INDOOR_ROWS = 9;
    private static final int COAST_COLUMNS = 24;
    private static final int COAST_ROWS = 12;

    private static final int WORLD_TILE_START = 0;
    private static final int INDOOR_TILE_START = WORLD_COLUMNS * WORLD_ROWS;
    private static final int COAST_TILE_START = INDOOR_TILE_START + INDOOR_COLUMNS * INDOOR_ROWS;
    private static final int TILE_COUNT = COAST_TILE_START + COAST_COLUMNS * COAST_ROWS;

    // Map tile IDs:
    // 3 rock wall, 11 dirt path, 13 fence, 41 grass, 42 field, 71 decoration,
    // 116 bridge, 224 house door/exit, 228 shop door, 231 house wall, 238 shop wall,
    // 251 indoor floor, 325 water, 328 encounter grass, 331 sand.
    // Each ID points to a row-major 64x64 cell in the sprite atlases.
    public static final int GRASS_TILE_ID = tileId(WORLD_TILE_START, WORLD_COLUMNS, 96, 288);
    public static final int WALL_TILE_ID = tileId(WORLD_TILE_START, WORLD_COLUMNS, 224, 32);
    public static final int TALL_GRASS_TILE_ID = tileId(COAST_TILE_START, COAST_COLUMNS, 256, 64);
    public static final int WATER_TILE_ID = tileId(COAST_TILE_START, COAST_COLUMNS, 64, 64);
    public static final int PATH_TILE_ID = tileId(WORLD_TILE_START, WORLD_COLUMNS, 64, 64);
    public static final int INDOOR_FLOOR_TILE_ID = tileId(INDOOR_TILE_START, INDOOR_COLUMNS, 64, 288);
    public static final int FIELD_TILE_ID = tileId(WORLD_TILE_START, WORLD_COLUMNS, 128, 304);
    public static final int FENCE_TILE_ID = tileId(WORLD_TILE_START, WORLD_COLUMNS, 192, 64);
    public static final int HOUSE_WALL_TILE_ID = tileId(INDOOR_TILE_START, INDOOR_COLUMNS, 80, 128);
    public static final int HOUSE_DOOR_TILE_ID = tileId(INDOOR_TILE_START, INDOOR_COLUMNS, 288, 64);
    public static final int SHOP_WALL_TILE_ID = tileId(INDOOR_TILE_START, INDOOR_COLUMNS, 528, 128);
    public static final int SHOP_DOOR_TILE_ID = tileId(INDOOR_TILE_START, INDOOR_COLUMNS, 544, 64);
    public static final int BRIDGE_TILE_ID = tileId(WORLD_TILE_START, WORLD_COLUMNS, 416, 720);
    public static final int DECORATION_TILE_ID = tileId(WORLD_TILE_START, WORLD_COLUMNS, 64, 448);
    public static final int SAND_TILE_ID = tileId(COAST_TILE_START, COAST_COLUMNS, 448, 64);
    public static final int EXIT_TILE_ID = tileId(INDOOR_TILE_START, INDOOR_COLUMNS, 304, 64);

    GamePanel gp;
    public Tile[] tile;
    public int[][] mapTileNum;
    private String currentMap = "/MapMatrix";

    public TileHandler(GamePanel gp) {
        this.gp = gp;
        tile = new Tile[TILE_COUNT];
        getTiles();
        loadMap(currentMap);
    }

    public void getTiles() {
        // OverWorld.png is intentionally excluded from the tile catalogue.
        // Catalog IDs 0-209: world.png, 10 columns x 21 rows, row-major.
        loadTileSheet("/TileSprite/world.png", WORLD_TILE_START, WORLD_COLUMNS, WORLD_ROWS);
        // Catalog IDs 210-299: indoor.png, 10 columns x 9 rows, row-major.
        loadTileSheet("/TileSprite/indoor.png", INDOOR_TILE_START, INDOOR_COLUMNS, INDOOR_ROWS);
        // Catalog IDs 300-587: coast.png, 24 columns x 12 rows, row-major.
        loadTileSheet("/TileSprite/coast.png", COAST_TILE_START, COAST_COLUMNS, COAST_ROWS);

        tile[WALL_TILE_ID].collision = true;
        tile[WATER_TILE_ID].collision = true;
        tile[FENCE_TILE_ID].collision = true;
        tile[HOUSE_WALL_TILE_ID].collision = true;
        tile[SHOP_WALL_TILE_ID].collision = true;
    }

    private void loadTileSheet(String resourcePath, int firstTileId, int columns, int rows) {
        var resource = getClass().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Missing tile sprite sheet: " + resourcePath);
        }
        Image spriteSheet = new Image(resource.toExternalForm());
        int expectedWidth = columns * SOURCE_TILE_SIZE;
        int expectedHeight = rows * SOURCE_TILE_SIZE;
        if (spriteSheet.isError()
                || spriteSheet.getWidth() != expectedWidth
                || spriteSheet.getHeight() != expectedHeight) {
            throw new IllegalStateException("Tile sprite sheet " + resourcePath + " must be "
                    + expectedWidth + "x" + expectedHeight + " pixels");
        }
        PixelReader pixelReader = spriteSheet.getPixelReader();
        if (pixelReader == null) {
            throw new IllegalStateException("Could not read tile sprite sheet pixels: " + resourcePath);
        }

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int tileId = firstTileId + row * columns + col;
                tile[tileId] = new Tile();
                tile[tileId].image = new WritableImage(
                        pixelReader,
                        col * SOURCE_TILE_SIZE,
                        row * SOURCE_TILE_SIZE,
                        SOURCE_TILE_SIZE,
                        SOURCE_TILE_SIZE);
            }
        }
    }

    private static int tileId(int firstTileId, int columns, int sourceX, int sourceY) {
        return firstTileId + (sourceY / SOURCE_TILE_SIZE) * columns + sourceX / SOURCE_TILE_SIZE;
    }

    public void loadMap(String mapPath) {
        String resourcePath = mapPath.startsWith("/") ? mapPath : "/" + mapPath;
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalArgumentException("Missing map resource: " + resourcePath);
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
                List<String> lines = new ArrayList<>();
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        lines.add(line.trim());
                    }
                }

                if (lines.isEmpty()) {
                    throw new IllegalArgumentException("Map resource is empty: " + resourcePath);
                }

                int rows = lines.size();
                int cols = lines.get(0).split("\\s+").length;
                int[][] loadedMap = new int[rows][cols];

                for (int row = 0; row < rows; row++) {
                    String[] values = lines.get(row).split("\\s+");
                    if (values.length != cols) {
                        throw new IllegalArgumentException("Map row " + row + " has " + values.length
                                + " columns; expected " + cols + " in " + resourcePath);
                    }
                    for (int col = 0; col < values.length; col++) {
                        int tileId;
                        try {
                            tileId = Integer.parseInt(values[col]);
                        } catch (NumberFormatException exception) {
                            throw new IllegalArgumentException("Invalid tile ID at row " + row + ", column "
                                    + col + " in " + resourcePath + ": " + values[col], exception);
                        }
                        if (tileId < 0 || tileId >= tile.length || tile[tileId] == null) {
                            throw new IllegalArgumentException("Tile ID " + tileId + " at row " + row
                                    + ", column " + col + " is unavailable in " + resourcePath);
                        }
                        loadedMap[row][col] = tileId;
                    }
                }

                mapTileNum = loadedMap;
                currentMap = resourcePath;
                gp.worldWidth = cols * gp.tileSize;
                gp.worldHeight = rows * gp.tileSize;
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read map resource: " + resourcePath, exception);
        }
    }

    public String getCurrentMap() {
        return currentMap;
    }

    public void draw(GraphicsContext graphics) {
        if (mapTileNum == null) {
            return;
        }

        for (int row = 0; row < mapTileNum.length; row++) {
            for (int col = 0; col < mapTileNum[row].length; col++) {
                int tileNum = mapTileNum[row][col];

                //tọa độ thực tế của ô tile trong game
                int worldX = col *  gp.tileSize ;
                int worldY = row *  gp.tileSize ;

                //những khung hình map sẽ vẽ
                int screenX = worldX - gp.player.x + gp.screenWidth/2 ;
                int screenY = worldY - gp.player.y + gp.screenHeight/2 ;

                //on draw in the frame
                if (screenX + gp.tileSize > 0 &&
                        screenX - gp.tileSize < gp.screenWidth &&
                         screenY + gp.tileSize > 0 &&
                        screenY - gp.tileSize < gp.screenHeight) {

                    if (tileNum >=0 && tileNum < tile.length && tile[tileNum] != null) {
                            graphics.drawImage(tile[tileNum].image, screenX, screenY, gp.tileSize, gp.tileSize);
                    }
                }
            }
        }
    }
}
