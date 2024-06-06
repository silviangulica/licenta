import java.util.List;

public class OneLecturePerGroupConstraint implements Constraint{
    @Override
    public int check(List<Lecture> lectures) {
        // Check if for a group, there are two lectures in the same period
        int score = 0;
        for (var lecture : lectures) {
            for (var lec : lectures) {
                if (lec.allocatedGroup.equals(lecture.allocatedGroup) && lec.allocatedPeriod.equals(lecture.allocatedPeriod) && lec != lecture) {
                    score += 40;
                }
            }
        }
        return score;
    }
}
