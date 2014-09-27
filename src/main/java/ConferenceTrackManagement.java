import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ConferenceTrackManagement {

    private static final Logger LOGGER = Logger.getLogger(ConferenceTrackManagement.class.getName());
    //This source path variable rewrite for your computer absolute file path project root directory
    private static final String SRC_PATH = "/Users/turgaycan/Documents/MyProjects/";
    private static final String FILE_DIR = SRC_PATH + "ConferenceTrackManagement/src/main/resources/";

    /**
     * Main method to execute program.
     *
     * @param args
     */
    public static void main(String[] args) throws Exception{
        File[] files = new File(FILE_DIR).listFiles();
        for (File file : files) {
            executeTrackManagement(file.getAbsolutePath());
        }
    }

    protected static void executeTrackManagement(String fileName) throws Exception {
        ConferenceManager conferenceManager = new ConferenceManager(fileName, false);
        try {

            System.out.println("\n\n\n***********************\n " +
                    "SORTED IN FILE \n***********************\n\n");

            //Read from file sort
            conferenceManager.scheduleConference();

            System.out.println("\n\n\n***********************\n" +
                    " to show SORTED TALK DURATION talks list - >" +
                    " true vale is given to ConferenceManager constructor in ConferenceTrackManagement Main Class " +
                    "\n ***********************\n\n");

        } catch (TalkException ite) {
            LOGGER.log(Level.SEVERE, ite.getMessage());
        }
    }

}
