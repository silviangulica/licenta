public class Lecture implements Cloneable {
    public String title;
    public Teacher teacher;
    public Classroom classroom;
    public Period allocatedPeriod;
    public String type;
    public Group allocatedGroup;
    public Subject subject;
    private boolean isCourse = false;

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
    public Lecture(String title, Subject subject, String type, Group allocatedGroup, boolean isCourse) {
        this.title = title;
        this.type = type;
        this.allocatedGroup = allocatedGroup;
        this.subject = subject;
        this.isCourse = isCourse;
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

    public boolean isCourse() {
        return isCourse;
    }

    public String getGroupName() {
        return this.allocatedGroup == null ? "Course" : this.allocatedGroup.getName();
    }

    @Override
    public String toString() {
        return title + " " + teacher + " " + classroom + " " + allocatedPeriod + " " + type + " " + allocatedGroup;
    }

    @Override
    public Lecture clone() {
        try {
            return (Lecture) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
