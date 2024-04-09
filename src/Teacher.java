public class Teacher {
    public String name;
    public Period[] listOfNotAvailablePeriods;

    public boolean isTeacherAvailable(Period period) {
        for (Period notAvailablePeriod : listOfNotAvailablePeriods) {
            if (notAvailablePeriod.time.equals(period.time) && notAvailablePeriod.weekDay == period.weekDay) {
                return false;
            }
        }
        return true;
    }

    public Teacher(String name, Period[] listOfNotAvailablePeriods) {
        this.name = name;
        this.listOfNotAvailablePeriods = listOfNotAvailablePeriods;
    }

    public String toString() {
        return name;
    }
}
