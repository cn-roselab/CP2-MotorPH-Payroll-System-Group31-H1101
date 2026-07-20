package app.models;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Model class representing a single daily attendance entry
 * from MotorPH_Attendance.csv.
 */
public class AttendanceRecord {

    private final String empNo;
    private final LocalDate date;
    private final LocalTime timeIn;
    private final LocalTime timeOut;

    public AttendanceRecord(String empNo, LocalDate date, LocalTime timeIn, LocalTime timeOut) {
        this.empNo = empNo;
        this.date = date;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public String getEmpNo() { return empNo; }
    public LocalDate getDate() { return date; }
    public LocalTime getTimeIn() { return timeIn; }
    public LocalTime getTimeOut() { return timeOut; }
}
