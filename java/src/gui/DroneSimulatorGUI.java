
package gui;

import API.api;
import Animation.*;
import Login.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static gui.DroneDynamicsApp.id;
import static javafx.geometry.Pos.CENTER;

public class DroneSimulatorGUI extends Application {

    private Scene mainScene;
    private Scene loginScene;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Drone Application");

        // Initialize and show the login page first
        showLoginPage(primaryStage);

        primaryStage.show();
    }

    private VBox createDashboard(Stage primaryStage) {
        Button btnMenu = createToolbarButton("Menu", "/image/menu.png");
       VBox menuItemsBox = new VBox(10); // VBox to hold the submenu items
        menuItemsBox.setVisible(false); // Initially hidden

        btnMenu.setOnAction(event -> menuItemsBox.setVisible(!menuItemsBox.isVisible())); // Toggle visibility

        Button btnLogout = createToolbarButton("Logout", "/image/logout.png");
        btnLogout.setOnAction(_ -> showLoginPage(primaryStage));

        Button btnRefresh = createToolbarButton("Refresh", "/image/refresh.png");
        btnRefresh.setOnAction(_ -> handleRefresh());

        HBox hbox = new HBox(btnMenu, btnLogout, btnRefresh);
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(200); // Set spacing between buttons
        hbox.setPadding(new Insets(5)); // Add padding around the HBox

        // Create a ToolBar and add the HBox to it
        ToolBar toolbar = new ToolBar();
        toolbar.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        toolbar.getItems().add(hbox);
        toolbar.setOpacity(1.0); // Set opacity of the toolbar

        // Add the submenu items to the VBox
        menuItemsBox.getChildren().addAll(
                createSubButton("Drone Flight Dynamic", primaryStage, this::showDynamicPage),
                createSubButton("Drone Catalogue", primaryStage, this::showCataloguePage),
                createSubButton("Drone History", primaryStage, this::showHistoryPage)
        );

        // Add the VBox to the VBox containing the toolbar
        VBox vbox = new VBox(toolbar, menuItemsBox);
        vbox.setSpacing(10); // Add some space between toolbar and submenu items

        return vbox;
    }

    private Button createToolbarButton(String text, String imagePath) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: black; -fx-font-size: 14px;");
        button.setGraphicTextGap(10); // Set gap between text and image
        button.setPadding(new Insets(5)); // Add padding inside the button

        // Load images
        ImageView imageView1 = new ImageView(new Image(getClass().getResourceAsStream(imagePath)));
        imageView1.setFitWidth(30);
        imageView1.setFitHeight(30);
        imageView1.setPreserveRatio(true);
        imageView1.setSmooth(true);
        button.setGraphic(imageView1);

        return button;
    }

    private Button createSubButton(String text, Stage primaryStage, SubButtonActionHandler actionHandler) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: lightblue; -fx-border-color: black; -fx-text-fill: black; -fx-font-size: 14px;");
        button.setPadding(new Insets(0)); // Add padding inside the button
        button.setMaxWidth(150); // Make the button stretch to fill width
        totalDrones=0;
        button.setOnAction(_ -> {
            try {
                actionHandler.handle(primaryStage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        return button;
    }

    private void handleLogin() {
        // Add login handling logic here
        System.out.println("Login button clicked");
    }

    private void handleRefresh() {
        // Add refresh handling logic here
        System.out.println("Refresh button clicked");
    }

    private void showLoginPage(Stage primaryStage) {
        LoginManager loginManager = new LoginManager();

        Label lblGroupName = new Label("Group Name:");
        TextField txtGroupName = new TextField();

        Label lblUsername = new Label("Username:");
        TextField txtUsername = new TextField();

        Label lblPassword = new Label("Password:");
        PasswordField txtPassword = new PasswordField();

        Button btnLogin = new Button("Login");
        btnLogin.setOnAction(e -> {
            String groupName = txtGroupName.getText();
            String username = txtUsername.getText();
            String password = txtPassword.getText();

            if (loginManager.validate(groupName, username, password)) {
                // Transition to menu scene upon successful login
                showMenu(primaryStage);
            } else {
                System.out.println("Invalid credentials");
                // Optionally show an alert dialog or error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Error");
                alert.setHeaderText(null);
                alert.setContentText("Invalid credentials. Please try again.");
                alert.showAndWait();
            }
        });

        VBox loginFields = new VBox(10, lblGroupName, txtGroupName, lblUsername, txtUsername, lblPassword, txtPassword, btnLogin);
        loginFields.setPadding(new Insets(20));
        loginFields.setAlignment(Pos.CENTER_RIGHT);
        loginFields.setMaxWidth(400);

        // Load the image
        ImageView imageView = new ImageView(new Image("/image/drone.jpg"));
        imageView.setFitWidth(600);
        imageView.setFitHeight(400);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        // Create an HBox to hold the image and the login fields
        HBox loginBox = new HBox(imageView, loginFields);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(20));

        Scene loginScene = new Scene(loginBox, 800, 400);

        primaryStage.setScene(loginScene);
    }

    private void showMenu(Stage primaryStage) {
        VBox dashboard = createDashboard(primaryStage);

        // Create DroneAnimation instance and get its ImageView
        DroneAnimation droneAnimation = new DroneAnimation();
        ImageView droneImageView = droneAnimation.getDroneImageView();
        droneAnimation.playAnimation(); // Start the animation

        // Center container for droneImageView
        VBox centerContainer = new VBox(5);
        centerContainer.setAlignment(Pos.CENTER);
        centerContainer.getChildren().addAll(droneImageView);

        BorderPane root = new BorderPane();
        root.setTop(dashboard);
        root.setCenter(centerContainer);

        Scene menuScene = new Scene(root, 1300, 1200);

        primaryStage.setScene(menuScene);
        primaryStage.show();
    }

    private interface SubButtonActionHandler {
        void handle(Stage primaryStage) throws IOException;
    }


    private Map<String, DroneDynamicsApp.DroneDynamics> droneDataMap;
    private Label idLabel, timeLabel, statusLabel, batteryLabel, speedLabel, yawLabel, pitchLabel, rollLabel, longitudeLabel, latitudeLabel, timestampLabel, lastSeenLabel, googleMapsLabel;
    private int offset = 0;
    private static final int LIMIT = 10;
    private static final int MAX_OFFSET = 2880;
    private ChoiceBox<String> choiceBox;
    private ChoiceBox<Integer> numberChoiceBox;
    private Hyperlink googleMapsLink;
    private int totalDrones = 0;



    private void showDynamicPage(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Drone Dynamics Information");
        droneDataMap = new HashMap<>();

        VBox dashboard = createDashboardDynamic(primaryStage);

        // TextField for search input
        TextField searchField = new TextField();
        searchField.setPromptText("Search Drone ID");
        searchField.setPrefWidth(200);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterDroneIDs(newValue));

        // ChoiceBox for selecting drone IDs
        choiceBox = new ChoiceBox<>();
        choiceBox.setPrefWidth(200);
        choiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                showDroneDetails(newValue);
            }
        });

        // ChoiceBox for selecting number of drones (1 to 30)
        numberChoiceBox = new ChoiceBox<>();
        ObservableList<Integer> numbers = FXCollections.observableArrayList();
        for (int i = 1; i <= 30; i++) {
            numbers.add(i);
        }
        numberChoiceBox.setItems(numbers);
        numberChoiceBox.setValue(1); // Set default value
        numberChoiceBox.setPrefWidth(100);
        numberChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                try {
                    offset = 0; // Reset offset
                    totalDrones=0;
                    refreshDroneData(newValue, offset, true); // Reset choice box
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // Icons and Labels for displaying drone details
        VBox droneDetails = new VBox(20);
        droneDetails.setPadding(new Insets(20));
        droneDetails.setAlignment(Pos.BASELINE_LEFT);

        idLabel = createIconLabel("/image/id.png", "Drone ID: ");
        timeLabel = createIconLabel("/image/time.png", "Time: ");
        statusLabel = createIconLabel("/image/status.png", "Status: ");
        batteryLabel = createIconLabel("/image/battery.png", "Battery: ");
        speedLabel = createIconLabel("/image/speed.png", "Speed: ");
        yawLabel = createIconLabel("/image/yaw.png", "Yaw: ");
        pitchLabel = createIconLabel("/image/pitch.png", "Pitch: ");
        rollLabel = createIconLabel("/image/roll.png", "Roll: ");
        longitudeLabel = createIconLabel("/image/longitude.png", "Longitude: ");
        latitudeLabel = createIconLabel("/image/latitude.png", "Latitude: ");
        timestampLabel = createIconLabel("/image/timestamp.png", "Timestamp: ");
        lastSeenLabel = createIconLabel("/image/timestamp.png", "Last Seen: ");
        googleMapsLabel = createIconLabel("/image/google-maps.png", "Open in Maps to see the Location: ");
        googleMapsLink = new Hyperlink();
        googleMapsLink.setOnAction(_ -> {
            if (!googleMapsLink.getText().isEmpty()) {
                getHostServices().showDocument(googleMapsLink.getText());
            }
        });

        droneDetails.getChildren().addAll(idLabel, timeLabel, statusLabel, batteryLabel, speedLabel, yawLabel, pitchLabel, rollLabel, longitudeLabel, latitudeLabel, timestampLabel, lastSeenLabel, googleMapsLabel, googleMapsLink);

        ScrollPane scrollPane = new ScrollPane(droneDetails);
        scrollPane.setFitToWidth(true);

        Button btnRefresh = new Button("Refresh");
        btnRefresh.setOnAction(_ -> {
            try {
                refreshDroneData(numberChoiceBox.getValue(), offset, false); // Do not reset choice box
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Button btnNext = new Button("Next");
        btnNext.setOnAction(e -> {
            if (offset + LIMIT <= MAX_OFFSET) {
                offset += LIMIT;
                try {
                    refreshDroneData(numberChoiceBox.getValue(), offset, true); // Reset choice box
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button btnPrevious = new Button("Previous");
        btnPrevious.setOnAction(e -> {
            if (offset - LIMIT >= 0) {
                offset -= LIMIT;
                try {
                    totalDrones -= 20;
                    refreshDroneData(numberChoiceBox.getValue(), offset, true); // Reset choice box
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button btnLast = new Button("Last");
        btnNext.setOnAction(e -> {
                offset += LIMIT;
                try {
                    refreshDroneData(numberChoiceBox.getValue(), offset, true); // Reset choice box
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
        });

        HBox nextButtonBox = new HBox(10, btnPrevious, btnNext);
        nextButtonBox.setAlignment(Pos.BOTTOM_LEFT);
        nextButtonBox.setPadding(new Insets(20));

        VBox mainLayout = new VBox(10, dashboard, searchField, choiceBox, numberChoiceBox, scrollPane, nextButtonBox);
        mainLayout.setSpacing(10);
        mainLayout.setPadding(new Insets(10));

        ScrollPane mainScrollPane = new ScrollPane(mainLayout);
        mainScrollPane.setFitToWidth(true);

        BorderPane root = new BorderPane();
        root.setCenter(mainScrollPane);

        Scene dynamicScene = new Scene(root, 1300, 1200);
        primaryStage.setScene(dynamicScene);
        primaryStage.show();

        refreshDroneData(numberChoiceBox.getValue(), offset, true);
    }

    private VBox createDashboardDynamic(Stage primaryStage) {
        Button btnMenu = createToolbarButton("Menu", "/image/menu.png");
        VBox menuItemsBox = new VBox(10);
        menuItemsBox.setVisible(false);

        btnMenu.setOnAction(event -> menuItemsBox.setVisible(!menuItemsBox.isVisible()));

        Button btnLogout = createToolbarButton("Logout", "/image/logout.png");
        btnLogout.setOnAction(_ -> showLoginPage(primaryStage));

        Button btnRefresh = createToolbarButton("Refresh", "/image/refresh.png");
        btnRefresh.setOnAction(_ -> {
            try {
                refreshDroneData(numberChoiceBox.getValue(), offset, false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Button btnBack = createToolbarButton("back", "/image/back.png");
        btnBack.setOnAction(_ -> {
            totalDrones = 0;
            showMenu(primaryStage);
        });

        HBox hbox = new HBox(btnMenu, btnLogout, btnRefresh, btnBack);
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(200);
        hbox.setPadding(new Insets(5));

        ToolBar toolbar = new ToolBar();
        toolbar.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        toolbar.getItems().add(hbox);
        toolbar.setOpacity(1.0);

        menuItemsBox.getChildren().addAll(
                createSubButton("Drone Flight Dynamic", primaryStage, this::showDynamicPage),
                createSubButton("Drone Catalogue", primaryStage, this::showCataloguePage),
                createSubButton("Drone History", primaryStage, this::showHistoryPage)
        );

        VBox vbox = new VBox(toolbar, menuItemsBox);
        vbox.setSpacing(0);

        return vbox;
    }

    private void showDroneDetails(String droneId) {
        DroneDynamicsApp.DroneDynamics drone = droneDataMap.get(droneId);
        if (drone != null) {
            idLabel.setText("Drone ID :   " + drone.getDrone());
            timeLabel.setText("Time Stamp :   " + drone.getTimestamp());
            statusLabel.setText("Status :   " + drone.getStatus());
            batteryLabel.setText("Battery :   " + drone.getBatteryStatus() + "%");
            speedLabel.setText("Speed :   " + drone.getSpeed() + " Km/h");
            yawLabel.setText("Yaw :   " + drone.getAlignYaw());
            pitchLabel.setText("Pitch :   " + drone.getAlignPitch());
            rollLabel.setText("Roll :   " + drone.getAlignRoll());
            longitudeLabel.setText("Longitude :   " + drone.getLongitude());
            latitudeLabel.setText("Latitude :   " + drone.getLatitude());
            timestampLabel.setText("Timestamp :   " + drone.getTimestamp());
            lastSeenLabel.setText("Last Seen :   " + drone.getLastSeen());

            String latitude = String.valueOf(drone.getLatitude());
            String longitude = String.valueOf(drone.getLongitude());
            String googleMapsUrl = String.format("https://www.google.com/maps/search/?api=1&query=%s,%s", latitude, longitude);
            googleMapsLink.setText(googleMapsUrl);
        }
    }

    private void filterDroneIDs(String query) {
        choiceBox.getItems().clear();
        droneDataMap.forEach((id, drone) -> {
            if (id.contains(query)) {
                choiceBox.getItems().add(id);
            }
        });
    }

    private Label createIconLabel(String iconFileName, String text) {
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconFileName)));
        ImageView iconView = new ImageView(icon);
        Label label = new Label(text, iconView);
        iconView.setFitWidth(50);
        iconView.setFitHeight(50);
        iconView.setPreserveRatio(true);
        iconView.setSmooth(true);
        label.setContentDisplay(ContentDisplay.LEFT);
        return label;
    }

    private void refreshDroneData(int number, int offset, boolean resetChoiceBox) throws IOException {
        if (resetChoiceBox) {
            choiceBox.getItems().clear();
            droneDataMap.clear();
        }

        CompletableFuture.runAsync(() -> {
            try {
                fetchAndProcessData(number, offset, resetChoiceBox);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).thenRun(() -> {
            Platform.runLater(() -> {
                // Update UI or handle further UI logic here if needed
            });
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    private void fetchAndProcessData(int number, int offset, boolean resetChoiceBox) throws IOException {
        String endpoint = "/api/" + number + "/dynamics/";
        String domain = "http://dronesim.facets-labs.com";
        String token = "Token 40a9557fac747f55c11ad20c85caac1d43654911";
        String agent = "Louay";

        api myApi2 = new api(endpoint, domain, token, agent);
        myApi2.createConnection(endpoint + "?limit=" + LIMIT + "&offset=" + offset);
        String response2 = myApi2.retrieveResponse();

        JSONArray drones = new JSONObject(response2).getJSONArray("results");
        for (int i = 0; i < drones.length(); i++) {
            JSONObject droneJson = drones.getJSONObject(i);
            int id = id(droneJson.getString("drone"));
            DroneDynamicsApp.DroneDynamics droneDynamics = new DroneDynamicsApp.DroneDynamics(
                    id,
                    droneJson.getString("timestamp"),
                    droneJson.getInt("speed"),
                    droneJson.getDouble("align_roll"),
                    droneJson.getDouble("align_pitch"),
                    droneJson.getDouble("align_yaw"),
                    droneJson.getDouble("longitude"),
                    droneJson.getDouble("latitude"),
                    droneJson.getInt("battery_status"),
                    droneJson.getString("last_seen"),
                    droneJson.getString("status")

                    // Pass Drone object here
            );

            if (resetChoiceBox) {
                int droneNumber = totalDrones + i + 1;
                Platform.runLater(() -> {
                    choiceBox.getItems().add(String.valueOf(droneNumber));
                    droneDataMap.put(String.valueOf(droneNumber), droneDynamics);
                });
            } else {
                String droneNumber = String.valueOf(totalDrones + i + 1);
                droneDataMap.put(droneNumber, droneDynamics);
            }
        }
        totalDrones += drones.length();
    }

    private void showCataloguePage(Stage primaryStage) throws IOException {
        VBox dashboard = createDashboardCatalogue(primaryStage);

        TableView<DroneTypeApp.DroneType> table = new TableView<>();

        TableColumn<DroneTypeApp.DroneType, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<DroneTypeApp.DroneType, String> manufacturerColumn = new TableColumn<>("Manufacturer");
        manufacturerColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));

        TableColumn<DroneTypeApp.DroneType, String> typenameColumn = new TableColumn<>("Type Name");
        typenameColumn.setCellValueFactory(new PropertyValueFactory<>("typename"));

        TableColumn<DroneTypeApp.DroneType, Integer> weightColumn = new TableColumn<>("Weight (g)");
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));

        TableColumn<DroneTypeApp.DroneType, Integer> maxSpeedColumn = new TableColumn<>("Max Speed (km/h)");
        maxSpeedColumn.setCellValueFactory(new PropertyValueFactory<>("maxSpeed"));

        TableColumn<DroneTypeApp.DroneType, Integer> batteryCapacityColumn = new TableColumn<>("Battery Capacity (mAh)");
        batteryCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("batteryCapacity"));

        TableColumn<DroneTypeApp.DroneType, Integer> controlRangeColumn = new TableColumn<>("Control Range (m)");
        controlRangeColumn.setCellValueFactory(new PropertyValueFactory<>("controlRange"));

        TableColumn<DroneTypeApp.DroneType, Integer> maxCarriageColumn = new TableColumn<>("Max Carriage (g)");
        maxCarriageColumn.setCellValueFactory(new PropertyValueFactory<>("maxCarriage"));

        table.getColumns().addAll(idColumn, manufacturerColumn, typenameColumn, weightColumn, maxSpeedColumn, batteryCapacityColumn, controlRangeColumn, maxCarriageColumn);

        // Set preferred column widths for better spacing
        idColumn.setPrefWidth(50);
        manufacturerColumn.setPrefWidth(200);
        typenameColumn.setPrefWidth(200);
        weightColumn.setPrefWidth(100);
        maxSpeedColumn.setPrefWidth(150);
        batteryCapacityColumn.setPrefWidth(150);
        controlRangeColumn.setPrefWidth(150);
        maxCarriageColumn.setPrefWidth(150);

        table.setPrefHeight(500);
        // Set initial sorting by ID column
        idColumn.setSortType(TableColumn.SortType.ASCENDING);
        table.getSortOrder().add(idColumn);

        // Fetch new data from API asynchronously
        CompletableFuture.runAsync(() -> {
            try {
                fetchAndPopulateTable(table);
                // Apply sorting once data is fetched
                table.sort();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        VBox vbox = new VBox(dashboard, table);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));

        Scene catalogueScene = new Scene(vbox, 1300, 1200);

        primaryStage.setScene(catalogueScene);
        primaryStage.show();
    }

    private void fetchAndPopulateTable(TableView<DroneTypeApp.DroneType> table) throws IOException {
        String endpoint = "/api/dronetypes/";
        String domain = "http://dronesim.facets-labs.com";
        String token = "Token 40a9557fac747f55c11ad20c85caac1d43654911";
        String agent = "Louay";

        api myApi1 = new api(endpoint, domain, token, agent);
        myApi1.createConnection("/api/dronetypes/?limit=20");
        String response1 = myApi1.retrieveResponse();

        JSONArray drones = new JSONObject(response1).getJSONArray("results");

        // Clear previous items in the table
        Platform.runLater(() -> table.getItems().clear());

        for (int i = 0; i < drones.length(); i++) {
            JSONObject drone = drones.getJSONObject(i);
            DroneTypeApp.DroneType droneType = new DroneTypeApp.DroneType(
                    drone.getInt("id"),
                    drone.getString("manufacturer"),
                    drone.getString("typename"),
                    drone.getInt("weight"),
                    drone.getInt("max_speed"),
                    drone.getInt("battery_capacity"),
                    drone.getInt("control_range"),
                    drone.getInt("max_carriage")
            );

            // Add items to the table on the JavaFX Application Thread
            Platform.runLater(() -> table.getItems().add(droneType));
        }
    }

    private VBox createDashboardCatalogue (Stage primaryStage) {
        Button btnMenu = createToolbarButton("Menu", "/image/menu.png");
        VBox menuItemsBox = new VBox(10);
        menuItemsBox.setVisible(false);

        btnMenu.setOnAction(event -> menuItemsBox.setVisible(!menuItemsBox.isVisible()));

        Button btnLogout = createToolbarButton("Logout", "/image/logout.png");
        btnLogout.setOnAction(_ -> showLoginPage(primaryStage));

        Button btnRefresh = createToolbarButton("Refresh", "/image/refresh.png");
        btnRefresh.setOnAction(_ -> {
            try {
                showCataloguePage(primaryStage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button btnBack = createToolbarButton("back", "/image/back.png");
        btnBack.setOnAction(_ -> {
            showMenu(primaryStage);
        });

        HBox hbox = new HBox(btnMenu, btnLogout, btnRefresh, btnBack);
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(200);
        hbox.setPadding(new Insets(5));

        ToolBar toolbar = new ToolBar();
        toolbar.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        toolbar.getItems().add(hbox);
        toolbar.setOpacity(1.0);

        menuItemsBox.getChildren().addAll(
                createSubButton("Drone Flight Dynamic", primaryStage, this::showDynamicPage),
                createSubButton("Drone Catalogue", primaryStage, this::showCataloguePage),
                createSubButton("Drone History", primaryStage, this::showHistoryPage)
        );

        VBox vbox = new VBox(toolbar, menuItemsBox);
        vbox.setSpacing(0);

        return vbox;
    }

    private void showHistoryPage(Stage primaryStage) throws IOException {
        VBox daschbord = createDashboardHistory(primaryStage);

        TableView<DroneApp.Drone> table = new TableView<>();

        TableColumn<DroneApp.Drone, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<DroneApp.Drone, String> dronetypeColumn = new TableColumn<>("Drone Type");
        dronetypeColumn.setCellValueFactory(new PropertyValueFactory<>("dronetype"));

        TableColumn<DroneApp.Drone, String> dronemanufacturerColumn = new TableColumn<>("Drone Manufacturer");
        dronemanufacturerColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));

        TableColumn<DroneApp.Drone, String> createdColumn = new TableColumn<>("Created");
        createdColumn.setCellValueFactory(new PropertyValueFactory<>("created"));

        TableColumn<DroneApp.Drone, String> serialnumberColumn = new TableColumn<>("Serial Number");
        serialnumberColumn.setCellValueFactory(new PropertyValueFactory<>("serialnumber"));

        TableColumn<DroneApp.Drone, Integer> carriageWeightColumn = new TableColumn<>("Carriage Weight");
        carriageWeightColumn.setCellValueFactory(new PropertyValueFactory<>("carriageWeight"));

        TableColumn<DroneApp.Drone, String> carriageTypeColumn = new TableColumn<>("Carriage Type");
        carriageTypeColumn.setCellValueFactory(new PropertyValueFactory<>("carriageType"));

        table.getColumns().addAll(idColumn, dronetypeColumn, dronemanufacturerColumn, createdColumn, serialnumberColumn, carriageWeightColumn, carriageTypeColumn);


        // Parse the JSON data and add to the table
        String endpoint = "/api/drone/";
        String domain = "http://dronesim.facets-labs.com";
        String token = "Token 40a9557fac747f55c11ad20c85caac1d43654911";
        String agent = "Louay";

        api myApi3 = new api(endpoint, domain, token, agent);

        myApi3.createConnection("/api/drones/?limit=30");
        String response3 = myApi3.retrieveResponse();

        JSONArray drones = new JSONObject(response3).getJSONArray("results");

        for (int i = 0; i < drones.length(); i++) {
            JSONObject drone = drones.getJSONObject(i);
            String dronetypeUrl = drone.getString("dronetype");
            String dronetypeName = DroneApp.NameDrone(dronetypeUrl);
            String dronemanufacturer = DroneApp.manufacturerDrone(dronetypeUrl);
            table.getItems().add(new DroneApp.Drone(
                    drone.getInt("id"),
                    dronetypeName,
                    dronemanufacturer,
                    drone.getString("created"),
                    drone.getString("serialnumber"),
                    drone.getInt("carriage_weight"),
                    drone.getString("carriage_type")));
        }




        idColumn.setPrefWidth(50);
        dronetypeColumn.setPrefWidth(200);
        dronemanufacturerColumn.setPrefWidth(200);
        createdColumn.setPrefWidth(200);
        serialnumberColumn.setPrefWidth(250);
        carriageWeightColumn.setPrefWidth(100);
        carriageTypeColumn.setPrefWidth(150);

        // Calculate the height needed for the table to fit all rows
        int rowHeight = 25;
        int rowCount = table.getItems().size();
        table.setPrefHeight(rowHeight * rowCount + 30); // +30 for header and padding
        table.setPrefWidth(1200);


        VBox vbox = new VBox(daschbord, table);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));

        Scene historyScene = new Scene(vbox, 1300, 800);

        primaryStage.setScene(historyScene);
        primaryStage.show();
    }

    private VBox createDashboardHistory (Stage primaryStage) {
        Button btnMenu = createToolbarButton("Menu", "/image/menu.png");
        VBox menuItemsBox = new VBox(10);
        menuItemsBox.setVisible(false);

        btnMenu.setOnAction(event -> menuItemsBox.setVisible(!menuItemsBox.isVisible()));

        Button btnLogout = createToolbarButton("Logout", "/image/logout.png");
        btnLogout.setOnAction(_ -> showLoginPage(primaryStage));

        Button btnRefresh = createToolbarButton("Refresh", "/image/refresh.png");
        btnRefresh.setOnAction(_ -> {
            try {
                showHistoryPage(primaryStage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button btnBack = createToolbarButton("back", "/image/back.png");
        btnBack.setOnAction(_ -> {
            showMenu(primaryStage);
        });

        HBox hbox = new HBox(btnMenu, btnLogout, btnRefresh, btnBack);
        hbox.setAlignment(Pos.CENTER);
        hbox.setSpacing(200);
        hbox.setPadding(new Insets(5));

        ToolBar toolbar = new ToolBar();
        toolbar.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        toolbar.getItems().add(hbox);
        toolbar.setOpacity(1.0);

        menuItemsBox.getChildren().addAll(
                createSubButton("Drone Flight Dynamic", primaryStage, this::showDynamicPage),
                createSubButton("Drone Catalogue", primaryStage, this::showCataloguePage),
                createSubButton("Drone History", primaryStage, this::showHistoryPage)
        );

        VBox vbox = new VBox(toolbar, menuItemsBox);
        vbox.setSpacing(0);

        return vbox;
    }

public static void main(String[] args) {
    launch();
}
}

