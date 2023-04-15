package com.example.qrevenement;

import android.content.Context;
import android.os.Environment;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;
import com.example.qrevenement.DBHandler;



public class xls {
    File directory, sd, file;
    WritableWorkbook workbook;
    private Context context;

    // constructor
    public  xls (Context context){
        this.context = context;
    }
    public String xls_write(String qr_code_scan_data){
        DBHandler dbHandler = new DBHandler(context);
        String csvFile = "evenement.xls";
        sd = Environment.getExternalStorageDirectory();
        directory = new File(sd.getAbsolutePath());
        file = new File(directory, csvFile);
         String numero_tel;
         String msg;



        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("fr", "FR"));
        /*Toast.makeText(getApplicationContext(),csvFile+"a ete creer", Toast.LENGTH_SHORT).show();*/
        try {

            workbook = Workbook.createWorkbook(file, wbSettings);
            String str = qr_code_scan_data;
            String[] qr_code_words_split = str.split("-");
            //private ArrayList<modal_xls> modal_xls;

            // recupere le nom sur le code qr les data du code QR.
            String nom_complet = qr_code_words_split[2];
            numero_tel = qr_code_words_split[0];
            String participant = qr_code_words_split[1];
            // la fonction pour ecrire dans le fichier xls
            dbHandler.addNewParticipant(nom_complet, numero_tel,participant);
            createFirstSheet(nom_complet, participant,  numero_tel);

            //closing cursor
            workbook.write();
            workbook.close();
            msg = "succed";
        } catch (Exception e) {
            e.printStackTrace();
            msg =  "echec";
        }
        return msg;
    }
    private void createFirstSheet(String nom_complet ,String participant, String numero_tel) {
        try {
            DBHandler dbHandler = new DBHandler(context);
            List<modal_xls> listdata;
            // list from db handler class.
            listdata= dbHandler.readParticipant();
            listdata.add(new modal_xls(nom_complet,participant,numero_tel));
            //listdata.add(new QrscanInterroModal("mr","firstName1","middleName1","lastName1"));
            //listdata.add(new QrscanInterroModal("mr","firstName1","middleName1","lastName1"));
            //Excel sheet name. 0 (number)represents first sheet
            WritableSheet sheet = workbook.createSheet("sheet1", 0);
            // column and row title
            sheet.addCell(new Label(0, 0, "Nom complet"));
            sheet.addCell(new Label(1, 0, "Numero de telephone"));
            sheet.addCell(new Label(2, 0, "type de participant"));



            for (int i = 0; i < listdata.size(); i++) {
                modal_xls modal = listdata.get(i);
                sheet.addCell(new Label(0, i + 1, modal.getNom_complet()));
                sheet.addCell(new Label(1, i + 1, modal.getNumero_tel()));
                sheet.addCell(new Label(2, i + 1, modal.getPraticipant()));
            }


    } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
