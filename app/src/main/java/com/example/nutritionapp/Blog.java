package com.example.nutritionapp;

public class Blog {

    public Blog(){

    }

    private String semana, circBrazo, circCadera, circCintura, circMuslo, circPantorrilla;

    public Blog(String semana, String circBrazo, String circCadera, String circCintura, String circMuslo, String circPantorrilla) {
        this.semana = semana;
        this.circBrazo = circBrazo;
        this.circCadera = circCadera;
        this.circCintura = circCintura;
        this.circMuslo = circMuslo;
        this.circPantorrilla = circPantorrilla;
    }

    public String getSemana() {
        return semana;
    }

    public void setSemana(String semana) {
        this.semana = semana;
    }

    public String getCircBrazo() {
        return circBrazo;
    }

    public void setCircBrazo(String circBrazo) {
        this.circBrazo = circBrazo;
    }

    public String getCircCadera() {
        return circCadera;
    }

    public void setCircCadera(String circCadera) {
        this.circCadera = circCadera;
    }

    public String getCircCintura() {
        return circCintura;
    }

    public void setCircCintura(String circCintura) {
        this.circCintura = circCintura;
    }

    public String getCircMuslo() {
        return circMuslo;
    }

    public void setCircMuslo(String circMuslo) {
        this.circMuslo = circMuslo;
    }

    public String getCircPantorrilla() {
        return circPantorrilla;
    }

    public void setCircPantorrilla(String circPantorrilla) {
        this.circPantorrilla = circPantorrilla;
    }
}
