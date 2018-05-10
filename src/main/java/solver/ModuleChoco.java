package solver;

import models.Cours;
import models.Module;
import utils.DateTimeHelper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModuleChoco {

    Module module;
    int id;
    int[][] periode ;
    int[] debut ;
    int[] fin ;
    int[] periodeIdentifier;
    int[] durationIdentifier;

    int[] lieu;

    int[] duration;
    List<Cours> coursDuModule;


    public ModuleChoco(Module module) {
        this.module = module;
        this.id = module.getIdModule();


        int nbCours = module.getCours().size();
        periode = new int[nbCours][2];
        lieu = new int[nbCours];
        duration = new int[nbCours];
        periodeIdentifier = new int[nbCours];
        durationIdentifier = new int[nbCours];


        coursDuModule = module.getCours().stream().sorted(Comparator.comparing(o -> o.getPeriode().getInstantDebut())).collect(Collectors.toList());

        debut = coursDuModule.stream().mapToInt(c -> DateTimeHelper.InstantToDays(c.getPeriode().getInstantDebut())).toArray();
        fin = coursDuModule.stream().mapToInt(c -> DateTimeHelper.InstantToDays(c.getPeriode().getInstantFin())).toArray();
        lieu = coursDuModule.stream().mapToInt(c -> c.getLieu()).toArray();

        for (int i = 0; i < coursDuModule.size(); i++)
        {
            periode[i][0] = DateTimeHelper.InstantToDays(coursDuModule.get(i).getPeriode().getInstantDebut());
            periode[i][1] = DateTimeHelper.InstantToDays(coursDuModule.get(i).getPeriode().getInstantFin());
            duration[i] = periode[i][1] - periode[i][0];
            // Permet à Choco de comparer les cours simplement avec la duration / 2
            periodeIdentifier[i] = (periode[i][1] + periode[i][0]) / 2;
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

    public int[] getPeriodeIdentifier() {
        return periodeIdentifier;
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
            if (periodeIdentifier[i] == value)
            {
                return coursDuModule.get(i);
            }
        }
        return null;
    }
}
