package mabillot.sar.upmc.gameoftrone.metier;

import android.app.Activity;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import mabillot.sar.upmc.gameoftrone.MainActivity;
import mabillot.sar.upmc.gameoftrone.outils.APIUtil;
import mabillot.sar.upmc.gameoftrone.outils.Util;

/**
 * Created by paulo on 10/01/2016.
 */
public class Arbitre {
    private Joueur joueur;
    private List<Arme> armes = new ArrayList<>();
    private List<Toilette> toilettes = new ArrayList<>();

    public Arbitre(String[] armes, String pseudo, String slogan, Activity activity, boolean loadedToilette){
        for(String arme : armes){
            String[] res = arme.split(" - ", -1);
            this.getArmes().add(new Arme(res[0], Integer.parseInt(res[1].substring(0, 1)), Integer.parseInt(res[2].substring(8, res[2].length() - 1))));
        }

        /* Initialisation des toilettes */
        if(!loadedToilette) {
            this.getToilettes().addAll(APIUtil.initializeToilette(activity));
        }

        /* Initialisation du joueur */
        this.setJoueur(new Joueur(pseudo, slogan, this.getArmes().get(0), new Localisation(25,25)));
    }

    /* Find the correct toilette with the marker */
    public Toilette findToilettes(Marker m){
        Iterator<Toilette> it = toilettes.iterator();
        Toilette t = it.next();
        Toilette tmp = new Toilette(0,new Localisation((float)m.getPosition().latitude,(float)m.getPosition().longitude));
        while( it.hasNext() && !t.equals(tmp)){
            t = it.next();
        }
        return t;
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public void setJoueur(Joueur joueur) {
        this.joueur = joueur;
    }

    public List<Arme> getArmes() {
        return armes;
    }

    public List<Toilette> getToilettes() {
        return toilettes;
    }
}
