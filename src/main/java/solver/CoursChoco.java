package solver;

import models.Cours;
import utils.DateTimeHelper;

public class CoursChoco {

    private final Cours cours;
    private int debut ;
    private int fin ;
    private Integer idModule;
    private Integer idCours;
    private int nbSemaine;
    private int nbHeure;
    private int coursIdentifier;
    private int duration;

    private int lieu;

    public CoursChoco(Cours cours, Integer idModule, Integer idCours, Integer nbSemaine, Integer nbHeure) {
        this.cours = cours;
        this.debut = DateTimeHelper.InstantToDays(cours.getPeriode().getInstantDebut());
        this.fin = DateTimeHelper.InstantToDays(cours.getPeriode().getInstantFin());
        this.idModule = idModule;
        this.idCours = idCours;
        this.nbSemaine = nbSemaine;
        this.nbHeure = nbHeure;
        this.coursIdentifier = (this.debut + this.fin) / 2;
        this.duration = cours.getNbHeureReel();
        this.lieu = lieu;
    }

    public int getDebut() {
        return debut;
    }

    public int getFin() {
        return fin;
    }

    public int getCoursIdentifier() {
        return coursIdentifier;
    }

    public int getDuration() {
        return duration;
    }

    public int getLieu() {
        return lieu;
    }

    public int getNbHeure() {
        return nbHeure;
    }

    public void setNbHeure(int nbHeure) {
        this.nbHeure = nbHeure;
    }

    public int getNbSemaine() {
        return nbSemaine;
    }

    public void setNbSemaine(int nbSemaine) {
        this.nbSemaine = nbSemaine;
    }

    public Cours getCours() {
        return cours;
    }

    public int[] getInt() {
        return new int[] { idModule, idCours, debut, fin, coursIdentifier, duration, nbSemaine, nbHeure, lieu};
    }
}
