package net.mready.weatherwake.alarms.models;

import java.io.Serializable;

public class Alarm implements Serializable {

    private long id;
    private String name = "";
    private boolean enabled;
    private int hour;
    private int minutes;
    private String alert;
    private String daysOfWeek = "";
    private int ringtoneSelected;
    private String[] days = {"", "Sun","Mon","Tue", "Wed","Thurs","Fri","Sat"};
    private String[] songs = {null, "/res/raw/elegantringtone","/res/raw/risingsun","/res/raw/superringtone"};
    private boolean[] checked = new boolean[8];

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setRingtoneSelected(int ringtoneSelected){
        this.ringtoneSelected = ringtoneSelected;
        setAlert(songs[ringtoneSelected]);
    }

    public int getRingtoneSelected() {return ringtoneSelected; }
    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public void setDaysOfWeek(String days) {
        daysOfWeek = days;
    }

    public String getDaysOfWeek() {
        String res ="";
        for(int i = 0 ; i< 8; i++){
            if(checked[i]){
                switch (i) {
                    case 1:
                        res += "Sun "; break;
                    case 2:
                        res += "Mon "; break;
                    case 3:
                        res += "Tue "; break;
                    case 4:
                        res += "Wed "; break;
                    case 5:
                        res += "Thurs "; break;
                    case 6:
                        res += "Fri "; break;
                    case 7:
                        res += "Mon "; break;

                }
            }
        }

        return daysOfWeek;
    }

    public void addDay(int weekDay) {
        String day = String.valueOf(weekDay);
        checked[weekDay] = true;
        if (!daysOfWeek.contains(day)) {
            daysOfWeek = daysOfWeek + days[weekDay]+" ";
        }
    }

    public void removeDay(int weekDay) {
        String day = days[weekDay];
        checked[weekDay] = false;
        daysOfWeek = daysOfWeek.replaceAll(day, "");
    }

    public boolean isDaySelected(int day) {
        return daysOfWeek.contains(String.valueOf(day));
    }

    public boolean isRepeating() {
        return !daysOfWeek.equals("");
    }

}