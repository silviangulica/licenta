import java.time.LocalTime;

public class Period {
    public LocalTime time;
    public int weekDay;

    public Period(LocalTime time, int weekDay) {
        this.time = time;
        this.weekDay = weekDay;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Period period)) {
            return false;
        }
        return time.equals(period.time) && weekDay == period.weekDay;
    }

    @Override
    public String toString() {
        return "Period{" +
                "time=" + time +
                ", weekDay=" + weekDay +
                '}';
    }
}
