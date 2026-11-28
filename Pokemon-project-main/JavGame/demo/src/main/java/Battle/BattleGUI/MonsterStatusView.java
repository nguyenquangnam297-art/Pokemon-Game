package Battle.BattleGUI;

import Entity.Monster;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class MonsterStatusView extends VBox {
    private Monster monster;
    private ProgressBar hpBar;
    private Label nameLabel;
    private ImageView imageView;

    private HBox hpRow;
    private HBox buffIconBox;
    private StackPane atkBuffBadge;
    private StackPane spdBuffBadge;
    private ScaleTransition atkPulseAnim;
    private ScaleTransition spdPulseAnim;

    public MonsterStatusView(Monster monster, boolean isEnemy) {
        super(10);
        this.monster = monster;

        hpBar = new ProgressBar(1.0);
        hpBar.setStyle("-fx-accent: #28c828;");
        hpBar.setPrefSize(400, 25);


        atkBuffBadge = createBuffBadge(
            loadUiIcon("attack.png"),
            "#ff5252",
            "rgba(65, 20, 20, 0.85)"
        );
        spdBuffBadge = createBuffBadge(
            loadUiIcon("speed.png"),
            "#ffd54f",
            "rgba(20, 45, 65, 0.85)"
        );

        atkPulseAnim = createBadgePulse(atkBuffBadge);
        spdPulseAnim = createBadgePulse(spdBuffBadge);

        buffIconBox = new HBox(6, atkBuffBadge, spdBuffBadge);
        buffIconBox.setAlignment(Pos.CENTER_LEFT);

        hpRow = new HBox(10, hpBar, buffIconBox);
        hpRow.setAlignment(Pos.CENTER_LEFT);

        nameLabel = new Label();
        nameLabel.setStyle(
            "-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #ffffff; "
            + "-fx-background-color: rgba(20, 25, 35, 0.75); -fx-padding: 4 12 4 12; -fx-background-radius: 4;"
        );

        imageView = new ImageView();
        loadMonsterImage();

        if (isEnemy) {
            imageView.setScaleX(-1);
            this.setAlignment(Pos.CENTER_RIGHT);
            this.getChildren().addAll(nameLabel, hpRow, imageView);
        } else {
            this.setAlignment(Pos.CENTER_LEFT);
            this.getChildren().addAll(imageView, nameLabel, hpRow);
        }

        updateHp();
    }

    private Image loadUiIcon(String fileName) {
        return BattleAssetLoader.loadImage(
            "/ui/" + fileName,
            "src/main/resources/ui/" + fileName,
            "resources/ui/" + fileName
        );
    }

    private StackPane createBuffBadge(Image iconImg, String borderHex, String bgRgba) {
        ImageView iconView = new ImageView();
        if (iconImg != null) {
            iconView.setImage(iconImg);
        }
        iconView.setFitWidth(20);
        iconView.setFitHeight(20);
        iconView.setPreserveRatio(true);
        iconView.setSmooth(false);

        DropShadow glow = new DropShadow();
        glow.setColor(Color.web(borderHex));
        glow.setRadius(8.0);
        glow.setSpread(0.35);
        iconView.setEffect(glow);

        StackPane badge = new StackPane(iconView);
        badge.setMinSize(28, 28);
        badge.setPrefSize(28, 28);
        badge.setMaxSize(28, 28);
        badge.setStyle(
            "-fx-background-color: " + bgRgba + "; "
            + "-fx-border-color: " + borderHex + "; "
            + "-fx-border-width: 2; "
            + "-fx-border-radius: 4; "
            + "-fx-background-radius: 4;"
        );
        badge.setVisible(false);
        badge.setManaged(false);
        return badge;
    }

    private ScaleTransition createBadgePulse(StackPane badge) {
        ScaleTransition pulse = new ScaleTransition(Duration.millis(550), badge);
        pulse.setFromX(0.95);
        pulse.setFromY(0.95);
        pulse.setToX(1.12);
        pulse.setToY(1.12);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(Animation.INDEFINITE);
        return pulse;
    }

    private void loadMonsterImage() {
        MonsterSpriteHelper.stopAnimation(imageView);
        imageView.setImage(null);
        try {
            Image img = new Image(getClass().getResourceAsStream(monster.getImagePath()));
            MonsterSpriteHelper.setupAnimatedSprite(imageView, img);
            imageView.setFitHeight(250);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {}
    }

    public void setMonster(Monster newMonster) {
        this.monster = newMonster;
        loadMonsterImage();
        updateHp();
    }

    public void setStatusHudVisible(boolean visible) {
        nameLabel.setVisible(visible);
        hpRow.setVisible(visible);
    }

    public void updateHp() {
        nameLabel.setText(monster.getName() + " (HP: " + monster.getCurrentHp() + "/" + monster.getMaxHp() + ")");
        hpBar.setProgress((double) monster.getCurrentHp() / monster.getMaxHp());
        updateBuffIcons();
    }

    public void updateBuffIcons() {
        boolean hasAtkBuff = monster != null && !monster.isFainted() && monster.getAtkBuffTurns() > 0;
        boolean hasSpdBuff = monster != null && !monster.isFainted() && monster.getSpdBuffTurns() > 0;

        setBadgeActive(atkBuffBadge, atkPulseAnim, hasAtkBuff);
        setBadgeActive(spdBuffBadge, spdPulseAnim, hasSpdBuff);
    }

    private void setBadgeActive(StackPane badge, ScaleTransition pulseAnim, boolean active) {
        if (active) {
            if (!badge.isVisible()) {
                badge.setVisible(true);
                badge.setManaged(true);
                pulseAnim.playFromStart();
            }
        } else {
            if (badge.isVisible()) {
                pulseAnim.stop();
                badge.setScaleX(1.0);
                badge.setScaleY(1.0);
                badge.setVisible(false);
                badge.setManaged(false);
            }
        }
    }

    public ImageView getImageView() {
        return imageView;
    }
}