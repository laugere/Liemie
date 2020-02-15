package com.example.liemie;

import java.util.Date;

public class utilisateur {
    private int id;
    private String nom;
    private String prenom;
    private String sexe;
    private String dateNaissance;
    private String dateDeces;
    private String ad1;
    private String ad2;
    private String cp;
    private String ville;
    private String telFixe;
    private String telPortable;
    private String mail;

    public utilisateur(int unId, String unNom, String unPrenom, String unSexe, String uneDateNaissance, String unDateDeces, String unAd1, String unAd2, String unCp, String uneVille, String unTelFixe, String unTelPort, String unMail) {
        this.id = unId;
        this.nom = unNom;
        this.prenom = unPrenom;
        this.sexe = unSexe;
        this.dateNaissance = uneDateNaissance;
        this.dateDeces = unDateDeces;
        this.ad1 = unAd1;
        this.ad2 = unAd2;
        this.cp = unCp;
        this.ville = uneVille;
        this.telFixe = unTelFixe;
        this.telPortable = unTelPort;
        this.mail = unMail;
    }

    public String getNom() {
        return this.nom;
    }

    public String getPrenom() {
        return this.prenom;
    }

    public String getSexe() {
        return this.sexe;
    }

    public String getDateNaissance() {
        return this.dateNaissance;
    }

    public String getDateDeces() {
        return this.dateDeces;
    }

    public String getVille() {
        return this.ville;
    }

    public String getMail() {
        return this.mail;
    }

    public String getTelFixe() {
        return this.telFixe;
    }

    public String getTelPortable() {
        return this.telPortable;
    }
}
