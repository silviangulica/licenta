import java.time.LocalTime;

public class Period {
    public LocalTime time;
    public int weekDay;

    public Period(LocalTime time, int weekDay) {
        this.time = time;
        this.weekDay = weekDay;
    }
}
