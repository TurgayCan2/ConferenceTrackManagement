import model.Talk;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConferenceManager {

    private static final Logger LOGGER = Logger.getLogger(ConferenceManager.class.getName());
    private static final String MIN_SUFFIX = "min";
    private static final String LIGHTNING_SUFFIX = "lightning";
    private static final String BLANK = " ";
    private static final int DAYMINTIME = 360;
    private static final int MINSESSIONTIME = 180;
    private static final int MAXSESSIONTIME = 240;
    private static final int LUNCHTIMEDURATION = 60;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mma ");

    private String fileName;
    private boolean sorted;

    /**
     * @param fileName
     * @param sorted
     * Constructor for ConferenceManager.
     */
    public ConferenceManager(String fileName, boolean sorted) {
        this.fileName = fileName;
        this.sorted = sorted;
    }

    /**
     * to create and schedule conference.
     * *
     *
     * @throws TalkException
     */
    public List<List<Talk>> scheduleConference() throws Exception {
        List<String> talkList = getTalkListFromFile(fileName);
        return scheduleConferenceTalks(talkList);
    }

    /**
     * public method to create and schedule conference.
     *
     * @param talkList
     * @throws TalkException
     */
    public List<List<Talk>> scheduleConferenceTalks(List<String> talkList) throws Exception {
        List<Talk> talksList = validateAndCreateTalkList(talkList);
        if (sorted) {
            Collections.sort(talkList);
        }
        return getScheduleConferenceTrack(talksList);
    }

    /**
     * Load talk list from input file.
     *
     * @param fileName
     * @return
     * @throws TalkException
     */
    protected List<String> getTalkListFromFile(String fileName) throws Exception {
        List<String> talkList = new ArrayList<String>();
        DataInputStream dataInputStream = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            dataInputStream = new DataInputStream(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
            String strLine = bufferedReader.readLine();
            while (strLine != null) {
                talkList.add(strLine);
                strLine = bufferedReader.readLine();
            }
        } finally {
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException ioe) {
                    LOGGER.log(Level.INFO, ioe.getMessage());
                }
            }
        }

        return talkList;
    }

    /**
     * Validate talk list, check the time for talk and initialize Talk Object.
     *
     * @param talkList
     * @throws Exception
     */
    protected List<Talk> validateAndCreateTalkList(List<String> talkList) throws TalkException {
        if (talkList == null) {
            throw new TalkException("There is not any Talk in List");
        }

        List<Talk> validTalkList = new ArrayList<Talk>();
        for (String talk : talkList) {
            int lastSpaceIndex = talk.lastIndexOf(BLANK);
            if (lastSpaceIndex == -1) {
                throw new TalkException("Invalid talk, " + talk + ". Talk time must be specify.");
            }
            String name = talk.substring(0, lastSpaceIndex);
            if (name == null || "".equals(name.trim())) {
                throw new TalkException("Invalid talk name, " + talk);
            }
            String timeStr = talk.substring(lastSpaceIndex + 1);
            if (!timeStr.endsWith(MIN_SUFFIX) && !timeStr.endsWith(LIGHTNING_SUFFIX)) {
                throw new TalkException("Invalid talk time, " + talk + ". Time must be in min or in lightning");
            }
            int time = convertTime(talk, timeStr);
            validTalkList.add(new Talk(talk, name, time));
        }
        return validTalkList;
    }

    private int convertTime(String talk, String timeStr) throws TalkException {
        int time = 0;
        try {
            if (timeStr.endsWith(MIN_SUFFIX)) {
                time = Integer.parseInt(timeStr.substring(0, timeStr.indexOf(MIN_SUFFIX)));
            } else if (timeStr.endsWith(LIGHTNING_SUFFIX)) {
                String lightningTime = timeStr.substring(0, timeStr.indexOf(LIGHTNING_SUFFIX));
                if ("".equals(lightningTime)) {
                    time = 5;
                } else {
                    time = Integer.parseInt(lightningTime) * 5;
                }
            }
        } catch (NumberFormatException nfe) {
            throw new TalkException("Unable to parse time " + timeStr + " for talk " + talk);
        }
        return time;
    }

    /**
     * change method private to protected for unit test
     * Schedule Conference tracks for morning and evening session.
     *
     * @param talksList
     * @throws Exception
     */
    protected List<List<Talk>> getScheduleConferenceTrack(List<Talk> talksList) throws Exception {
        int totalPossibleDays = ConferenceManagerHelper.getTotalTalksTime(talksList) / DAYMINTIME;

        List<Talk> talks = new ArrayList<Talk>();
        talks.addAll(talksList);

        // Find possible combinations of talks for the morning session
        List<List<Talk>> morningSessionList = findPossibleSessions(talks, totalPossibleDays, true);

        // Remove all the scheduled talks for morning session, from the operationList
        for (List<Talk> talkList : morningSessionList) {
            talks.removeAll(talkList);
        }

        // Find possible combinations of talks for the evening session
        List<List<Talk>> eveningSessionList = findPossibleSessions(talks, totalPossibleDays, false);

        // Remove all the scheduled talks for evening session, from the operationList.
        for (List<Talk> talkList : eveningSessionList) {
            talks.removeAll(talkList);
        }

        //prepare scheduled talks list
        List<Talk> revisedTalkList = ConferenceManagerHelper.prepareScheduledTalkList(talks, eveningSessionList);
        // If operation list is still not empty, its mean the conference can not be scheduled with the provided data.
        if (!revisedTalkList.isEmpty()) {
            throw new Exception("Unable to schedule all task for conferencing");
        }

        // Schedule the day event from morning and evening session.
        return getScheduledTalksList(morningSessionList, eveningSessionList);
    }



    /**
     * method changed to private to protected for unit tests
     * Find possible combination for the session.
     * If morning session then each session must have total time 3 hr.
     * if evening session then each session must have total time greater then 3 hr.
     *
     * @param talksListForOperation
     * @param totalPossibleDays
     * @param morningSession
     * @return
     */
    protected List<List<Talk>> findPossibleSessions(List<Talk> talksListForOperation, int totalPossibleDays, boolean morningSession) {
        int sessionTime;

        if (morningSession) {
            sessionTime = MINSESSIONTIME;
        } else {
            sessionTime = MAXSESSIONTIME;
        }

        List<List<Talk>> possibleCombinations = new ArrayList<List<Talk>>();
        int possibleCombinationCount = 0;

        // Loop to get combination for total possible days
        for (int count = 0; count < talksListForOperation.size(); count++) {
            int startPoint = count;
            int totalTime = 0;
            List<Talk> possibleCombinationList = new ArrayList<Talk>();

            // Loop to get possible combination.
            while (startPoint != talksListForOperation.size()) {
                int currentCount = startPoint;
                startPoint++;
                Talk currentTalk = talksListForOperation.get(currentCount);
                //Scheduled talks then cont.
                if (currentTalk.isScheduled()) {
                    continue;
                }
                int talkTime = currentTalk.getTimeDuration();
                // If the current talk time is greater than maxSessionTimeLimit or
                // sum of the current time and total of talk time added in list  is greater than sessionTime(max or min session time) then continue.
                if (talkTime > sessionTime || talkTime + totalTime > sessionTime) {
                    continue;
                }

                possibleCombinationList.add(currentTalk);
                totalTime += talkTime;

                // If total time is completed for this session than break this loop.
                if (morningSession) {
                    if (totalTime == MINSESSIONTIME) {
                        break;
                    }
                } else if (totalTime > MAXSESSIONTIME) {
                    possibleCombinationList.remove(currentTalk);
                    break;
                }
            }

            // Valid session time for morning session or evening session.
            boolean validSession;
            if (morningSession) {
                validSession = (totalTime == sessionTime);
            } else {
                validSession = (totalTime >= MINSESSIONTIME && totalTime <= sessionTime);
            }
            // If session is valid than add this session in the possible combination list and set all added talk as scheduled.
            if (validSession) {
                possibleCombinations.add(possibleCombinationList);
                for (Talk talk : possibleCombinationList) {
                    talk.setScheduled(true);
                }
                possibleCombinationCount++;
                if (possibleCombinationCount == totalPossibleDays) {
                    break;
                }
            }
        }

        return possibleCombinations;
    }

    /**
     * method changed to private to protected for unit tests
     * Print the scheduled talks with the expected messages.
     *
     * @param morningSessions
     * @param eveningSessions
     */
    protected List<List<Talk>> getScheduledTalksList(List<List<Talk>> morningSessions, List<List<Talk>> eveningSessions) {
        List<List<Talk>> scheduledTalksList = new ArrayList<List<Talk>>();
        int totalPossibleDays = morningSessions.size();
        // Loop to schedule event for all days.
        for (int dayCount = 0; dayCount < totalPossibleDays; dayCount++) {
            List<Talk> talkList = new ArrayList<Talk>();
            // Create a date and initialize start time 09:00 AM.
            Date date = initDate();
            int trackCount = dayCount + 1;
            String scheduledTime = dateFormat.format(date);
            System.out.println("Track " + trackCount + ":");
            // Morning Session - set the scheduled time in the talk and get the next time using time duration of current talk.
            List<Talk> mornSessionTalkList = morningSessions.get(dayCount);
            scheduledTime = printMorningSessionTalks(talkList, date, scheduledTime, mornSessionTalkList);
            // Scheduled Lunch Time for 60 min.
            int lunchTimeDuration = printLunch(talkList, scheduledTime);
            // Evening Session - set the scheduled time in the talk and get the next time using time duration of current talk.
            scheduledTime = getNextScheduledTime(date, lunchTimeDuration);
            scheduledTime = printEveningSessionTalks(eveningSessions.get(dayCount), talkList, date, scheduledTime);
            // Scheduled Networking Event at the end of session, Time duration is just to initialize the Talk object.
            printNetworkEvent(scheduledTalksList, talkList, scheduledTime);
        }

        return scheduledTalksList;
    }

    /**
     * init date 09:00 am.
     * @return
     */
    private Date initDate() {
        Date date = new Date();
        date.setHours(9);
        date.setMinutes(0);
        date.setSeconds(0);
        return date;
    }

    /**
     * print morning session talks
     * @param talkList
     * @param date
     * @param scheduledTime
     * @param mornSessionTalkList
     * @return
     */
    private String printMorningSessionTalks(List<Talk> talkList, Date date, String scheduledTime, List<Talk> mornSessionTalkList) {
        for (Talk talk : mornSessionTalkList) {
            talk.setScheduledTime(scheduledTime);
            System.out.println(scheduledTime + talk.getTitle());
            scheduledTime = getNextScheduledTime(date, talk.getTimeDuration());
            talkList.add(talk);
        }
        return scheduledTime;
    }

    /**
     * print Lunch time
     * @param talkList
     * @param scheduledTime
     * @return
     */
    private int printLunch(List<Talk> talkList, String scheduledTime) {
        Talk lunchTalk = new Talk("Lunch", "Lunch", 60);
        lunchTalk.setScheduledTime(scheduledTime);
        talkList.add(lunchTalk);
        System.out.println(scheduledTime + "Lunch");
        return LUNCHTIMEDURATION;
    }

    /**
     * print evening session talks
     * @param eveSessionTalkList
     * @param talkList
     * @param date
     * @param scheduledTime
     * @return
     */
    private String printEveningSessionTalks(List<Talk> eveSessionTalkList, List<Talk> talkList, Date date, String scheduledTime) {
        for (Talk talk : eveSessionTalkList) {
            talk.setScheduledTime(scheduledTime);
            talkList.add(talk);
            System.out.println(scheduledTime + talk.getTitle());
            scheduledTime = getNextScheduledTime(date, talk.getTimeDuration());
        }
        return scheduledTime;
    }

    /**
     * print Network Event
     * @param scheduledTalksList
     * @param talkList
     * @param scheduledTime
     */
    private void printNetworkEvent(List<List<Talk>> scheduledTalksList, List<Talk> talkList, String scheduledTime) {
        Talk networkingTalk = new Talk("Networking Event", "Networking Event", 60);
        networkingTalk.setScheduledTime(scheduledTime);
        talkList.add(networkingTalk);
        System.out.println(scheduledTime + "Networking Event");
        scheduledTalksList.add(talkList);
    }


    /**
     * method changed to private to protected for unit tests
     * To get next scheduled time in form of String.
     *
     * @param date
     * @param timeDuration
     * @return
     */
    protected String getNextScheduledTime(Date date, int timeDuration) {
        date.setTime(date.getTime() + (timeDuration * 60 * 1000));
        return dateFormat.format(date);
    }

}
