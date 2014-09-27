import model.Talk;

import java.util.ArrayList;
import java.util.List;

public final class ConferenceManagerHelper {

    private static final int MAXSESSIONTIME = 240;

    /**
     * change method private to protected for unit test
     *
     * check if the operation list is not empty,
     * then try to fill all the remaining talks in evening session.
     *
     * @param talks
     * @param eveningSessionList
     * @return
     */
    protected static List<Talk> prepareScheduledTalkList(List<Talk> talks, List<List<Talk>> eveningSessionList) {
        if (!talks.isEmpty()) {
            List<Talk> scheduledTalkList = new ArrayList<Talk>();
            for (List<Talk> talkList : eveningSessionList) {
                int totalTime = getTotalTalksTime(talkList);
                for (Talk talk : talks) {
                    int talkTime = talk.getTimeDuration();

                    if (talkTime + totalTime <= MAXSESSIONTIME) {
                        talkList.add(talk);
                        talk.setScheduled(true);
                        scheduledTalkList.add(talk);
                    }
                }

                talks.removeAll(scheduledTalkList);
                if (talks.isEmpty()) {
                    break;
                }
            }
        }
        return talks;
    }

    /**
     * method changed to private to protected for unit tests
     * To get total time of talks of the given list.
     *
     * @param talksList
     * @return
     */
    protected static int getTotalTalksTime(List<Talk> talksList) {
        if (talksList == null || talksList.isEmpty()) {
            return 0;
        }
        int totalTime = 0;
        for (Talk talk : talksList) {
            totalTime += talk.getTimeDuration();
        }
        return totalTime;
    }
}
