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


        var currentSolution = this.lectureList;

        var temperature = 1.0;
        var coolingRate = 0.003;
        var iters = 0;

        while (temperature > 0.1) {
            var newSolution = this.generateRandomSolution(currentSolution);

            var currentEnergy = this.calculateEnergy(currentSolution);
            var newEnergy = this.calculateEnergy(newSolution);

            var deltaEnergy = newEnergy - currentEnergy;

            if (deltaEnergy < 0) {
                currentSolution = newSolution;
            } else {
                var random = Math.random();
                if (random < Math.exp(-deltaEnergy / temperature)) {
                    currentSolution = newSolution;
                }
            }

            temperature *= 1 - coolingRate;
            System.out.println("Energy: " + deltaEnergy + " Temperature: " + temperature + " Iters: " + iters++);
        }


        this.lectureList = currentSolution;
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

            if (lecture.isCourse()) {
                while( !classroom.isForCourse() ) {
                    classroom = this.classroomList.get((int) (Math.random() * this.classroomList.size()));
                }
            }

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

    public Lecture getLecture(Period period, Classroom classroom)
    {
        for (var lecture : this.lectureList) {
            if (lecture.allocatedPeriod.equals(period) && lecture.classroom.equals(classroom)) {
                return lecture;
            }
        }

        return null;
    }

    public Lecture getRandomLecture() {
        return this.lectureList.get((int) (Math.random() * this.lectureList.size()));
    }

    private List<Lecture> periodMove(List<Lecture> lectures) {
//        // pick random period and random classroom
//        var randomPeriod1 = this.periodList.get((int) (Math.random() * this.periodList.size()));
//        var randomClassroom1 = this.classroomList.get((int) (Math.random() * this.classroomList.size()));
//
//        // get the lecture if there are any
//        Lecture lectureAtRandomPos1 = getLecture(randomPeriod1, randomClassroom1);

        // pick random period and random classroom
        var randomPeriod1 = this.periodList.get((int) (Math.random() * this.periodList.size()));

        Lecture lecture;
        do {
            lecture = getRandomLecture();
        } while (lecture.allocatedPeriod.equals(randomPeriod1));
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

        // Calculate 2.
        // Check if the courses are not on monday or tuesday
        for (Lecture lecture : lectures) {
            if (lecture.isCourse() && lecture.allocatedPeriod.weekDay > 2) {
                energy += 1;
            }
        }

        return energy;
    }
}