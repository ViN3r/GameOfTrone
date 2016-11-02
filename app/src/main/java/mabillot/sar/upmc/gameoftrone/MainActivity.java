package mabillot.sar.upmc.gameoftrone;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.FileInputStream;
import java.io.FileOutputStream;

import mabillot.sar.upmc.gameoftrone.fragment.FragmentMap;
import mabillot.sar.upmc.gameoftrone.fragment.FragmentProfile;
import mabillot.sar.upmc.gameoftrone.fragment.FragmentWeapon;
import mabillot.sar.upmc.gameoftrone.metier.Arbitre;
import mabillot.sar.upmc.gameoftrone.metier.Toilette;
import mabillot.sar.upmc.gameoftrone.outils.Util;
import mabillot.sar.upmc.gameoftrone.sound.SoundBackgroundService;
import mabillot.sar.upmc.gameoftrone.sound.SoundSwordBackground;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final static int PICK_IMAGE_REQUEST = 1;
    private Fragment[] fragments = {new FragmentMap(), new FragmentProfile(), new FragmentWeapon()};
    private Intent backgroundIntent;
    private Intent swordIntent;
    private boolean musicIsPlaying = true;
    private boolean loadedToilette = false;
    private static final String fileName = "GotSave4";
    private int fragmentCourant = 0;
    private ImageButton imgButton;
    private Menu menu;

    private static Arbitre arbitre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        String nom = intent.getStringExtra("nom");
        String slogan = intent.getStringExtra("slogan");


        /* Test si changement d'orientation */
        /* recuperation des informations de connexion */
        MainActivity.arbitre = initArbitre(nom, slogan);

        //Navigation permission SDK 23
        //TODO: A DEPLACER DANS MAP
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

    }

    @Override
    public void onStart(){
        super.onStart();

        // On demarre le son
        if(musicIsPlaying) {
            this.launchSoundBackground();
        }

        /* Load first fragment */
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contentFragment, fragments[fragmentCourant]);
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            this.editJoueur();
        } catch(NullPointerException e){
            Log.e("Loaded Layout", "Layout pas encore là");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        /* Sauvegarde musique */
        outState.putBoolean("musicIsPlaying", this.musicIsPlaying);

        /* Sauvegarde Fragment courant */
        outState.putInt("fragmentCourant", this.fragmentCourant);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        /* Recharge musique */
        this.musicIsPlaying = savedInstanceState.getBoolean("musicIsPlaying");

        /* Sauvegarde Fragment courant */
        fragmentCourant = savedInstanceState.getInt("fragmentCourant");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == 1 && resultCode == RESULT_OK
                    && null != data) {

                Uri selectedImage = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);

                //Resize
                Matrix m = new Matrix();
                m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0, 0, imgButton.getWidth(), imgButton.getHeight()), Matrix.ScaleToFit.CENTER);
                Bitmap b = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);

                imgButton.setImageBitmap(b);
                onResume();
            } else {
                Log.e("Img", "Please select an image");
            }
        } catch (Exception e) {
            Log.e("Img", "Problem ...");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        try {
            editJoueur();


            //AvatarImage
            imgButton = (ImageButton) findViewById(R.id.avatar);
            Bitmap bitmap = Util.load(this);
            if(bitmap != null){
                imgButton.setImageBitmap(bitmap);
            }

            if(imgButton == null){
                Log.e("ImgImage ", "pas init");
            }
            imgButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, Uri.parse("content://media/internal/images/media"));
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

                }
            });

        }catch(NullPointerException ex){
            Log.e("Layout", "Layout pas encore là");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sound){
            if (musicIsPlaying){
                getApplicationContext().stopService(backgroundIntent);
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.pause));
            } else {
                launchSoundBackground();
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.play));
            }
            musicIsPlaying = !musicIsPlaying;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = null;
        if (id == R.id.profile) {
            fragment = fragments[1];
            this.fragmentCourant = 1;
        } else if (id == R.id.map) {
            fragment = fragments[0];
            this.fragmentCourant = 0;
        } else if (id == R.id.weapon) {
            fragment = fragments[2];
            this.fragmentCourant = 2;
        } else if (id == R.id.nav_share) {
            //TODO: a modifier quand share ok
            fragment = new FragmentProfile();
        }


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.contentFragment, fragment);
        ft.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static Arbitre getArbitre() {
        return arbitre;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(new Intent(this, SoundBackgroundService.class));

        //Save the game
        Util.saveInfos(this);
        Util.saveToilettes(this);
        Util.saveBitmap(((BitmapDrawable)imgButton.getDrawable()).getBitmap(), this);
    }

    /* Fonction à call au demarrage de l'application */
    private Arbitre initArbitre(String nom, String slogan){
        /* Initialisation de la liste d'arme depuis res/weapons.xml */
        String[] armes = getResources().getStringArray(R.array.armes);
        Arbitre a = new Arbitre(armes, nom, slogan, this, loadedToilette);
        this.loadedToilette = true;
        Toast.makeText(this, "Toilettes disponibles: " + a.getToilettes().size(), Toast.LENGTH_SHORT).show();

        return a;
    }

    /* Mise a jour de l'affichage du profile */
    public void editJoueur(){
        TextView tvPseudo = (TextView) findViewById(R.id.pseudo);
        TextView tvSlogan = (TextView) findViewById(R.id.slogan);
        tvPseudo.setText(arbitre.getJoueur().getNom());
        tvSlogan.setText(arbitre.getJoueur().getSlogan());
    }

    /* Appel quand on appui sur le bouton save de la modification de profile */
    public void onSaveAction(View v){
        String pseudo = ((TextView) findViewById(R.id.editPseudo)).getText().toString();
        String slogan = ((TextView) findViewById(R.id.editSlogan)).getText().toString();

        arbitre.getJoueur().setNom(pseudo);
        arbitre.getJoueur().setSlogan(slogan);

        editJoueur();

        Toast.makeText(this, "Pseudo bien édité", Toast.LENGTH_SHORT).show();
    }

    /* Attack the selected marker */
    public void onAttackAction(View v){
        FragmentMap fragMap = (FragmentMap) fragments[0];
        if(fragMap.attackToilette()) {

            //Start sound
            swordIntent = new Intent(getBaseContext(), SoundSwordBackground.class);
            getApplicationContext().startService(swordIntent);

            //Start animation
            ImageView iv = (ImageView) findViewById(R.id.sword1);
            iv.setVisibility(View.VISIBLE);
            Animation translateAnimation = new TranslateAnimation(-iv.getWidth(), getWindowManager().getDefaultDisplay().getWidth() / 2 - iv.getWidth() / 2, getWindowManager().getDefaultDisplay().getHeight() / 2, getWindowManager().getDefaultDisplay().getHeight() / 2);
            translateAnimation.setDuration(500);
            translateAnimation.setFillAfter(false);
            iv.setVisibility(View.INVISIBLE);

            ImageView iv2 = (ImageView) findViewById(R.id.sword2);
            iv2.setVisibility(View.VISIBLE);
            Animation translateAnimation2 = new TranslateAnimation(getWindowManager().getDefaultDisplay().getWidth() + iv.getWidth(), getWindowManager().getDefaultDisplay().getWidth() / 2 - iv.getWidth() / 2, getWindowManager().getDefaultDisplay().getHeight() / 2, getWindowManager().getDefaultDisplay().getHeight() / 2);
            translateAnimation2.setDuration(500);
            translateAnimation2.setFillAfter(false);
            iv2.setVisibility(View.INVISIBLE);

            iv2.startAnimation(translateAnimation2);
            iv.startAnimation(translateAnimation);
        }
    }

    private void launchSoundBackground(){
        this.backgroundIntent = new Intent(getBaseContext(), SoundBackgroundService.class);
        getApplicationContext().startService(backgroundIntent);
    }

}
