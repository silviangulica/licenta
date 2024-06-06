import java.util.List;

public class IsTeacherTeachingConstraint implements Constraint {
    @Override
    public int check(List<Lecture> lectures) {
        int score = 0;
        for (var lecture : lectures) {
            for (var lec : lectures) {
                if (lec.allocatedPeriod.equals(lecture.allocatedPeriod) && lec.teacher.equals(lecture.teacher) && lec != lecture) {
                    score += 20;
                }
            }
        }
        return score;
    }
}
