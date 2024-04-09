public class Course {
    public String title;
    public Teacher teacher;
    public Classroom classroom;
    public Period allocatedPeriod;

    public Course(String title, Teacher teacher){
        this.title = title;
        this.teacher = teacher;
    }
}
