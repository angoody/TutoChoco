package models;

import java.io.Serializable;
import java.util.Set;

public class Module implements Serializable {

    private Integer idModule;
    private Set<Module> prerequis;
    private Set<Cours> cours;

    public Module() {
    }

    public Module(Integer idModule, Set<Module> prerequis, Set<Cours> cours) {
        this.idModule = idModule;
        this.prerequis = prerequis;
        this.cours = cours;
    }

    public Integer getIdModule() {
        return idModule;
    }

    public void setIdModule(Integer idModule) {
        this.idModule = idModule;
    }

    public Set<Module> getPrerequis() {
        return prerequis;
    }

    public void setPrerequis(Set<Module> prerequis) {
        this.prerequis = prerequis;
    }

    public Set<Cours> getCours() {
        return cours;
    }

    public void setCours(Set<Cours> cours) {
        this.cours = cours;
    }

    /*libelle: String,
    dureeEnHeures: Int,
    dureeEnSemaines: Int,
    prixPublicEnCours: Float,
    libelleCourt: String,
    idModule: Int,
    archiver: Boolean,
    typeModule: Int*/
}
