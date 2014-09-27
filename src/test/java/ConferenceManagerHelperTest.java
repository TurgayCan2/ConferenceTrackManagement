import model.Talk;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class ConferenceManagerHelperTest {

    @Test
    public void shouldReturnZeroIfTalkListIsEmptyOrNull(){
        assertEquals(ConferenceManagerHelper.getTotalTalksTime(null), 0);
        assertEquals(ConferenceManagerHelper.getTotalTalksTime(new ArrayList<Talk>()), 0);
    }

    @Test
    public void shouldReturnTotalTimeOfTalks(){
        List<Talk> talks = new ArrayList<Talk>();
        Talk talk1 = new Talk("talk1", "java", 45);
        Talk talk2 = new Talk("talk2", "scala", 35);
        Talk talk3 = new Talk("talk3", "python", 35);
        talks.addAll(Arrays.asList(talk1, talk2, talk3));

        assertEquals(ConferenceManagerHelper.getTotalTalksTime(talks), 115);
    }

    @Test
    public void shouldPrepareScheduledTalksListNotProvidedData(){
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
        talks.addAll(Arrays.asList(talk1, talk2, talk3, talk4, talk5, talk6, talk7, talk8, talk9));

        List<Talk> eTalks = new ArrayList<Talk>();
        List<List<Talk>> eTalkList = new ArrayList<List<Talk>>();
        eTalks.addAll(Arrays.asList(talk1, talk2, talk3, talk8));
        eTalkList.add(eTalks);

        List<Talk> revisedTalkList = ConferenceManagerHelper.prepareScheduledTalkList(talks, eTalkList);

        assertEquals(revisedTalkList.size(), 8);

        assertThat(revisedTalkList, not(hasItems(talk6)));

    }

}