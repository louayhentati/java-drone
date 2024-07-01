package gui;

import API.api;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.json.JSONObject;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

    public class DroneApp {

        private static api initializeApi(String endpoint) {
            return new api(endpoint, "http://dronesim.facets-labs.com",
                    "Token 40a9557fac747f55c11ad20c85caac1d43654911", "Louay");
        }

        public static String fetchDataFromApi(String apiEndpoint) throws IOException {
            api myApi = initializeApi(apiEndpoint);
            myApi.createConnection(apiEndpoint);
            return myApi.retrieveResponse();
        }

        public static String NameDrone(String dronetypeUrl) throws IOException {
            // Extract the relevant part of the URL
            String[] urlParts = dronetypeUrl.split("/");
            String apiEndpoint = "/api/dronetypes/" + urlParts[urlParts.length - 1] + "/";
            String response = fetchDataFromApi(apiEndpoint);

            // Parse the JSON response
            JSONObject responseObject = new JSONObject(response);

            // Extract typename
            return responseObject.getString("typename");
        }

        public static String manufacturerDrone(String dronetypeUrl) throws IOException {
            // Extract the relevant part of the URL
            String[] urlParts = dronetypeUrl.split("/");
            String apiEndpoint = "/api/dronetypes/" + urlParts[urlParts.length - 1] + "/";
            String response = fetchDataFromApi(apiEndpoint);

            // Parse the JSON response
            JSONObject responseObject = new JSONObject(response);

            // Extract manufacturer
            return responseObject.getString("manufacturer");
        }

        public static class Drone {
            private final SimpleIntegerProperty id;
            private final SimpleStringProperty dronetype;
            private final SimpleStringProperty manufacturer;
            private final SimpleStringProperty created;
            private final SimpleStringProperty serialnumber;
            private final SimpleIntegerProperty carriageWeight;
            private final SimpleStringProperty carriageType;

            public Drone(int id, String dronetype, String manufacturer, String created, String serialnumber, int carriageWeight, String carriageType) {
                this.id = new SimpleIntegerProperty(id);
                this.dronetype = new SimpleStringProperty(dronetype);
                this.manufacturer = new SimpleStringProperty(manufacturer);
                this.created = new SimpleStringProperty(created);
                this.serialnumber = new SimpleStringProperty(serialnumber);
                this.carriageWeight = new SimpleIntegerProperty(carriageWeight);
                this.carriageType = new SimpleStringProperty(carriageType);
            }

            public int getId() {
                return id.get();
            }

            public String getDronetype() {
                return dronetype.get();
            }

            public String getManufacturer() {
                return manufacturer.get();
            }

            public String getCreated() {
                // Parse the current timestamp string to LocalDateTime
                LocalDateTime dateTime = LocalDateTime.parse(created.get(), DateTimeFormatter.ISO_DATE_TIME);

                // Define a date-time formatter with the desired format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMMM-dd HH:mm:ss");

                // Format the current date-time and return the formatted string
                return dateTime.format(formatter);
            }

           public String getSerialnumber() {
                return serialnumber.get();
            }
            public int getCarriageWeight() {
                return carriageWeight.get();
            }

            public String getCarriageType() {
                return carriageType.get();
            }
        }
    }