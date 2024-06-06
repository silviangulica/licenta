import java.util.List;

public class UniqueTimetableSlotConstraint implements Constraint {
    @Override
    public int check(List<Lecture> lectures) {
        // check if there are any two lectures in the same period and classroom
        int score = 0;
        for (var lecture : lectures) {
            for (var lec : lectures) {
                if (lec.allocatedPeriod.equals(lecture.allocatedPeriod) && lec.classroom.equals(lecture.classroom)
                        && lec != lecture) {
                    score += 20;
                }
            }
        }
        return score;
    }
}
