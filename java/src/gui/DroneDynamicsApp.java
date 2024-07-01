package gui;

import API.api;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.json.JSONObject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DroneDynamicsApp  {
    public static Integer id(String dronetypeUrl) throws IOException {
        // Extract the relevant part of the URL
        String[] urlParts = dronetypeUrl.split("/");
        String apiEndpoint = "/api/drones/" + urlParts[urlParts.length - 1]+"/";
        // Initialize the API connection
        api myApi4 = new api("/api/drones/", "http://dronesim.facets-labs.com", "Token 40a9557fac747f55c11ad20c85caac1d43654911", "Louay");
        myApi4.createConnection(apiEndpoint);
        String response4 = myApi4.retrieveResponse();
        // Parse the JSON response
        JSONObject responseObject = new JSONObject(response4);
        // Extract manufacturer and typename
        Integer id = responseObject.getInt("id");
        return id;
    }



    public static class DroneDynamics {
        private final SimpleIntegerProperty drone;
        private final SimpleStringProperty timestamp;
        private final SimpleIntegerProperty speed;
        private final SimpleDoubleProperty alignRoll;
        private final SimpleDoubleProperty alignPitch;
        private final SimpleDoubleProperty alignYaw;
        private final SimpleDoubleProperty longitude;
        private final SimpleDoubleProperty latitude;
        private final SimpleIntegerProperty batteryStatus;
        private final SimpleStringProperty lastSeen;
        private final SimpleStringProperty status;


        public DroneDynamics(int drone, String timestamp, int speed, double alignRoll, double alignPitch, double alignYaw, double longitude, double latitude, int batteryStatus, String lastSeen, String status ) {
            this.drone = new SimpleIntegerProperty(drone);
            this.timestamp = new SimpleStringProperty(timestamp);
            this.speed = new SimpleIntegerProperty(speed);
            this.alignRoll = new SimpleDoubleProperty(alignRoll);
            this.alignPitch = new SimpleDoubleProperty(alignPitch);
            this.alignYaw = new SimpleDoubleProperty(alignYaw);
            this.longitude = new SimpleDoubleProperty(longitude);
            this.latitude = new SimpleDoubleProperty(latitude);
            this.batteryStatus = new SimpleIntegerProperty(batteryStatus);
            this.lastSeen = new SimpleStringProperty(lastSeen);
            this.status = new SimpleStringProperty(status);

        }


        public int getDrone() {
            return drone.get();
        }

        public String getTimestamp() {

            // Parse the current timestamp string to LocalDateTime
            LocalDateTime dateTime = LocalDateTime.parse(timestamp.get(), DateTimeFormatter.ISO_DATE_TIME);

            // Define a date-time formatter with the desired format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMMM-dd HH:mm:ss");

            // Format the current date-time and return the formatted string
            return dateTime.format(formatter);
        }

        public int getSpeed() {
            return speed.get();
        }

        public double getAlignRoll() {
            return alignRoll.get();
        }

        public double getAlignPitch() {
            return alignPitch.get();
        }

        public double getAlignYaw() {
            return alignYaw.get();
        }

        public double getLongitude() {
            return longitude.get();
        }

        public double getLatitude() {
            return latitude.get();
        }

        public int getBatteryStatus() {
            int maxBatteryCapacity;
            switch (drone.get()) {
                case 1:
                    maxBatteryCapacity = 5100;
                    break;
                case 2:
                    maxBatteryCapacity = 380;
                    break;
                case 3:
                    maxBatteryCapacity = 7500;
                    break;
                case 4:
                    maxBatteryCapacity = 5000;
                    break;
                case 5:
                    maxBatteryCapacity = 5400;
                    break;
                case 6:
                    maxBatteryCapacity = 5870;
                    break;
                case 7:
                    maxBatteryCapacity = 5400;
                    break;
                case 8:
                    maxBatteryCapacity =7100 ;
                    break;
                case 9:
                    maxBatteryCapacity = 2700;
                    break;
                case 10:
                    maxBatteryCapacity = 4280;
                    break;
                case 11:
                    maxBatteryCapacity = 500;
                    break;
                case 12:
                    maxBatteryCapacity = 100;
                    break;
                case 13:
                    maxBatteryCapacity = 150;
                    break;
                case 14:
                    maxBatteryCapacity = 500;
                    break;
                case 15:
                    maxBatteryCapacity = 3500;
                    break;
                case 16:
                    maxBatteryCapacity = 1100;
                    break;
                case 17:
                    maxBatteryCapacity = 750;
                    break;
                case 18:
                    maxBatteryCapacity = 550;
                    break;
                case 19:
                    maxBatteryCapacity = 2800;
                    break;
                case 20:
                    maxBatteryCapacity = 2500;
                    break;
                default:
                    maxBatteryCapacity = 7100; // Handle unrecognized drone types
                    break;
            }

            // Calculate battery percentage
            float batteryPercentage = ((float) batteryStatus.get() / maxBatteryCapacity) * 100;
            return Math.round(batteryPercentage);


            /*
            int currentBatteryStatus = batteryStatus.get();
            int maxBatteryCapacity = droneType.getBatteryCapacity(); // Accessing battery capacity from DroneType instance
            float batteryPercentage = ((float) currentBatteryStatus / maxBatteryCapacity) * 100;
            return Math.round(batteryPercentage);*/
           // return batteryStatus.get();
        }

        public String getLastSeen() {
            return lastSeen.get();
        }

        public String getStatus() {
            return status.get();
        }
    }
}