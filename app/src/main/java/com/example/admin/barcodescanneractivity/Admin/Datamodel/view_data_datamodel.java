package com.example.admin.barcodescanneractivity.Admin.Datamodel;

public class view_data_datamodel {

    String correct_barcode;

    public String getCorrect_barcode() {
        return correct_barcode;
    }

    public void setCorrect_barcode(String correct_barcode) {
        this.correct_barcode = correct_barcode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInvoice_number() {
        return invoice_number;
    }

    public void setInvoice_number(String invoice_number) {
        this.invoice_number = invoice_number;
    }

    public String getPart_name() {
        return part_name;
    }

    public void setPart_name(String part_name) {
        this.part_name = part_name;
    }

    public String getPart_quantity() {
        return part_quantity;
    }

    public void setPart_quantity(String part_quantity) {
        this.part_quantity = part_quantity;
    }

    public String getVehicle_number() {
        return vehicle_number;
    }

    public void setVehicle_number(String vehicle_number) {
        this.vehicle_number = vehicle_number;
    }

    public String getWrong_barcode() {
        return wrong_barcode;
    }

    public void setWrong_barcode(String wrong_barcode) {
        this.wrong_barcode = wrong_barcode;
    }

    public view_data_datamodel(String correct_barcode, String date, String invoice_number, String part_name, String part_quantity, String vehicle_number, String wrong_barcode) {
        this.correct_barcode = correct_barcode;
        this.date = date;
        this.invoice_number = invoice_number;
        this.part_name = part_name;
        this.part_quantity = part_quantity;
        this.vehicle_number = vehicle_number;
        this.wrong_barcode = wrong_barcode;
    }


    public view_data_datamodel(){

    }


    String date;
    String invoice_number;
    String part_name;
    String part_quantity;
    String vehicle_number;
    String wrong_barcode;

}
