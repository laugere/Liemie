package com.example.liemie;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.prefs.PreferenceChangeEvent;

import EDU.purdue.cs.bloat.diva.Main;

public class MainActivity extends AppCompatActivity implements MainFragment.OnButtonClickedListener {

    private Fragment Frgm_login;

    private Button login_cancel;
    private Button login_enter;
    private EditText login_id;
    private EditText login_password;
    private TextView login_error;
    private ProgressBar login_loading;


    private Fragment Frgm_info;

    private TextView info_name;
    private TextView info_numP;
    private TextView info_numF;
    private TextView info_mail;


    private Toolbar toolbar;

    private MenuItem menuConnec;
    private MenuItem menuExport;
    private MenuItem menuImport;
    private MenuItem menuList;
    private MenuItem menuInfo;


    private JSONObject utilisateur;

    private utilisateur utilCourant;

    private String userName;
    private String password;

    private ImageView main_img;
    private FloatingActionButton fab;

    SharedPreferences sharedPreferences;
    public static final String preference = "identifier";


    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        main_img = findViewById(R.id.main_img);
        sharedPreferences = getSharedPreferences(preference, Context.MODE_PRIVATE);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.main_disconnect)).setMessage(getString(R.string.main_disconnectMessage));
                builder.setPositiveButton(R.string.strOK, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        disconnect();
                    }
                });
                builder.setNegativeButton(R.string.strCANCEL, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        // FRAGMENT login

        Frgm_login = getSupportFragmentManager().findFragmentById(R.id.MainFragment);
        Frgm_login.getView().setVisibility(View.GONE);
        login_cancel = Frgm_login.getView().findViewById(R.id.login_cancel);
        login_enter = Frgm_login.getView().findViewById(R.id.login_enter);
        login_id = Frgm_login.getView().findViewById(R.id.login_id);
        login_password = Frgm_login.getView().findViewById(R.id.login_password);
        login_error = Frgm_login.getView().findViewById(R.id.login_error);
        login_loading = Frgm_login.getView().findViewById(R.id.login_loading);

        login_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Frgm_login.getView().setVisibility(View.GONE);
                main_img.setVisibility(View.VISIBLE);
            }
        });
        login_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (login_id.length() < 2 && login_password.length() < 4) {
                    login_error.setText(R.string.error_entry);
                }
                login_loading.setVisibility(View.VISIBLE);
                connexion(login_id.getText().toString(), login_password.getText().toString());
            }
        });

        //FRAGMENT info

        Frgm_info = getSupportFragmentManager().findFragmentById(R.id.Frgm_info);
        Frgm_info.getView().setVisibility(View.GONE);
        info_name = Frgm_info.getView().findViewById(R.id.info_name);
        info_numF = Frgm_info.getView().findViewById(R.id.info_numF);
        info_numP = Frgm_info.getView().findViewById(R.id.info_numP);
        info_mail = Frgm_info.getView().findViewById(R.id.info_mail);

        // PERMISSIONS

        checkPermission();

        // HOME
    }

    private void connexion(String Login, String Password) {
        String url = "http://www.btssio-carcouet.fr/ppe4/public/connect2/";
        url = ((String) url).concat(Login.trim()).concat("/").concat(Password.trim()).concat("/infirmiere");
        String[] mesparams = {url};
        mThreadCon = new Async(MainActivity.this).execute(mesparams);
        userName = Login;
        password = Password;
    }

    public void stopLoading() {
        login_loading.setVisibility(View.GONE);
    }

    public void startLoading() {
        login_loading.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_connect:
                main_img.setVisibility(View.GONE);
                Frgm_login.getView().setVisibility(View.VISIBLE);
                return true;
            case R.id.menu_list:
                Toast.makeText(getApplicationContext(), "clic sur list", Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_import:
                startActivity(new Intent(this, Act_import.class));
                return true;
            case R.id.menu_export:
                Toast.makeText(getApplicationContext(), "clic sur export", Toast.LENGTH_LONG).show();
                return true;
            case R.id.menu_info:
                Frgm_info.getView().setVisibility(View.VISIBLE);
                try {
                    info_name.setText(utilCourant.getPrenom() + " " + utilCourant.getNom());
                    info_numP.setText(utilCourant.getTelPortable());
                    info_numF.setText(utilCourant.getTelFixe());
                    info_mail.setText(utilCourant.getMail());
                }
                catch(Exception e) {
                    alertmsg(getString(R.string.general_message), getString(R.string.offline_message));
                    Frgm_info.getView().setVisibility(View.GONE);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    // Function to check and request permission
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) == true && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) == true && shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) == true) {
            } else {
                askForPermission();
            }
        } else {
            return;
        }
    }

    public void explain() {
        Toast.makeText(getApplicationContext(), R.string.error_permission, Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void askForPermission() {
        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
    }

    // appel internet
    private AsyncTask<String, String, Boolean> mThreadCon = null;

    public void alertmsg(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(title).setMessage(msg);
        builder.setPositiveButton(R.string.strOK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void retourConnexion(StringBuilder sb) {
        int id;
        String nom;
        String prenom;
        String sexe;
        String dateNaissance;
        String dateDeces;
        String ad1;
        String ad2;
        String cp;
        String ville;
        String mail;
        String telFixe;
        String telPort;
        try {
            utilisateur = new JSONObject(sb.toString());
            id = utilisateur.getInt("id");
            nom = utilisateur.getString("nom");
            prenom = utilisateur.getString("prenom");
            sexe = utilisateur.getString("sexe");
            dateNaissance = utilisateur.getString("date_naiss");
            dateDeces = utilisateur.getString("date_deces");
            ad1 = utilisateur.getString("ad1");
            ad2 = utilisateur.getString("ad2");
            cp = utilisateur.getString("cp");
            ville = utilisateur.getString("ville");
            telFixe = utilisateur.getString("tel_fixe");
            telPort = utilisateur.getString("tel_port");
            mail = utilisateur.getString("mail");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userName", userName);
            editor.putString("password", MD5(password));
            editor.commit();
            utilCourant = new utilisateur(id, nom, prenom, sexe, dateNaissance, dateDeces, ad1, ad2, cp, ville, telFixe, telPort, mail);
            Frgm_login.getView().setVisibility(View.GONE);
            menuConnec = toolbar.getMenu().findItem(R.id.menu_connect);
            menuExport = toolbar.getMenu().findItem(R.id.menu_export);
            menuImport = toolbar.getMenu().findItem(R.id.menu_import);
            menuList = toolbar.getMenu().findItem(R.id.menu_list);
            menuInfo = toolbar.getMenu().findItem(R.id.menu_info);
            menuConnec.setVisible(false);
            menuList.setVisible(true);
            menuImport.setVisible(true);
            menuExport.setVisible(true);
            menuInfo.setVisible(true);
        } catch (Exception e) {
            if(sharedPreferences.contains("userName") && sharedPreferences.contains("password")) {
                if(sharedPreferences.getString("userName", "").equals(userName) && sharedPreferences.getString("password", "").equals(MD5(password))) {
                    Frgm_login.getView().setVisibility(View.GONE);
                    menuConnec = toolbar.getMenu().findItem(R.id.menu_connect);
                    menuExport = toolbar.getMenu().findItem(R.id.menu_export);
                    menuImport = toolbar.getMenu().findItem(R.id.menu_import);
                    menuList = toolbar.getMenu().findItem(R.id.menu_list);
                    menuInfo = toolbar.getMenu().findItem(R.id.menu_info);
                    menuConnec.setVisible(false);
                    menuList.setVisible(true);
                    menuImport.setVisible(true);
                    menuExport.setVisible(true);
                    menuInfo.setVisible(true);

                    alertmsg("Connexion hors-ligne", "la connexion est hors ligne");
                }
            }
            else {
                alertmsg("ERREUR DE CONNEXION", "Mot de passe ou identifiant ne correspondent pas.");
            }
        }
    }

    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        } catch(UnsupportedEncodingException ex){
        }
        return null;
    }

    public void disconnect() {
        utilCourant = null;
        menuConnec = toolbar.getMenu().findItem(R.id.menu_connect);
        menuExport = toolbar.getMenu().findItem(R.id.menu_export);
        menuImport = toolbar.getMenu().findItem(R.id.menu_import);
        menuList = toolbar.getMenu().findItem(R.id.menu_list);
        menuInfo = toolbar.getMenu().findItem(R.id.menu_info);
        menuConnec.setVisible(true);
        menuList.setVisible(false);
        menuImport.setVisible(false);
        menuExport.setVisible(false);
        menuInfo.setVisible(false);
        main_img.setVisibility(View.VISIBLE);
    }

    @Override
    public void onButtonClicked(View view) {

    }
}