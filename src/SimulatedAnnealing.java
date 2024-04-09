import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SimulatedAnnealing {
    public List<Period> listOfPeriods;
    public List<Classroom> listOfClassrooms;
    public List<Course> listOfCourses;


    private void generatePeriods() {
        for(int weekDay = 1; weekDay <= 5; weekDay++) {
            for(int hours = 8; hours <= 18; hours+=2) {
                listOfPeriods.add(new Period(LocalTime.of(hours,0), weekDay));
            }
        }
    }

    private void generateClassrooms() {
        for(int i = 1; i <= 5; i++) {
            listOfClassrooms.add(new Classroom("Classroom " + i));
        }
    }

    public void init() {
        this.generatePeriods();
        this.generateClassrooms();
        // Cum aleg perioade?
        // 0 -> 5 => weekDay = 1, respect 0 e 8, 5 e 18
        // 6 -> 11 => weekDay = 2, respect 6 e 8, 11 e 18
        // 12 -> 17 => weekDay = 3, respect 12 e 8, 17 e 18
        // 18 -> 23 => weekDay = 4, respect 18 e 8, 23 e 18
        // 24 -> 29 => weekDay = 5, respect 24 e 8, 29 e 18

        Teacher t1 = new Teacher("Teacher 1", new Period[] {listOfPeriods.get(3), listOfPeriods.get(4), listOfPeriods.get(5)});
        Teacher t2 = new Teacher("Teacher 2", new Period[] {listOfPeriods.get(1), listOfPeriods.get(2), listOfPeriods.get(4), listOfPeriods.get(5)});

        Course c1 = new Course("Course 1", t1);
        Course c2 = new Course("Course 2", t2);
        Course c3 = new Course("Course 3", t2);

        listOfCourses.add(c1);
        listOfCourses.add(c2);
        listOfCourses.add(c3);

        // Sortam profesorii dupa numarul de perioade in care sunt ocupati
        listOfCourses.sort(Comparator.comparingInt(o -> o.teacher.listOfNotAvailablePeriods.length));

    }
    public void generateHardSolution() {
        for (Classroom classroom : listOfClassrooms) {
            for (Period period : listOfPeriods) {
                for (Course course : listOfCourses) {
                    if (course.allocatedPeriod != null) {
                        continue;
                    }
                    if (!course.teacher.isTeacherAvailable(period)) {
                        continue;
                    }
                    course.classroom = classroom;
                    course.allocatedPeriod = period;
                    break;
                }
            }
        }

        for (Course course : listOfCourses) {
            System.out.println(course.title + " " + course.teacher + " " + course.classroom + " " + course.allocatedPeriod.time + " " + course.allocatedPeriod.weekDay);
        }
    }

    public SimulatedAnnealing() {
        this.listOfPeriods = new ArrayList<>();
        this.listOfClassrooms = new ArrayList<>();
        this.listOfCourses = new ArrayList<>();
    }
}
