package com.example.liemie;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Async extends AsyncTask<String, String, Boolean> {
    public Async() {
        super();
    }

    private WeakReference<Activity> activityAppelante = null;
    private String classActivityAppelante;
    private StringBuilder stringBuilder = new StringBuilder();

    public Async(Activity pActivity) {
        activityAppelante = new WeakReference<Activity>(pActivity);
        classActivityAppelante = pActivity.getClass().toString();
    }

    @Override
    protected void onPreExecute() {
        if (activityAppelante.get() != null)
            ((MainActivity) activityAppelante.get()).startLoading();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (activityAppelante.get() != null) {
            if (result) {
                if (classActivityAppelante.contains("MainActivity")) {
                    ((MainActivity) activityAppelante.get()).retourConnexion(stringBuilder);
                    ((MainActivity) activityAppelante.get()).stopLoading();
                }
            } else Toast.makeText(activityAppelante.get(), "Fin ko", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected Boolean doInBackground(String... params) {// Exécution en arrière plan
        String vurl = "";
        vurl = params[0];
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(vurl);
            urlConnection = (HttpURLConnection) url.openConnection();
            if (!classActivityAppelante.contains("MainActivity")) {
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");//ou put ou delete selon le web service
                OutputStreamWriter out = new OutputStreamWriter(urlConnection.getOutputStream()); //selon l'activity appelante on peut passer des paramètres en JSON exemple util pour export /* if (classActivityAppelante.contains("ActImport")) { // Création objet jsonn clé valeur jsonParam.put("xxxx", xxxx); jsonParam.put("xxxx", xxxx); out.write(jsonParam.toString()); out.flush(); }*/

                out.close();
            } else {
                urlConnection.setRequestMethod("GET");
            }
            urlConnection.setConnectTimeout(4000); // récupération du serveur
            int HttpResult = urlConnection.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line);
                }
                br.close(); /* String[] vstring0 = {"Reçu du serveur", stringBuilder.toString()}; publishProgress(vstring0); */
            } else {
                String[] vstring0 = {"Erreur", urlConnection.getResponseMessage()};
                publishProgress(vstring0);
            }
        } catch (MalformedURLException e) {
            String[] vstring0 = {"Erreur", "Pbs url"};
            publishProgress(vstring0);
            return false;
        } catch (java.net.SocketTimeoutException e) {
            String[] vstring0 = {"Erreur", "temps trop long"};
            publishProgress(vstring0);
            return false;
        } catch (IOException e) {

            //String[] vstring0 = {"Erreur", "Pbs IO->".concat(e.getMessage())};
            //publishProgress(vstring0);
            return true;
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(String... param) { // utilisation de on progress pour afficher des message pendant le doInBackground
        if (classActivityAppelante.contains("MainActivity")) {
            ((MainActivity) activityAppelante.get()).alertmsg(param[0].toString(), param[1].toString());
        }
    }

    @Override
    protected void onCancelled() {
        if (activityAppelante.get() != null)
            Toast.makeText(activityAppelante.get(), "Annulation", Toast.LENGTH_SHORT).show();
    }
}

