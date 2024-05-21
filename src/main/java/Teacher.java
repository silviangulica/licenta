import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Teacher {
    @JsonProperty("name")
    public String name;

    public List<Period> listOfNotAvailablePeriods;

    @JsonProperty("subjects")
    public List<String> subjects;

    public boolean isTeacherAvailable(Subject subject, Period period) {
        for (Period notAvailablePeriod : listOfNotAvailablePeriods) {
            if (notAvailablePeriod.time.equals(period.time) && notAvailablePeriod.weekDay == period.weekDay) {
                return false;
            }
        }
        String subjectName = subject.getName();
        return subjects.contains(subjectName);
    }

    public Teacher(String name) {
        this.name = name;
        this.subjects = new ArrayList<>();
        this.listOfNotAvailablePeriods = new ArrayList<>();
    }

    public Teacher(String name, List<String> subjects) {
        this.name = name;
        this.subjects = subjects;
        this.listOfNotAvailablePeriods = new ArrayList<>();
    }

    public Teacher() {
        this.listOfNotAvailablePeriods = new ArrayList<>();
    }

    public void addSubject(String subject) {
        this.subjects.add(subject);
    }

    @Override
    public String toString() {
        return name + " " + subjects;
    }
}
