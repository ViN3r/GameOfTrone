package mabillot.sar.upmc.gameoftrone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import mabillot.sar.upmc.gameoftrone.metier.Toilette;
import mabillot.sar.upmc.gameoftrone.outils.Util;

public class ConnectActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        // show the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);

        /* Set cursor to the end */
        EditText input = (EditText)findViewById(R.id.logPseudo);
        input.setSelection(input.getText().length());
        input = (EditText)findViewById(R.id.logSlogan);
        input.setSelection(input.getText().length());

        /* Animations */
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        Animation translateAnimation = new TranslateAnimation(0, 0, -getWindowManager().getDefaultDisplay().getHeight()+200, 0);
        translateAnimation.setDuration(3000);
        iv.startAnimation(translateAnimation);

        TextView tv = (TextView) findViewById(R.id.title);
        translateAnimation = new TranslateAnimation(0, 0, -getWindowManager().getDefaultDisplay().getHeight()*2, 300);
        translateAnimation.setDuration(4000);
        translateAnimation.setFillAfter(true);
        tv.startAnimation(translateAnimation);

        //Load save if existe
        Util.loadInfos(this);
    }

    /* Appel quand on appui sur le bouton de connexion  */
    public void onConnectAction(View v){
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.putExtra("nom", ((TextView) findViewById(R.id.logPseudo)).getText().toString());
        myIntent.putExtra("slogan", ((TextView) findViewById(R.id.logSlogan)).getText().toString());
        startActivity(myIntent);
        finish();
    }


}
