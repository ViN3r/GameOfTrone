package mabillot.sar.upmc.gameoftrone.outils;

import android.app.Activity;
import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mabillot.sar.upmc.gameoftrone.MainActivity;
import mabillot.sar.upmc.gameoftrone.R;
import mabillot.sar.upmc.gameoftrone.metier.Localisation;
import mabillot.sar.upmc.gameoftrone.metier.Toilette;

/**
 * Created by paulo on 09/01/2016.
 */
public class APIUtil {

    public static List<Toilette> initializeToilette(Activity activity){
        List<Toilette> toilettes = run(activity);
        return toilettes;
    }

    public static List<Toilette> run(Activity activity) {
        List<Toilette> toilettes = new ArrayList<>();
        String[] localisations = activity.getResources().getStringArray(R.array.toilettes);
        for(int i = 0; i < localisations.length; i++){
            String[] explode = localisations[i].split(",");
            toilettes.add(new Toilette((int) (Math.random()*10)+1, new Localisation(Float.parseFloat(explode[0]), Float.parseFloat(explode[1]))));
        }

        //Initilize isConquis
        if(toilettes.size() > 0){
            Util.loadToilettes(activity, toilettes);
        }

        return toilettes;
    }
}
