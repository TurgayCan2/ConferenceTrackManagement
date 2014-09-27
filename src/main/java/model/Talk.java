package model;

/**
 * Created by turgaycan on 9/25/14.
 * <p/>
 * class Talk, to store and retrieve information about talk.
 * implements Comparable interface to sort talk on the basis of time duration.
 */

public class Talk implements Comparable {
    private String title;
    private String name;
    private int timeDuration;
    private boolean scheduled = false;
    private String scheduledTime;

    /**
    *Constructor for Talk
    *
    */
    public Talk() {
    }

    /**
     * Constructor for Talk.
     *
     * @param title
     * @param name
     * @param time
     */
    public Talk(String title, String name, int time) {
        this.title = title;
        this.name = name;
        this.timeDuration = time;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTimeDuration(int timeDuration) {
        this.timeDuration = timeDuration;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }


    public boolean isScheduled() {
        return scheduled;
    }


    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }


    public int getTimeDuration() {
        return timeDuration;
    }


    public String getTitle() {
        return title;
    }

    /**
     * Sort data in descending order.
     *
     * @param obj
     * @return
     */
    @Override
    public int compareTo(Object obj) {
        Talk talk = (Talk) obj;
        if (this.timeDuration > talk.timeDuration) {
            return -1;
        } else if (this.timeDuration < talk.timeDuration) {
            return 1;
        } else {
            return 0;
        }
    }
}
