import java.util.LinkedList;
import java.util.Arrays;

public class Model {
    private Polygon basedPolygon;
    private Polygon leftPolygon;
    private Polygon rightPolygon;
    private LinkedList<Triangle> modelTriangle = new LinkedList<>();
    private LinkedList<Triangle> modelTriangleLink = new LinkedList<>();
    private LinkedList<Axis> modelAxis = new LinkedList<>();
    private LinkedList<Axis> edgePointMap = new LinkedList<>();
    private LinkedList<Point> vertexPoint = new LinkedList<>();
    //所有点的VBO集
    private float[] vertexCoords = {};
    //依照点的坐标的法向量的VBO集
    private float[] normalIndexCoords = {};
    //依照点的坐标的法向量的VBO集
    private float[] normalCoords = {};
    //依照点的坐标的三角形VBO集
    private float[] triCoords = {};
    //依照点的索引的三角形VBO集
    private int[] elementArray = {};

    //依照点的坐标的剖分后的左右三角形VBO集
    private float[] leftTriCoords = {};
    private float[] rightTriCoords = {};

    //依照点的坐标的剖分后的左右三角形法向量VBO集
    private float[] leftTriNormal = {};
    private float[] rightTriNormal = {};

    private int[] pointLinkCoords = {};
    private LinkedList<Integer> pointLinkIndex = new LinkedList<>();

    //Getter and Setter

    public float[] getLeftTriNormal() {
        return leftTriNormal;
    }

    public float[] getRightTriNormal() {
        return rightTriNormal;
    }

    public Polygon getLeftPolygon() {
        return leftPolygon;
    }

    public Polygon getRightPolygon() {
        return rightPolygon;
    }

    public LinkedList<Axis> getEdgePointMap() {
        return edgePointMap;
    }

    public LinkedList<Point> getVertexPoint() {
        return vertexPoint;
    }

    public float[] getNormalIndexCoords() {
        return normalIndexCoords;
    }

    public float[] getLeftTriCoords() {
        return leftTriCoords;
    }

    public float[] getRightTriCoords() {
        return rightTriCoords;
    }

    public float[] getTriCoords() {
        return triCoords;
    }

    public float[] getNormalCoords() {
        return normalCoords;
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

    //计算抬升高度
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
//        for (int i = 0; i < 2; i++) {
//            smoothHeight();
//        }
        System.out.println("Lifting Success");
    }

    public void clearAll() {
        basedPolygon.clearAll();
        modelTriangle = new LinkedList<>();
        modelAxis = new LinkedList<>();
        edgePointMap = new LinkedList<>();
        modelLine = new LinkedList<>();
        modelTriangleLink = new LinkedList<>();
        vertexPoint = new LinkedList<>();

        vertexCoords = new float[0];
        elementArray = new int[0];
        triCoords = new float[0];
        normalCoords = new float[0];
        normalIndexCoords = new float[0];
        leftTriCoords = new float[0];
        rightTriCoords = new float[0];
        leftTriNormal = new float[0];
        rightTriNormal = new float[0];

        pointLinkCoords = new int[0];
        pointLinkIndex = new LinkedList<>();
    }

    //构造椭圆的边
    private LinkedList<Point> buildEllipse(double height, Point edgePoint, Point middlePoint) {
        LinkedList<Point> ellipsePoint = new LinkedList<>();
        Point moveWay = new Point(middlePoint.getX() - edgePoint.getX(), middlePoint.getY() - edgePoint.getY(), 0);
        double r = edgePoint.distance(middlePoint);
        double liftAngle = height * 0.25;
        for (int i = 3; i >= 1; i--) {
            ellipsePoint.add(countEllipsePoint(height, liftAngle * i, middlePoint, edgePoint));
        }
        return ellipsePoint;
    }

    //给定一个角度来计算点的位置
    private Point countEllipsePoint(double b, double liftHeight, Point middlePoint, Point edgePoint) {
        double liftAngle = Math.atan(liftHeight / middlePoint.distance(edgePoint));
        double x, y, z, percent;
//        z = b * Math.abs(Math.sin(liftAngle));
        z = liftHeight;
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
                buildVAO(beginList.get(i), endList.get(i), endList.get(i + 1));
                addTri(beginList.get(i), endList.get(i), endList.get(i + 1));
                addTri(beginList.get(i).mirrorPoint(), endList.get(i).mirrorPoint(), endList.get(i + 1).mirrorPoint());
            }
            isAdd = isAddTriangle(0, new Triangle(beginList.get(i + 1), endList.get(i + 1), beginList.get(i)));
            if (isAdd) {
                modelTriangle.add(new Triangle(beginList.get(i + 1).mirrorPoint(), endList.get(i + 1).mirrorPoint(), beginList.get(i).mirrorPoint()));
                buildVAO(beginList.get(i + 1), endList.get(i + 1), beginList.get(i));
                addTri(beginList.get(i + 1), endList.get(i + 1), beginList.get(i));
                addTri(beginList.get(i + 1).mirrorPoint(), endList.get(i + 1).mirrorPoint(), beginList.get(i).mirrorPoint());
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
            buildVAO(edgePointMap.get(edgePointIndex).getBasedPoint(), beginList.getLast(), endList.getLast());
            addTri(edgePointMap.get(edgePointIndex).getBasedPoint(), beginList.getLast(), endList.getLast());
            addTri(edgePointMap.get(edgePointIndex).getBasedPoint().mirrorPoint(), beginList.getLast().mirrorPoint(), endList.getLast().mirrorPoint());
        }
        addInsideTriangle(beginList, endList);

        isAdd = isAddTriangle(0, new Triangle(beginList.get(0), endList.get(0), modelAxis.get(axisIndexEnd).getLiftPoint()));
        if (isAdd) {
            modelTriangle.add(0, new Triangle(beginList.get(0).mirrorPoint(), endList.get(0).mirrorPoint(), modelAxis.get(axisIndexEnd).getLiftPoint().mirrorPoint()));
            buildVAO(beginList.get(0), endList.get(0), modelAxis.get(axisIndexEnd).getLiftPoint());
            addTri(beginList.get(0), endList.get(0), modelAxis.get(axisIndexEnd).getLiftPoint());
            addTri(beginList.get(0).mirrorPoint(), endList.get(0).mirrorPoint(), modelAxis.get(axisIndexEnd).getLiftPoint().mirrorPoint());
        }
        isAdd = isAddTriangle(0, new Triangle(beginList.get(0), modelAxis.get(axisIndexEnd).getLiftPoint(), modelAxis.get(axisIndexBegin).getLiftPoint()));
        if (isAdd) {
            modelTriangle.add(new Triangle(beginList.get(0).mirrorPoint(), modelAxis.get(axisIndexEnd).getLiftPoint().mirrorPoint(), modelAxis.get(axisIndexBegin).getLiftPoint().mirrorPoint()));
            buildVAO(beginList.get(0), modelAxis.get(axisIndexEnd).getLiftPoint(), modelAxis.get(axisIndexBegin).getLiftPoint());
            addTri(beginList.get(0), modelAxis.get(axisIndexEnd).getLiftPoint(), modelAxis.get(axisIndexBegin).getLiftPoint());
            addTri(beginList.get(0).mirrorPoint(), modelAxis.get(axisIndexEnd).getLiftPoint().mirrorPoint(), modelAxis.get(axisIndexBegin).getLiftPoint().mirrorPoint());
        }
    }

    private void addTriangleInTwoPoints(int edgeIndexBegin, int edgeIndexEnd, int axisPointIndex) {
        LinkedList<Point> beginList = edgePointMap.get(edgeIndexBegin).getEllipsePoint().get(axisPointIndex);
        LinkedList<Point> endList = edgePointMap.get(edgeIndexEnd).getEllipsePoint().get(axisPointIndex);
        boolean isAdd;
        isAdd = isAddTriangle(1, new Triangle(modelAxis.get(axisPointIndex).getLiftPoint(), beginList.getFirst(), endList.getFirst()));
        if (isAdd) {
            modelTriangleLink.add(new Triangle(modelAxis.get(axisPointIndex).getLiftPoint().mirrorPoint(), beginList.getFirst().mirrorPoint(), endList.getFirst().mirrorPoint()));
            buildVAO(modelAxis.get(axisPointIndex).getLiftPoint(), beginList.getFirst(), endList.getFirst());
            addTri(modelAxis.get(axisPointIndex).getLiftPoint(), beginList.getFirst(), endList.getFirst());
            addTri(modelAxis.get(axisPointIndex).getLiftPoint().mirrorPoint(), beginList.getFirst().mirrorPoint(), endList.getFirst().mirrorPoint());
        }
        addInsideTriangle(endList, beginList);

        isAdd = isAddTriangle(1, new Triangle(beginList.getLast(), endList.getLast(), edgePointMap.get(edgeIndexEnd).getBasedPoint()));
        if (isAdd) {
            modelTriangleLink.add(new Triangle(beginList.getLast().mirrorPoint(), endList.getLast().mirrorPoint(), edgePointMap.get(edgeIndexEnd).getBasedPoint().mirrorPoint()));
            buildVAO(beginList.getLast(), endList.getLast(), edgePointMap.get(edgeIndexEnd).getBasedPoint());
            addTri(beginList.getLast(), endList.getLast(), edgePointMap.get(edgeIndexEnd).getBasedPoint());
            addTri(beginList.getLast().mirrorPoint(), endList.getLast().mirrorPoint(), edgePointMap.get(edgeIndexEnd).getBasedPoint().mirrorPoint());
        }
        isAdd = isAddTriangle(1, new Triangle(beginList.getLast(), edgePointMap.get(edgeIndexEnd).getBasedPoint(), edgePointMap.get(edgeIndexBegin).getBasedPoint()));
        if (isAdd) {
            modelTriangleLink.add(new Triangle(beginList.getLast().mirrorPoint(), edgePointMap.get(edgeIndexEnd).getBasedPoint().mirrorPoint(), edgePointMap.get(edgeIndexBegin).getBasedPoint().mirrorPoint()));
            buildVAO(beginList.getLast(), edgePointMap.get(edgeIndexEnd).getBasedPoint(), edgePointMap.get(edgeIndexBegin).getBasedPoint());
            addTri(beginList.getLast(), edgePointMap.get(edgeIndexEnd).getBasedPoint(), edgePointMap.get(edgeIndexBegin).getBasedPoint());
            addTri(beginList.getLast().mirrorPoint(), edgePointMap.get(edgeIndexEnd).getBasedPoint().mirrorPoint(), edgePointMap.get(edgeIndexBegin).getBasedPoint().mirrorPoint());
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

        Point normal = new Triangle(p0, p1, p2).countNormal();
        float newNormal[] = new float[index + 9];
        System.arraycopy(normalCoords, 0, newNormal, 0, index);


        newNormal[index] = (float) normal.getX();
        newNormal[index + 1] = (float) normal.getY();
        newNormal[index + 2] = (float) normal.getZ();
        newNormal[index + 3] = (float) normal.getX();
        newNormal[index + 4] = (float) normal.getY();
        newNormal[index + 5] = (float) normal.getZ();
        newNormal[index + 6] = (float) normal.getX();
        newNormal[index + 7] = (float) normal.getY();
        newNormal[index + 8] = (float) normal.getZ();
        normalCoords = newNormal;

    }

    //更新基于element的triCoords
    private void updateTri() {
        triCoords = new float[elementArray.length * 3];
        for (int i = 0; i < elementArray.length; i += 3) {
            for (int j = 0; j < 3; j++) {
                triCoords[i * 3 + j * 3] = (float) vertexPoint.get(elementArray[i + j]).getX();
                triCoords[i * 3 + 1 + j * 3] = (float) vertexPoint.get(elementArray[i + j]).getY();
                triCoords[i * 3 + 2 + j * 3] = (float) vertexPoint.get(elementArray[i + j]).getZ();
            }
        }
    }

    //计算基于triCoords的normal
    private void buildNormal() {
        normalCoords = new float[triCoords.length];
        for (int index = 0; index < triCoords.length; index += 9) {
            Point p0 = new Point(triCoords[index], triCoords[index + 1], triCoords[index + 2]);
            Point p1 = new Point(triCoords[index + 3], triCoords[index + 4], triCoords[index + 5]);
            Point p2 = new Point(triCoords[index + 6], triCoords[index + 7], triCoords[index + 8]);
//            Point normal = new Triangle(p0, p1, p2).countNormal();
            Point normal = new Triangle(p0, p2, p1).countNormal();
            boolean normalZ = normal.getZ() > 0;
            boolean normalTri = p0.getZ() + p1.getZ() + p2.getZ() > 0;
//            boolean normalTri = normalCoords[index + 2] + normalCoords[index + 5] + normalCoords[index + 8] > 0;
            int dx = 1, dy = 1, dz = 1;
//            if (normalZ != normalTri) {
//                normal = new Triangle(p0, p2, p1).countNormal();
//                dz = -1;
//            }
//
//            if (normal.getX() > 0 != (p0.getX() + p1.getX() + p2.getX()) > 0) {
//                dx = -1;
//            }
//
//            if (Math.toDegrees(normal.angle(new Point(1, 0, 0))) > 80 || Math.toDegrees(normal.angle(new Point(1, 0, 0))) < 0) {
//                dx = -1;
//            }
//            if (Math.toDegrees(normal.angle(new Point(0, 1, 0))) > 80 || Math.toDegrees(normal.angle(new Point(0, 1, 0))) < 0) {
//                dy = -1;
//            }
//            if (normal.getY() > 0 != (p0.getY() + p1.getY() + p2.getY()) > 0) {
//                dy = -1;
//            }
            normalCoords[index] = (float) normal.getX() * dx;
            normalCoords[index + 1] = (float) normal.getY() * dy;
            normalCoords[index + 2] = (float) normal.getZ() * dz;
            normalCoords[index + 3] = (float) normal.getX() * dx;
            normalCoords[index + 4] = (float) normal.getY() * dy;
            normalCoords[index + 5] = (float) normal.getZ() * dz;
            normalCoords[index + 6] = (float) normal.getX() * dx;
            normalCoords[index + 7] = (float) normal.getY() * dy;
            normalCoords[index + 8] = (float) normal.getZ() * dz;
        }
    }

    //更新normal
    private void updateNormal() {
//        int judgeCount = 0;
        for (int i = 0; i < vertexPoint.size(); i++) {
            float x = 0f;
            float y = 0f;
            float z = 0f;
            int count = 0;
            for (int j = 0; j < elementArray.length; j += 3) {
                if (elementArray[j] == i || elementArray[j + 1] == i || elementArray[j + 2] == i) {
//                    Point normalP = new Triangle();
//                    int indexJ = j;
//                    if (elementArray[j + 1] == i) {
//                        indexJ++;
//                    }
//                    if (elementArray[j + 2] == i) {
//                        indexJ += 2;
//                    }
                    x += normalCoords[j * 3];
                    y += normalCoords[j * 3 + 1];
                    z += normalCoords[j * 3 + 2];
//                    x += (float) normalP.getX();
//                    y += (float) normalP.getY();
//                    z += (float) normalP.getZ();
                    count++;
                }
//1 2 3 4 5 6 7 8 9
//1     2     3
            }
//            System.out.println("x,y,z:" + x + "," + y + ',' + z);
//            System.out.println("x0,y0,z0:" + x0 + "," + y0+ ',' + z0);
//            System.out.println("Count: " + count);
            if (count != 0) {
                Point normalP = new Point(x / count, y / count, z / count).normalize();
//            Point normalP = new Point(x / count, y / count, z / count);
                for (int j = 0; j < elementArray.length; j += 3) {
                    if (elementArray[j] == i || elementArray[j + 1] == i || elementArray[j + 2] == i) {
//                    System.out.println("Old Normal:" + normalCoords[j] + "," + normalCoords[j + 1] + ',' + normalCoords[j + 2]);
                        int indexJ = 0;
                        if (elementArray[j + 1] == i) {
                            indexJ += 3;
                        }
                        if (elementArray[j + 2] == i) {
                            indexJ += 6;
                        }
                        normalCoords[3 * j + indexJ] = (float) normalP.getX();
                        normalCoords[3 * j + indexJ + 1] = (float) normalP.getY();
                        normalCoords[3 * j + indexJ + 2] = (float) normalP.getZ();
//                    System.out.println("New Normal:" + normalCoords[j] + "," + normalCoords[j + 1] + ',' + normalCoords[j + 2]);
//                        judgeCount++;
                    }
                }
            }
        }
//        System.out.println("JudgeCount: " + judgeCount);
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

            float newNormalCoords[] = new float[index + 3];
            System.arraycopy(normalIndexCoords, 0, newNormalCoords, 0, index);
            newVertexCoords[index] = 0;
            newVertexCoords[index + 1] = 0;
            newVertexCoords[index + 2] = 0;
            normalIndexCoords = newNormalCoords;
            if (Math.abs(p.getZ()) >= 0.00000001) {
                vertexPoint.add(p.mirrorPoint());
                index = vertexCoords.length;
                newVertexCoords = new float[index + 3];
                System.arraycopy(vertexCoords, 0, newVertexCoords, 0, index);
                newVertexCoords[index] = (float) p.mirrorPoint().getX();
                newVertexCoords[index + 1] = (float) p.mirrorPoint().getY();
                newVertexCoords[index + 2] = (float) p.mirrorPoint().getZ();
                vertexCoords = newVertexCoords;

                newNormalCoords = new float[index + 3];
                System.arraycopy(normalIndexCoords, 0, newNormalCoords, 0, index);
                newVertexCoords[index] = 0;
                newVertexCoords[index + 1] = 0;
                newVertexCoords[index + 2] = 0;
                normalIndexCoords = newNormalCoords;
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

    //根据绘制的线条建立模型
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
        buildNormal();
//        updateNormal();
        buildAxisIndex();
        System.out.println("Building Step 3");
        for (int i = 0; i < 4; i++) {
            smooth(1, 0);
        }
    }

    //根据中线分割模型
    public void divideModel(int startIndexX, int startIndexY, int endIndexX, int endIndexY, LinkedList<Point> cutLinePoint) {
        if (startIndexX != endIndexX && endIndexY != startIndexY) {
            //右边的框
            rightPolygon = new Polygon();
            rightPolygon.addPolygonPoint(cutLinePoint.getFirst());
            int lastIndex = endIndexX;
            int maxIndex = basedPolygon.getPolygonPoint().size();
            if (lastIndex < startIndexY) {
                lastIndex += maxIndex;
            }
            for (int i = startIndexY; i <= lastIndex; i++) {
                rightPolygon.addPolygonPoint(basedPolygon.getPolygonPoint().get(i % maxIndex));
            }
            rightPolygon.addPolygonPoint(cutLinePoint.getLast());
            for (int i = cutLinePoint.size() - 2; i >= 1; i--) {
                rightPolygon.addPolygonPoint(cutLinePoint.get(i));
            }
            //左边的框
            leftPolygon = new Polygon();
            leftPolygon.addPolygonPoint(cutLinePoint.getLast());
            lastIndex = startIndexX;
            if (lastIndex < endIndexY) {
                lastIndex += maxIndex;
            }
            for (int i = endIndexY; i <= lastIndex; i++) {
                leftPolygon.addPolygonPoint(basedPolygon.getPolygonPoint().get(i % maxIndex));
            }
            leftPolygon.addPolygonPoint(cutLinePoint.getFirst());
            for (int i = 1; i <= cutLinePoint.size() - 2; i++) {
                leftPolygon.addPolygonPoint(cutLinePoint.get(i));
            }
        } else {
            //外面的框
            rightPolygon = new Polygon();
            int maxIndex = basedPolygon.getPolygonPoint().size();
            for (int i = 0; i < maxIndex; i++) {
                rightPolygon.addPolygonPoint(basedPolygon.getPolygonPoint().get((startIndexY + i) % maxIndex));
            }
            if (cutLinePoint.getFirst().distance(basedPolygon.getPolygonPoint().get(startIndexX)) < cutLinePoint.getLast().distance(basedPolygon.getPolygonPoint().get(startIndexX))) {
                for (int i = 0; i < cutLinePoint.size(); i++) {
                    rightPolygon.addPolygonPoint(cutLinePoint.get(i));
                }
            } else {
                for (int i = cutLinePoint.size() - 1; i >= 0; i--) {
                    rightPolygon.addPolygonPoint(cutLinePoint.get(i));
                }
            }
        }
        rightPolygon.buildPolygonLine();
        leftPolygon.buildPolygonLine();
//
        for (int i = 0; i < triCoords.length - 9; i += 9) {
            divideTriangle(i);
        }

    }

    //分割三角形
    private void divideTriangle(int index) {
        int leftCount = 0;
        int leftIndexSet[] = {-1, -1, -1};
        if (selectModel(new Point(triCoords[index], triCoords[index + 1], 0))) {
            leftCount++;
            leftIndexSet[0] = 1;
        }
        if (selectModel(new Point(triCoords[index + 3], triCoords[index + 4], 0))) {
            leftCount++;
            leftIndexSet[1] = 1;
        }
        if (selectModel(new Point(triCoords[index + 6], triCoords[index + 7], 0))) {
            leftCount++;
            leftIndexSet[2] = 1;
        }
        //加入右边的三角形阵列
        if (leftCount == 0) {
            addCutTri(1, index);
        } else if (leftCount == 3) {
            //加入左边的三角形阵列
            addCutTri(0, index);
        } else if (leftCount == 1) {

        }
    }

    private void addCutTri(int mode, int index) {
        float point[];
        float normal[];
        if (mode == 0) {
            point = leftTriCoords;
            normal = leftTriNormal;
        } else {
            point = rightTriCoords;
            normal = rightTriNormal;
        }
        int lastIndex = normal.length;
        float newNormal[] = new float[lastIndex + 9];
        System.arraycopy(normal, 0, newNormal, 0, lastIndex);
        System.arraycopy(normalCoords, index, newNormal, lastIndex, 9);
//        for (int i = 0; i < 9; i++) {
//            newNormal[lastIndex + i] = normalCoords[index + i];
//        }

        float newCoords[] = new float[lastIndex + 9];
        System.arraycopy(point, 0, newCoords, 0, lastIndex);
        System.arraycopy(triCoords, index, newCoords, lastIndex, 9);
//        for (int i = 0; i < 9; i++) {
//            newCoords[lastIndex + i] = rightTriCoords[index + i];
//        }

        if (mode == 0) {
            leftTriNormal = newNormal;
            leftTriCoords = newCoords;
        } else {
            rightTriNormal = newNormal;
            rightTriCoords = newCoords;
        }
    }

    private void smoothHeight() {
        for (int i = 0; i < basedPolygon.getEndPoint().size(); i++) {
            double sumHeight = 0;
            int sumPoint = 0;
            double thisHeight = modelAxis.get(i).getAxisHeight();
            for (int j = 0; j < basedPolygon.getEndPoint().size(); j++) {
                if (j != i) {
                    if (isOnAxis(basedPolygon.getEndPoint().get(j), basedPolygon.getEndPoint().get(i))) {
                        sumPoint++;
                        sumHeight += modelAxis.get(j).getAxisHeight();
                    }
                }
            }
//            if (thisHeight <= sumHeight / sumPoint) {
//                modelAxis.get(i).setAxisHeight(sumHeight / sumPoint);
//            }
            modelAxis.get(i).setAxisHeight(sumHeight / sumPoint);

        }
    }

    //false:右边 true:左边
    public boolean selectModel(Point p) {
        return leftPolygon.inPolygon(p);
    }

    public void smooth(int type, int vertexSize) {
        LinkedList<Point> newPointList = new LinkedList<>();
        double lambda = 0.5;
        if (type == 1) {
            vertexSize = vertexPoint.size();
        }
        for (int i = 0; i < vertexSize; i++) {
            int beginIndex = pointLinkIndex.get(i);
            int endIndex = pointLinkCoords.length;
            if (i != vertexSize - 1) {
                endIndex = pointLinkIndex.get(i + 1);
            }
            double distance[] = new double[endIndex - beginIndex];
            double sumDistance = 0.0;
            double newX = 0.0;
            double newY = 0.0;
            double newZ = 0.0;

            if (type == 0) {
                for (int j = beginIndex; j < endIndex; j++) {
                    double dis = vertexPoint.get(i).distance(vertexPoint.get(pointLinkCoords[j]));
                    sumDistance += dis;
                    distance[j - beginIndex] = dis;
                }
                for (int j = beginIndex; j < endIndex; j++) {
//                newX += (distance[j - beginIndex] / sumDistance) * (vertexPoint.get(pointLinkCoords[j]).getX() - vertexPoint.get(i).getX());
                    newX += (distance[j - beginIndex] / sumDistance) * vertexPoint.get(pointLinkCoords[j]).getX();
//                newY += (distance[j - beginIndex] / sumDistance) * (vertexPoint.get(pointLinkCoords[j]).getY() - vertexPoint.get(i).getY());
                    newY += (distance[j - beginIndex] / sumDistance) * vertexPoint.get(pointLinkCoords[j]).getY();
//                newZ += (distance[j - beginIndex] / sumDistance) * (vertexPoint.get(pointLinkCoords[j]).getZ() - vertexPoint.get(i).getZ());
                    newZ += (distance[j - beginIndex] / sumDistance) * vertexPoint.get(pointLinkCoords[j]).getZ();
                }
                newX = lambda * newX + (1 - lambda) * vertexPoint.get(i).getX();
                newY = lambda * newY + (1 - lambda) * vertexPoint.get(i).getY();
                newZ = lambda * newZ + (1 - lambda) * vertexPoint.get(i).getZ();
            }
            if (type == 1) {
                int n = endIndex - beginIndex;
                double beta = (1.0 / n) * (5 / 8.0 - (3 / 8.0 + (1 / 4.0) * Math.cos((2 * Math.PI) / n)) * (3 / 8.0 + (1 / 4.0) * Math.cos((2 * Math.PI) / n)));
                for (int j = beginIndex; j < endIndex; j++) {
                    newX += beta * vertexPoint.get(pointLinkCoords[j]).getX();
                    newY += beta * vertexPoint.get(pointLinkCoords[j]).getY();
                    newZ += beta * vertexPoint.get(pointLinkCoords[j]).getZ();
                }
                newX += (1 - n * beta) * vertexPoint.get(i).getX();
                newY += (1 - n * beta) * vertexPoint.get(i).getY();
                newZ += (1 - n * beta) * vertexPoint.get(i).getZ();
            }
            newPointList.add(new Point(newX, newY, newZ));
        }
        vertexPoint.clear();
        vertexPoint = newPointList;
        updateTri();
        buildNormal();
        updateNormal();
    }

    //建立边索引
    private void buildAxisIndex() {
        pointLinkCoords = new int[0];
        for (int i = 0; i < vertexPoint.size(); i++) {
            LinkedList<Integer> linkPoint = new LinkedList<>();
            for (int index = 0; index < elementArray.length; index += 3) {
                boolean isInside = false;
                for (int j = 0; j < 3; j++) {
                    if (elementArray[index + j] == i) {
                        isInside = true;
                        break;
                    }
                }
                if (isInside) {
                    for (int j = 0; j < 3; j++) {
                        if (elementArray[index + j] != i) {
                            boolean isIn = false;
                            for (int num : linkPoint) {
                                if (num == elementArray[index + j]) {
                                    isIn = true;
                                    break;
                                }
                            }
                            if (!isIn) {
                                linkPoint.add(elementArray[index + j]);
                            }
                        }
                    }
                }
            }
            if (linkPoint.size() == 0) {
                System.out.println("wtf");
            }
            pointLinkIndex.add(i, pointLinkCoords.length);
            int[] newLink = new int[pointLinkCoords.length + linkPoint.size()];
            System.arraycopy(pointLinkCoords, 0, newLink, 0, pointLinkCoords.length);
            for (int j = 0; j < linkPoint.size(); j++) {
                newLink[pointLinkCoords.length + j] = linkPoint.get(j);
            }
            pointLinkCoords = newLink;
        }
    }

    //曲面细分
    public void loopSub() {
        LinkedList<Point> innerPoint = new LinkedList<>();
        LinkedList<Point> newPointList = vertexPoint;
        int vertexSize = vertexPoint.size();
        smooth(1, vertexSize);
//        System.out.println("length: " + elementArray.length);
        int elementLength = elementArray.length;
        for (int i = 0; i < elementLength; i += 3) {
            //增加内部顶点
            int index01, index12, index02;

            Point inn = calInnerPoint(elementArray[i], elementArray[i + 1]);
            index01 = isInListPoint(innerPoint, inn);
            if (index01 == -1) {
                newPointList.add(inn);
                innerPoint.add(inn);
                index01 = vertexPoint.size() - 1;
            } else {
                index01 += vertexSize;
            }
            inn = calInnerPoint(elementArray[i], elementArray[i + 2]);
            index02 = isInListPoint(innerPoint, inn);
            if (index02 == -1) {
                index02 = vertexPoint.size();
                innerPoint.add(inn);
                newPointList.add(inn);
            } else {
                index02 += vertexSize;
            }
            inn = calInnerPoint(elementArray[i + 2], elementArray[i + 1]);
            index12 = isInListPoint(innerPoint, inn);
            if (index12 == -1) {
                innerPoint.add(inn);
                index12 = newPointList.size();
                newPointList.add(inn);
            } else {
                index12 += vertexSize;
            }
            //构建内部三角形
//            System.out.println("Vertex Size:" + newPointList.size() + ", index01:" + index01 + ", index02:" + index02 + ", index12:" + index12);
            addTriFromIndex(index01, index02, index12);
            addTriFromIndex(i, index01, index02);
            addTriFromIndex(i + 1, index01, index12);
            addTriFromIndex(i + 2, index02, index12);
            System.out.println("size = " + elementLength + ", i = " + i);
            //移动顶点位置

        }
        vertexPoint.clear();
        vertexPoint = newPointList;
        updateTri();
        buildAxisIndex();
        buildNormal();
        //            updateNormal();
    }

    private Point calInnerPoint(int leftIndex, int rightIndex) {
        int upIndex = -1, downIndex = -1;
        int count = 0;

        for (int i = 0; i < elementArray.length; i += 3) {
            if (((elementArray[i] == leftIndex && elementArray[i + 1] == rightIndex)) || ((elementArray[i + 1] == leftIndex && elementArray[i] == rightIndex))) {
                if (count == 0) {
                    upIndex = elementArray[i + 2];
                    count++;
                } else {
                    downIndex = elementArray[i + 2];
                    break;
                }
            }
            if (((elementArray[i] == leftIndex && elementArray[i + 2] == rightIndex)) || ((elementArray[i + 2] == leftIndex && elementArray[i] == rightIndex))) {
                if (count == 0) {
                    upIndex = elementArray[i + 1];
                    count++;
                } else {
                    downIndex = elementArray[i + 1];
                    break;
                }
            }
            if (((elementArray[i + 1] == leftIndex && elementArray[i + 2] == rightIndex)) || ((elementArray[i + 2] == leftIndex && elementArray[i + 1] == rightIndex))) {
                if (count == 0) {
                    upIndex = elementArray[i];
                    count++;
                } else {
                    downIndex = elementArray[i];
                    break;
                }
            }
        }

//        int leftBegin = pointLinkIndex.get(leftIndex);
//        int rightBegin = pointLinkIndex.get(rightIndex);
//        int leftEnd = pointLinkCoords.length;
//        int rightEnd = pointLinkCoords.length;
//
//        if (rightIndex != vertexPoint.size() - 1) {
//            rightEnd = pointLinkIndex.get(rightIndex + 1);
//        }
//        if (leftIndex != vertexPoint.size() - 1) {
//            leftEnd = pointLinkIndex.get(leftIndex + 1);
//        }
//
//        for (int i = leftBegin; i < leftEnd; i++) {
//            for (int j = rightBegin; j < rightEnd; j++) {
//                if (i == j) {
//                    if (count == 0) {
//                        count++;
//                        upIndex = i;
//                    } else if (count == 1) {
//                        if (i != upIndex) {
//                            count++;
//                            downIndex = i;
//                            break;
//                        }
//                    }
//                }
//            }
//            if (count >= 2) {
//                break;
//            }
//        }

        if (upIndex == -1 || downIndex == -1) {
            double x = 0.5 * (vertexPoint.get(leftIndex).getX() + vertexPoint.get(rightIndex).getX());
            double y = 0.5 * (vertexPoint.get(leftIndex).getY() + vertexPoint.get(rightIndex).getY());
            double z = 0.5 * (vertexPoint.get(leftIndex).getZ() + vertexPoint.get(rightIndex).getZ());
            return new Point(x, y, z);
        } else {
            Point upPoint = vertexPoint.get(upIndex);
            Point downPoint = vertexPoint.get(downIndex);
            Point rightPoint = vertexPoint.get(rightIndex);
            Point leftPoint = vertexPoint.get(leftIndex);
            double x = (1 / 8.0) * (upPoint.getX() + downPoint.getX()) + (3 / 8.0) * (rightPoint.getX() + leftPoint.getX());
            double y = (1 / 8.0) * (upPoint.getY() + downPoint.getY()) + (3 / 8.0) * (rightPoint.getY() + leftPoint.getY());
            double z = (1 / 8.0) * (upPoint.getZ() + downPoint.getZ()) + (3 / 8.0) * (rightPoint.getZ() + leftPoint.getZ());
            return new Point(x, y, z);
        }
    }

    private int isInListPoint(LinkedList<Point> pointList, Point p) {
        if (pointList.size() == 0) {
            return -1;
        }
        int isIn = -1;
        for (int i = 0; i < pointList.size(); i++) {
            if (p.equal(pointList.get(i))) {
                isIn = i;
                break;
            }
        }
        return isIn;
    }

    private void addTriFromIndex(int a, int b, int c) {
        int newElementArray[] = new int[elementArray.length + 3];
        System.arraycopy(elementArray, 0, newElementArray, 0, elementArray.length);
        newElementArray[elementArray.length] = a;
        newElementArray[elementArray.length + 1] = b;
        newElementArray[elementArray.length + 2] = c;
        elementArray = newElementArray;
//        float newTriCoords[] = new float[triCoords.length + 9];
//        int len = triCoords.length;
//        System.arraycopy(triCoords, 0, newTriCoords, 0, triCoords.length);
//        newTriCoords[len] = (float) vertexPoint.get(a).getX();
//        newTriCoords[len + 1] = (float) vertexPoint.get(a).getY();
//        newTriCoords[len + 2] = (float) vertexPoint.get(a).getZ();
//
//        newTriCoords[len + 3] = (float) vertexPoint.get(b).getX();
//        newTriCoords[len + 4] = (float) vertexPoint.get(b).getY();
//        newTriCoords[len + 5] = (float) vertexPoint.get(b).getZ();
//
//        newTriCoords[len + 6] = (float) vertexPoint.get(c).getX();
//        newTriCoords[len + 7] = (float) vertexPoint.get(c).getY();
//        newTriCoords[len + 8] = (float) vertexPoint.get(c).getZ();
//        triCoords = newTriCoords;
    }
}
