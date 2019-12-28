package net.lingin.max.android.ui.model;

public class BluetoothDeviceDTO {

    private String name;

    private String mac;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public BluetoothDeviceDTO(String name, String mac) {
        this.name = name;
        this.mac = mac;
    }
}
