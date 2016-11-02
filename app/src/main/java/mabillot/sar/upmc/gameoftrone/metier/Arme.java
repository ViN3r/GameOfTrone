package mabillot.sar.upmc.gameoftrone.metier;

/**
 * Created by paulo on 09/01/2016.
 */
public class Arme {
    private String nom;
    private int degats;
    private int portee;

    public Arme(String nom, int degats, int portee) {
        this.nom = nom;
        this.degats = degats;
        this.portee = portee;
    }

    public String getNom() {
        return nom;
    }

    public int getDegats() {
        return degats;
    }

    public int getPortee() {
        return portee;
    }

    public String toString(){
        return nom + " " + " d:" + degats + " p:" + portee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Arme)) return false;

        Arme arme = (Arme) o;

        return nom.equals(arme.nom);

    }

    @Override
    public int hashCode() {
        return nom.hashCode();
    }
}
