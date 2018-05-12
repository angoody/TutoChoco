package solver;

import models.*;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.loop.monitors.IMonitorSolution;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.assignments.DecisionOperatorFactory;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import utils.DateTimeHelper;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ChocoSolver {

    private Probleme probleme;
    int nbModules;
    private Model model;
    private Set<ChocoSolverListener> listeners = new HashSet<>();


    public ChocoSolver(Probleme probleme){
        this.probleme = probleme;
    }

    public ChocoSolver(Probleme probleme, ChocoSolverListener listener){
        this(probleme);
        listeners.add(listener);
    }

    public void addListener(ChocoSolverListener listener)
    {
        listeners.add(listener);
    }

    public void removeListener(ChocoSolverListener listener)
    {
        listeners.remove(listener);
    }


    public List<Calendrier> resoudre4 (int nbCalendrier) {

        List<Calendrier> calendriersTrouve = new ArrayList<>();
        model = new Model("Generer calendrier");
        nbModules = probleme.getModulesFormation().size();
        int nbMaxStagiaireEntreprise = probleme.getContraintes().stream().mapToInt(c -> c.getMaxStagiaireEntrepriseEnFormation()).min().getAsInt();
        int debutFormation = DateTimeHelper.InstantToDays(probleme.getPeriodeFormation().getInstantDebut());
        int finFormation = DateTimeHelper.InstantToDays(probleme.getPeriodeFormation().getInstantFin());

        // Transforme les modules en objet préparé pour Choco
        List<ModuleChoco> moduleInChoco = probleme.getModulesFormation().stream().map(m -> new ModuleChoco(m, probleme.getContraintes(), probleme.getPeriodeFormation())).collect(Collectors.toList());

         new ArrayList<>();

        // Filtre sur les contraintes :
        // les lieux autorisés
        Set<Integer> lieuxAutorise = probleme.getContraintes().stream().flatMap(c -> c.getIdLieux().stream()).collect(Collectors.toSet());

        // les cours autorisés des stagiaires recquis
        Set<Cours> coursAutorise = probleme.getContraintes().stream().flatMap(c -> c.getStagiairesRecquis().stream().flatMap(stagiaire -> stagiaire.getCours().stream())).collect(Collectors.toSet());

        // les cours dont le nombre de stagiaire a atteint le nombre maximum
        Set<Cours> coursRefuse = probleme.getContraintes().stream()
                .flatMap(c -> c.getStagiairesEntreprise().stream()
                        .flatMap(stagiaire -> stagiaire.getCours().stream()))
                .collect(Collectors.groupingBy( e->e, Collectors.counting())).entrySet().stream()
                .filter(c -> c.getValue() >= nbMaxStagiaireEntreprise ).map(c -> c.getKey()).collect(Collectors.toSet());

        IntVar[] coursIdentifier = IntStream
                .range(0, nbModules)
                .mapToObj(i ->
                        model.intVar(
                                "Module " + moduleInChoco.get(i).getId(),
                                moduleInChoco.get(i).getCoursIdentifier()))
                .toArray(IntVar[]::new);

        // pour chaque module 'i'

        IntVar[] modulesDebut = IntStream
                .range(0, nbModules)
                .mapToObj(i ->
                        model.intVar(
                                "Debut " + moduleInChoco.get(i).getId(),
                                moduleInChoco.get(i).getDebut()))
                .toArray(IntVar[]::new);

        IntVar[] modulesFin = IntStream
                .range(0, nbModules)
                .mapToObj(i ->
                        model.intVar(
                                "Fin " + moduleInChoco.get(i).getId(),
                                moduleInChoco.get(i).getFin()))
                .toArray(IntVar[]::new);

        IntVar[] modulesLieu = IntStream
                .range(0, nbModules)
                .mapToObj(i ->
                        model.intVar(
                                "Fin " + moduleInChoco.get(i).getId(),
                                moduleInChoco.get(i).getLieu()))
                .toArray(IntVar[]::new);

        //IntVar tot_dev = model.intVar("tot_dev", 0, IntVar.MAX_INT_BOUND);
        // Constraint posting
        // one module to be schedule at a time:
        //model.allDifferent(modulesDebut).post();
        //model.allDifferent(modulesFin).post();

        // Permet de restreindre la recherche à des cours avec des périodes différentes pour chaque module
        model.allDifferent(coursIdentifier).post();

        // pour chaque module 'i'
        for (int i = 0; i < nbModules; i++) {
            // permet de lié les débuts et les fins aux modulesPeriode
            modulesDebut[i].le(coursIdentifier[i]).post();
            modulesFin[i].ge(coursIdentifier[i]).post();

            // Début et fin de la formation
            modulesDebut[i].ge(debutFormation).post();
            modulesFin[i].le(finFormation).post();

            // Pour chaque module qui n'a pas été traité
            // On ajoute une contrainte entre les modules,
            // soit la fin du suivant est inférieur au début du module en cours
            // soit la fin du en cours est inférieur au début du suivant
            // Cette contraintes évite les chevauchements
            for (int j = i + 1; j < nbModules; j++) {
                model.or(
                        model.arithm(modulesFin[i], "<", modulesDebut[j]),
                        model.arithm(modulesFin[j], "<", modulesDebut[i])
                ).post();
            }
            int finalI = i;

            //probleme.getContraintes().forEach(contrainte -> contrainte.getIdLieux().forEach(lieux -> modulesLieu[finalI].(lieux).post() ));
        }


        // Ajout du moniteur pour avoir les informations au fil de l'eau
        Solver solver = model.getSolver();
        solver.plugMonitor((IMonitorSolution) () -> {
            List<Cours> lesCoursChoisi = new ArrayList<>();
            for (int i = 0; i < nbModules; i++) {
                //System.out.println("modulesPeriode " + modulesPeriode[i].getName() + " Value = " + modulesPeriode[i].getValue() + " " + moduleInChoco.get(i).getCoursByPeriodIdentifier( modulesPeriode[i].getValue()).getPeriode().getDebut() + " au " + moduleInChoco.get(i).getCoursByPeriodIdentifier( modulesPeriode[i].getValue()).getPeriode().getFin()  );
                //System.out.println("modulesDebut " + modulesDebut[i].getName() + " Value = " + modulesDebut[i].getValue() + " " + DateTimeHelper.DaysToInstant(modulesDebut[i].getValue()) );
                Cours leCoursChoisi = moduleInChoco.get(i).getCoursByPeriodIdentifier(coursIdentifier[i].getValue());
                lesCoursChoisi.add(leCoursChoisi);
                listeners.forEach(l -> l.foundCours(leCoursChoisi));
            }
            Calendrier calendrierTrouve = new Calendrier(lesCoursChoisi.stream().sorted(Comparator.comparing(o -> o.getPeriode().getInstantDebut())).collect(Collectors.toList()));
            calendriersTrouve .add(calendrierTrouve);
            listeners.forEach(l -> l.foundCalendar(calendrierTrouve));
        });

        // Si aucune solution n'est trouvée, permet de savoir pourquoi
        //solver.showContradiction();

        // Lorsqu'une solution est trouvé, permet de comprendre le cheminement
        solver.showDecisions();

        // Statistiques complètes
        //solver.showStatistics();

        solver.showShortStatistics();
        //solver.findOptimalSolution(tot_dev, false);

        Map<IntVar, Integer> map = IntStream
                .range(0, nbModules)
                .boxed()
                .collect(Collectors.toMap(i -> coursIdentifier[i], i -> coursIdentifier[i].getValue()));
        solver.setSearch(Search.intVarSearch(
                variables -> Arrays.stream(variables)
                        .filter(v -> !v.isInstantiated())
                        .min((v1, v2) -> closest(v2, map) - closest(v1, map))
                        .orElse(null),
                var -> closest(var, map),
                DecisionOperatorFactory.makeIntEq(),
                coursIdentifier
        ));

        for (int i = 0; i < nbCalendrier; i++) solver.solve();

        /*Solution solution = solver.findSolution();
        if (solution != null) {
            System.out.println(solution.toString());
        }*/
        return calendriersTrouve;

    }

    public List<Calendrier> resoudre3 () {

        List<Calendrier> calendriersTrouve = new ArrayList<>();
        model = new Model("Generer calendrier");
        nbModules = probleme.getModulesFormation().size();

        // Transforme les modules en objet préparé pour Choco
        List<ModuleChoco> moduleInChoco = probleme.getModulesFormation().stream().map(m -> new ModuleChoco(m, probleme.getContraintes(), probleme.getPeriodeFormation())).collect(Collectors.toList());
        IntVar[] modulesPeriode = IntStream
                .range(0, nbModules)
                .mapToObj(i ->
                        model.intVar(
                                "Module " + moduleInChoco.get(i).getId(),
                                moduleInChoco.get(i).getCoursIdentifier()))
                .toArray(IntVar[]::new);

        // pour chaque module 'i'

        IntVar[] modulesDebut = IntStream
                .range(0, nbModules)
                .mapToObj(i ->
                        model.intVar(
                                "Debut " + moduleInChoco.get(i).getId(),
                                moduleInChoco.get(i).getDebut()))
                .toArray(IntVar[]::new);

        IntVar[] modulesFin = IntStream
                .range(0, nbModules)
                .mapToObj(i ->
                        model.intVar(
                                "Fin " + moduleInChoco.get(i).getId(),
                                moduleInChoco.get(i).getFin()))
                .toArray(IntVar[]::new);

        //IntVar tot_dev = model.intVar("tot_dev", 0, IntVar.MAX_INT_BOUND);
        // Constraint posting
        // one module to be schedule at a time:
        //model.allDifferent(modulesDebut).post();
        //model.allDifferent(modulesFin).post();
        model.allDifferent(modulesPeriode).post();

        // pour chaque module 'i'
        for (int i = 0; i < nbModules; i++) {
            // permet de lié les débuts et les fins aux modulesPeriode
            modulesDebut[i].le(modulesPeriode[i]).post();
            modulesFin[i].ge(modulesPeriode[i]).post();

            // Pour chaque module qui n'a pas été traité
            // On ajoute une contrainte entre les modules,
            // soit la fin du suivant est inférieur au début du module en cours
            // soit la fin du en cours est inférieur au début du suivant
            // Cette contraintes évite les chevauchements
            for (int j = i + 1; j < nbModules; j++) {
                model.or(
                        model.arithm(modulesFin[i], "<", modulesDebut[j]),
                        model.arithm(modulesFin[j], "<", modulesDebut[i])
                ).post();
            }
        }


        Solver solver = model.getSolver();
        solver.plugMonitor((IMonitorSolution) () -> {
            List<Cours> lesCoursChoisi = new ArrayList<>();
            for (int i = 0; i < nbModules; i++) {
                // Pour permettre de comprendre les choix avant de les transformer en calendrier
                //System.out.println("modulesPeriode " + modulesPeriode[i].getName() + " Value = " + modulesPeriode[i].getValue() + " " + moduleInChoco.get(i).getCoursByPeriodIdentifier( modulesPeriode[i].getValue()).getPeriode().getDebut() + " au " + moduleInChoco.get(i).getCoursByPeriodIdentifier( modulesPeriode[i].getValue()).getPeriode().getFin()  );
                //System.out.println("modulesDebut " + modulesDebut[i].getName() + " Value = " + modulesDebut[i].getValue() + " " + DateTimeHelper.DaysToInstant(modulesDebut[i].getValue()) );
                lesCoursChoisi.add(moduleInChoco.get(i).getCoursByPeriodIdentifier(modulesPeriode[i].getValue()));
            }
            calendriersTrouve.add(new Calendrier(lesCoursChoisi.stream().sorted(Comparator.comparing(o -> o.getPeriode().getInstantDebut())).collect(Collectors.toList())));
        });

        // Si aucune solution n'est trouvée, permet de savoir pourquoi
        //solver.showContradiction();

        // Lorsqu'une solution est trouvé, permet de comprendre le cheminement
        //solver.showDecisions();

        // Statistiques complètes
        //solver.showStatistics();

        solver.showShortStatistics();
        //solver.findOptimalSolution(tot_dev, false);

        Map<IntVar, Integer> map = IntStream
                .range(0, nbModules)
                .boxed()
                .collect(Collectors.toMap(i -> modulesPeriode[i], i -> modulesPeriode[i].getValue()));
        solver.setSearch(Search.intVarSearch(
                variables -> Arrays.stream(variables)
                        .filter(v -> !v.isInstantiated())
                        .min((v1, v2) -> closest(v2, map) - closest(v1, map))
                        .orElse(null),
                var -> closest(var, map),
                DecisionOperatorFactory.makeIntEq(),
                modulesPeriode
        ));

        for (int i = 0; i < 100; i++) solver.solve();

        /*Solution solution = solver.findSolution();
        if (solution != null) {
            System.out.println(solution.toString());
        }*/
        return calendriersTrouve;

    }

    private static int closest(IntVar var, Map<IntVar, Integer> map) {
        int target = map.get(var);
        if (var.contains(target)) {
            return target;
        } else {
            int p = var.previousValue(target);
            int n = var.nextValue(target);
            return Math.abs(target - p) < Math.abs(n - target) ? p : n;
        }
    }


    public List<Calendrier> resoudre (){



        List<Calendrier> calendriersTrouve = new ArrayList<>();

        model = new Model("Generer calendrier");

        nbModules = probleme.getModulesFormation().size();

        // Transforme les module en objet préparé pour Choco
        List<ModuleChoco> moduleInChoco = probleme.getModulesFormation().stream().map(m -> new ModuleChoco(m, probleme.getContraintes(), probleme.getPeriodeFormation())).collect(Collectors.toList());
        /*new ArrayList<>();
        modulesList.forEach(module -> moduleInChoco.add(new ModuleChoco(module)));*/

        // Utilisation d'un builder pour la création des variables choco
        //IntStream.Builder builder = IntStream.builder();
        //probleme.getModulesFormation().forEach(m -> m.getCours().forEach(cours -> builder.add(DateTimeHelper.InstantToDays(cours.getPeriode().getInstantDebut()))));

        // Convert module in IntVar for Choco
        IntStream.Builder moduleBuilder = IntStream.builder();
        // modulesList.forEach(m -> moduleBuilder.add(m.getIdModule()));

        IntVar[] modulesPeriode = IntStream
                .range(0, nbModules)
                .mapToObj(i ->
                        model.intVar(
                                "Periode " + moduleInChoco.get(i).getId(),
                                moduleInChoco.get(i).getCoursIdentifier()))
                .toArray(IntVar[]::new);

        /*IntVar[] modulesDuration = IntStream
                .range(0, nbModules)
                .mapToObj(i ->
                        model.intVar(
                                "Duration " + moduleInChoco.get(i).getId(),
                                moduleInChoco.get(i).getDurationIdentifier()))
                .toArray(IntVar[]::new);*/

        IntVar[] modulesDebut = IntStream
                .range(0, nbModules)
                .mapToObj(i ->
                        model.intVar(
                                "Debut " + moduleInChoco.get(i).getId(),
                                moduleInChoco.get(i).getDebut()))
                .toArray(IntVar[]::new);

        IntVar[] modulesFin = IntStream
                .range(0, nbModules)
                .mapToObj(i ->
                        model.intVar(
                                "Fin " + moduleInChoco.get(i).getId(),
                                moduleInChoco.get(i).getFin()))
                .toArray(IntVar[]::new);

        IntVar tot_dev = model.intVar("tot_dev", 0, IntVar.MAX_INT_BOUND);
        // Constraint posting
        // one module to be schedule at a time:
        model.allDifferent(modulesDebut).post();
        model.allDifferent(modulesFin).post();
        model.allDifferent(modulesPeriode).post();

        // pour chaque module 'i'
        for (int i = 0; i < nbModules; i++) {
            BoolVar c1 = model.arithm(modulesDebut[i], "<", modulesPeriode[i]).reify();
            BoolVar c2 = model.arithm(modulesPeriode[i], "<", modulesFin[i]).reify();
            model.arithm(c1, "+", c2, "=", 1).post();
            for (int j = i + 1; j < nbModules; j++) {
                BoolVar iBeforej = model.arithm(modulesFin[i], "<", modulesDebut[j]).reify();
                BoolVar jBeforei = model.arithm(modulesFin[j], "<", modulesDebut[i]).reify();
                model.arithm(iBeforej, "-", jBeforei, "=", 0).post();
            }
        }


        Solver solver = model.getSolver();
        solver.plugMonitor((IMonitorSolution) () -> {
            List<Cours> lesCoursChoisi = new ArrayList<>();
            for (int i = 0; i < nbModules; i++) {
                System.out.println("modulesPeriode " + modulesPeriode[i].getName() + " Value = " + modulesPeriode[i].getValue() + " " + moduleInChoco.get(i).getCoursByPeriodIdentifier( modulesPeriode[i].getValue()).getPeriode().getDebut() + " au " + moduleInChoco.get(i).getCoursByPeriodIdentifier( modulesPeriode[i].getValue()).getPeriode().getFin()  );
                System.out.println("modulesDebut " + modulesDebut[i].getName() + " Value = " + modulesDebut[i].getValue() + " " + DateTimeHelper.DaysToInstant(modulesDebut[i].getValue()) );
                Cours leCoursChoisi = moduleInChoco.get(i).getCoursByPeriodIdentifier(modulesPeriode[i].getValue());
                lesCoursChoisi.add(leCoursChoisi);
                listeners.forEach(l -> l.foundCours(leCoursChoisi));

            }
            Calendrier calendrierTrouve = new Calendrier(lesCoursChoisi.stream().sorted(Comparator.comparing(o -> o.getPeriode().getInstantDebut())).collect(Collectors.toList()));
            calendriersTrouve .add(calendrierTrouve);
            listeners.forEach(l -> l.foundCalendar(calendrierTrouve));
        });

        solver.showShortStatistics();
        solver.findOptimalSolution(tot_dev, false);
        for (int i = 0; i < 5; i++) {
            solver.solve();
        }

        /*Solution solution = solver.findSolution();
        if (solution != null) {
            System.out.println(solution.toString());
        }*/
        return calendriersTrouve;
    }









        /*IntVar[] modules = model.intVarArray(modulesList.size(), moduleBuilder.build().toArray());

        IntVar[] planes = IntStream
                .range(0, nbModules)
                .mapToObj(i -> model.intVar("module #" + probleme.getModulesFormation(), LT[i][0], LT[i][2], false))
                .toArray(IntVar[]::new);
        IntVar[] earliness = IntStream
                .range(0, N)
                .mapToObj(i -> model.intVar("earliness #" + i, 0, LT[i][1] - LT[i][0], false))
                .toArray(IntVar[]::new);
        IntVar[] tardiness = IntStream
                .range(0, N)
                .mapToObj(i -> model.intVar("tardiness #" + i, 0, LT[i][2] - LT[i][1], false))
                .toArray(IntVar[]::new);
        IntVar tot_dev = model.intVar("tot_dev", 0, IntVar.MAX_INT_BOUND);
        System.out.println(cours.getPeriode().getInstantDebut().toEpochMilli());

        for (Module module: modules) {
            for (Cours cours: module.getCours()) {
// Variables declaration

            }
        }*/

    public static void main (String[] args){

    }
}
