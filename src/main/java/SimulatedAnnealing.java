import java.util.List;
import java.util.stream.Collectors;


public class SimulatedAnnealing {
    private final List<Period> periodList;
    private final List<Classroom> classroomList;

    public SimulatedAnnealing(List<Period> periodList, List<Classroom> classroomList) {
        this.periodList = periodList;
        this.classroomList = classroomList;
    }

    public void runSimulatedAnnealing(List<Lecture> rawSolution) {
        ConstraintChecker constraintChecker = new ConstraintChecker();
        constraintChecker.addConstraint(new OneLecturePerGroupConstraint());
        constraintChecker.addConstraint(new FirstTwoWeekDayCourseConstraint());
        constraintChecker.addConstraint(new FitClassroomConstraint());
        constraintChecker.addConstraint(new IsTeacherTeachingConstraint());



        // Implementing the SA part of the algorithm
        var k = 0;

        var solution = rawSolution;
        var optimalSolution = rawSolution;

        int IT = 1200;
        int L = 2000;
        double T = 20;
        double a = 0.995;

        int energySolution = 0;
        int energyCandidate = 0;
        int energyOptimal = 0;

        while (k < IT) {

            for (int i = 1; i <= L; i++) {
                var N1 = periodMove(solution);
                var N2 = classroomMove(solution);

                var N = Math.random() < 0.5 ? N1 : N2;

                energySolution = constraintChecker.checkConstraints(solution);
                energyCandidate = constraintChecker.checkConstraints(N);

                var delta = energyCandidate - energySolution;

                if (delta < 0) {
                    solution = N;

                    energyOptimal = constraintChecker.checkConstraints(optimalSolution);
                    if (energyCandidate < energyOptimal) {
                        optimalSolution = N;
                        System.out.println("\nOptimal solution found!");
                        System.out.println("Iteration: " + k + " iter: " + i + " T: " + T);
                        System.out.println("Energy: " + energyCandidate);

                        if (energyCandidate == 0) {
                            Utils.generateHTMLDocumentForLectures(optimalSolution);
                            return;
                        }
                    }

                } else if (Math.random() < Math.exp(delta / T)) {
                    solution = N;
                }
            }

            T = a * T;
            k++;
            solution = optimalSolution;
        }

        Utils.generateHTMLDocumentForLectures(optimalSolution);
    }

    public Lecture getRandomLecture(List<Lecture> lectures) {
        return lectures.get((int) (Math.random() * lectures.size()));
    }

    public Lecture getLecture(List<Lecture> lectures, Period period, Classroom classroom) {
        for (var lecture : lectures) {
            if (lecture.allocatedPeriod.equals(period) && lecture.classroom.equals(classroom)) {
                return lecture;
            }
        }
        return null;
    }

    private List<Lecture> periodMove(List<Lecture> lectures) {
        List<Lecture> newLectureList = Utils.deepCopy(lectures);


        var randomPeriod = this.periodList.get((int) (Math.random() * this.periodList.size()));
        // Lecture that will posible switch the period
        Lecture lecture;
        do {
            lecture = getRandomLecture(newLectureList);
        } while (lecture.allocatedPeriod.equals(randomPeriod));

        // Get the lecture that will probabil switch
        Lecture possibleLecture = getLecture(newLectureList, randomPeriod, lecture.classroom);

        // Verify the cases
        if (possibleLecture != null) {
            // Swap the periods
            possibleLecture.allocatedPeriod = lecture.allocatedPeriod;
        }
        lecture.allocatedPeriod = randomPeriod;

        return newLectureList;
    }

    private List<Lecture> classroomMove(List<Lecture> lectures) {
        List<Lecture> newLectureList = Utils.deepCopy(lectures);

        var randomClassroom = this.classroomList.get((int) (Math.random() * this.classroomList.size()));

        Lecture lecture;
        do {
            lecture = getRandomLecture(newLectureList);
        } while (lecture.classroom.equals(randomClassroom));

        Lecture possibleLecture = getLecture(newLectureList, lecture.allocatedPeriod, randomClassroom);

        // Verify the cases
        if (possibleLecture != null) {
            // Swap the classrooms
            possibleLecture.classroom = lecture.classroom;
        }
        lecture.classroom = randomClassroom;

        return newLectureList;
    }
}