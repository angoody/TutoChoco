package models;

import java.io.Serializable;
import java.util.Set;

public class Contrainte implements Serializable {
    private Set<Integer> idLieux;
    private Integer nbHeureAnnuel;
    private Integer dureeMaxFormation;
    private Integer maxSemaineFormation;
    private Set<Periode> periodeFormationExclusion;
    private Set<Periode> periodeFormationInclusion;
    private Integer maxStagiaireEntrepriseEnFormation;
    private Set<Stagiaire> stagiairesEntreprise;
    private Set<Stagiaire> stagiairesRecquis;

    public Contrainte() {
    }

    public Contrainte(Set<Integer> idLieux, Integer nbHeureAnnuel, Integer dureeMaxFormation, Integer maxSemaineFormation, Set<Periode> periodeFormationExclusion, Set<Periode> periodeFormationInclusion, Integer maxStagiaireEntrepriseEnFormation, Set<Stagiaire> stagiairesEntreprise, Set<Stagiaire> stagiairesRecquis) {
        this.idLieux = idLieux;
        this.nbHeureAnnuel = nbHeureAnnuel;
        this.dureeMaxFormation = dureeMaxFormation;
        this.maxSemaineFormation = maxSemaineFormation;
        this.periodeFormationExclusion = periodeFormationExclusion;
        this.periodeFormationInclusion = periodeFormationInclusion;
        this.maxStagiaireEntrepriseEnFormation = maxStagiaireEntrepriseEnFormation;
        this.stagiairesEntreprise = stagiairesEntreprise;
        this.stagiairesRecquis = stagiairesRecquis;
    }

    public Set<Integer> getIdLieux() {
        return idLieux;
    }

    public void setIdLieux(Set<Integer> idLieux) {
        this.idLieux = idLieux;
    }

    public Integer getNbHeureAnnuel() {
        return nbHeureAnnuel;
    }

    public void setNbHeureAnnuel(Integer nbHeureAnnuel) {
        this.nbHeureAnnuel = nbHeureAnnuel;
    }

    public Integer getDureeMaxFormation() {
        return dureeMaxFormation;
    }

    public void setDureeMaxFormation(Integer dureeMaxFormation) {
        this.dureeMaxFormation = dureeMaxFormation;
    }

    public Integer getMaxSemaineFormation() {
        return maxSemaineFormation;
    }

    public void setMaxSemaineFormation(Integer maxSemaineFormation) {
        this.maxSemaineFormation = maxSemaineFormation;
    }

    public Set<Periode> getPeriodeFormationExclusion() {
        return periodeFormationExclusion;
    }

    public void setPeriodeFormationExclusion(Set<Periode> periodeFormationExclusion) {
        this.periodeFormationExclusion = periodeFormationExclusion;
    }

    public Set<Periode> getPeriodeFormationInclusion() {
        return periodeFormationInclusion;
    }

    public void setPeriodeFormationInclusion(Set<Periode> periodeFormationInclusion) {
        this.periodeFormationInclusion = periodeFormationInclusion;
    }

    public Integer getMaxStagiaireEntrepriseEnFormation() {
        return maxStagiaireEntrepriseEnFormation;
    }

    public void setMaxStagiaireEntrepriseEnFormation(Integer maxStagiaireEntrepriseEnFormation) {
        this.maxStagiaireEntrepriseEnFormation = maxStagiaireEntrepriseEnFormation;
    }

    public Set<Stagiaire> getStagiairesEntreprise() {
        return stagiairesEntreprise;
    }

    public void setStagiairesEntreprise(Set<Stagiaire> stagiairesEntreprise) {
        this.stagiairesEntreprise = stagiairesEntreprise;
    }

    public Set<Stagiaire> getStagiairesRecquis() {
        return stagiairesRecquis;
    }

    public void setStagiairesRecquis(Set<Stagiaire> stagiairesRecquis) {
        this.stagiairesRecquis = stagiairesRecquis;
    }
/*

case class Contrainte (

                        lieux: Seq[Lieu],
                        nbHeureAnnuel: Int = 0,
                        dureeMaxFormation : Int = 0,
                        maxSemaineFormation: Int = 0,
                        maxStagiaireEnFormationParCours: Int = 0,

                        periodeFormationExclusion : Seq[Periode],
                        periodeFormationInclusion : Seq[Periode]

                        // todo
                        // Stagiaires dont la présence est requise
                        // une autre case class stagaire avec le calendrier qui la liste des cours à suivre?
                        //
                      )
     */
}
