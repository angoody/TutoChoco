import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.nary.automata.FA.FiniteAutomaton;
import org.chocosolver.solver.constraints.nary.automata.FA.IAutomaton;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.util.tools.ArrayUtils;

public class NonogramProblem {
    private static void dfa(BoolVar[] cells, int[] rest, Model model) {
        StringBuilder regexp = new StringBuilder("0*");
        int m = rest.length;
        for (int i = 0; i < m; i++) {
            regexp.append('1').append('{').append(rest[i]).append('}');
            regexp.append('0');
            regexp.append(i == m - 1 ? '*' : '+');
        }
        IAutomaton auto = new FiniteAutomaton(regexp.toString());
        model.regular(cells, auto).post();
    }

    private static void dfa2(BoolVar[] cells, int[] seq, Model model) {
        FiniteAutomaton auto = new FiniteAutomaton();
        int si = auto.addState();
        auto.setInitialState(si); // declare it as initial state
        int i0 = auto.addState();
        auto.addTransition(si, i0, 0); // first transition
        auto.addTransition(i0, i0, 0); // second transition
        int from = i0;
        int m = seq.length;
        for (int i = 0; i < m; i++) {
            int ii = auto.addState();
            // word can start with '1'
            if(i == 0){
                auto.addTransition(si, ii, 1);
            }
            auto.addTransition(from, ii, 1);
            from = ii;
            for(int j = 1; j < seq[i]; j++){
                int jj = auto.addState();
                auto.addTransition(from, jj, 1);
                from = jj;
            }
            int ii0 = auto.addState();
            auto.addTransition(from, ii0, 0);
            auto.addTransition(ii0, ii0, 0);
            // the word can end with '1' or '0'
            if(i == m - 1){
                auto.setFinal(from, ii0);
            }
            from = ii0;
        }
        model.regular(cells, auto).post();
    }


    public static void main (String[] args){
        // number of columns
        int N = 15;
        // number of rows
        int M = 15;
        // sequence of shaded blocks
        int[][][] BLOCKS =
                new int[][][]{{
                        {2},
                        {4, 2},
                        {1, 1, 4},
                        {1, 1, 1, 1},
                        {1, 1, 1, 1},
                        {1, 1, 1, 1},
                        {1, 1, 1, 1},
                        {1, 1, 1, 1},
                        {1, 2, 2, 1},
                        {1, 3, 1},
                        {2, 1},
                        {1, 1, 1, 2},
                        {2, 1, 1, 1},
                        {1, 2},
                        {1, 2, 1},
                }, {
                        {3},
                        {3},
                        {10},
                        {2},
                        {2},
                        {8, 2},
                        {2},
                        {1, 2, 1},
                        {2, 1},
                        {7},
                        {2},
                        {2},
                        {10},
                        {3},
                        {2}}};

        Model model = new Model("Nonogram");
        // Variables declaration
        BoolVar[][] cells = model.boolVarMatrix("c", N, M);
        // Constraint declaration
        // one regular per row
        for (int i = 0; i < M; i++) {
            dfa(cells[i], BLOCKS[0][i], model);
        }
        for (int j = 0; j < N; j++) {
            dfa(ArrayUtils.getColumn(cells, j), BLOCKS[1][j], model);
        }
        if(model.getSolver().solve()){
            for (int i = 0; i < cells.length; i++) {
                System.out.printf("\t");
                for (int j = 0; j < cells[i].length; j++) {
                    System.out.printf(cells[i][j].getValue() == 1 ? "#" : " ");
                }
                System.out.printf("\n");
            }
        }
    }
}
