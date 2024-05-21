public class Group {
    private final String name;
    private final int studentsAmount;

    public Group(String name, int studentsAmount) {
        this.name = name;
        this.studentsAmount = studentsAmount;
    }

    public String getName() {
        return name;
    }

    public int getStudentsAmount() {
        return studentsAmount;
    }

    @Override
    public String toString() {
        return name;
    }
}
