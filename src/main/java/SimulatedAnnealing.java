import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.System.exit;
import static java.lang.System.setOut;

public class SimulatedAnnealing {
    private final List<Subject> subjectList;
    private final List<Period> periodList;
    private final List<Classroom> classroomList;
    private final List<Teacher> teacherList;
    private final List<Group> groupList;
    private List<Lecture> lectureList;

    public SimulatedAnnealing() {
        this.subjectList = new ArrayList<>();
        this.teacherList = new ArrayList<>();
        this.periodList = new ArrayList<>();
        this.classroomList = new ArrayList<>();
        this.groupList = new ArrayList<>();
        this.lectureList = new ArrayList<>();
        this.init();
    }

    private void loadTeachers(List<Teacher> teachers) {
        this.teacherList.addAll(teachers);
    }

    private void loadSubjects(List<Subject> rawData) {
        this.subjectList.addAll(rawData);
    }

    private void generatePeriods() {
        for (int i = 1; i <= 5; i++) {
            var time = LocalTime.of(8, 0);
            for (int j = 0; j < 6; j++) {
                var period = new Period(time, i);
                this.periodList.add(period);
                time = time.plusHours(2);
            }
        }
    }

    private void generateClassrooms() {
        for (int i = 1; i <= 7; i++) {
            var classroom = new Classroom("C " + (100 + i), 50);
            this.classroomList.add(classroom);
        }
        for (int i = 1; i <= 5; i++) {
            var classroom = new Classroom("C " + i, 150, true);
            this.classroomList.add(classroom);
        }
    }

    private void generateGroups() {
        for (int i = 1; i <= 5; i++) {
            var group = new Group("Group " + i, 25);
            this.groupList.add(group);
        }
    }

    private void init() {
        // Load the teachers
        var teachers = Utils.readTeachersFromJson("./teachers.json");

        if (teachers.isEmpty()) {
            System.out.println("No teachers found!");
        }

        teachers.ifPresent(this::loadTeachers);

        // Load the subjects
        var subjects = Utils.readSubjectsFromJson("./subjects.json");

        if (subjects.isEmpty()) {
            System.out.println("No subjects found!");
        }

        subjects.ifPresent(this::loadSubjects);

        // Generate the periods
        this.generatePeriods();

        // Generate the classrooms
        this.generateClassrooms();

        // Generate the groups
        this.generateGroups();

        // Add groups to the subjects
        for (Subject subject : this.subjectList) {
            for (Group group : this.groupList) {
                subject.addGroup(group);
            }
        }

        // Generate the lectures
        for (Subject subject : this.subjectList) {
            subject.generateLectures();
        }

        // Add lectures to the lecture list
        for (Subject subject : this.subjectList) {
            this.lectureList.addAll(subject.getLectureList());
        }
    }

    public void runSimulatedAnnealing() {
        // Implementing the first part of the algorithm
        for (Classroom classroom : this.classroomList) {
            for (Period period : this.periodList) {
                this.findLecture(classroom, period);
            }
        }


        // Implementing the SA part of the algorithm
        var k = 0;

        var solution = this.lectureList;
        var optimalSolution = this.lectureList;

        int     IT = 1200;
        int     L  = 2000;
        double  T  = 20;
        double  a  = 0.995;

        int energySolution = 0;
        int energyCandidate = 0;
        int energyOptimal = 0;

        while (k < IT) {

            for (int i = 1; i <= L; i++) {
                var N1 = periodMove(solution);
                var N2 = classroomMove(solution);

                var N = Math.random() < 0.5 ? N1 : N2;

                energySolution = calculateEnergy(solution);
                energyCandidate = calculateEnergy(N);

                var delta = energyCandidate - energySolution;

                if (delta < 0) {
                    solution = N;

                    energyOptimal = calculateEnergy(optimalSolution);
                    if (energyCandidate < energyOptimal) {
                        optimalSolution = N;
                        System.out.println("\nOptimal solution found!");
                        System.out.println("Iteration: " + k + " iter: " + i + " T: " + T );
                        System.out.println("Energy: " + energyCandidate);

                        if (energyCandidate <= 0) {
                            Utils.generateHTMLDocumentForLectures(optimalSolution);
                            exit(0);
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

        this.lectureList = optimalSolution;
        Utils.generateHTMLDocumentForLectures(this.lectureList);
    }

    private void findLecture(Classroom classroom, Period period) {
        for (Subject subject : this.subjectList) {
            for (Lecture lecture : subject.getLectureList()) {
                if (null != lecture.allocatedPeriod) continue;

                if (this.findTeacher(lecture, classroom, period)) {
                    return;  // Lecture found, next period
                }
            }
        }
    }

    private boolean findTeacher(Lecture lecture, Classroom classroom, Period period) {
        for (Teacher teacher : this.teacherList) {
            if (teacher.isTeacherAvailable(lecture.subject, period)) {
                lecture.setTeacher(teacher);
                lecture.setClassroom(classroom);
                lecture.setAllocatedPeriod(period);
                return true;
            }
        }
        return false;
    }

    private boolean isLectureValid(Lecture lecture, Classroom classroom, Period period) {
        for (Lecture lec : this.lectureList) {
            if (lec == lecture) continue;
            // check if classroom is ocupied
            if (lec.classroom.equals(classroom) && lec.allocatedPeriod.equals(period)) {
                return false;
            }
            if (lec.allocatedPeriod.equals(period) && lec.allocatedGroup.equals(lecture.allocatedGroup)) {
                return false;
            }
        }

        return true;
    }

    private List<Lecture> deepCopy(List<Lecture> lectures) {
        return lectures.stream()
                .map(Lecture::clone)
                .collect(Collectors.toList());
    }

    public Lecture getRandomLecture(List<Lecture> lectures) {
        return lectures.get((int) (Math.random() * this.lectureList.size()));
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
        List<Lecture> newLectureList = deepCopy(lectures);


        var randomPeriod = this.periodList.get((int) (Math.random() * this.periodList.size()));
        // Lecture that will posible switch the period
        Lecture lecture;
        do {
            lecture = getRandomLecture(newLectureList);
        } while (lecture.allocatedPeriod.equals(randomPeriod));

//        for (var lecture : newLectureList) {
//            Period randomPeriod;
//            do {
//                randomPeriod = this.periodList.get((int) (Math.random() * this.periodList.size()));
//            } while (lecture.allocatedPeriod.equals(randomPeriod));
//
//            // Get the lecture that will probabil switch
//            Lecture possibleLecture = getLecture(newLectureList, randomPeriod, lecture.classroom);
//
//            // Verify the cases
//            if (possibleLecture != null) {
//                // Swap the periods
//                possibleLecture.allocatedPeriod = lecture.allocatedPeriod;
//            }
//            lecture.allocatedPeriod = randomPeriod;
//        }

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
        List<Lecture> newLectureList = deepCopy(lectures);

        for (var lecture : newLectureList) {
            Classroom randomClassroom;
            do {
                randomClassroom = this.classroomList.get((int) (Math.random() * this.classroomList.size()));
            } while (lecture.classroom.equals(randomClassroom));

            // Get the lecture that will probabil switch
            Lecture possibleLecture = getLecture(newLectureList, lecture.allocatedPeriod, randomClassroom);

            // Verify the cases
            if (possibleLecture != null) {
                // Swap the periods
                possibleLecture.classroom = lecture.classroom;
            }
            lecture.classroom = randomClassroom;
        }

        return newLectureList;
    }

    private int calculateEnergy(List<Lecture> lectures) {
        int energy = 0;
//         Calcualte 1.
//         Check if a classroom is too small for a group and also verify if the classroom is for course or not
        for (Lecture lecture : lectures) {
            if (lecture.classroom.getCapacity() < lecture.allocatedGroup.getStudentsAmount()) {
                energy += 1;
            }
            if (lecture.classroom.isForCourse() && !lecture.isCourse()) {
                energy += 1;
            }
        }

        // Calculate 2.
        // Check if a grroup have the same lecture in the same period
        for (var lecture : lectures) {
            for (var lec : lectures) {
                if (lec.allocatedPeriod.equals(lecture.allocatedPeriod) && lec.allocatedGroup.equals(lecture.allocatedGroup)
                && lec != lecture) {
                    energy += 2;
                }
            }
        }


        return energy;
    }
}