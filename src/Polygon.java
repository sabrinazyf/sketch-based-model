import java.util.LinkedList;
import java.util.List;

public class Polygon {
    //多边形点集
    private LinkedList<Point> polygonPoint = new LinkedList<>();
    //多边形边集
    private LinkedList<Line> polygonLine = new LinkedList<>();
    //多边形的超级三角形
    private Triangle superTriangle;
    //三角剖分后的边集
    private LinkedList<Line> CATLine = new LinkedList<>();
    //三角剖分后的三角形集合
    private LinkedList<Triangle> CATTriangle = new LinkedList<>();
    //多边形中轴线
    private LinkedList<Line> polygonAxis = new LinkedList<>();
    //扇形划分后的三角形集合
    private LinkedList<Triangle> fanTriangle = new LinkedList<>();
    //扇形区域的三角形集合
    private LinkedList<Triangle> fanRegionTriangle = new LinkedList<>();
    //扇形划分后的边集
    private LinkedList<Line> fanLine = new LinkedList<>();
    //终点集，防止出现没有中轴的情况
    private LinkedList<Point> endPoint = new LinkedList<>();
    private Point middlePoint;

    public Point getMiddlePoint() {
        return middlePoint;
    }

    //Getter
    public LinkedList<Point> getEndPoint() {
        return endPoint;
    }

    public LinkedList<Point> getPolygonPoint() {
        return polygonPoint;
    }

    public LinkedList<Triangle> getFanTriangle() {
        return fanTriangle;
    }

    public LinkedList<Triangle> getFanRegionTriangle() {
        return fanRegionTriangle;
    }

    public LinkedList<Line> getFanLine() {
        return fanLine;
    }

    public LinkedList<Line> getPolygonLine() {
        return polygonLine;
    }

    public Triangle getSuperTriangle() {
        return superTriangle;
    }

    public LinkedList<Line> getCATLine() {
        return CATLine;
    }

    public LinkedList<Triangle> getCATTriangle() {
        return CATTriangle;
    }

    public LinkedList<Line> getPolygonAxis() {
        return polygonAxis;
    }

    //Setter
    public void addPolygonLine(Line s) {
        polygonLine.add(s);
    }

    public void addPolygonPoint(Point p) {
        polygonPoint.add(p);
    }

    public void setFanLine(LinkedList<Line> fanLine) {
        this.fanLine = fanLine;
    }

    public void clearAll() {
        polygonPoint.clear();
        polygonLine.clear();
        CATLine.clear();
        CATTriangle.clear();
        polygonAxis.clear();
        fanTriangle.clear();
        fanRegionTriangle.clear();
        fanLine.clear();
    }

    //构造超级三角形，默认多边形的z值均相等
    private void buildSuperTriangle() {
        double min_x, min_y, max_x, max_y, pol_z, temp_x, temp_y;
        min_x = max_x = polygonPoint.get(0).getX();
        min_y = max_y = polygonPoint.get(0).getY();
        pol_z = polygonPoint.get(0).getZ();
        for (int i = 1; i < polygonPoint.size(); i++) {
            temp_x = polygonPoint.get(i).getX();
            temp_y = polygonPoint.get(i).getY();
            if (temp_x >= max_x) max_x = temp_x;
            if (temp_x <= min_x) min_x = temp_x;
            if (temp_y >= max_y) max_y = temp_y;
            if (temp_y <= min_y) min_y = temp_y;
        }
        double length = max_x - min_x;
        double width = max_y - min_y;
        Point x = new Point(min_x - length * 0.5 - 0.5, min_y - 0.5, pol_z);
        Point y = new Point(min_x + 0.5 * length, max_y + width + 0.5, pol_z);
        Point z = new Point(max_x + length * 0.5 + 0.5, min_y - 0.5, pol_z);
        superTriangle = new Triangle(x, y, z);
    }

    //以x由小到大，y由大到小的顺序对点进行排序
    private void sortPoint() {
        quickSort(0, polygonPoint.size() - 1);
    }

    //两点交换位置，用于排序函数内
    private void swap(int oldPosition, int newPosition) {
        Point tempElement = this.polygonPoint.get(oldPosition);
        // 向前移动，前面的元素需要向后移动
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                this.polygonPoint.set(i, this.polygonPoint.get(i + 1));
            }
            this.polygonPoint.set(newPosition, tempElement);
        }
        // 向后移动，后面的元素需要向前移动
        if (oldPosition > newPosition) {
            for (int i = oldPosition; i > newPosition; i--) {
                this.polygonPoint.set(i, this.polygonPoint.get(i - 1));
            }
            this.polygonPoint.set(newPosition, tempElement);
        }
    }

    //排序函数
    private void quickSort(int left, int right) {
        if (left > right) return;
        int storeIndex = left;
        Point pivot = polygonPoint.get(right);
        for (int i = left; i < right; i++) {
            if (polygonPoint.get(i).getX() < pivot.getX() || (polygonPoint.get(i).getX() == pivot.getX() && polygonPoint.get(i).getY() > pivot.getY())) {
                swap(storeIndex, i);
                storeIndex += 1;
            }
        }
        swap(right, storeIndex);
        quickSort(left, storeIndex - 1);
        quickSort(storeIndex + 1, right);
    }

    //取大小
    private double min(double a, double b) {
        if (a <= b) {
            return a;
        } else {
            return b;
        }
    }

    private double max(double a, double b) {
        if (a <= b) {
            return b;
        } else {
            return a;
        }
    }

    //用多边形的点来创建多边形的边
    public void buildPolygonLine() {
        for (int i = 0; i < polygonPoint.size(); i++) {
            Line line = new Line(polygonPoint.get(i), polygonPoint.get(0));
            if (i != polygonPoint.size() - 1) {
                line = new Line(polygonPoint.get(i), polygonPoint.get(i + 1));
            }
            polygonLine.add(line);
        }
        for (int m = 0; m < polygonLine.size() - 1; m++) {
            for (int n = polygonLine.size() - 1; n > m; n--) {
                if (polygonLine.get(m).equal(polygonLine.get(n))) {
                    polygonLine.remove(n);
                }
            }
        }
    }

    //判断点是否在多边形的边上
    public boolean onSegment(Point p) {
        for (Line line : polygonLine) {
            if ((p.getX() - line.getStart().getX()) * (line.getEnd().getY() - line.getStart().getY())
                    == (line.getEnd().getX() - line.getStart().getX()) * (p.getY() - line.getStart().getY())  //叉乘
                    //保证Q点坐标在pi,pj之间
                    && min(line.getStart().getX(), line.getEnd().getX()) <= p.getX() && p.getX() <= max(line.getStart().getX(), line.getEnd().getX())
                    && min(line.getStart().getY(), line.getEnd().getY()) <= p.getY() && p.getY() <= max(line.getStart().getY(), line.getEnd().getY()))
                return true;
        }
        return false;
    }

    public boolean onSegmentLine(Line l) {
        for (Line line : polygonLine) {
            if (line.equal(l)) {
                return true;
            }
        }
        return false;
    }

    private int isInteractForPolygon(Line thisLine, Line judgeLine) {
//        boolean isInteract = judgeLine.intersect2D(new Line(postLine.getStart(), thisLine.getEnd()));
        if (!judgeLine.parallel(thisLine)) {
            if (thisLine.intersect2D(judgeLine)) {
                //上实下空
                Point upPoint = thisLine.getStart();
                Point downPoint = thisLine.getEnd();
                if (thisLine.getStart().getY() < thisLine.getEnd().getY()) {
                    downPoint = thisLine.getStart();
                    upPoint = thisLine.getEnd();
                }
                Line upLine = new Line(upPoint, judgeLine.getStart());
                Line downLine = new Line(downPoint, judgeLine.getStart());
                if (upLine.parallel(judgeLine)) {
                    return 1;
                }
                if (downLine.parallel(judgeLine)) {
                    return 0;
                }
                if (!upLine.parallel(judgeLine) && !downLine.parallel(judgeLine)) {
                    return 1;
                }
            }
        }
        return 0;
    }

    //判断点是否在多边形内部，true为在内部，false为在外部
    public boolean inPolygon(Point p) {
        if (this.onSegment(p)) {
            return true;
        } else {
            double min_x, max_x, temp_x;
            min_x = max_x = polygonPoint.get(0).getX();
            for (int i = 1; i < polygonPoint.size(); i++) {
                temp_x = polygonPoint.get(i).getX();
                if (temp_x >= max_x) max_x = temp_x;
                if (temp_x <= min_x) min_x = temp_x;
            }
            max_x += 1.0;
            min_x -= 1.0;
            Line rightLine = new Line(p, new Point(max_x, p.getY(), p.getZ()));
            Line leftLine = new Line(new Point(min_x, p.getY(), p.getZ()), p);
            int rightSum = 0;
            int leftSum = 0;
            for (Line line : polygonLine) {
                rightSum += isInteractForPolygon(line, rightLine);
                leftSum += isInteractForPolygon(line, leftLine);
            }
//            System.out.println("Point:(" + p.getX() + ", " + p.getY() + ", " + p.getZ() + "), Right: " + rightSum + ", Left: " + leftSum);
            return rightSum % 2 != 0 && leftSum % 2 != 0;
        }
    }

    private boolean inPolygonLine(Line l) {
        boolean isInside = true;
        if (!inPolygon(l.getMiddlePoint())) {
            isInside = false;
        } else {
            for (Line line : polygonLine) {
                if (!line.haveSamePoint(l) && line.intersect2D(l)) {
                    isInside = false;
                }
            }
        }
        return isInside;
    }

    private int addLineInBuildCAT(Line l, Point p) {
        if (onSegmentLine(l)) {
            CATLine.add(l);
            return 1;
        } else {
            if (this.inPolygonLine(l)) {
                CATLine.add(l);
                return 1;
            }
        }
        return 0;
    }

    //可能会误删一些线
    private void completeCAT() {
        for (int m = 0; m < polygonPoint.size(); m++) {
            for (int n = 0; n < polygonPoint.size(); n++) {
                if (m != n) {
                    Line judgeLine = new Line(polygonPoint.get(m), polygonPoint.get(n));
                    boolean isIntersect = false;
                    for (Line l : CATLine) {
                        if (!l.haveSamePoint(judgeLine) && l.intersect2D(judgeLine) || !inPolygon(judgeLine.getMiddlePoint())) {
                            isIntersect = true;
                            break;
                        }
                    }
                    if (!isIntersect) {
                        CATLine.add(judgeLine);
//                        addCompleteCATTriangle(m, n);
                    }
                }
            }
        }
    }

    private void addCompleteCATTriangle(int m, int n) {
        Line judgeLine = new Line(polygonPoint.get(m), polygonPoint.get(n));
        CATLine.add(judgeLine);
        for (int i = -1; i <= 1; i++) {
            if (i != 0) {
                int mj = m + i;
                int nj = n + i;
                if (mj < 0) {
                    mj = polygonPoint.size() - 1;
                }
                if (nj < 0) {
                    nj = polygonPoint.size() - 1;
                }
                if (mj >= polygonPoint.size()) {
                    mj = 0;
                }
                if (nj >= polygonPoint.size()) {
                    nj = 0;
                }
                Line lineN = new Line(polygonPoint.get(mj), polygonPoint.get(n));
                Line lineM = new Line(polygonPoint.get(nj), polygonPoint.get(m));
                boolean isInsideM = false;
                boolean isInsideN = false;
                for (Line l : CATLine) {
                    if (l.equal(lineM)) {
                        isInsideM = true;
                    }
                    if (l.equal(lineN)) {
                        isInsideN = true;
                    }
                    if (isInsideM && isInsideN) {
                        break;
                    }
                }
                if (isInsideM) {
                    Triangle T = new Triangle(polygonPoint.get(nj), polygonPoint.get(m), polygonPoint.get(n));
                    T.calTriangleType(this);
                    CATTriangle.add(T);
                }
                if (isInsideN) {
                    Triangle T = new Triangle(polygonPoint.get(mj), polygonPoint.get(m), polygonPoint.get(n));
                    T.calTriangleType(this);
                    CATTriangle.add(T);
                }
            }
        }
    }

    //构造CAT
    public void buildCAT() {
        middlePoint = calMiddlePoint();
        if (polygonPoint.size() == 3) {
            CATTriangle.add(new Triangle(polygonPoint.get(0), polygonPoint.get(1), polygonPoint.get(2)));
            CATLine.add(polygonLine.get(0));
            CATLine.add(polygonLine.get(1));
            CATLine.add(polygonLine.get(2));
            return;
        }
        List<Triangle> tempTriangles = new LinkedList<>();
        List<Triangle> triangles = new LinkedList<>();
        List<Triangle> tempTrianglesCopy = new LinkedList<>();
        List<Triangle> insideTriangles = new LinkedList<>();
        buildSuperTriangle();
        tempTriangles.add(superTriangle);
//        sortPoint();
        List<Line> lineBuffer = new LinkedList<>();
        for (Point p : polygonPoint) {
            lineBuffer.clear();
            insideTriangles.clear();
            for (Triangle triangle : tempTriangles) {
                int judge = triangle.isInCircumCircle(p);
                if (judge == 0) {

                    insideTriangles.add(triangle);
                } else {
                    tempTrianglesCopy.add(triangle);
                }
            }
            tempTriangles.clear();
            tempTriangles.addAll(tempTrianglesCopy);
            tempTrianglesCopy.clear();
            List<Line> insideLine = new LinkedList<>();
            for (int m = 0; m < insideTriangles.size(); m++) {
                Line line1[] = {new Line(insideTriangles.get(m).getA(), insideTriangles.get(m).getB()),
                        new Line(insideTriangles.get(m).getA(), insideTriangles.get(m).getC()),
                        new Line(insideTriangles.get(m).getB(), insideTriangles.get(m).getC())};
                int crossCount = 0;
                for (int n = 0; n < insideTriangles.size(); n++) {
                    if (n != m) {
                        Line line2[] = {new Line(insideTriangles.get(n).getA(), insideTriangles.get(n).getB()),
                                new Line(insideTriangles.get(n).getA(), insideTriangles.get(n).getC()),
                                new Line(insideTriangles.get(n).getB(), insideTriangles.get(n).getC())};
                        for (Line l1 : line1) {
                            for (Line l2 : line2) {
                                if (l1.equal(l2)) {
                                    crossCount++;
                                    insideLine.add(l1);
                                }
                            }
                        }
                    }
                }
                if (insideTriangles.size() == 1 || crossCount != 0) {
                    lineBuffer.add(new Line(insideTriangles.get(m).getA(), insideTriangles.get(m).getB()));
                    lineBuffer.add(new Line(insideTriangles.get(m).getA(), insideTriangles.get(m).getC()));
                    lineBuffer.add(new Line(insideTriangles.get(m).getB(), insideTriangles.get(m).getC()));
                }
            }
            //去重
            for (int m = 0; m < lineBuffer.size() - 1; m++) {
                for (int n = lineBuffer.size() - 1; n > m; n--) {
                    if (lineBuffer.get(m).equal(lineBuffer.get(n))) {
                        lineBuffer.remove(n);
                    }
                }
            }
            for (int m = 0; m < insideLine.size() - 1; m++) {
                for (int n = insideLine.size() - 1; n > m; n--) {
                    if (insideLine.get(m).equal(insideLine.get(n))) {
                        insideLine.remove(n);
                    }
                }
            }

            List<Line> lineBufferCopy = new LinkedList<>();
            for (Line line : lineBuffer) {
                boolean isInside = false;
                for (Line line1 : insideLine) {
                    if (line1.equal(line)) {
                        isInside = true;
                    }
                }
                if (!isInside) {
                    lineBufferCopy.add(line);
                }
            }
            lineBuffer.clear();
            lineBuffer.addAll(lineBufferCopy);


            for (Line segment : lineBuffer) {
                tempTriangles.add(new Triangle(segment.getStart(), segment.getEnd(), p));
            }
            for (Triangle tri : tempTriangles) {
                Line AB = new Line(tri.getA(), tri.getB());
                Line AC = new Line(tri.getA(), tri.getC());
                Line BC = new Line(tri.getB(), tri.getC());
                boolean isABInside = false;
                boolean isBCInside = false;
                boolean isACInside = false;
                for (Line line1 : insideLine) {
                    if (line1.equal(AB)) {
                        isABInside = true;
                    }
                    if (line1.equal(AC)) {
                        isACInside = true;
                    }
                    if (line1.equal(BC)) {
                        isBCInside = true;
                    }
                }
                if (!isABInside && !isBCInside && !isACInside) {
                    tempTrianglesCopy.add(tri);
                }
            }
            tempTriangles.clear();
            tempTriangles.addAll(tempTrianglesCopy);
            tempTrianglesCopy.clear();
//            System.out.println();
        }
        buildPolygonLine();
        triangles.addAll(tempTriangles);
        CATTriangle.clear();
        for (Triangle triangle : triangles) {
            if (!triangle.getA().equal(superTriangle.getA()) && !triangle.getA().equal(superTriangle.getB()) && !triangle.getA().equal(superTriangle.getC())
                    && !triangle.getB().equal(superTriangle.getA()) && !triangle.getB().equal(superTriangle.getB()) && !triangle.getB().equal(superTriangle.getC())
                    && !triangle.getC().equal(superTriangle.getA()) && !triangle.getC().equal(superTriangle.getB()) && !triangle.getC().equal(superTriangle.getC())) {
                Point middle_AB = new Point(0.5 * (triangle.getA().getX() + triangle.getB().getX()), 0.5 * (triangle.getA().getY() + triangle.getB().getY()), 0.5 * (triangle.getA().getZ() + triangle.getB().getZ()));
                Point middle_AC = new Point(0.5 * (triangle.getA().getX() + triangle.getC().getX()), 0.5 * (triangle.getA().getY() + triangle.getC().getY()), 0.5 * (triangle.getA().getZ() + triangle.getC().getZ()));
                Point middle_BC = new Point(0.5 * (triangle.getC().getX() + triangle.getB().getX()), 0.5 * (triangle.getC().getY() + triangle.getB().getY()), 0.5 * (triangle.getC().getZ() + triangle.getB().getZ()));

                int count = 0;
                Line AB = new Line(triangle.getA(), triangle.getB());
                count += addLineInBuildCAT(AB, middle_AB);
                Line AC = new Line(triangle.getA(), triangle.getC());
                count += addLineInBuildCAT(AC, middle_AC);
                Line BC = new Line(triangle.getB(), triangle.getC());
                count += addLineInBuildCAT(BC, middle_BC);
                if (count == 3) {
                    triangle.calTriangleType(this);
                    CATTriangle.add(triangle);
                }
//                triangle.calTriangleType(this);
//                CATTriangle.add(triangle);
//                CATLine.add(new Line(triangle.getB(), triangle.getC()));
//                CATLine.add(new Line(triangle.getA(), triangle.getC()));
//                CATLine.add(new Line(triangle.getA(), triangle.getB()));
            }
        }
//        completeCAT();
        //去重
        for (int m = 0; m < CATLine.size() - 1; m++) {
            for (int n = CATLine.size() - 1; n > m; n--) {
                if (CATLine.get(m).equal(CATLine.get(n))) {
                    CATLine.remove(n);
                }
            }
        }
        System.out.println("Point Count: " + polygonPoint.size());
        System.out.println("Inside Segment Count: " + (CATLine.size() - polygonLine.size()));
        System.out.println("CAT Triangle Count: " + CATTriangle.size());

    }

    //构造中轴线过程中所需的扇形，返回扇形的交点
    private Point drawFan(Triangle T) {
        //初始化返回值

        Point crossPoint = T.getA();
        Line triLine[] = {new Line(T.getA(), T.getB()), new Line(T.getA(), T.getC()), new Line(T.getB(), T.getC())};
        Line judgeLine = triLine[0];
        Triangle judgeTriangle = T;
        int outsideCount = 0;
        LinkedList<Point> linkedPoint = new LinkedList<>();
        linkedPoint.add(T.getA());
        linkedPoint.add(T.getB());
        linkedPoint.add(T.getC());
        while (outsideCount < 3) {
            outsideCount = 0;
            for (Line l : triLine) {
                if (!onSegmentLine(l) && !l.equal(judgeLine)) {
                    judgeLine = l;
                    break;
                }
            }
            Point middlePoint = judgeLine.getMiddlePoint();
            double len = middlePoint.distance(judgeLine.getStart());
            if (T.getA().distance(middlePoint) - len > -0.0000000001) {
                outsideCount++;
            }
            if (T.getB().distance(middlePoint) - len > -0.0000000001) {
                outsideCount++;
            }
            if (T.getC().distance(middlePoint) - len > -0.0000000001) {
                outsideCount++;
            }
            for (Line fLine : fanLine) {
                if (fLine.equal(judgeLine)) {
                    fanLine.remove(fLine);
                    break;
                }
            }
            for (Triangle Ftri : fanTriangle) {
                if (Ftri.equal(judgeTriangle)) {
                    fanTriangle.remove(Ftri);
                    break;
                }
            }

            if (outsideCount < 3) {
                for (Triangle tri : CATTriangle) {
                    if (tri.includeLine(judgeLine) && !tri.equal(judgeTriangle)) {
                        judgeTriangle = tri;
                        break;
                    }
                }
                if (judgeTriangle.getTriangleType() == 0) {
                    //去重
                    LinkedList<Point> triPoint = new LinkedList<>();
                    triPoint.add(judgeTriangle.getA());
                    triPoint.add(judgeTriangle.getB());
                    triPoint.add(judgeTriangle.getC());

                    for (int m = 0; m < linkedPoint.size() - 1; m++) {
                        for (int n = linkedPoint.size() - 1; n > m; n--) {
                            if (linkedPoint.get(m).equal(linkedPoint.get(n))) {
                                linkedPoint.remove(n);
                            }
                        }
                    }
                    crossPoint = judgeTriangle.getBarycentre();
                    for(Point p:endPoint){
                        if (judgeTriangle.inTriangle(p)){
                            crossPoint = p;
                            break;
                        }
                    }
                    addFanLine(crossPoint, linkedPoint);
                    fanLine.add(new Line(crossPoint, judgeLine.getStart()));
                    fanLine.add(new Line(crossPoint, judgeLine.getEnd()));
                    triPoint.remove(judgeLine.getStart());
                    triPoint.remove(judgeLine.getEnd());
                    fanLine.add(new Line(crossPoint, triPoint.get(0)));
//                    System.out.println("triPoint count: " + triPoint.size());
                    Triangle Tri = new Triangle(crossPoint, judgeLine.getStart(), triPoint.get(0));
                    Tri.setTriangleType(3);
                    fanTriangle.add(Tri);
//                    fanRegionTriangle.add(Tri);
                    Tri = new Triangle(crossPoint, judgeLine.getEnd(), triPoint.get(0));
                    Tri.setTriangleType(3);
                    fanTriangle.add(Tri);
//                    fanRegionTriangle.add(Tri);
                    break;
                } else if (judgeTriangle.getTriangleType() == 1) {
                    linkedPoint.add(judgeTriangle.getA());
                    linkedPoint.add(judgeTriangle.getB());
                    linkedPoint.add(judgeTriangle.getC());
                    triLine[0] = new Line(judgeTriangle.getA(), judgeTriangle.getB());
                    triLine[1] = new Line(judgeTriangle.getA(), judgeTriangle.getC());
                    triLine[2] = new Line(judgeTriangle.getB(), judgeTriangle.getC());
                } else {
                    linkedPoint.add(judgeTriangle.getA());
                    linkedPoint.add(judgeTriangle.getB());
                    linkedPoint.add(judgeTriangle.getC());
                    for (int m = 0; m < linkedPoint.size() - 1; m++) {
                        for (int n = linkedPoint.size() - 1; n > m; n--) {
                            if (linkedPoint.get(m).equal(linkedPoint.get(n))) {
                                linkedPoint.remove(n);
                            }
                        }
                    }
                    crossPoint = judgeLine.getMiddlePoint();
                    for(Point p:endPoint){
                        if (judgeTriangle.inTriangle(p)){
                            crossPoint = p;
                            break;
                        }
                    }
                    addFanLine(crossPoint, linkedPoint);
                    break;
                }
            } else {
                //去重
                for (int m = 0; m < linkedPoint.size() - 1; m++) {
                    for (int n = linkedPoint.size() - 1; n > m; n--) {
                        if (linkedPoint.get(m).equal(linkedPoint.get(n))) {
                            linkedPoint.remove(n);
                        }
                    }
                }
                crossPoint = judgeLine.getMiddlePoint();
                for(Point p:endPoint){
                    if (judgeTriangle.inTriangle(p)){
                        crossPoint = p;
                        break;
                    }
                }
                addFanLine(crossPoint, linkedPoint);
            }
        }
        //去重
        for (int m = 0; m < fanLine.size() - 1; m++) {
            for (int n = fanLine.size() - 1; n > m; n--) {
                if (fanLine.get(m).equal(fanLine.get(n))) {
                    fanLine.remove(n);
                }
            }
        }
        return crossPoint;
    }

    //添加扇形线
    private void addFanLine(Point crossPoint, LinkedList<Point> linkedPoint) {
        for (int i = 0; i < linkedPoint.size(); i++) {
            fanLine.add(new Line(crossPoint, linkedPoint.get(i)));
            for (int j = 0; j < linkedPoint.size(); j++) {
                Line judge = new Line(linkedPoint.get(i), linkedPoint.get(j));
                if (onSegmentLine(judge)) {
                    Triangle Tri = new Triangle(crossPoint, linkedPoint.get(i), linkedPoint.get(j));
                    Tri.setTriangleType(3);
                    fanTriangle.add(Tri);
                    fanRegionTriangle.add(Tri);
                    fanLine.add(new Line(crossPoint, linkedPoint.get(j)));
                }
            }
        }
        for (int m = 0; m < fanTriangle.size() - 1; m++) {
            for (int n = fanTriangle.size() - 1; n > m; n--) {
                if (fanTriangle.get(m).equal(fanTriangle.get(n))) {
                    fanTriangle.remove(n);
                }
            }
        }
    }

    private boolean judgeAxisLine(Line l) {
        boolean isInFanTri = false;
        for (Triangle tri : fanRegionTriangle) {
            isInFanTri = tri.inTriangle(l.getMiddlePoint());
            if (isInFanTri) {
                break;
            }
        }
        if (!isInFanTri) {
            polygonAxis.add(l);
            return true;
        }
        return false;
//        polygonAxis.add(l);
    }

    //针对每个Triangle画中轴线
    private void drawTriangleAxis(Triangle T) {
        Line triLine[] = {new Line(T.getA(), T.getB()), new Line(T.getA(), T.getC()), new Line(T.getB(), T.getC())};
        if (T.getTriangleType() == 0) {
            Point middlePoint = T.getBarycentre();
            for (Line line : triLine) {
                Line l = new Line(middlePoint, line.getMiddlePoint());

                boolean isAdd = judgeAxisLine(l);
                if (!isAdd) {
                    boolean isFindLine = false;
                    for (Line fanL : fanLine) {
                        isFindLine = fanL.equal(line);
                        if (isFindLine) {
                            break;
                        }
                    }

                    if (isFindLine) {
                        polygonAxis.add(l);
                        isAdd = true;
                    }
                }
                if (isAdd) {
                    fanLine.add(new Line(middlePoint, line.getEnd()));
                    fanLine.add(new Line(middlePoint, line.getStart()));
                    endPoint.add(middlePoint);
                    endPoint.add(line.getMiddlePoint());
                }
//
            }
        } else if (T.getTriangleType() == 2) {
            LinkedList<Point> triPoint = new LinkedList<>();
            for (Line line : triLine) {
                Point middleP = line.getMiddlePoint();
                if (!onSegment(middleP)) {
                    Line l = new Line(T.getBarycentre(), middleP);
                    boolean isAdd = judgeAxisLine(l);
                    if (isAdd) {
                        fanLine.add(new Line(middleP, T.getA()));
                        fanLine.add(new Line(middleP, T.getB()));
                        fanLine.add(new Line(middleP, T.getC()));
                        endPoint.add(middleP);
                    }
                    break;
                }
            }
        } else if (T.getTriangleType() == 1) {
            LinkedList<Line> triLineCopy = new LinkedList<>();
            for (Line line : triLine) {
                if (!onSegmentLine(line)) {
                    triLineCopy.add(line);
                }
            }
            Line l = new Line(triLineCopy.get(0).getMiddlePoint(), triLineCopy.get(1).getMiddlePoint());
            boolean isAdd = judgeAxisLine(l);
            if (isAdd) {
                endPoint.add(triLineCopy.get(0).getMiddlePoint());
                endPoint.add(triLineCopy.get(1).getMiddlePoint());
                LinkedList<Point> lastPoint = new LinkedList<>();
                lastPoint.add(T.getA());
                lastPoint.add(T.getB());
                lastPoint.add(T.getC());
                lastPoint.remove(triLineCopy.get(0).getStart());
                lastPoint.remove(triLineCopy.get(0).getEnd());
                fanLine.add(new Line(lastPoint.get(0), triLineCopy.get(0).getMiddlePoint()));
            }
        }
    }

    //构造中轴线
    public void buildAxis() {
        LinkedList<Triangle> TTriangleList = new LinkedList<>();
        fanTriangle.clear();
//        for (Triangle tri : CATTriangle) {
//            if(tri.getTriangleType()!=0)
//            {fanTriangle.add(tri);}
//        }
        fanTriangle.addAll(CATTriangle);
        fanLine.clear();
        fanLine.addAll(CATLine);
        for (Triangle T : CATTriangle) {
            if (T.getTriangleType() == 2) {
                TTriangleList.add(T);
            }
        }
        for (Triangle T : TTriangleList) {
            endPoint.add(drawFan(T));
        }
        int axisCount = polygonAxis.size();

        //处理特殊情况
        int count = CATLine.size();
        for (Line l : CATLine) {
            if (onSegmentLine(l) || onSegment(l.getMiddlePoint())) {
                count--;
            }
        }

        for (Triangle T : CATTriangle) {
            drawTriangleAxis(T);
        }
        if (axisCount == polygonAxis.size() && endPoint.size() == 2) {
            polygonAxis.add(new Line(endPoint.get(0), endPoint.get(1)));
        }
        //去重

        if (count == 3) {
            System.out.println("Found 3-Line!");
            fanLine.clear();
            fanLine.addAll(CATLine);
            endPoint.clear();
            fanRegionTriangle.clear();
            polygonAxis.clear();
            for (Triangle T : CATTriangle) {
                if (T.getTriangleType() != 2) {
                    drawTriangleAxis(T);
                } else {
                    Line triLine[] = {new Line(T.getA(), T.getB()), new Line(T.getA(), T.getC()), new Line(T.getB(), T.getC())};
                    for (Line l : triLine) {
                        Point middleP = l.getMiddlePoint();
                        if (!onSegment(middleP)) {
                            endPoint.add(middleP);
                            fanLine.add(new Line(middleP, T.getA()));
                            fanLine.add(new Line(middleP, T.getB()));
                            fanLine.add(new Line(middleP, T.getC()));
                        }
                    }
                }
            }
        }

        if (CATTriangle.size() == 3) {
            System.out.println("Found 3-Triangle!");
            fanLine.clear();
            endPoint.clear();
            fanRegionTriangle.clear();
            polygonAxis.clear();
            for (Triangle T : CATTriangle) {
                if (T.getTriangleType() != 2) {
                    Point middlePoint = T.getBarycentre();
                    endPoint.add(middlePoint);
                    for (Point p : polygonPoint) {
                        fanLine.add(new Line(middlePoint, p));
                    }
                }
            }
        }

        if (polygonPoint.size() == 3) {
            endPoint.clear();
            fanRegionTriangle.clear();
            polygonAxis.clear();
            Triangle polyTri = new Triangle(polygonPoint.get(0), polygonPoint.get(1), polygonPoint.get(2));
            endPoint.add(polyTri.getBarycentre());
            fanLine.add(new Line(endPoint.get(0), polygonPoint.get(0)));
            fanLine.add(new Line(endPoint.get(0), polygonPoint.get(1)));
            fanLine.add(new Line(endPoint.get(0), polygonPoint.get(2)));
        }


        for (int m = 0; m < fanLine.size() - 1; m++) {
            for (int n = fanLine.size() - 1; n > m; n--) {
                if (fanLine.get(m).equal(fanLine.get(n))) {
                    fanLine.remove(n);
                }
            }
        }
        for (int m = 0; m < endPoint.size() - 1; m++) {
            for (int n = endPoint.size() - 1; n > m; n--) {
                if (endPoint.get(m).equal(endPoint.get(n))) {
                    endPoint.remove(n);
                }
            }
        }
    }

    private Point calMiddlePoint() {
        double maxX = polygonPoint.get(0).getX();
        double minX = polygonPoint.get(0).getX();
        double maxY = polygonPoint.get(0).getY();
        double minY = polygonPoint.get(0).getY();

        for (Point p : polygonPoint) {
            if (p.getX() > maxX) {
                maxX = p.getX();
            }
            if (p.getX() < minX) {
                minX = p.getX();
            }
            if (p.getY() < minY) {
                minY = p.getY();
            }
            if (p.getY() > maxY) {
                maxY = p.getY();
            }
        }
        return new Point(0.5 * (maxX + minX), 0.5 * (minY + maxY), 0);
    }
}


