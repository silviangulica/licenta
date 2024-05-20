public class Lecture {
    public String title;
    public Teacher teacher;
    public Classroom classroom;
    public Period allocatedPeriod;
    public String type;
    public Group allocatedGroup;
    public Subject subject;

    public Lecture(String title, Subject subject, Teacher teacher, String type, Group allocatedGroup) {
        this.title = title;
        this.teacher = teacher;
        this.type = type;
        this.allocatedGroup = allocatedGroup;
        this.subject = subject;
    }

    public Lecture(String title, Subject subject, String type, Group allocatedGroup) {
        this.title = title;
        this.type = type;
        this.allocatedGroup = allocatedGroup;
        this.subject = subject;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public void setAllocatedPeriod(Period period) {
        this.allocatedPeriod = period;
    }

    @Override
    public String toString() {
        return title + " " + teacher + " " + classroom + " " + allocatedPeriod + " " + type + " " + allocatedGroup;
    }
}
