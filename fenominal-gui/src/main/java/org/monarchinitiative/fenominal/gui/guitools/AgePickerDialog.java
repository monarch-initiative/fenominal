package org.monarchinitiative.fenominal.gui.guitools;

import java.util.ArrayList;
import java.util.List;


/**
 * TODO Spinner
 * Spinner
 * https://www.tutorialspoint.com/how-to-create-a-spinner-in-javafx
 */
public class AgePickerDialog {

    private final String message;

    private final Browser browser;

    private final List<String> ages;

    private final String buttonStyle =
            " -fx-background-color:" +
                    "        linear-gradient(#f2f2f2, #d6d6d6)," +
                    "        linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%)," +
                    "        linear-gradient(#dddddd 0%, #f6f6f6 50%);" +
                    "    -fx-background-radius: 8,7,6;" +
                    "    -fx-background-insets: 0,1,2;" +
                    "    -fx-text-fill: black;\n" +
                    "    -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );";

    public AgePickerDialog(String msg) {
        message = msg;
        ages = new ArrayList<>();
        browser = new Browser(message);
    }

    public AgePickerDialog( List<String> previousAges) {
        message = getHtmlWithAges(previousAges);
        browser = new Browser(message);
        ages = new ArrayList<>(previousAges);
    }



    private final static String setupHtml ="<html><body><h3>Fenomimal Phenopacket generator</h3>" +
            "<p><i>Fenominal</i> allows users to indicate the age of patients by having users indicate the birthdate as" +
            " well as the dates of the medical encounters that are being recorded.<p>" +
            "<p>Fenominal subtracts the birthdate from the encounter dates to get the age of the patient during each encounter." +
            " It does not store or output the birthdate.</p>" +
            "</body></html>";

    public String getHtmlWithAges(List<String> previousAges) {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body><h3>Fenomimal Phenopacket generator</h3>");
        builder.append("<p>Encounter ages:</p>");
        if (previousAges.isEmpty()) {
            builder.append("<p>You will see encounter ages as the encounters are entered.</p>");
        } else {
            builder.append("<ol>");
            for (String isoAge : previousAges) {
                builder.append("<li>").append(isoAge).append("</li>");
            }
            builder.append("</ol>");
        }
        builder.append("</body></html>");
        return builder.toString();
    }

}
