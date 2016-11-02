package mabillot.sar.upmc.gameoftrone.metier;


/**
 * Created by paulo on 09/01/2016.
 */
public class Joueur {
    private String nom;
    private String slogan;
    private Arme arme;
    private Localisation localisation;

    public Joueur(String nom, String slogan, Arme arme, Localisation localisation) {
        this.nom = nom;
        this.slogan = slogan;
        this.arme = arme;
        this.localisation = localisation;
    }

    public boolean attacks(Toilette toilette){
        if(!toilette.isConquis()) {
            toilette.setPv(toilette.getPv() - this.getArme().getDegats());
            if (toilette.getPv() <= 0) {
                toilette.setIsConquis(true);
            }
            return true;
        }
        return false;
    }

    public String getNom() {
        return nom;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public void setArme(Arme arme) {
        this.arme = arme;
    }

    public void setLocalisation(Localisation localisation) {
        this.localisation = localisation;
    }

    public Arme getArme() {
        return arme;
    }

    public Localisation getLocalisation() {
        return localisation;
    }
}
