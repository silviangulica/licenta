import java.util.List;

public class FirstTwoWeekDayCourseConstraint implements Constraint{

    @Override
    public int check(List<Lecture> lectures) {
        int score = 0;
        for (var lecture : lectures) {
            if (lecture.isCourse() && lecture.allocatedPeriod.weekDay > 2) {
                score += 3;
            }
        }
        return score;
    }
}
