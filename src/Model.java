import java.util.LinkedList;
import java.util.Arrays;

public class Model {
    private Polygon basedPolygon;
    private LinkedList<Triangle> modelTriangle = new LinkedList<>();
    private LinkedList<Triangle> modelTriangleLink = new LinkedList<>();
    private LinkedList<Axis> modelAxis = new LinkedList<>();
    private LinkedList<Axis> edgePointMap = new LinkedList<>();
    private LinkedList<Point> vertexPoint = new LinkedList<>();
    private float[] vertexCoords = {};
    private float[] triCoords = {};
    private int[] elementArray = {};

    public float[] getTriCoords() {
        return triCoords;
    }

    public float[] getVertexCoords() {
        return vertexCoords;
    }

    public int[] getElementArray() {
        return elementArray;
    }

    private LinkedList<Line> modelLine = new LinkedList<>();

    public LinkedList<Line> getModelLine() {
        return modelLine;
    }

    //Getter and Setter
    public Polygon getBasedPolygon() {
        return basedPolygon;
    }

    public void setBasedPolygon(Polygon basedPolygon) {
        this.basedPolygon = basedPolygon;
    }

    public LinkedList<Triangle> getModelTriangle() {
        return modelTriangle;
    }

    public LinkedList<Triangle> getModelTriangleLink() {
        return modelTriangleLink;
    }

    public void setModelTriangle(LinkedList<Triangle> modelTriangle) {
        this.modelTriangle = modelTriangle;
    }

    public LinkedList<Axis> getModelAxis() {
        return modelAxis;
    }

    public void setModelAxis(LinkedList<Axis> modelAxis) {
        this.modelAxis = modelAxis;
    }

    private double biggestZ = 0.0f;

    public double getBiggestZ() {
        return biggestZ;
    }

    private void countBasedPolygon() {
        basedPolygon.buildPolygonLine();
        System.out.println("Build Polygon Line Success");
        basedPolygon.buildCAT();
        System.out.println("Build Polygon CATLine Success");
        basedPolygon.buildAxis();
        System.out.println("Build Polygon Axis Success");
    }

    private void calHeight(Point p, int index) {
        Axis thisAxis = new Axis(p);
        int pointCount = 0;
        double height = 0;
        LinkedList<Line> fanLine = basedPolygon.getFanLine();
        for (Line fanL : fanLine) {
            boolean startEqual = fanL.getStart().equal(p);
            boolean endEqual = fanL.getEnd().equal(p);
            if (startEqual || endEqual) {
                pointCount++;
                height += fanL.length();
                Point judgeP = fanL.getStart();
                if (startEqual) {
                    judgeP = fanL.getEnd();
                }
                int pointIndex = findOnEdge(judgeP);
                if (pointIndex != -1) {
                    thisAxis.addLinkedPointIndex(pointIndex);
                    edgePointMap.get(pointIndex).addLinkedPointIndex(index);
                }
            }
        }
//        System.out.println("Count = "+pointCount);
        if (pointCount == 0) {
            pointCount = 1;
        }
        height = height / pointCount * 1.0;
//        System.out.println("Height = " + height);
//        for (int i : thisAxis.getLinkedPointIndex()) {
//            System.out.print(i + " ");
//        }
//        System.out.println();
        thisAxis.setAxisHeight(height);
        if (height >= biggestZ) {
            biggestZ = height;
        }
        modelAxis.add(index, thisAxis);
    }

    private int findOnEdge(Point p) {
        LinkedList<Point> edgePoint = basedPolygon.getPolygonPoint();
        for (int i = 0; i < edgePoint.size(); i++) {
            if (p.equal(edgePoint.get(i))) {
                return i;
            }
        }
        return -1;
    }

    //抬升
    private void lifting() {
        LinkedList<Line> fanLine = basedPolygon.getFanLine();
        for (int m = 0; m < fanLine.size(); ) {
            if (findOnEdge(fanLine.get(m).getEnd()) != -1 && findOnEdge(fanLine.get(m).getStart()) != -1) {
                if (basedPolygon.onSegment(fanLine.get(m).getMiddlePoint()) || basedPolygon.onSegmentLine(fanLine.get(m))) {
                    fanLine.remove(m);
                } else {
                    fanLine.add(new Line(fanLine.get(m).getMiddlePoint(), fanLine.get(m).getStart()));
                    fanLine.add(new Line(fanLine.get(m).getMiddlePoint(), fanLine.get(m).getEnd()));
                    fanLine.remove(m);
                }
            } else {
                m++;
            }

        }
        basedPolygon.setFanLine(fanLine);
        for (int i = 0; i < basedPolygon.getPolygonPoint().size(); i++) {
            Axis axis = new Axis(basedPolygon.getPolygonPoint().get(i));
            edgePointMap.add(i, axis);
        }
        for (int i = 0; i < basedPolygon.getEndPoint().size(); i++) {
            calHeight(basedPolygon.getEndPoint().get(i), i);
        }
        System.out.println("Lifting Success");
    }

    public void clearAll() {
        basedPolygon.clearAll();
        modelTriangle = new LinkedList<>();
        modelAxis = new LinkedList<>();
        edgePointMap = new LinkedList<>();
        modelLine = new LinkedList<>();
        modelTriangleLink = new LinkedList<>();
        triCoords = new float[0];
    }

    //构造椭圆的边
    private LinkedList<Point> buildEllipse(double height, Point edgePoint, Point middlePoint) {
        LinkedList<Point> ellipsePoint = new LinkedList<>();
        Point moveWay = new Point(middlePoint.getX() - edgePoint.getX(), middlePoint.getY() - edgePoint.getY(), 0);
        double r = edgePoint.distance(middlePoint);
        double liftAngle = Math.PI * 0.5 * 0.25;
        for (int i = 3; i >= 1; i--) {
            ellipsePoint.add(countEllipsePoint(height, liftAngle * i, middlePoint, edgePoint));
        }
        return ellipsePoint;
    }

    //给定一个角度来计算点的位置
    private Point countEllipsePoint(double b, double liftAngle, Point middlePoint, Point edgePoint) {
        double x, y, z, percent;
        z = b * Math.abs(Math.sin(liftAngle));
        percent = 1 - Math.abs(Math.cos(liftAngle));
        x = percent * middlePoint.getX() + (1 - percent) * edgePoint.getX();
        y = percent * middlePoint.getY() + (1 - percent) * edgePoint.getY();
        return new Point(x, y, z);
    }

    private void addInsideTriangle(LinkedList<Point> beginList, LinkedList<Point> endList) {
        for (int i = 0; i < 2; i++) {
            boolean isAdd;
            isAdd = isAddTriangle(0, new Triangle(beginList.get(i), endList.get(i), endList.get(i + 1)));
            if (isAdd) {
                modelTriangle.add(new Triangle(beginList.get(i).mirrorPoint(), endList.get(i).mirrorPoint(), endList.get(i + 1).mirrorPoint()));
//                buildVAO(beginList.get(i), endList.get(i), endList.get(i + 1));
//                addTri(beginList.get(i), endList.get(i), endList.get(i + 1));
//                addTri(beginList.get(i).mirrorPoint(), endList.get(i).mirrorPoint(), endList.get(i + 1).mirrorPoint());
            }
            isAdd = isAddTriangle(0, new Triangle(beginList.get(i + 1), endList.get(i + 1), beginList.get(i)));
            if (isAdd) {
                modelTriangle.add(new Triangle(beginList.get(i + 1).mirrorPoint(), endList.get(i + 1).mirrorPoint(), beginList.get(i).mirrorPoint()));
//                buildVAO(beginList.get(i + 1), endList.get(i + 1), beginList.get(i));
//                addTri(beginList.get(i + 1), endList.get(i + 1), beginList.get(i));
//                addTri(beginList.get(i + 1).mirrorPoint(), endList.get(i + 1).mirrorPoint(), beginList.get(i).mirrorPoint());
            }
        }
    }

    private void addTriangleInSinglePoint(int edgePointIndex, int axisIndexBegin, int axisIndexEnd) {
        LinkedList<Point> beginList = edgePointMap.get(edgePointIndex).getEllipsePoint().get(axisIndexBegin);
        LinkedList<Point> endList = edgePointMap.get(edgePointIndex).getEllipsePoint().get(axisIndexEnd);
        boolean isAdd;
        isAdd = isAddTriangle(0, new Triangle(edgePointMap.get(edgePointIndex).getBasedPoint(), beginList.getLast(), endList.getLast()));
        if (isAdd) {
            modelTriangle.add(new Triangle(edgePointMap.get(edgePointIndex).getBasedPoint().mirrorPoint(), beginList.getLast().mirrorPoint(), endList.getLast().mirrorPoint()));
//            buildVAO(edgePointMap.get(edgePointIndex).getBasedPoint(), beginList.getLast(), endList.getLast());
//            addTri(edgePointMap.get(edgePointIndex).getBasedPoint(), beginList.getLast(), endList.getLast());
//            addTri(edgePointMap.get(edgePointIndex).getBasedPoint().mirrorPoint(), beginList.getLast().mirrorPoint(), endList.getLast().mirrorPoint());
        }
        addInsideTriangle(beginList, endList);

        isAdd = isAddTriangle(0, new Triangle(beginList.get(0), endList.get(0), modelAxis.get(axisIndexEnd).getLiftPoint()));
        if (isAdd) {
            modelTriangle.add(0, new Triangle(beginList.get(0).mirrorPoint(), endList.get(0).mirrorPoint(), modelAxis.get(axisIndexEnd).getLiftPoint().mirrorPoint()));
//            buildVAO(beginList.get(0), endList.get(0), modelAxis.get(axisIndexEnd).getLiftPoint());
//            addTri(beginList.get(0), endList.get(0), modelAxis.get(axisIndexEnd).getLiftPoint());
//            addTri(beginList.get(0).mirrorPoint(), endList.get(0).mirrorPoint(), modelAxis.get(axisIndexEnd).getLiftPoint().mirrorPoint());
        }
        isAdd = isAddTriangle(0, new Triangle(beginList.get(0), modelAxis.get(axisIndexEnd).getLiftPoint(), modelAxis.get(axisIndexBegin).getLiftPoint()));
        if (isAdd) {
            modelTriangle.add(new Triangle(beginList.get(0).mirrorPoint(), modelAxis.get(axisIndexEnd).getLiftPoint().mirrorPoint(), modelAxis.get(axisIndexBegin).getLiftPoint().mirrorPoint()));
//            buildVAO(beginList.get(0), modelAxis.get(axisIndexEnd).getLiftPoint(), modelAxis.get(axisIndexBegin).getLiftPoint());
//            addTri(beginList.get(0), modelAxis.get(axisIndexEnd).getLiftPoint(), modelAxis.get(axisIndexBegin).getLiftPoint());
//            addTri(beginList.get(0).mirrorPoint(), modelAxis.get(axisIndexEnd).getLiftPoint().mirrorPoint(), modelAxis.get(axisIndexBegin).getLiftPoint().mirrorPoint());
        }
    }

    private void addTriangleInTwoPoints(int edgeIndexBegin, int edgeIndexEnd, int axisPointIndex) {
        LinkedList<Point> beginList = edgePointMap.get(edgeIndexBegin).getEllipsePoint().get(axisPointIndex);
        LinkedList<Point> endList = edgePointMap.get(edgeIndexEnd).getEllipsePoint().get(axisPointIndex);
        boolean isAdd;
        isAdd = isAddTriangle(1, new Triangle(modelAxis.get(axisPointIndex).getLiftPoint(), beginList.getFirst(), endList.getFirst()));
        if (isAdd) {
            modelTriangleLink.add(new Triangle(modelAxis.get(axisPointIndex).getLiftPoint().mirrorPoint(), beginList.getFirst().mirrorPoint(), endList.getFirst().mirrorPoint()));
//            buildVAO(modelAxis.get(axisPointIndex).getLiftPoint(), beginList.getFirst(), endList.getFirst());
//            addTri(modelAxis.get(axisPointIndex).getLiftPoint(), beginList.getFirst(), endList.getFirst());
//            addTri(modelAxis.get(axisPointIndex).getLiftPoint().mirrorPoint(), beginList.getFirst().mirrorPoint(), endList.getFirst().mirrorPoint());
        }
        addInsideTriangle(endList, beginList);

        isAdd = isAddTriangle(1, new Triangle(beginList.getLast(), endList.getLast(), edgePointMap.get(edgeIndexEnd).getBasedPoint()));
        if (isAdd) {
            modelTriangleLink.add(new Triangle(beginList.getLast().mirrorPoint(), endList.getLast().mirrorPoint(), edgePointMap.get(edgeIndexEnd).getBasedPoint().mirrorPoint()));
//            buildVAO(beginList.getLast(), endList.getLast(), edgePointMap.get(edgeIndexEnd).getBasedPoint());
//            addTri(beginList.getLast(), endList.getLast(), edgePointMap.get(edgeIndexEnd).getBasedPoint());
//            addTri(beginList.getLast().mirrorPoint(), endList.getLast().mirrorPoint(), edgePointMap.get(edgeIndexEnd).getBasedPoint().mirrorPoint());
        }
        isAdd = isAddTriangle(1, new Triangle(beginList.getLast(), edgePointMap.get(edgeIndexEnd).getBasedPoint(), edgePointMap.get(edgeIndexBegin).getBasedPoint()));
        if (isAdd) {
            modelTriangleLink.add(new Triangle(beginList.getLast().mirrorPoint(), edgePointMap.get(edgeIndexEnd).getBasedPoint().mirrorPoint(), edgePointMap.get(edgeIndexBegin).getBasedPoint().mirrorPoint()));
//            buildVAO(beginList.getLast(), edgePointMap.get(edgeIndexEnd).getBasedPoint(), edgePointMap.get(edgeIndexBegin).getBasedPoint());
//            addTri(beginList.getLast(), edgePointMap.get(edgeIndexEnd).getBasedPoint(), edgePointMap.get(edgeIndexBegin).getBasedPoint());
//            addTri(beginList.getLast().mirrorPoint(), edgePointMap.get(edgeIndexEnd).getBasedPoint().mirrorPoint(), edgePointMap.get(edgeIndexBegin).getBasedPoint().mirrorPoint());
        }
    }

    private boolean isOnAxis(Point p, Point q) {
        boolean isOn = false;
        Line judgeLine = new Line(p, q);
        LinkedList<Line> axis = basedPolygon.getPolygonAxis();
        for (Line line : axis) {
            if (line.equal(judgeLine)) {
                isOn = true;
                break;
            }
        }
        return isOn;
    }

    private int findSameAxisPoint(int edgeIndexBegin, int edgeIndexEnd) {
        for (int i : edgePointMap.get(edgeIndexBegin).getLinkedPointIndex()) {
            for (int j : edgePointMap.get(edgeIndexEnd).getLinkedPointIndex()) {
                if (i == j) {
                    return i;
                }
            }
        }
        return -1;
    }

    private boolean isAddTriangle(int type, Triangle T) {
        boolean isInside = false;
        if (type == 0) {
            for (Triangle temp : modelTriangle) {
                if (temp.equal(T)) {
                    isInside = true;
                    break;
                }
            }
            if (!isInside) {
                modelTriangle.add(T);
                return true;
            }
        } else {
            for (Triangle temp : modelTriangleLink) {
                if (temp.equal(T)) {
                    isInside = true;
                    break;
                }
            }
            if (!isInside) {
                modelTriangleLink.add(T);
                return true;
            }
        }
        return false;
    }

    private void addTri(Point p0, Point p1, Point p2) {
        int index = triCoords.length;
        float newCoords[] = new float[index + 9];
        System.arraycopy(triCoords, 0, newCoords, 0, index);
        newCoords[index] = (float) p0.getX();
        newCoords[index + 1] = (float) p0.getY();
        newCoords[index + 2] = (float) p0.getZ();
        newCoords[index + 3] = (float) p1.getX();
        newCoords[index + 4] = (float) p1.getY();
        newCoords[index + 5] = (float) p1.getZ();
        newCoords[index + 6] = (float) p2.getX();
        newCoords[index + 7] = (float) p2.getY();
        newCoords[index + 8] = (float) p2.getZ();
        triCoords = newCoords;
    }


    private int findIndex(Point p) {
        int index = -1;
        for (int i = 0; i < vertexPoint.size(); i++) {
            if (p.equal(vertexPoint.get(i))) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void addPoint(Point p) {
        boolean isInside = false;
        if (vertexPoint.size() != 0) {
            for (Point i : vertexPoint) {
                if (i.equal(p)) {
                    isInside = true;
                    break;
                }
            }
        }
        if (!isInside) {
            vertexPoint.add(p);
            int index = vertexCoords.length;
            float newVertexCoords[] = new float[index + 3];
            System.arraycopy(vertexCoords, 0, newVertexCoords, 0, index);
            newVertexCoords[index] = (float) p.getX();
            newVertexCoords[index + 1] = (float) p.getY();
            newVertexCoords[index + 2] = (float) p.getZ();
            vertexCoords = newVertexCoords;
            if (Math.abs(p.getZ()) >= 0.00000001) {
                vertexPoint.add(p.mirrorPoint());
                index = vertexCoords.length;
                newVertexCoords = new float[index + 3];
                System.arraycopy(vertexCoords, 0, newVertexCoords, 0, index);
                newVertexCoords[index] = (float) p.mirrorPoint().getX();
                newVertexCoords[index + 1] = (float) p.mirrorPoint().getY();
                newVertexCoords[index + 2] = (float) p.mirrorPoint().getZ();
                vertexCoords = newVertexCoords;
            }
        }

    }


    private void buildVAO(Point p0, Point p1, Point p2) {
        addPoint(p0);
        addPoint(p1);
        addPoint(p2);
        int index = elementArray.length;
        int newIndex[] = new int[index + 6];
        System.arraycopy(elementArray, 0, newIndex, 0, index);
        newIndex[index] = findIndex(p0);
        newIndex[index + 1] = findIndex(p1);
        newIndex[index + 2] = findIndex(p2);
        newIndex[index + 3] = findIndex(p0.mirrorPoint());
        newIndex[index + 4] = findIndex(p1.mirrorPoint());
        newIndex[index + 5] = findIndex(p2.mirrorPoint());
        elementArray = newIndex;
    }

    public void buildSketchModel() {
        countBasedPolygon();
        System.out.println("Count OK");
        lifting();
        System.out.println("Lifting OK");
        for (int i = 0; i < basedPolygon.getPolygonPoint().size(); i++) {
            for (int j : edgePointMap.get(i).getLinkedPointIndex()) {
                LinkedList<Point> tempList = buildEllipse(modelAxis.get(j).getAxisHeight(), basedPolygon.getPolygonPoint().get(i), modelAxis.get(j).getBasedPoint());
                edgePointMap.get(i).addMapItem(j, tempList);
                for (int k = 0; k < tempList.size() - 1; k++) {
                    modelLine.add(new Line(tempList.get(k), tempList.get(k + 1)));
                }
                modelLine.add(new Line(tempList.getLast(), basedPolygon.getPolygonPoint().get(i)));
                modelLine.add(new Line(tempList.get(0), new Point(modelAxis.get(j).getBasedPoint().getX(), modelAxis.get(j).getBasedPoint().getY(), modelAxis.get(j).getAxisHeight())));
            }
        }
        System.out.println("Building Step 1");
        // 这里有问题
        for (int i = 0; i < basedPolygon.getPolygonPoint().size(); i++) {
            int size = edgePointMap.get(i).getLinkedPointIndex().size();
            int nextIndex = i + 1;
            if (i == basedPolygon.getPolygonPoint().size() - 1) {
                nextIndex = 0;
            }
            if (size == 2) {
                addTriangleInSinglePoint(i, edgePointMap.get(i).getLinkedPointIndex().getFirst(), edgePointMap.get(i).getLinkedPointIndex().getLast());
            }
            if (size > 2) {
                for (int m = 0; m < edgePointMap.get(i).getLinkedPointIndex().size(); m++) {
                    for (int n = m + 1; n < edgePointMap.get(i).getLinkedPointIndex().size(); n++) {
                        if (edgePointMap.get(i).getLinkedPointIndex().get(m) != edgePointMap.get(i).getLinkedPointIndex().get(n) && isOnAxis(modelAxis.get(edgePointMap.get(i).getLinkedPointIndex().get(m)).getBasedPoint(), modelAxis.get(edgePointMap.get(i).getLinkedPointIndex().get(n)).getBasedPoint())) {
                            addTriangleInSinglePoint(i, edgePointMap.get(i).getLinkedPointIndex().get(m), edgePointMap.get(i).getLinkedPointIndex().get(n));
                        }
                    }
                }
            }
            int axisPointIndex = findSameAxisPoint(i, nextIndex);
            if (axisPointIndex != -1) {
                addTriangleInTwoPoints(i, nextIndex, axisPointIndex);
            }
        }
        System.out.println("Building Step 2");
        for (Triangle T : modelTriangle) {
            addTri(T.getA(),T.getB(),T.getC());
        }
        for (Triangle T : modelTriangleLink) {
            addTri(T.getA(),T.getB(),T.getC());
        }
        System.out.println("Building Step 3");
    }
}
