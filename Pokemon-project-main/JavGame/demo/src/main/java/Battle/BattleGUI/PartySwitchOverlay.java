package Battle.BattleGUI;

import Entity.Monster;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PartySwitchOverlay extends StackPane {

    private VBox dialogBox;
    private Label titleLabel;
    private Label subtitleLabel;
    private GridPane partyGrid;
    private Button btnCancel;

    private List<Monster> team;
    private Monster activeMonster;
    private boolean isForced;
    private Consumer<Monster> onPokemonSelected;
    private Runnable onCancel;

    private List<VBox> cardNodes = new ArrayList<VBox>();
    private List<Monster> cardMonsters = new ArrayList<Monster>();
    private List<Boolean> cardSelectable = new ArrayList<Boolean>();
    private int selectedIndex = 0;

    public PartySwitchOverlay(Consumer<Monster> onPokemonSelected, Runnable onCancel) {
        this.onPokemonSelected = onPokemonSelected;
        this.onCancel = onCancel;

        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");
        this.setAlignment(Pos.CENTER);
        this.setVisible(false);

        dialogBox = new VBox(16);
        dialogBox.setMaxSize(1060, 650);
        dialogBox.setPrefSize(1060, 650);
        dialogBox.setPadding(new Insets(24, 30, 24, 30));
        dialogBox.setAlignment(Pos.TOP_CENTER);
        dialogBox.setStyle("-fx-background-color: #000000; -fx-background-radius: 0; "
            + "-fx-border-color: #4a90e2; -fx-border-width: 3; -fx-border-radius: 0;");

        titleLabel = new Label("CHỌN POKÉMON RA TRẬN");
        titleLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 26px; -fx-font-weight: bold;");

        subtitleLabel = new Label("Chọn một Pokémon khỏe mạnh trong đội hình để thay thế");
        subtitleLabel.setStyle("-fx-text-fill: #b0bec5; -fx-font-size: 16px;");

        partyGrid = new GridPane();
        partyGrid.setHgap(20);
        partyGrid.setVgap(16);
        partyGrid.setAlignment(Pos.CENTER);
        VBox.setVgrow(partyGrid, Priority.ALWAYS);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        partyGrid.getColumnConstraints().addAll(col1, col2);

        btnCancel = new Button("QUAY LẠI");
        btnCancel.setPrefSize(200, 48);
        btnCancel.setFocusTraversable(false);
        btnCancel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #e53935; "
            + "-fx-text-fill: white; -fx-background-radius: 0; -fx-cursor: hand;");
        btnCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!isForced) {
                    close();
                    if (onCancel != null) {
                        onCancel.run();
                    }
                }
            }
        });

        dialogBox.getChildren().addAll(titleLabel, subtitleLabel, partyGrid, btnCancel);
        this.getChildren().add(dialogBox);
    }

    public void openInitialSelection(List<Monster> team) {
        open(
            team,
            null,
            true,
            "CHỌN POKÉMON RA TRẬN",
            "Chọn một Pokémon khỏe mạnh để mở màn trận đấu"
        );
    }

    public void open(List<Monster> team, Monster activeMonster, boolean isForced) {
        if (isForced) {
            open(team, activeMonster, true, "POKÉMON ĐÃ GỤC NGÃ!", "Chọn một Pokémon");
        } else {
            open(team, activeMonster, false, "ĐỔI POKÉMON RA TRẬN", "Chọn một Pokémon");
        }
    }

    public void open(
            List<Monster> team,
            Monster activeMonster,
            boolean isForced,
            String customTitle,
            String customSubtitle) {
        this.team = team;
        this.activeMonster = activeMonster;
        this.isForced = isForced;

        titleLabel.setText(customTitle);
        subtitleLabel.setText(customSubtitle);

        if (isForced) {
            btnCancel.setVisible(false);
            btnCancel.setManaged(false);
        } else {
            btnCancel.setVisible(true);
            btnCancel.setManaged(true);
        }

        buildPartyCards();
        this.setVisible(true);
        this.toFront();
    }

    public void close() {
        this.setVisible(false);
    }

    private void buildPartyCards() {
        partyGrid.getChildren().clear();
        cardNodes.clear();
        cardMonsters.clear();
        cardSelectable.clear();
        selectedIndex = -1;

        for (int i = 0; i < team.size(); i++) {
            final Monster m = team.get(i);
            final boolean isActive = (activeMonster != null && m == activeMonster);
            final boolean isFainted = m.isFainted();
            final boolean canSelect = (!isActive && !isFainted);

            VBox card = createMonsterCard(m, isActive, isFainted, canSelect);
            final int cardIdx = i;

            card.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (canSelect) {
                        selectedIndex = cardIdx;
                        updateCardHighlight();
                    }
                }
            });

            card.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (canSelect) {
                        triggerSelect(m);
                    }
                }
            });

            int col = i % 2;
            int row = i / 2;
            partyGrid.add(card, col, row);

            cardNodes.add(card);
            cardMonsters.add(m);
            cardSelectable.add(canSelect);

            if (canSelect && selectedIndex == -1) {
                selectedIndex = cardIdx;
            }
        }

        updateCardHighlight();
    }

    private VBox createMonsterCard(Monster m, boolean isActive, boolean isFainted, boolean canSelect) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12, 16, 12, 16));
        card.setPrefHeight(145);

        HBox topRow = new HBox(14);
        topRow.setAlignment(Pos.CENTER_LEFT);


        ImageView sprite = new ImageView();
        try {
            Image rawImg = new Image(getClass().getResourceAsStream(m.getImagePath()));
            MonsterSpriteHelper.applyStaticFrame(sprite, rawImg);
            sprite.setFitWidth(85);
            sprite.setFitHeight(85);
            sprite.setPreserveRatio(true);
        } catch (Exception e) {}


        VBox infoCol = new VBox(6);
        HBox.setHgrow(infoCol, Priority.ALWAYS);

        HBox nameRow = new HBox(10);
        nameRow.setAlignment(Pos.CENTER_LEFT);
        Label nameLbl = new Label(m.getName());
        nameLbl.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 19px; -fx-font-weight: bold;");

        Label typeLbl = new Label("[" + m.getType() + "]");
        typeLbl.setStyle("-fx-text-fill: #ffd54f; -fx-font-size: 14px; -fx-font-weight: bold;");
        nameRow.getChildren().addAll(nameLbl, typeLbl);

        double ratio = 0.0;
        if (m.getMaxHp() > 0) {
            ratio = (double) m.getCurrentHp() / m.getMaxHp();
        }
        ProgressBar hpBar = new ProgressBar(ratio);
        hpBar.setPrefWidth(250);
        hpBar.setPrefHeight(16);
        if (ratio > 0.5) {
            hpBar.setStyle("-fx-accent: #28c828;");
        } else if (ratio > 0.2) {
            hpBar.setStyle("-fx-accent: #ffa726;");
        } else {
            hpBar.setStyle("-fx-accent: #e53935;");
        }

        Label hpText = new Label("HP: " + m.getCurrentHp() + " / " + m.getMaxHp());
        hpText.setStyle("-fx-text-fill: #e0e0e0; -fx-font-size: 14px;");

        infoCol.getChildren().addAll(nameRow, hpBar, hpText);

        Label statusBadge = new Label();
        statusBadge.setPadding(new Insets(4, 10, 4, 10));
        if (isActive) {
            statusBadge.setText("ĐANG RA TRẬN");
            statusBadge.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 0;");
        } else if (isFainted) {
            statusBadge.setText("NGẤT XỈU");
            statusBadge.setStyle("-fx-background-color: #c62828; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 0;");
        } else {
            statusBadge.setText("SẴN SÀNG");
            statusBadge.setStyle("-fx-background-color: #2e7d32; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 0;");
        }

        topRow.getChildren().addAll(sprite, infoCol, statusBadge);

        Label statsLabel = new Label("ATK: " + m.getAttack() + " | DEF: " + m.getDefense() + " | SPD: " + m.getSpeed());
        statsLabel.setStyle("-fx-text-fill: #90caf9; -fx-font-size: 14px; -fx-font-weight: bold;");

        card.getChildren().addAll(topRow, statsLabel);

        if (!canSelect) {
            card.setOpacity(0.55);
        }
        return card;
    }

    private void updateCardHighlight() {
        for (int i = 0; i < cardNodes.size(); i++) {
            VBox card = cardNodes.get(i);
            boolean canSelect = cardSelectable.get(i);

            if (i == selectedIndex && canSelect) {
                card.setStyle("-fx-background-color: #2c3e50; -fx-background-radius: 0; "
                    + "-fx-border-color: #ffd54f; -fx-border-width: 3; -fx-border-radius: 0; -fx-cursor: hand;");
            } else if (canSelect) {
                card.setStyle("-fx-background-color: #263238; -fx-background-radius: 0; "
                    + "-fx-border-color: #455a64; -fx-border-width: 2; -fx-border-radius: 0; -fx-cursor: hand;");
            } else {
                card.setStyle("-fx-background-color: #1c222b; -fx-background-radius: 0; "
                    + "-fx-border-color: #37474f; -fx-border-width: 2; -fx-border-radius: 0;");
            }
        }
    }

    private void triggerSelect(Monster m) {
        close();
        if (onPokemonSelected != null) {
            onPokemonSelected.accept(m);
        }
    }

    public void handleKeyPress(KeyEvent event) {
        KeyCode code = event.getCode();

        if (code == KeyCode.ESCAPE || code == KeyCode.BACK_SPACE) {
            if (!isForced) {
                btnCancel.fire();
            }
            return;
        }

        if (cardNodes.isEmpty()) {
            return;
        }

        if (code == KeyCode.RIGHT || code == KeyCode.D || code == KeyCode.DOWN || code == KeyCode.S) {
            moveSelection(1);
        } else if (code == KeyCode.LEFT || code == KeyCode.A || code == KeyCode.UP || code == KeyCode.W) {
            moveSelection(-1);
        } else if (code == KeyCode.ENTER || code == KeyCode.SPACE) {
            if (selectedIndex >= 0 && selectedIndex < cardNodes.size()) {
                if (cardSelectable.get(selectedIndex)) {
                    triggerSelect(cardMonsters.get(selectedIndex));
                }
            }
        }
    }

    private void moveSelection(int step) {
        int total = cardNodes.size();
        for (int attempt = 1; attempt <= total; attempt++) {
            int nextIdx;
            if (selectedIndex == -1) {
                nextIdx = 0;
            } else {
                nextIdx = (selectedIndex + step * attempt + total * total) % total;
            }
            if (cardSelectable.get(nextIdx)) {
                selectedIndex = nextIdx;
                updateCardHighlight();
                return;
            }
        }
    }
}