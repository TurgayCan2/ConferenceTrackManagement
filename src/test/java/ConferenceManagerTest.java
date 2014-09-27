import model.Talk;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ConferenceManagerTest {

    private static final String FILENAME = "";
    private ConferenceManager conferenceManager;

    @Before
    public void init(){
        conferenceManager = new ConferenceManager(FILENAME, false);
    }

    @Test
    public void shouldThrowExceptionIfNotFoundFile(){
        try {
            conferenceManager.getTalkListFromFile(FILENAME);
        } catch (Exception e) {
            assertThat(e.getMessage(), equalTo(" (No such file or directory)"));
        }
    }

    @Test(expected = TalkException.class)
    public void shouldThrowTalkExceptionIfTalkIsNull() throws TalkException{
        conferenceManager.validateAndCreateTalkList(null);
    }

    @Test(expected = TalkException.class)
    public void shouldThrowTalkExceptionIfTalkNotHaveTimeOrTitleOrAnySpace() throws TalkException{
        List<String> talkList = new ArrayList<String>();
        String talk1 = "talk1";
        String talk2 = "talk22";

        talkList.addAll(Arrays.asList(talk1, talk2));

        conferenceManager.validateAndCreateTalkList(talkList);
    }

    @Test(expected = TalkException.class)
    public void shouldThrowTalkExceptionIfTalkNotHaveTitleOrBlank() throws TalkException{
        List<String> talkList = new ArrayList<String>();
        String talk1 = " 30min";
        String talk2 = " 30";

        talkList.addAll(Arrays.asList(talk1, talk2));

        conferenceManager.validateAndCreateTalkList(talkList);
    }

    @Test(expected = TalkException.class)
    public void shouldThrowTalkExceptionIfTalkNotHaveValidTime() throws TalkException{
        List<String> talkList = new ArrayList<String>();
        String talk1 = "java 30min";
        String talk2 = "java 30";

        talkList.addAll(Arrays.asList(talk1, talk2));

        conferenceManager.validateAndCreateTalkList(talkList);
    }

    @Test(expected = TalkException.class)
    public void shouldThrowTalkExceptionIfTalkNotHaveValidFormatOfTime() throws TalkException{
        List<String> talkList = new ArrayList<String>();
        String talk1 = "java 30min";
        String talk2 = "java 30smin";

        talkList.addAll(Arrays.asList(talk1, talk2));

        conferenceManager.validateAndCreateTalkList(talkList);
    }

    @Test
    public void shouldReturnValidTalkList() throws TalkException{
        List<String> talkList = new ArrayList<String>();
        String talk1 = "java 30min";
        String talk2 = "scala 50min";
        String talk3 = "python 50min";
        String talk4 = "python lightning";
        String talk5 = "spring 30min";

        talkList.addAll(Arrays.asList(talk1, talk2, talk3, talk4, talk5));

        List<Talk> talks = conferenceManager.validateAndCreateTalkList(talkList);

        assertEquals(talks.size(), 5);
        assertEquals(talks.size(), talkList.size());
        assertEquals(talks.get(0).getName(), "java");
        assertEquals(talks.get(4).getName(), "spring");
        assertEquals(talks.get(3).getTimeDuration(), 5);
    }

    @Test
    public void shouldNotAnyGetMorningSessionTalksListForOneDayIfNotMeetMinSessionTime(){
        List<Talk> talks = new ArrayList<Talk>();
        Talk talk1 = new Talk("talk1", "java", 45);
        Talk talk2 = new Talk("talk2", "scala", 60);
        Talk talk3 = new Talk("talk3", "python", 35);
        Talk talk4 = new Talk("talk4", "ruby", 45);
        Talk talk5 = new Talk("talk5", "lightning", 15);
        talks.addAll(Arrays.asList(talk1, talk2, talk3, talk4, talk5));

        List<List<Talk>> possibleCombSession = conferenceManager.findPossibleSessions(talks, 1, true);

        assertEquals(possibleCombSession.size(), 0);
    }

    @Test
    public void shouldGetMorningSessionTalksListForOneDay(){
        List<Talk> talks = new ArrayList<Talk>();
        Talk talk1 = new Talk("talk1", "java", 45);
        Talk talk2 = new Talk("talk2", "scala", 60);
        Talk talk3 = new Talk("talk3", "python", 45);
        Talk talk4 = new Talk("talk4", "ruby", 45);
        Talk talk5 = new Talk("talk5", "lightning", 30);
        talks.addAll(Arrays.asList(talk1, talk2, talk3, talk4, talk5));

        List<List<Talk>> possibleCombSession = conferenceManager.findPossibleSessions(talks, 1, true);

        assertEquals(possibleCombSession.size(), 1);
        assertEquals(possibleCombSession.get(0).size(), 4);
        assertThat(possibleCombSession.get(0), hasItems(talk1, talk2, talk3, talk5));

    }

    @Test
    public void shouldGetEveningSessionTalksListForOneDay(){
        List<Talk> talks = new ArrayList<Talk>();
        Talk talk1 = new Talk("talk1", "java", 45);
        Talk talk2 = new Talk("talk2", "scala", 60);
        Talk talk3 = new Talk("talk3", "python", 45);
        Talk talk4 = new Talk("talk4", "ruby", 45);
        Talk talk5 = new Talk("talk5", "c#", 60);
        Talk talk6 = new Talk("talk6", "c++", 40);
        talks.addAll(Arrays.asList(talk1, talk2, talk3, talk4, talk5, talk6));

        List<List<Talk>> possibleCombSession = conferenceManager.findPossibleSessions(talks, 1, false);

        assertEquals(possibleCombSession.size(), 1);
        assertEquals(possibleCombSession.get(0).size(), 5);
        assertThat(possibleCombSession.get(0), hasItems(talk1, talk2, talk3, talk4, talk6));
    }

    @Test
    public void shouldGetEveningSessionTalksListForTwoDays(){
        List<Talk> talks = new ArrayList<Talk>();
        Talk talk1 = new Talk("talk1", "java", 45);
        Talk talk2 = new Talk("talk2", "scala", 60);
        Talk talk3 = new Talk("talk3", "python", 45);
        Talk talk4 = new Talk("talk4", "ruby", 45);
        Talk talk5 = new Talk("talk5", "c#", 60);
        Talk talk6 = new Talk("talk6", "c++", 30);
        Talk talk7 = new Talk("talk7", "c", 40);
        Talk talk8 = new Talk("talk8", "php", 60);
        Talk talk9 = new Talk("talk9", "asp.net", 45);
        Talk talk10 = new Talk("talk10", "javascript", 60);
        talks.addAll(Arrays.asList(talk1, talk2, talk3, talk4, talk5, talk6, talk7, talk8, talk9, talk10));

        List<List<Talk>> possibleCombSession = conferenceManager.findPossibleSessions(talks, 2, false);

        assertEquals(possibleCombSession.size(), 2);
        assertEquals(possibleCombSession.get(0).size(), 5);
        assertThat(possibleCombSession.get(0), hasItems(talk1, talk2, talk3, talk4, talk6));
        assertEquals(possibleCombSession.get(1).size(), 4);
        assertThat(possibleCombSession.get(1), hasItems(talk5, talk7, talk8, talk9));
    }


    @Test(expected = Exception.class)
    public void shouldThrowExceptionIfRevisedTalksListIsNotEmpty() throws Exception {
        List<Talk> talks = new ArrayList<Talk>();
        Talk talk1 = new Talk("talk1", "java", 45);
        Talk talk2 = new Talk("talk2", "scala", 60);
        Talk talk3 = new Talk("talk3", "python", 45);
        Talk talk4 = new Talk("talk4", "ruby", 45);
        Talk talk5 = new Talk("talk5", "c#", 60);
        Talk talk6 = new Talk("talk6", "c++", 30);
        Talk talk7 = new Talk("talk7", "c", 40);
        Talk talk8 = new Talk("talk8", "php", 60);
        Talk talk9 = new Talk("talk9", "asp.net", 45);
        Talk talk10 = new Talk("talk10", "javascript", 60);
        talks.addAll(Arrays.asList(talk1, talk2, talk3, talk4, talk5, talk6, talk7, talk8, talk9, talk10));

        conferenceManager.getScheduleConferenceTrack(talks);
    }

    @Test
    public void shouldGetScheduleConferenceTrackProvidedAndRevisedData(){
        List<Talk> mTalks = new ArrayList<Talk>();
        List<List<Talk>> mTalkList = new ArrayList<List<Talk>>();

        Talk mTalk1 = new Talk("mtalk1", "java", 45);
        Talk mTalk2 = new Talk("mtalk2", "scala", 60);
        Talk mTalk3 = new Talk("mtalk3", "python", 45);
        Talk mTalk4 = new Talk("mtalk4", "ruby", 30);

        mTalks.addAll(Arrays.asList(mTalk1, mTalk2, mTalk3, mTalk4));

        mTalkList.add(mTalks);

        List<Talk> eTalks = new ArrayList<Talk>();
        List<List<Talk>> eTalkList = new ArrayList<List<Talk>>();

        Talk eTalk1 = new Talk("etalk1", "c++", 30);
        Talk eTalk2 = new Talk("etalk2", "c", 60);
        Talk eTalk3 = new Talk("etalk3", "php", 60);
        Talk eTalk4 = new Talk("etalk4", "asp.net", 45);
        Talk eTalk5 = new Talk("etalk5", "javascript", 30);

        eTalks.addAll(Arrays.asList(eTalk1, eTalk2, eTalk3, eTalk4, eTalk5));

        eTalkList.add(eTalks);

        List<List<Talk>> scheduledTalksList = conferenceManager.getScheduledTalksList(mTalkList, eTalkList);

        assertEquals(scheduledTalksList.size(), 1);
        List<Talk> talks = scheduledTalksList.get(0);
        assertEquals(talks.size(), 11);
        assertThat(talks.get(0), equalTo(mTalk1));
        assertThat(talks.get(4).getName(), equalTo("Lunch"));
        assertThat(talks.get(5), equalTo(eTalk1));
        assertThat(talks.get(10).getName(), equalTo("Networking Event"));

    }



}