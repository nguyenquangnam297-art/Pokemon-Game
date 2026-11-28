package Battle.BattleGUI;

import Entity.Monster;
import java.util.List;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class BattleIntroOverlay {

    private static final int PLAYER_FRAME_SIZE = 128;
    private static final int PLAYER_BACK_ROW = 3;
    private static final int MAX_PARTY_SLOTS = 6;

    private Group enemyPlatform;
    private Group playerPlatform;
    private ImageView playerBackView;
    private VBox enemyBallBar;
    private VBox playerBallBar;
    private StackPane bottomDialogueBox;
    private Label dialogueTextLabel;
    private Polygon cursorIndicator;
    private Timeline cursorBlinkTimeline;

    public BattleIntroOverlay(AnchorPane gameRoot, List<Monster> playerTeam) {
        buildIntroScene(gameRoot, playerTeam);
    }

    private void buildIntroScene(AnchorPane gameRoot, List<Monster> playerTeam) {

        enemyPlatform = createTurfPlatform(340.0, 96.0);
        enemyPlatform.setVisible(false);
        AnchorPane.setTopAnchor(enemyPlatform, 315.0);
        AnchorPane.setRightAnchor(enemyPlatform, 80.0);

        playerPlatform = createTurfPlatform(430.0, 112.0);
        playerPlatform.setVisible(false);
        AnchorPane.setBottomAnchor(playerPlatform, 145.0);
        AnchorPane.setLeftAnchor(playerPlatform, 70.0);

        playerBackView = new ImageView();
        Image playerSheet = BattleAssetLoader.loadImage(
            "/PlayerSprite/player.png",
            "/player.png",
            "src/main/resources/PlayerSprite/player.png",
            "graphics/PlayerSprite/player.png"
        );
        if (playerSheet != null && !playerSheet.isError() && playerSheet.getWidth() >= PLAYER_FRAME_SIZE) {
            playerBackView.setImage(playerSheet);
            playerBackView.setViewport(new Rectangle2D(
                0.0,
                PLAYER_BACK_ROW * PLAYER_FRAME_SIZE,
                PLAYER_FRAME_SIZE,
                PLAYER_FRAME_SIZE
            ));
        }
        playerBackView.setFitWidth(260.0);
        playerBackView.setFitHeight(260.0);
        playerBackView.setPreserveRatio(true);
        playerBackView.setSmooth(false);
        playerBackView.setMouseTransparent(true);
        playerBackView.setVisible(false);
        AnchorPane.setBottomAnchor(playerBackView, 175.0);
        AnchorPane.setLeftAnchor(playerBackView, 155.0);

        enemyBallBar = createTeamBallBar(null, true);
        enemyBallBar.setVisible(false);
        AnchorPane.setTopAnchor(enemyBallBar, 145.0);
        AnchorPane.setLeftAnchor(enemyBallBar, 0.0);

        playerBallBar = createTeamBallBar(playerTeam, false);
        playerBallBar.setVisible(false);
        AnchorPane.setTopAnchor(playerBallBar, 525.0);
        AnchorPane.setRightAnchor(playerBallBar, 0.0);

        buildBottomDialogueBox();

        gameRoot.getChildren().addAll(
            enemyPlatform,
            playerPlatform,
            playerBackView,
            enemyBallBar,
            playerBallBar,
            bottomDialogueBox
        );
    }

    private Group createTurfPlatform(double width, double height) {
        double rx = width / 2.0;
        double ry = height / 2.0;

        Ellipse outerRim = new Ellipse(rx, ry, rx, ry);
        outerRim.setFill(Color.web("#d7cf9e", 0.92));
        outerRim.setStroke(Color.web("#7c8759", 0.85));
        outerRim.setStrokeWidth(3.0);

        Ellipse innerTurf = new Ellipse(rx, ry, rx * 0.84, ry * 0.78);
        innerTurf.setFill(Color.web("#8ec06c", 0.92));

        Group platform = new Group(outerRim, innerTurf);
        platform.setMouseTransparent(true);
        return platform;
    }

    private VBox createTeamBallBar(List<Monster> team, boolean isEnemySide) {
        HBox ballsRow = new HBox(16);
        ballsRow.setAlignment(Pos.CENTER);

        if (isEnemySide) {
            ballsRow.setPadding(new Insets(0, 35, 0, 145));
            for (int i = 0; i < MAX_PARTY_SLOTS; i++) {
                if (i == MAX_PARTY_SLOTS - 1) {
                    ballsRow.getChildren().add(createSlotBallIcon(1));
                } else {
                    ballsRow.getChildren().add(createSlotBallIcon(0));
                }
            }
        } else {
            ballsRow.setPadding(new Insets(0, 145, 0, 35));
            int teamSize = 0;
            if (team != null) {
                teamSize = team.size();
            }
            for (int i = 0; i < MAX_PARTY_SLOTS; i++) {
                if (i < teamSize) {
                    Monster m = team.get(i);
                    if (m != null && !m.isFainted()) {
                        ballsRow.getChildren().add(createSlotBallIcon(1));
                    } else {
                        ballsRow.getChildren().add(createSlotBallIcon(2));
                    }
                } else {
                    ballsRow.getChildren().add(createSlotBallIcon(0));
                }
            }
        }

        HBox lineWithArrow = new HBox(0);
        lineWithArrow.setAlignment(Pos.CENTER_LEFT);

        Rectangle barLine = new Rectangle(520.0, 6.0, Color.web("#2b2e33"));
        Polygon arrowHead = new Polygon();
        if (isEnemySide) {
            arrowHead.getPoints().addAll(
                0.0, 0.0,
                34.0, 14.0,
                0.0, 14.0
            );
            arrowHead.setFill(Color.web("#2b2e33"));
            arrowHead.setTranslateY(-4.0);
            lineWithArrow.getChildren().addAll(barLine, arrowHead);
        } else {
            arrowHead.getPoints().addAll(
                34.0, 0.0,
                34.0, 14.0,
                0.0, 14.0
            );
            arrowHead.setFill(Color.web("#2b2e33"));
            arrowHead.setTranslateY(-4.0);
            lineWithArrow.getChildren().addAll(arrowHead, barLine);
        }

        VBox container = new VBox(6, ballsRow, lineWithArrow);
        container.setMouseTransparent(true);
        if (isEnemySide) {
            container.setAlignment(Pos.CENTER_LEFT);
        } else {
            container.setAlignment(Pos.CENTER_RIGHT);
        }
        return container;
    }

    private Node createSlotBallIcon(int state) {
        double radius = 16.0;
        Group icon = new Group();

        if (state == 0) {
            Circle emptyCircle = new Circle(radius, Color.web("#ffffff", 0.25));
            emptyCircle.setStroke(Color.web("#2b2e33"));
            emptyCircle.setStrokeWidth(3.0);
            icon.getChildren().add(emptyCircle);
        } else {
            Color topColor;
            Color bottomColor;
            if (state == 1) {
                topColor = Color.web("#e53935");
                bottomColor = Color.web("#ffffff");
            } else {
                topColor = Color.web("#607d8b");
                bottomColor = Color.web("#cfd8dc");
            }

            Arc topHalf = new Arc(0.0, 0.0, radius, radius, 0.0, 180.0);
            topHalf.setType(ArcType.CHORD);
            topHalf.setFill(topColor);

            Arc bottomHalf = new Arc(0.0, 0.0, radius, radius, 180.0, 180.0);
            bottomHalf.setType(ArcType.CHORD);
            bottomHalf.setFill(bottomColor);

            Circle outerRing = new Circle(0.0, 0.0, radius, Color.TRANSPARENT);
            outerRing.setStroke(Color.web("#2b2e33"));
            outerRing.setStrokeWidth(3.0);

            Rectangle centerBand = new Rectangle(-radius, -2.0, radius * 2.0, 4.0);
            centerBand.setFill(Color.web("#2b2e33"));

            Circle centerButton = new Circle(0.0, 0.0, 5.0, Color.WHITE);
            centerButton.setStroke(Color.web("#2b2e33"));
            centerButton.setStrokeWidth(2.5);

            icon.getChildren().addAll(topHalf, bottomHalf, centerBand, outerRing, centerButton);
        }

        return icon;
    }

    private void buildBottomDialogueBox() {
        bottomDialogueBox = new StackPane();
        bottomDialogueBox.setPrefHeight(185.0);
        bottomDialogueBox.setMinHeight(185.0);
        bottomDialogueBox.setMaxHeight(185.0);
        bottomDialogueBox.setStyle("-fx-background-color: #2b2d35; -fx-padding: 12 16 12 16;");
        bottomDialogueBox.setVisible(false);
        bottomDialogueBox.setMouseTransparent(true);

        AnchorPane.setBottomAnchor(bottomDialogueBox, 0.0);
        AnchorPane.setLeftAnchor(bottomDialogueBox, 0.0);
        AnchorPane.setRightAnchor(bottomDialogueBox, 0.0);

        HBox innerBox = new HBox(12);
        innerBox.setAlignment(Pos.CENTER_LEFT);
        innerBox.setStyle(
            "-fx-background-color: #537c85; "
            + "-fx-border-color: #d8524e; "
            + "-fx-border-width: 6; "
            + "-fx-border-radius: 14; "
            + "-fx-background-radius: 14; "
            + "-fx-padding: 20 42 20 42;"
        );

        dialogueTextLabel = new Label("");
        dialogueTextLabel.setWrapText(true);
        dialogueTextLabel.setStyle(
            "-fx-text-fill: #ffffff; "
            + "-fx-font-size: 32px; "
            + "-fx-font-weight: bold; "
            + "-fx-line-spacing: 6px; "
            + "-fx-effect: dropshadow(one-pass-box, #1f2d30, 1, 1.0, 2, 2);"
        );

        cursorIndicator = new Polygon(
            0.0, 0.0,
            18.0, 0.0,
            9.0, 12.0
        );
        cursorIndicator.setFill(Color.web("#ff5252"));
        cursorIndicator.setStroke(Color.WHITE);
        cursorIndicator.setStrokeWidth(2.0);
        cursorIndicator.setTranslateY(18.0);
        cursorIndicator.setVisible(false);

        cursorBlinkTimeline = new Timeline(
            new KeyFrame(Duration.millis(0), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    cursorIndicator.setTranslateY(16.0);
                }
            }),
            new KeyFrame(Duration.millis(320), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    cursorIndicator.setTranslateY(22.0);
                }
            }),
            new KeyFrame(Duration.millis(640), new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    cursorIndicator.setTranslateY(16.0);
                }
            })
        );
        cursorBlinkTimeline.setCycleCount(Animation.INDEFINITE);

        innerBox.getChildren().addAll(dialogueTextLabel, cursorIndicator);
        bottomDialogueBox.getChildren().add(innerBox);
    }

    public Group getEnemyPlatform() {
        return enemyPlatform;
    }

    public Group getPlayerPlatform() {
        return playerPlatform;
    }

    public ImageView getPlayerBackView() {
        return playerBackView;
    }

    public VBox getEnemyBallBar() {
        return enemyBallBar;
    }

    public VBox getPlayerBallBar() {
        return playerBallBar;
    }

    public void prepareEntranceLayout() {
        enemyPlatform.setVisible(true);
        enemyPlatform.setOpacity(1.0);
        enemyPlatform.setTranslateX(-620.0);

        playerPlatform.setVisible(true);
        playerPlatform.setOpacity(1.0);
        playerPlatform.setTranslateX(620.0);

        playerBackView.setVisible(true);
        playerBackView.setOpacity(1.0);
        playerBackView.setTranslateX(620.0);

        enemyBallBar.setVisible(true);
        enemyBallBar.setOpacity(1.0);
        enemyBallBar.setTranslateX(-560.0);

        playerBallBar.setVisible(true);
        playerBallBar.setOpacity(1.0);
        playerBallBar.setTranslateX(560.0);

        bottomDialogueBox.setVisible(true);
        bottomDialogueBox.setOpacity(1.0);
        dialogueTextLabel.setText("");
        setCursorVisible(false);
    }

    public void snapToEntranceTarget() {
        enemyPlatform.setTranslateX(0.0);
        playerPlatform.setTranslateX(0.0);
        playerBackView.setTranslateX(0.0);
        enemyBallBar.setTranslateX(0.0);
        playerBallBar.setTranslateX(0.0);
    }

    public void setDialogueText(String text) {
        dialogueTextLabel.setText(text);
    }

    public void setCursorVisible(boolean visible) {
        cursorIndicator.setVisible(visible);
        if (visible) {
            cursorBlinkTimeline.play();
        } else {
            cursorBlinkTimeline.stop();
        }
    }

    public void hideAll() {
        cursorBlinkTimeline.stop();
        playerBackView.setVisible(false);
        enemyBallBar.setVisible(false);
        playerBallBar.setVisible(false);
        enemyPlatform.setVisible(false);
        playerPlatform.setVisible(false);
        bottomDialogueBox.setVisible(false);
    }
}