import java.util.List;

public class FitClassroomConstraint implements Constraint {
    @Override
    public int check(List<Lecture> lectures) {
        int score = 0;
        for (Lecture lecture : lectures) {
            if (lecture.classroom.getCapacity() < lecture.allocatedGroup.getStudentsAmount()) {
                score += 1;
            }
            if (lecture.classroom.isForCourse() && !lecture.isCourse()) {
                score += 1;
            }
        }
        return score;
    }
}
