import javafx.collections.ObservableList;
import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class HTMLGenerator {

    public String generateHTMLString(ObservableList<Section> sectionObservableList) {
        String template = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/><title>Resume</title><style type=\"text/css\">* { margin: 0; padding: 0; }body { font: 16px Helvetica, Sans-Serif; line-height: 24px; background: url(\"http://www.richardyang.net/wp-content/uploads/2019/09/noise.jpg\"); }.clear { clear: both; }#page-wrap { width: 800px; max-width: 90%%; margin: 80px auto 60px; }#pic { float: right; margin: -30px 0 0 0; }h1 { margin: 0 0 16px 0; padding: 0 0 16px 0; font-size: 42px; font-weight: bold; letter-spacing: -2px; border-bottom: 1px solid #999; }h2 { font-size: 20px; margin: 0 0 6px 0; position: relative; }h2 span { position: absolute; bottom: 0; right: 0; font-style: italic; font-family: Georgia, Serif; font-size: 16px; color: #999; font-weight: normal; }p { margin: 0 0 16px 0; }a { color: #999; text-decoration: none; border-bottom: 1px dotted #999; }a:hover { border-bottom-style: solid; color: black; }ul { margin: 0 0 32px 17px; }#objective { width: 500px; float: left; }#objective p { font-family: Georgia, Serif; font-style: italic; color: #666; }dt { font-style: italic; font-weight: bold; font-size: 18px; text-align: right; padding: 0 26px 0 0; width: 150px; float: left; height: 100px; border-right: 1px solid #999;  }dd { width: 600px; float: right; }dd.clear { float: none; margin: 0; height: 15px; }</style></head><body><div id=\"page-wrap\">%s %s</div></body></html>";

        String nameTemplate =
                "<img src=\"http://www.richardyang.net/wp-content/uploads/2019/09/mrgoose.png\" alt=\"Photo of Mr Goose\" id=\"pic\" />" +
                "<div id=\"contact-info\" class=\"vcard\">\n" +
                "            <h1 class=\"fn\">Mr Goose</h1>\n" +
                "            <p>\n" +
                "                Cell: <span class=\"tel\">555-666-7777</span><br />\n" +
                "                Email: <a class=\"email\" href=\"mailto:mrgoose@goosemail.ca\">mrgoose@goosemail.ca</a>\n" +
                "            </p>\n" +
                "        </div>";

        StringBuilder sections = new StringBuilder();

        for (Section section : sectionObservableList) {
            if (section.isEnabled()) {
                sections.append(sectionToHTMLString(section));
            }
        }

        String htmlString = String.format(template, nameTemplate, sections.toString());
        System.out.println(htmlString);
        // save HTML string to file

        return htmlString;
    }

    private String sectionToHTMLString(Section section) {
        if (section.isUsesResumeField()) {
            ObservableList<ResumeField> resumeFields = section.getData();

            String template = "<dl>%s<dt>%s</dt><dd>%s</dd><dd class=\"clear\"></dd></dl>";

            String name = section.getName();
            StringBuilder fields = new StringBuilder();

            String newLine = (section.getType().equals("education")) ? "<dd class=\"clear\"></dd>" : "";

            for (ResumeField resumeField : resumeFields) {
                if (resumeField.isEnabled()) {
                    if (section.getType().equals("skill")) {
                        fields.append(fewResumeFieldToHTMLString(resumeField));
                    } else {
                        fields.append(resumeFieldToHTMLString(resumeField));
                    }
                }
            }

            String htmlString =  String.format(template, newLine, name, fields.toString());
            System.out.println(htmlString);
            return htmlString;
        } else {
            String template = "<div><p>%s</p></div>";

            if(section.getType().equals("blurb")) {
                template =  "<div id=\"objective\"><p>%s</p></div><div class=\"clear\"></div>";
            }

            String notResumeFieldData = section.getNotResumeFieldData();

            String htmlString =  String.format(template, notResumeFieldData);
            System.out.println(htmlString);
            return htmlString;
        }
    }

    private String resumeFieldToHTMLString (ResumeField resumeField) {
        String template = "<h2>%s <span>%s - %s - %s-%s</span></h2><ul>%s</ul>";

        String title = resumeField.getTitle();
        String position = resumeField.getPosition();
        String location = resumeField.getLocation();
        String startDate = resumeField.getStartDate();
        String toDate = resumeField.getToDate();
        ArrayList<String> points = resumeField.getPoints();
        StringBuilder pointsString = new StringBuilder();

        for (String point : points) {
            if (!point.equals("")) {
                pointsString.append("<li>").append(point).append("</li>");
            }
        }

        String htmlString = String.format(template, title, position, location, startDate, toDate, pointsString);
        System.out.println(htmlString);
        return htmlString;
    }

    private String fewResumeFieldToHTMLString(ResumeField resumeField) {
        String template = "<h2>%s</h2><ul>%s</ul>";

        String title = resumeField.getTitle();
        ArrayList<String> points = resumeField.getPoints();
        StringBuilder pointsString = new StringBuilder();

        for (String point : points) {
            if (!point.equals("")) {
                pointsString.append("<p>").append(point).append("</p>");
            }
        }

        String htmlString = String.format(template, title, pointsString);
        System.out.println(htmlString);
        return htmlString;
    }

    public String prettyPrintHTML(String rawHTML) {
        Tidy tidy = new Tidy();
        tidy.setXHTML(true);
        tidy.setIndentContent(true);
        tidy.setPrintBodyOnly(true);
        tidy.setTidyMark(false);

        // HTML to DOM
        Document htmlDOM = tidy.parseDOM(new ByteArrayInputStream(rawHTML.getBytes()), null);

        // Pretty Print
        OutputStream out = new ByteArrayOutputStream();
        tidy.pprint(htmlDOM, out);

        return out.toString();
    }
}
