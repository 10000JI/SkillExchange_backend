package place.skillexchange.backend.talent.entity;

public enum DayOfWeek {
    SUN, MON, TUE, WED, THU, FRI, SAT;
    public static DayOfWeek fromString(String day) {
        return DayOfWeek.valueOf(day);
    }
}
