package gui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class DroneTypeApp  {


    public static class DroneType {
        private final SimpleIntegerProperty id;
        private final SimpleStringProperty manufacturer;
        private final SimpleStringProperty typename;
        private final SimpleIntegerProperty weight;
        private final SimpleIntegerProperty maxSpeed;
        private final SimpleIntegerProperty batteryCapacity;
        private final SimpleIntegerProperty controlRange;
        private final SimpleIntegerProperty maxCarriage;

        public DroneType(int id, String manufacturer, String typename, int weight, int maxSpeed, int batteryCapacity, int controlRange, int maxCarriage) {
            this.id = new SimpleIntegerProperty(id);
            this.manufacturer = new SimpleStringProperty(manufacturer);
            this.typename = new SimpleStringProperty(typename);
            this.weight = new SimpleIntegerProperty(weight);
            this.maxSpeed =new SimpleIntegerProperty(maxSpeed) ;
            this.batteryCapacity =new SimpleIntegerProperty(batteryCapacity) ;
            this.controlRange =new SimpleIntegerProperty (controlRange);
            this.maxCarriage = new SimpleIntegerProperty (maxCarriage);
        }

        public int getId() {
            return id.get();
        }

        public String getManufacturer() {
            return manufacturer.get();
        }

        public String getTypename() {
            return typename.get();
        }

        public int getWeight() {
            return weight.get();
        }

        public int getMaxSpeed() {
            return maxSpeed.get();
        }

        public int getBatteryCapacity() {
            return batteryCapacity.get();
        }

        public int getControlRange() {
            return controlRange.get();
        }

        public int getMaxCarriage() {
            return maxCarriage.get();
        }
    }
}
