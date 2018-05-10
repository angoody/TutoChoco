package models;

import java.io.Serializable;
import java.util.Set;

public class Probleme implements Serializable {
    private Periode periodeFormation;
    private Set<Module> modulesFormation;
    private Set<Contrainte> contraintes;

    public Probleme() {
    }

    public Probleme(Periode periodeFormation, Set<Module> modulesFormation, Set<Contrainte> contraintes) {
        this.periodeFormation = periodeFormation;
        this.modulesFormation = modulesFormation;
        this.contraintes = contraintes;
    }

    public Periode getPeriodeFormation() {
        return periodeFormation;
    }

    public void setPeriodeFormation(Periode periodeFormation) {
        this.periodeFormation = periodeFormation;
    }

    public Set<Module> getModulesFormation() {
        return modulesFormation;
    }

    public void setModulesFormation(Set<Module> modulesFormation) {
        this.modulesFormation = modulesFormation;
    }

    public Set<Contrainte> getContraintes() {
        return contraintes;
    }

    public void setContraintes(Set<Contrainte> contraintes) {
        this.contraintes = contraintes;
    }
}
