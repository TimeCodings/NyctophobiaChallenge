package dev.timecoding.nyctophobia.challenge.api;

public class ChallengeTimer {

    private Integer hours = 0;
    private Integer minutes = 0;
    private Integer seconds = 0;

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public void setSeconds(Integer seconds) {
        this.seconds = seconds;
    }

    public void addHours(Integer i){
        setHours((i+hours));
    }

    public void addMinutes(Integer i){
        setMinutes((i+minutes));
    }

    public void addSeconds(Integer i){
        setSeconds((i+seconds));
    }

    public String getHours() {
        if(hours.toString().length() == 1){
            return "0"+hours;
        }
        return hours.toString();
    }

    public String getMinutes() {
        if(minutes.toString().length() == 1){
            return "0"+minutes;
        }
        return minutes.toString();
    }

    public String getSeconds() {
        if(seconds.toString().length() == 1){
            return "0"+seconds;
        }
        return seconds.toString();
    }

    public String getFullFormat(){
        return getHours()+":"+getMinutes()+":"+getSeconds();
    }

}
