import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Axis {
    private Point basedPoint;
    private double axisHeight;
    private LinkedList<Integer> linkedPointIndex = new LinkedList<>();
    private HashMap<Integer, LinkedList<Point>> ellipsePoint = new HashMap<>();

    Axis(Point p) {
        basedPoint = p;
    }

    public Point getLiftPoint(){
        return new Point(basedPoint.getX(),basedPoint.getY(),axisHeight);
    }

    public Point getBasedPoint() {
        return basedPoint;
    }

    public void setBasedPoint(Point basedPoint) {
        this.basedPoint = basedPoint;
    }

    public double getAxisHeight() {
        return axisHeight;
    }

    public void setAxisHeight(double axisHeight) {
        this.axisHeight = axisHeight;
    }

    public LinkedList<Integer> getLinkedPointIndex() {
        return linkedPointIndex;
    }

    public void setLinkedPointIndex(LinkedList<Integer> linkedPointIndex) {
        this.linkedPointIndex = linkedPointIndex;
    }

    public void addLinkedPointIndex(int i) {
        linkedPointIndex.add(i);
    }

    public HashMap<Integer, LinkedList<Point>> getEllipsePoint() {
        return ellipsePoint;
    }

    public void setEllipsePoint(HashMap<Integer, LinkedList<Point>> ellipsePoint) {
        this.ellipsePoint = ellipsePoint;
    }

    public void addMapItem(int index, LinkedList<Point> list) {
        ellipsePoint.put(index, list);
    }

}
