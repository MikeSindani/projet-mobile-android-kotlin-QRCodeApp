package com.example.qrevenement;

public class modal_xls {
        // variables for our coursename,
        // description, tracks and duration, id.
        private String nom_complet;
        private String praticipant;
        private String numero_tel;

        // constructor
        public modal_xls (String nom_complet, String praticipant, String numero_tel) {
            this.nom_complet = nom_complet;
            this.praticipant = praticipant;
            this.numero_tel = numero_tel;
        }

        public String getNom_complet() {
            return nom_complet;
        }

        public void setNom_complet(String nom_complet) {
            this.nom_complet = nom_complet;
        }

        public String getPraticipant() {
            return praticipant;
        }

        public void setPraticipant(String praticipant) {
            this.praticipant = praticipant;
        }

        public String getNumero_tel() {
            return numero_tel;
        }

        public void setNumero_tel(String numero_tel) {
            this.numero_tel = numero_tel;
        }
    }
