package com.example.liemie;

import android.os.Environment;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Modele {
    private String db4oFileName;
    private ObjectContainer dataBase;
    private File appDir;

    public void open(){
        db4oFileName = Environment.getExternalStorageDirectory() + "/baseDB4o" + "/BasePPE4.db4o";
        dataBase = Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), db4oFileName);
    }

    public void createDirectory() {
        appDir = new File(Environment.getExternalStorageDirectory() + "/baseDB4o");
        if (!appDir.exists() && !appDir.isDirectory()) {
            appDir.mkdir();
        }
    }

    public ArrayList<Visite> listeVisite() {
        open();
        ArrayList<Visite> listeVisite = new ArrayList<Visite>();
        ObjectSet<Visite> result = dataBase.queryByExample(Visite.class);
        while (result.hasNext()) {
            listeVisite.add(result.next());
        }
        dataBase.close();
        return listeVisite;
    }

    public Visite trouveVisite (int id) {
        open();
        Visite vretour = new Visite();
        vretour.setId(id);
        ObjectSet<Visite> result = dataBase.queryByExample(vretour);
        vretour = (Visite) result.next();
        dataBase.close();
        return vretour;
    }

    public void saveVisite(Visite Visite) {
        open();
        Visite vretour = new Visite();
        vretour.setId(Visite.getId());
        ObjectSet<Visite> result = dataBase.queryByExample(vretour);
        if (result.size() == 0) {
            dataBase.store(Visite);
        } else {
            vretour = (Visite) result.next();
            vretour.recopieVisite(Visite);
            dataBase.store(vretour);
        }
        dataBase.close();
    }

    // deleteVisite() qui permet de supprimer toutes les instances de la classe Visite

    public void deleteVisite() {
        open();
        ObjectSet<Visite> result = dataBase.queryByExample(Visite.class);
        while (result.hasNext()) {
            dataBase.delete(result.next());
        }
        dataBase.close();
    }

    // addVisite(ArrayList<Visite> vVisite) qui, à partir d'une collection de Visite, va les ajouter à DB4o. Nous ne testerons pas l'existence de ces objets puisque c'est une création (appel de la méthode après l'appel de deleteVisite()).

    public void addVisite(ArrayList<Visite> vVisite) {
        open();
        for (Visite v : vVisite) {
            dataBase.store(v);
        }
        dataBase.close();
    }

}
