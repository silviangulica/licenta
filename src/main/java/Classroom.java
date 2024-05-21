public class Classroom {
    public String name;
    private final int capacity;
    private final boolean isForCourse;

    public Classroom(String name) {
        this.name = name;
        this.capacity = 30;
        this.isForCourse = false;
    }

    public Classroom(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
        this.isForCourse = false;
    }
    public Classroom(String name, int capacity, boolean isForCourse) {
        this.name = name;
        this.capacity = capacity;
        this.isForCourse = isForCourse;
    }

    public String toString() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isForCourse() {
        return isForCourse;
    }
}
