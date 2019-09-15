import java.util.ArrayList;

public class rHTMLGenerator {

    public String resumeFieldToHTMLString (ResumeField resumeField) {
        String template = "<h2>%s <span>%s - %s - %s-%s</span></h2><ul>%s</ul>";

        String title = resumeField.getTitle();
        String position = resumeField.getPosition();
        String location = resumeField.getLocation();
        String startDate = resumeField.getStartDate();
        String toDate = resumeField.getToDate();
        ArrayList<String> points = resumeField.getPoints();

        String pointsString = "";
        for (int i = 0; i < points.size(); i++) {
            pointsString += "<li>" + points.get(i) + "</li>";
        }

        return String.format(template, title, position, location, startDate, toDate, pointsString);
    }
}
