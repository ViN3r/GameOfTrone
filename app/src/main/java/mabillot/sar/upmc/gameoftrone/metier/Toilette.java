package mabillot.sar.upmc.gameoftrone.metier;

/**
 * Created by paulo on 09/01/2016.
 */

public class Toilette {
    private int pv;
    private Localisation localisation;
    private boolean isConquis = false;

    public Toilette( int pv, Localisation localisation) {
        this(pv, localisation, false);
    }

    public Toilette(int pv, Localisation localisation, boolean isConquis) {
        this.pv = pv;
        this.localisation = localisation;
        this.isConquis = isConquis;
    }

    public boolean equals(Object o){
        if (this == o) {
            return true;
        }
        if (!(o instanceof Toilette)) {
            return false;
        }
        Toilette tmp = (Toilette) o;
        return this.localisation.getX() == tmp.getLocalisation().getX() && this.localisation.getY() == tmp.getLocalisation().getY();

    }

    public int getPv() {
        return pv;
    }

    public void setPv(int pv) {
        this.pv = pv;
    }

    public Localisation getLocalisation() {
        return localisation;
    }

    public boolean isConquis() {
        return isConquis;
    }

    public void setIsConquis(boolean isConquis) {
        this.isConquis = isConquis;
    }
}
