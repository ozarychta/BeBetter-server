package com.ozarychta.bebetter.enums;

public enum RepeatPeriod {
    ONCE_PER_WEEK(1),
    TWICE_PER_WEEK(2),
    THREE_PER_WEEK(3),
    FOUR_PER_WEEK(4),
    FIVE_PER_WEEK(5),
    SIX_PER_WEEK(6),
    EVERYDAY(7);

    private Integer timesPerWeek;

    RepeatPeriod(Integer timesPerWeek){
        this.timesPerWeek = timesPerWeek;
    }

    public Integer getTimesPerWeek() {
        return timesPerWeek;
    }
}
