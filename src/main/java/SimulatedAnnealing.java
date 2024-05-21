import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
        for (int i = 1; i <= 5; i++) {
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

        // Implementing the simulated annealing part ;)
        var temperature = 1000000.0;
        var coolingRate = 0.003;

        double lastDelta = -1;
        while (temperature > 1) {
            var currentEnergy = this.calculateEnergy(this.lectureList);
            var newLectures = this.generateRandomSolution(this.lectureList);
            var newEnergy = this.calculateEnergy(newLectures);

            // TODO: vreau ca abs de lastDelta sa fie cat mai mic
            var deltaEnergy = newEnergy - currentEnergy;

            if (deltaEnergy < 0 && deltaEnergy < lastDelta){
                this.lectureList = newLectures;
                lastDelta = deltaEnergy;
                if (currentEnergy == 0) {
                    break;
                }
            } else {
                var random = Math.random();
                if (random < Math.exp(-deltaEnergy / temperature)) {
                    this.lectureList = newLectures;
                }
            }

            System.out.println("deltaEnergy" + deltaEnergy + " lastDelta " + lastDelta + " temperature " + temperature);
            temperature *= 1 - coolingRate;
        }

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

    private List<Lecture> generateRandomSolution(List<Lecture> lectures) {
        var newLectures = new ArrayList<>(lectures);
        for (Lecture lecture : newLectures) {
            var classroom = this.classroomList.get((int) (Math.random() * this.classroomList.size()));
            var period = this.periodList.get((int) (Math.random() * this.periodList.size()));



            if (this.isLectureValid(lecture, classroom, period)) {
                lecture.setClassroom(classroom);
                lecture.setAllocatedPeriod(period);
            }
        }

        return newLectures;
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

    private double calculateEnergy(List<Lecture> lectures) {
        var energy = 0.0;
        // Calcualte 1.
        // Check if a classroom is too small for a group and also verify if the classroom is for course or not
        for (Lecture lecture : lectures) {
            if (lecture.classroom.getCapacity() < lecture.allocatedGroup.getStudentsAmount()) {
                energy += 1;
            }
        }

        return energy;
    }
}