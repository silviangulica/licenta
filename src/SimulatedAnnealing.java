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

    private void loadTeachers(List<String> teachers) {
        for (String teacher : teachers) {
            var teacherData = teacher.split(",");
            var teacherName = teacherData[0];
            var subjects = teacherData[1].replace("[ ", "").replace(" ]", "").split(";");
            var teacherObject = new Teacher(teacherName);
            for (String subject : subjects) {
                teacherObject.addSubject(subject.strip());
            }
            this.teacherList.add(teacherObject);
        }
    }

    private void loadSubjects(List<String> rawData) {
        var subjects = rawData.getFirst().split(",");
        for (String subject : subjects) {
            var subjectObject = new Subject(subject.strip());
            this.subjectList.add(subjectObject);
        }
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
        var teachers = Utils.readFromCsv("./teachers.csv");

        if (teachers.isEmpty()) {
            System.out.println("No teachers found!");
        }

        teachers.ifPresent(this::loadTeachers);

        // Load the subjects
        var subjects = Utils.readFromCsv("./subjects.csv");

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

        for (Subject subject : this.subjectList) {
            System.out.println(subject);
            for (Lecture lecture : subject.getLectureList()) {
                System.out.println(lecture);
            }
        }
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

}