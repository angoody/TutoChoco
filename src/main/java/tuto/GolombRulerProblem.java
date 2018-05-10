package tuto;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;

public class GolombRulerProblem {

    public static void main (String[] args){
        int m = 10;
// A new model instance
        Model model = new Model("Golomb ruler");

// VARIABLES
// set of marks that should be put on the ruler
        IntVar[] ticks = ticks = model.intVarArray("a", m, 0, 999, false);
// set of distances between two distinct marks
        IntVar[] diffs = model.intVarArray("d", (m * (m - 1)) / 2, 0, 999, false);

// CONSTRAINTS
// the first mark is set to 0
        model.arithm(ticks[0], "=", 0).post();

        for (int i = 0, k = 0 ; i < m - 1; i++) {
            // // the mark variables are ordered
            model.arithm(ticks[i + 1], ">", ticks[i]).post();
            for (int j = i + 1; j < m; j++, k++) {
                // declare the distance constraint between two distinct marks
                model.scalar(new IntVar[]{ticks[j], ticks[i]}, new int[]{1, -1}, "=", diffs[k]).post();
                // redundant constraints on bounds of diffs[k]
                model.arithm(diffs[k], ">=", (j - i) * (j - i + 1) / 2).post();
                model.arithm(diffs[k], "<=", ticks[m - 1], "-", ((m - 1 - j + i) * (m - j + i)) / 2).post();
            }
        }
// all distances must be distinct
        model.allDifferent(diffs, "BC").post();
//symmetry-breaking constraints
        model.arithm(diffs[0], "<", diffs[diffs.length - 1]).post();

        Solver solver = model.getSolver();
        solver.setSearch(Search.inputOrderLBSearch(ticks));
// show resolution statistics
        solver.showShortStatistics();
// Find a solution that minimizes the last mark
        solver.findOptimalSolution(ticks[m - 1], false);
    }
}
