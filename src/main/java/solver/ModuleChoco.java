package solver;

import models.Contrainte;
import models.Cours;
import models.Module;
import models.Periode;
import org.chocosolver.solver.variables.IntVar;
import utils.DateTimeHelper;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ModuleChoco {

    private Module module;
    private IntVar moduleInChoco;
    private int id;
    private int[][] periode ;
    private int[] debut ;
    private int[] fin ;
    private int[] coursIdentifier;
    private int[] durationIdentifier;

    private int[] lieu;

    private int[] duration;
    private List<Cours> coursDuModule;


    public ModuleChoco(Module module, List<Contrainte> contraintes, Periode periodeFormation) {

        // Initialisation des variables
        this.module = module;
        this.id = module.getIdModule();
        int nbCours = module.getCours().size();
        periode = new int[nbCours][2];
        lieu = new int[nbCours];
        duration = new int[nbCours];
        coursIdentifier = new int[nbCours];
        durationIdentifier = new int[nbCours];

        coursDuModule = module.getCours().stream()
                .sorted(Comparator.comparing(o -> o.getPeriode().getInstantDebut()))
                .collect(Collectors.toList());

        debut = coursDuModule.stream().mapToInt(c -> DateTimeHelper.InstantToDays(c.getPeriode().getInstantDebut())).toArray();
        fin = coursDuModule.stream().mapToInt(c -> DateTimeHelper.InstantToDays(c.getPeriode().getInstantFin())).toArray();
        lieu = coursDuModule.stream().mapToInt(c -> c.getLieu()).toArray();

        for (int i = 0; i < coursDuModule.size(); i++)
        {
            periode[i][0] = DateTimeHelper.InstantToDays(coursDuModule.get(i).getPeriode().getInstantDebut());
            periode[i][1] = DateTimeHelper.InstantToDays(coursDuModule.get(i).getPeriode().getInstantFin());
            duration[i] = periode[i][1] - periode[i][0];
            // Permet à Choco de comparer les cours simplement avec la duration / 2
            coursIdentifier[i] = (periode[i][1] + periode[i][0]) / 2;
            durationIdentifier[i] = (periode[i][1] - periode[i][0]) / 2;
        }

        /*range(0,module.getCours().size())
                .mapToObj(i -> {DateTimeHelper.InstantToDays(coursDuModule.get(i).getPeriode().getInstantDebut()),
                DateTimeHelper.InstantToDays(coursDuModule.get(i).getPeriode().getInstantFin())}).toArray();
        //new int[module.getCours().size()][2];
        module.getCours().forEach(cours1 -> periode +=
                {DateTimeHelper.InstantToDays(cours1.getPeriode().getInstantDebut()),
                DateTimeHelper.InstantToDays(cours1.getPeriode().getInstantFin())});*/
    }


    public List<Cours> getCoursDuModule() {
        return coursDuModule;
    }

    public int[] getCoursIdentifier() {
        return coursIdentifier;
    }

    public int[] getDurationIdentifier() {
        return durationIdentifier;
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

    public int[][] getPeriode() {
        return periode;
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



    public Cours getCoursByPeriodIdentifier(int value) {
        for (int i = 0; i < coursDuModule.size(); i++)
        {
            if (coursIdentifier[i] == value)
            {
                return coursDuModule.get(i);
            }
        }
        return null;
    }

    public IntVar getModuleInChoco() {
        return moduleInChoco;
    }

    public void setModuleInChoco(IntVar moduleInChoco) {
        this.moduleInChoco = moduleInChoco;
    }
}
