package mabillot.sar.upmc.gameoftrone.outils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.SphericalUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import mabillot.sar.upmc.gameoftrone.ConnectActivity;
import mabillot.sar.upmc.gameoftrone.MainActivity;
import mabillot.sar.upmc.gameoftrone.R;
import mabillot.sar.upmc.gameoftrone.metier.Toilette;

/**
 * Created by paulo on 29/01/2016.
 */
public class Util {
    public static final String fileNameInfo = "GotInfo";
    public static final String fileName = "GotToilette";

    public static float distance(LatLng l1, LatLng l2){
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(l2.latitude -l1.latitude);
        double lngDiff = Math.toRadians(l2.longitude-l1.longitude);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(l1.latitude)) * Math.cos(Math.toRadians(l2.longitude)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }


    public static LatLngBounds convertCenterAndRadiusToBounds(LatLng center, double radius) {
        LatLng southwest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 225);
        LatLng northeast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2.0), 45);
        return new LatLngBounds(southwest, northeast);
    }



    /*
     * Save user name and slogan
     * Save the toilettes list
     */
    public static void saveToilettes(MainActivity activity){

        try {
            FileOutputStream goTSave = activity.openFileOutput(fileName, Context.MODE_PRIVATE);
            //toilettes
            for (Toilette toilette : activity.getArbitre().getToilettes()){
                if(toilette.isConquis()){
                    goTSave.write("1".getBytes());
                }else{
                    goTSave.write("0".getBytes());
                }
            }
            goTSave.close();
        } catch (java.io.IOException e) {
            Log.e("File", "File Not Found: " + fileName);
        }
    }

    /*
     * Save user name and slogan
     */
    public static void saveInfos(MainActivity activity){

        try {
            FileOutputStream goTSave = activity.openFileOutput(fileNameInfo, Context.MODE_PRIVATE);
            //Player
            goTSave.write((activity.getArbitre().getJoueur().getNom() + "-" + activity.getArbitre().getJoueur().getSlogan()).getBytes());
            goTSave.close();
        } catch (java.io.IOException e) {
            Log.e("File", "File Not Found: " + fileNameInfo);
        }
    }

    /*
     * Try to load player toilette
     */
    public static  void loadToilettes(Activity activity, List<Toilette> toilettes){
        try {
            FileInputStream goTSave = activity.openFileInput(fileName);
            byte[] buffer = new byte[1024];
            int n;
            String line;
            int cpt = 0;

            while ((n = goTSave.read(buffer)) != -1) {
                line = new String(buffer, 0, n);
                for (int i = 0; i < line.length(); i++) {
                    int res = Integer.parseInt(line.charAt(i) + "");
                    if (res == 1) {
                        toilettes.get(cpt).setIsConquis(true);
                    }
                    cpt++;
                }
            }

            goTSave.close();
        } catch (java.io.IOException e) {
            Log.e("File", "File Not Found: " + fileName);
        }
    }


    /*
     * Try to load player toilette
     */
    public static void loadInfos(ConnectActivity activity){
        try {
            FileInputStream goTSave = activity.openFileInput(Util.fileNameInfo);
            byte[] buffer = new byte[1024];
            String line = new String(buffer, 0, goTSave.read(buffer));

            //Parsing

            String[] infos = line.split("-");
            if(infos.length == 2 && infos[0].length() > 0 && infos[1].length() > 0) {
                ((EditText) activity.findViewById(R.id.logPseudo)).setText(infos[0]);
                ((EditText) activity.findViewById(R.id.logSlogan)).setText(infos[1]);
            }

        } catch (java.io.IOException e) {
            Log.e("File", "File Not Found: " + Util.fileNameInfo);
        }
    }

    public static void saveBitmap(Bitmap bmp, Activity a) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File(a.getFilesDir()+ "/avatar"));
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                Log.e("Img", "Save error");
            }
        }
    }

    public static Bitmap load(Activity a) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(a.getFilesDir()+ "/avatar", options);
    }

}
