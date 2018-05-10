package solver;

import models.Calendrier;

public interface ChocoSolverListener {
    public void found(Calendrier calendrier);
    public void finish();
}
