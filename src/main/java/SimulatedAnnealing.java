import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SimulatedAnnealing {
    private final List<Subject> subjectList;
    private final List<Period> periodList;
    private final List<Classroom> classroomList;
    private final List<Teacher> teacherList;
    private final List<Group> groupList;

    public SimulatedAnnealing() {
        this.subjectList = new ArrayList<>();
        this.teacherList = new ArrayList<>();
        this.periodList = new ArrayList<>();
        this.classroomList = new ArrayList<>();
        this.groupList = new ArrayList<>();
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
            var classroom = new Classroom("Classroom " + i);
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
    }

    public void runSimulatedAnnealing() {
        // Implementing the first part of the algorithm
        for (Classroom classroom : this.classroomList) {
            for (Period period : this.periodList) {
                this.findLecture(classroom, period);
            }
        }

        // Implementing the simulated annealing part ;)
        var temperature = 10000.0;
        var coolingRate = 0.003;

        while (temperature > 1) {
            var newSolution = new ArrayList<>(this.subjectList);
            var randomSubjectIndex = (int) (Math.random() * newSolution.size());
            var subject = newSolution.get(randomSubjectIndex);

            var newPeriodIndex = (int) (Math.random() * this.periodList.size());
            var newPeriod = this.periodList.get(newPeriodIndex);

            var currentEnergy = this.calculateEnergy(this.subjectList);
            var neighbourEnergy = this.calculateEnergy(newSolution);

            if (this.acceptanceProbability(currentEnergy, neighbourEnergy, temperature) > Math.random()) {
                this.subjectList.clear();
                this.subjectList.addAll(newSolution);
            }

            temperature *= 1 - coolingRate;
        }

        Utils.generateHTMLDocumentForSubject(this.subjectList);
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

    public float calculateEnergy(List<Subject> subjects) {
        var energy = 0.0f;
        for (Subject subject : subjects) {
            for (Lecture lecture : subject.getLectureList()) {
                if (null == lecture.allocatedPeriod) {
                    energy += 1;
                }
            }
        }
        return energy;
    }

    public float acceptanceProbability(float energy, float newEnergy, double temperature) {
        if (newEnergy < energy) {
            return 1.0f;
        }
        return (float) Math.exp((energy - newEnergy) / temperature);
    }
}