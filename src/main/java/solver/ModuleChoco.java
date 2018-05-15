package solver;

import models.Contrainte;
import models.Cours;
import models.Module;
import models.Periode;
import org.chocosolver.solver.variables.IntVar;
import utils.DateTimeHelper;

import java.util.*;
import java.util.stream.Collectors;

public class ModuleChoco {

    private Module module;
    private IntVar moduleInChoco;
    private int id;
    private int[] debut ;
    private int[] fin ;
    private int[] coursIdentifier;

    private int[] lieu;

    private int[] duration;
    private Map<String, CoursChoco> coursDuModule;

    public ModuleChoco(Module module, List<Contrainte> contraintes, Periode periodeFormation) {

        // Initialisation des variables
        this.module = module;
        this.id = module.getIdModule();

        List<Cours> lesCours = module.getCours().stream()
                .sorted(Comparator.comparing(o -> o.getPeriode().getInstantDebut())).collect(Collectors.toList());

        coursDuModule = lesCours.stream().collect(
                Collectors.toMap(
                        c -> c.getIdCours(),
                        c -> new CoursChoco(c, id, module.getNbSemainePrevu(), module.getNbHeurePrevu()) ));


        debut = coursDuModule.values().stream().mapToInt(c -> c.getDebut()).toArray();
        fin = coursDuModule.values().stream().mapToInt(c -> c.getFin()).toArray();
        lieu = coursDuModule.values().stream().mapToInt(c -> c.getLieu()).toArray();
        duration = coursDuModule.values().stream().mapToInt(c -> c.getDuration()).toArray();
        coursIdentifier = coursDuModule.values().stream().mapToInt(c -> c.getCoursIdentifier()).toArray();



    }


    public List<CoursChoco> getCoursDuModule() {
        return coursDuModule.values().stream().collect(Collectors.toList());
    }

    public int[] getCoursIdentifier() {
        return coursIdentifier;
    }

    public int[] getIdModule() {
        return coursDuModule.values().stream().mapToInt(c -> c.getDebut()).toArray();
    }



    public Module getModule() {
        return module;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int[] getLieu() {
        return lieu;
    }

    public int[] getDuration() {
        return duration;
    }

    public int[] getDebut() {
        return debut;
    }

    public int[] getFin() {
        return fin;
    }



    public Cours getCoursId(String id) {

        return coursDuModule.get(id).getCours();
    }

    public Cours getCoursByPeriodIdentifier(int value) {

        final CoursChoco[] coursARetourner = new CoursChoco[1];
        coursDuModule.values().stream().filter(v -> v.getCoursIdentifier() == value).forEach(c-> coursARetourner[0] = c);
        return coursARetourner[0].getCours();
    }


}
