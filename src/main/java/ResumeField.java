import java.util.ArrayList;

public class ResumeField {

    String title;
    String position;
    String location;
    String startDate;
    String toDate;
    ArrayList<String> points;

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

    public void addPoint(String point) {
        points.add(point);
    }

    @Override
    public String toString() {
        return "[" + title + "] " + position + " at " + location + " from " + startDate + "-" + toDate;
    }
}