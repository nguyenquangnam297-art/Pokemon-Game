package Map;

import java.util.ArrayList;
import java.util.List;

import Main.GamePanel;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public final class Objects {
    private static final String MAIN_MAP = "/MapMatrix";
    private static final int TOWN_HOUSE_X = 18;
    private static final int TOWN_HOUSE_Y = 8;
    private static final int TOWN_SHOP_X = 24;
    private static final int TOWN_SHOP_Y = 17;
    private static final int TOWN_HOSPITAL_X = 9;
    private static final int TOWN_HOSPITAL_Y = 17;

    private final GamePanel gp;
    private final List<MapObject> objects = new ArrayList<>();
    private final List<TransitionZone> transitionZones = new ArrayList<>();
    private final List<TreeBorder> treeBorders = new ArrayList<>();

    public Objects(GamePanel gp) {
        this.gp = gp;
        loadMainMapObjects();
        loadInteriorExits();
        loadTreeBorders();
    }

    private void loadMainMapObjects() {
        addBuilding("house_large.png", TOWN_HOUSE_X, TOWN_HOUSE_Y,
                box(180, 447, 88, 2),
                "/MapMatrixHouse",
                centeredPlayerX(6 * gp.tileSize + gp.tileSize / 2),
                6 * gp.tileSize,
                box(16, 312, 416, 38),
                box(16, 350, 164, 98),
                box(268, 350, 164, 98));
        addBuilding("house_small.png", TOWN_SHOP_X, TOWN_SHOP_Y,
                box(52, 319, 88, 2),
                "/MapMatrixShop",
                centeredPlayerX(5 * gp.tileSize + gp.tileSize / 2),
                4 * gp.tileSize,
                box(16, 196, 288, 40),
                box(16, 236, 36, 84),
                box(140, 236, 164, 84));
        addBuilding("hospital.png", TOWN_HOSPITAL_X, TOWN_HOSPITAL_Y,
                box(120, 383, 144, 2),
                "/MapMatrixHospital",
                centeredPlayerX(6 * gp.tileSize + gp.tileSize / 2),
                6 * gp.tileSize,
                box(16, 260, 352, 30),
                box(16, 290, 104, 93),
                box(264, 290, 104, 93));
        addObjectAtTile(
                "arena_plant.png",
                MAIN_MAP,
                3,
                0,
                box(16, 300, 416, 40),
                box(16, 340, 160, 60),
                box(272, 340, 160, 60),
                box(92, 400, 104, 48),
                box(252, 400, 104, 48));
        addObjectAtTile("palm.png", MAIN_MAP, 34, 11, box(46, 148, 38, 40));
        addObjectAtTile("palm_alt.png", MAIN_MAP, 36, 14, box(46, 148, 38, 40));
        addObjectAtTile("palm_small.png", MAIN_MAP, 34, 23, box(20, 96, 24, 30));
        addObjectAtTile("sandrock1.png", MAIN_MAP, 32, 7, box(8, 28, 44, 30));
        addObjectAtTile("sandrock2.png", MAIN_MAP, 36, 25, box(6, 28, 44, 30));

        addTransition(
                MAIN_MAP,
                0,
                10,
                1,
                5,
                "/MapMatrixForest",
                2,
                9);
        addTransition(MAIN_MAP, 7, 29, 4, 1, "/MapMatrixRuins", 3, 9);

        addObjectAtTile("green_tree.png", "/MapMatrixForest", 3, 2, box(45, 144, 38, 42));
        addObjectAtTile("green_tree_bushy.png", "/MapMatrixForest", 10, 3, box(43, 144, 42, 44));
        addObjectAtTile("green_tree_small.png", "/MapMatrixForest", 15, 2, box(20, 96, 24, 30));
        addObjectAtTile("teal_tree.png", "/MapMatrixForest", 6, 12, box(45, 144, 38, 42));
        addObjectAtTile("teal_tree_bushy.png", "/MapMatrixForest", 16, 11, box(43, 144, 42, 44));
        addObjectAtTile("teal_tree_small.png", "/MapMatrixForest", 4, 15, box(20, 96, 24, 30));
        addTransition("/MapMatrixForest", 0, 8, 2, 2, MAIN_MAP, 2, 12);

        addObjectAtTile("gate_top.png", "/MapMatrixRuins", 5, 4);
        addObjectAtTile("ruin_gate.png", "/MapMatrixRuins", 5, 5,
                box(4, 82, 52, 110), box(136, 82, 52, 110));
        addObjectAtTile("gate_pillar.png", "/MapMatrixRuins", 10, 6, box(12, 90, 40, 38));
        addObjectAtTile("ruin_pillar.png", "/MapMatrixRuins", 3, 10, box(16, 150, 32, 42));
        addObjectAtTile("ruin_pillar_broke.png", "/MapMatrixRuins", 13, 11, box(10, 70, 44, 30));
        addObjectAtTile("ruin_pillar_broke_alt.png", "/MapMatrixRuins", 8, 13, box(12, 90, 40, 32));
        addTransition("/MapMatrixRuins", 0, 8, 2, 2, MAIN_MAP, 7, 24);
    }

    private void loadTreeBorders() {
        addTreeBorder(
                MAIN_MAP,
                gp.tileH.mapTileNum[0].length,
                3,
                10,
                32,
                43,
                gp.tileH.mapTileNum.length,
                true,
                true,
                true,
                false,
                10,
                15,
                7,
                11);
        addTreeBorder(
                "/MapMatrixForest",
                22,
                -1,
                -1,
                22,
                22,
                18,
                true,
                true,
                true,
                true,
                8,
                10,
                -1,
                -1);
        addTreeBorder(
                "/MapMatrixRuins",
                20,
                -1,
                -1,
                20,
                20,
                18,
                true,
                true,
                true,
                true,
                8,
                10,
                -1,
                -1);
    }

    private void addTreeBorder(
            String mapId,
            int columns,
            int topExitStartColumn,
            int topExitEndColumn,
            int topColumns,
            int bottomColumns,
            int rows,
            boolean top,
            boolean bottom,
            boolean left,
            boolean right,
            int leftExitStartRow,
            int leftExitEndRow,
            int bottomExitStartColumn,
            int bottomExitEndColumn
    ) {
        Image treeImage = loadObjectImage("green_tree_small.png");
        int tileSize = gp.tileSize;
        int mapWidth = columns * tileSize;
        int mapHeight = rows * tileSize;
        List<Rectangle2D> collisionBoxes = new ArrayList<>();
        List<Rectangle2D> treePositions = new ArrayList<>();

        if (top) {
            double trunkHeight = tileSize / 2.0;
            double trunkY = Math.max(0, treeImage.getHeight() - trunkHeight);
            if (topExitStartColumn >= 0 && topExitEndColumn > topExitStartColumn) {
                collisionBoxes.add(box(
                        0,
                        trunkY,
                        topExitStartColumn * tileSize,
                        trunkHeight));
                collisionBoxes.add(box(
                        topExitEndColumn * tileSize,
                        trunkY,
                        (topColumns - topExitEndColumn) * tileSize,
                        trunkHeight));
            } else {
                collisionBoxes.add(box(0, trunkY, topColumns * tileSize, trunkHeight));
            }
            for (int column = 0; column < topColumns; column++) {
                if (column >= topExitStartColumn && column < topExitEndColumn) {
                    continue;
                }
                treePositions.add(box(column * tileSize, 0, treeImage.getWidth(), treeImage.getHeight()));
            }
        }
        if (bottom) {
            if (bottomExitStartColumn >= 0 && bottomExitEndColumn > bottomExitStartColumn) {
                collisionBoxes.add(box(
                        0,
                        mapHeight - tileSize,
                        bottomExitStartColumn * tileSize,
                        tileSize));
                collisionBoxes.add(box(
                        bottomExitEndColumn * tileSize,
                        mapHeight - tileSize,
                        (bottomColumns - bottomExitEndColumn) * tileSize,
                        tileSize));
            } else {
                collisionBoxes.add(box(0, mapHeight - tileSize, bottomColumns * tileSize, tileSize));
            }
            for (int column = 0; column < bottomColumns; column++) {
                if (column >= bottomExitStartColumn && column < bottomExitEndColumn) {
                    continue;
                }
                treePositions.add(box(
                        column * tileSize,
                        mapHeight - treeImage.getHeight(),
                        treeImage.getWidth(),
                        treeImage.getHeight()));
            }
        }
        if (left) {
            if (leftExitStartRow >= 0 && leftExitEndRow > leftExitStartRow) {
                collisionBoxes.add(box(0, 0, tileSize, leftExitStartRow * tileSize));
                collisionBoxes.add(box(
                        0,
                        leftExitEndRow * tileSize,
                        tileSize,
                        mapHeight - leftExitEndRow * tileSize));
            } else {
                collisionBoxes.add(box(0, 0, tileSize, mapHeight));
            }
            for (int row = 0; row < rows; row++) {
                int treeY = row * tileSize;
                if (leftExitStartRow < 0
                        || row < leftExitStartRow
                        || row >= leftExitEndRow) {
                    treePositions.add(box(0, treeY, treeImage.getWidth(), treeImage.getHeight()));
                }
            }
        }
        if (right) {
            collisionBoxes.add(box(mapWidth - tileSize, 0, tileSize, mapHeight));
            for (int row = 0; row < rows; row++) {
                treePositions.add(box(
                        mapWidth - treeImage.getWidth(),
                        row * tileSize,
                        treeImage.getWidth(),
                        treeImage.getHeight()));
            }
        }

        treeBorders.add(new TreeBorder(mapId, treeImage, collisionBoxes, treePositions));
    }

    private void loadInteriorExits() {
        addExit("/MapMatrixHouse", 5, 9, 2,
                centeredPlayerX(TOWN_HOUSE_X * gp.tileSize + 224),
                TOWN_HOUSE_Y * gp.tileSize + 448);
        addExit("/MapMatrixShop", 4, 7, 2,
                centeredPlayerX(TOWN_SHOP_X * gp.tileSize + 96),
                TOWN_SHOP_Y * gp.tileSize + 320);
        addExit("/MapMatrixHospital", 5, 9, 2,
                centeredPlayerX(TOWN_HOSPITAL_X * gp.tileSize + 192),
                TOWN_HOSPITAL_Y * gp.tileSize + 384);
    }

    private void addBuilding(
            String fileName,
            int tileColumn,
            int tileRow,
            Rectangle2D door,
            String destinationMap,
            int destinationX,
            int destinationY,
            Rectangle2D... collisionBoxes
    ) {
        int worldX = tileColumn * gp.tileSize;
        int worldY = tileRow * gp.tileSize;
        addObjectAtWorld(
                fileName,
                MAIN_MAP,
                worldX,
                worldY,
                collisionBoxes);
        if (door != null) {
            transitionZones.add(new TransitionZone(
                    MAIN_MAP,
                    new Rectangle2D(
                            worldX + door.getMinX(),
                            worldY + door.getMinY(),
                            door.getWidth(),
                            door.getHeight()),
                    destinationMap,
                    destinationX,
                    destinationY));
        }
    }

    private MapObject addObjectAtTile(
            String fileName,
            String mapId,
            int tileColumn,
            int tileRow,
            Rectangle2D... collisionBoxes
    ) {
        return addObjectAtWorld(fileName, mapId,
                tileColumn * gp.tileSize, tileRow * gp.tileSize, collisionBoxes);
    }

    private MapObject addObjectAtWorld(
            String fileName,
            String mapId,
            int worldX,
            int worldY,
            Rectangle2D... collisionBoxes
    ) {
        Image image = loadObjectImage(fileName);
        for (Rectangle2D collisionBox : collisionBoxes) {
            if (collisionBox != null
                    && (collisionBox.getMinX() < 0 || collisionBox.getMinY() < 0
                    || collisionBox.getMaxX() > image.getWidth()
                    || collisionBox.getMaxY() > image.getHeight())) {
                throw new IllegalArgumentException("Collision box is outside sprite: /objects/" + fileName);
            }
        }

        MapObject object = new MapObject(
                image, mapId, worldX, worldY, List.of(collisionBoxes));
        objects.add(object);
        return object;
    }

    private Image loadObjectImage(String fileName) {
        String resourcePath = "/objects/" + fileName;
        var resource = getClass().getResource(resourcePath);
        if (resource == null) {
            throw new IllegalStateException("Missing map object sprite: " + resourcePath);
        }

        Image image = new Image(resource.toExternalForm());
        if (image.isError() || image.getWidth() <= 0 || image.getHeight() <= 0) {
            throw new IllegalStateException("Could not load map object sprite: " + resourcePath);
        }
        return image;
    }

    private void addExit(String mapId, int tileColumn, int tileRow, int tileWidth, int returnX, int returnY) {
        transitionZones.add(new TransitionZone(
                mapId,
                new Rectangle2D(
                        tileColumn * gp.tileSize,
                        tileRow * gp.tileSize,
                        tileWidth * gp.tileSize,
                        gp.tileSize),
                MAIN_MAP,
                returnX,
                returnY));
    }

    private void addTransition(
            String mapId,
            int tileColumn,
            int tileRow,
            int tileWidth,
            int tileHeight,
            String destinationMap,
            int destinationColumn,
            int destinationRow
    ) {
        transitionZones.add(new TransitionZone(
                mapId,
                new Rectangle2D(
                        tileColumn * gp.tileSize,
                        tileRow * gp.tileSize,
                        tileWidth * gp.tileSize,
                        tileHeight * gp.tileSize),
                destinationMap,
                destinationColumn * gp.tileSize,
                destinationRow * gp.tileSize));
    }

    private int centeredPlayerX(int worldCenterX) {
        return worldCenterX - gp.tileSize / 2;
    }

    private static Rectangle2D box(double x, double y, double width, double height) {
        return new Rectangle2D(x, y, width, height);
    }

    public boolean collidesWith(Rectangle2D worldBounds) {
        String activeMap = gp.tileH.getCurrentMap();
        for (MapObject object : objects) {
            if (!object.mapId.equals(activeMap) || !object.collision) {
                continue;
            }
            for (Rectangle2D localBox : object.collisionBoxes) {
                Rectangle2D worldBox = new Rectangle2D(
                        object.worldX + localBox.getMinX(),
                        object.worldY + localBox.getMinY(),
                        localBox.getWidth(),
                        localBox.getHeight());
                if (worldBounds.intersects(worldBox)) {
                    return true;
                }
            }
        }
        for (TreeBorder border : treeBorders) {
            if (!border.mapId.equals(activeMap) || !border.collision) {
                continue;
            }
            for (Rectangle2D borderBounds : border.collisionBoxes) {
                if (worldBounds.intersects(borderBounds)) {
                    return true;
                }
            }
        }
        return false;
    }

    public MapTransition getTransitionAt(Rectangle2D playerBounds) {
        String activeMap = gp.tileH.getCurrentMap();
        for (TransitionZone zone : transitionZones) {
            if (zone.mapId.equals(activeMap) && playerBounds.intersects(zone.bounds)) {
                return new MapTransition(zone.destinationMap, zone.destinationX, zone.destinationY);
            }
        }
        return null;
    }

    public void drawTransitionZones(GraphicsContext graphics) {
        String activeMap = gp.tileH.getCurrentMap();
        double pulse = 0.28 + 0.12 * (0.5 + 0.5 * Math.sin(System.nanoTime() / 450_000_000.0));

        graphics.save();
        for (TransitionZone zone : transitionZones) {
            if (!zone.mapId.equals(activeMap)) {
                continue;
            }

            double screenX = zone.bounds.getMinX() - gp.player.x + gp.screenWidth / 2.0;
            double screenY = zone.bounds.getMinY() - gp.player.y + gp.screenHeight / 2.0;
            double width = zone.bounds.getWidth();
            double height = zone.bounds.getHeight();

            graphics.setGlobalAlpha(pulse);
            graphics.setFill(javafx.scene.paint.Color.BLACK);
            graphics.fillRect(screenX, screenY, width, height);
            graphics.setGlobalAlpha(Math.min(0.75, pulse + 0.3));
            graphics.setStroke(javafx.scene.paint.Color.BLACK);
            graphics.setLineWidth(2);
            graphics.strokeRect(screenX + 1, screenY + 1, Math.max(0, width - 2), Math.max(0, height - 2));
        }
        graphics.restore();
    }

    public void drawBehind(GraphicsContext graphics, int playerFeetWorldY) {
        draw(graphics, playerFeetWorldY, true);
    }

    public void drawInFront(GraphicsContext graphics, int playerFeetWorldY) {
        draw(graphics, playerFeetWorldY, false);
    }

    private void draw(GraphicsContext graphics, int playerFeetWorldY, boolean behindPlayer) {
        String activeMap = gp.tileH.getCurrentMap();
        for (MapObject object : objects) {
            if (!object.mapId.equals(activeMap)) {
                continue;
            }

            int objectFeetWorldY = object.worldY + (int) object.image.getHeight();
            if ((objectFeetWorldY < playerFeetWorldY) != behindPlayer) {
                continue;
            }

            int screenX = object.worldX - gp.player.x + gp.screenWidth / 2;
            int screenY = object.worldY - gp.player.y + gp.screenHeight / 2;
            int width = (int) object.image.getWidth();
            int height = (int) object.image.getHeight();
            if (screenX + width > 0 && screenX < gp.screenWidth
                    && screenY + height > 0 && screenY < gp.screenHeight) {
                graphics.drawImage(object.image, screenX, screenY);
            }
        }

        for (TreeBorder border : treeBorders) {
            if (!border.mapId.equals(activeMap)) {
                continue;
            }
            for (Rectangle2D treePosition : border.treePositions) {
                int treeFeetWorldY = (int) treePosition.getMaxY();
                if ((treeFeetWorldY < playerFeetWorldY) != behindPlayer) {
                    continue;
                }
                int screenX = (int) treePosition.getMinX() - gp.player.x + gp.screenWidth / 2;
                int screenY = (int) treePosition.getMinY() - gp.player.y + gp.screenHeight / 2;
                graphics.drawImage(border.treeImage, screenX, screenY);
            }
        }
    }

    private static final class TreeBorder {
        private final String mapId;
        private final Image treeImage;
        private final List<Rectangle2D> collisionBoxes;
        private final List<Rectangle2D> treePositions;
        private final boolean collision;

        private TreeBorder(
                String mapId,
                Image treeImage,
                List<Rectangle2D> collisionBoxes,
                List<Rectangle2D> treePositions
        ) {
            this.mapId = mapId;
            this.treeImage = treeImage;
            this.collisionBoxes = collisionBoxes;
            this.treePositions = treePositions;
            this.collision = !collisionBoxes.isEmpty();
        }
    }

    private static final class MapObject {
        private final Image image;
        private final String mapId;
        private final int worldX;
        private final int worldY;
        private final boolean collision;
        private final List<Rectangle2D> collisionBoxes;

        private MapObject(
                Image image,
                String mapId,
                int worldX,
                int worldY,
                List<Rectangle2D> collisionBoxes
        ) {
            this.image = image;
            this.mapId = mapId;
            this.worldX = worldX;
            this.worldY = worldY;
            this.collisionBoxes = collisionBoxes;
            this.collision = !collisionBoxes.isEmpty();
        }
    }

    private static final class TransitionZone {
        private final String mapId;
        private final Rectangle2D bounds;
        private final String destinationMap;
        private final int destinationX;
        private final int destinationY;

        private TransitionZone(
                String mapId,
                Rectangle2D bounds,
                String destinationMap,
                int destinationX,
                int destinationY
        ) {
            this.mapId = mapId;
            this.bounds = bounds;
            this.destinationMap = destinationMap;
            this.destinationX = destinationX;
            this.destinationY = destinationY;
        }
    }

    public record MapTransition(String mapId, int playerX, int playerY) {
    }
}
