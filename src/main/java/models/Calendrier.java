package models;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class Calendrier implements Serializable{

    private List<Cours> cours;

    public Calendrier() {
    }

    public Calendrier(List<Cours> cours) {
        this.cours = cours;
    }

    public List<Cours> getCours() {
        return cours;
    }

    public void setCours(List<Cours> cours) {
        this.cours = cours;
    }

    /*private Set<Contrainte> contraintesResolus;
    private Set<Contrainte> contrainteNonResolu;*/


    /*
case class Calendrier (
                        periode: Periode,
                        formation: Formation,
                        lieu : Lieu,
                        cours : Seq[Cours],
                        contrainteResolu : Seq[Contrainte],
                        contrainteNonResolu : Seq[Contrainte]
                      )
     */
}
