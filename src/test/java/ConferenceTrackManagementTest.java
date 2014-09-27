import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;


public class ConferenceTrackManagementTest {


    @Test
    public void shouldThrowExceptionIfTextFileNotFoundInPath() {
        try {
            ConferenceTrackManagement.executeTrackManagement("abc.txt");
        }catch(Exception e){
            assertThat(e.getMessage(), equalTo("abc.txt (No such file or directory)"));
        }
    }

}