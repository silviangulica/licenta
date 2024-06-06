import java.util.ArrayList;
import java.util.List;

public class ConstraintChecker {
    private final List<Constraint> constraints;

    public ConstraintChecker() {
        this.constraints = new ArrayList<>();
    }

    public void addConstraint(Constraint constraint) {
        this.constraints.add(constraint);
    }

    public int checkConstraints(List<Lecture> lectures) {
        int score = 0;
        for (Constraint constraint : constraints) {
            score += constraint.check(lectures);
        }
        return score;
    }

}
