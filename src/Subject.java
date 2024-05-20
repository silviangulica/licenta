import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Subject {
  private final List<Lecture> lectureList;
  private final List<Group> groups;
  private final String name;

    public Subject(String name) {
        this.name = name;
        this.groups = new ArrayList<>();
        this.lectureList = new ArrayList<>();
    }

    public void generateLectures() {
        for (Group group : groups) {
            var lecture = new Lecture(this.name, this, "lecture", group);
            this.lectureList.add(lecture);
        }
        var courseLecture = new Lecture(this.name, this,  "course", null);
        this.lectureList.add(courseLecture);
    }

    public void addGroup(Group group) {
        this.groups.add(group);
    }

    public List<Lecture> getLectureList() {
        return this.lectureList;
    }

    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }
}
