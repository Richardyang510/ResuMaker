import java.util.ArrayList;

public class ResumeField {

    private String title;
    private String position;
    private String location;
    private String startDate;
    private String toDate;
    private ArrayList<String> points;

    ResumeField() {
        points = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public ArrayList<String> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<String> points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "[" + title + "] " + position + " at " + location + " from " + startDate + "-" + toDate;
    }
}