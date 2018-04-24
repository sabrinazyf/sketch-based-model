public class Triangle {
    private Point a;
    private Point b;
    private Point c;

    private int indexA;
    private int indexB;
    private int indexC;

    public void setIndex(int a, int b, int c){
        indexA = a;
        indexB = b;
        indexC = c;
    }
    //首先用原模型来跑一边计算法向量，把能计算出正确法向量的ABC给存下来
    //然后在其他时候就可以直接用这个顺序来跑了
    public Point countNormal(){
        Point AB = new Point(a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ());
        Point AC = new Point(a.getX() - c.getX(), a.getY() - c.getY(), a.getZ() - c.getZ());
        Point normal = AB.crossProduct(AC);
//        if (a.getZ() < 0 || b.getZ() < 0 || c.getZ() < 0) {
//            if (normal.getZ() > 0) {
//                AB = new Point(b.getX() - a.getX(), b.getY() - a.getY(), b.getZ() - a.getZ());
//                normal = AB.crossProduct(AC);
//            }
//        } else {
//            if (normal.getZ() < 0) {
//                AB = new Point(b.getX() - a.getX(), b.getY() - a.getY(), b.getZ() - a.getZ());
//                normal = AB.crossProduct(AC);
//            }
//        }
        normal = normal.normalize();
        return normal;
    }

    public boolean isTriangleCorrect(){
        Point AB = new Point(a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ());
        Point AC = new Point(a.getX() - c.getX(), a.getY() - c.getY(), a.getZ() - c.getZ());
        Point normal = AB.crossProduct(AC);
        boolean isCorrect = true;
        if (a.getZ() < 0 || b.getZ() < 0 || c.getZ() < 0) {
            if (normal.getZ() > 0) {
                isCorrect = false;
            }
        } else {
            if (normal.getZ() < 0) {
                isCorrect = false;
            }
        }
        return isCorrect;
    }

    public Point countNormalOld(){
        Point AB = new Point(a.getX() - b.getX(), a.getY() - b.getY(), a.getZ() - b.getZ());
        Point AC = new Point(a.getX() - c.getX(), a.getY() - c.getY(), a.getZ() - c.getZ());
        Point normal = AB.crossProduct(AC);
        if (a.getZ() < 0 || b.getZ() < 0 || c.getZ() < 0) {
            if (normal.getZ() > 0) {
                AB = new Point(b.getX() - a.getX(), b.getY() - a.getY(), b.getZ() - a.getZ());
                normal = AB.crossProduct(AC);
            }
        } else {
            if (normal.getZ() < 0) {
                AB = new Point(b.getX() - a.getX(), b.getY() - a.getY(), b.getZ() - a.getZ());
                normal = AB.crossProduct(AC);
            }
        }
        normal = normal.normalize();
        return normal;
    }

    public int getIndexA() {
        return indexA;
    }

    public int getIndexB() {
        return indexB;
    }

    public int getIndexC() {
        return indexC;
    }

    public void setTriangleType(int triangleType) {
        this.triangleType = triangleType;
    }

    //0:J-Triangle, 1:S-Triangle, 2:T-Triangle
    private int triangleType;

    Triangle(Point a, Point b, Point c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public int getTriangleType() {
        return triangleType;
    }

    public Point getA() {
        return a;
    }

    public Point getB() {
        return b;
    }

    public Point getC() {
        return c;
    }

    private double cross(Point a, Point b, Point p) {
        return (b.getX() - a.getX()) * (p.getY() - a.getY()) - (b.getY() - a.getY()) * (p.getX() - a.getX());
    }

    private boolean toLineLeft(Point a, Point b, Point p) {
        return cross(a, b, p) > 0;
    }

    public boolean inTriangle(Point p) {
        boolean res = toLineLeft(a, b, p);
        if (res != toLineLeft(b, c, p))
            return false;
        if (res != toLineLeft(c, a, p)) {
            return false;
        }
        return true;
    }


    //判断点p是否在三角形T的外接圆右侧，0为在圆内，1为在圆外，2为在右侧
    public int isInCircumCircle(Point p) {
        double x11 = (this.a.getX() * this.a.getX() - this.b.getX() * this.b.getX() + this.a.getY() * this.a.getY() - this.b.getY() * this.b.getY()) * (this.a.getY() - this.c.getY());
        double x12 = (this.a.getX() * this.a.getX() - this.c.getX() * this.c.getX() + this.a.getY() * this.a.getY() - this.c.getY() * this.c.getY()) * (this.a.getY() - this.b.getY());
        double x21 = 2 * (this.a.getY() - this.c.getY()) * (this.a.getX() - this.b.getX());
        double x22 = 2 * (this.a.getY() - this.b.getY()) * (this.a.getX() - this.c.getX());
        double y11 = (this.a.getX() * this.a.getX() - this.b.getX() * this.b.getX() + this.a.getY() * this.a.getY() - this.b.getY() * this.b.getY()) * (this.a.getX() - this.c.getX());
        double y12 = (this.a.getX() * this.a.getX() - this.c.getX() * this.c.getX() + this.a.getY() * this.a.getY() - this.c.getY() * this.c.getY()) * (this.a.getX() - this.b.getX());
        double y21 = 2 * (this.a.getY() - this.b.getY()) * (this.a.getX() - this.c.getX());
        double y22 = 2 * (this.a.getY() - this.c.getY()) * (this.a.getX() - this.b.getX());

        double circumCircleX = (x11 - x12) / (x21 - x22);
        double circumCircleY = (y11 - y12) / (y21 - y22);
        double circumCircleZ = p.getZ();

        double circumCircleLen = this.a.distance(new Point(circumCircleX, circumCircleY, circumCircleZ));
        double pointDistance = p.distance(new Point(circumCircleX, circumCircleY, circumCircleZ));
        if (p.getX() - (circumCircleX + circumCircleLen) > 0.0000000001) {
            return 2;
        }
        if (pointDistance <= circumCircleLen) {
            return 0;
        } else {
            return 1;
        }
    }

    //计算三角形的属性
    //0: J-Triangle，三边都在多边形的内部
    //1: S-Triangle，有一条边在外部
    //2：T-Triangle，有两条边在外部
    public void calTriangleType(Polygon poly) {
        triangleType = 0;
        Point middle_AB = new Point(0.5 * (this.a.getX() + this.b.getX()), 0.5 * (this.a.getY() + this.b.getY()), 0.5 * (this.a.getZ() + this.b.getZ()));
        Point middle_AC = new Point(0.5 * (this.a.getX() + this.c.getX()), 0.5 * (this.a.getY() + this.c.getY()), 0.5 * (this.a.getZ() + this.c.getZ()));
        Point middle_BC = new Point(0.5 * (this.c.getX() + this.b.getX()), 0.5 * (this.c.getY() + this.b.getY()), 0.5 * (this.c.getZ() + this.b.getZ()));
        if (poly.onSegment(middle_AB) || poly.onSegmentLine(new Line(a, b))) {
            triangleType++;
        }
        if (poly.onSegment(middle_AC) || poly.onSegmentLine(new Line(a, c))) {
            triangleType++;
        }
        if (poly.onSegment(middle_BC) || poly.onSegmentLine(new Line(c, b))) {
            triangleType++;
        }
    }

    //求三角形的重心
    public Point getBarycentre() {
        return new Point((a.getX() + b.getX() + c.getX()) / 3.0, (a.getY() + b.getY() + c.getY()) / 3.0, (a.getZ() + b.getZ() + c.getZ()) / 3.0);
    }

    //判断三角形是否有这条边
    public boolean includeLine(Line l) {
        Line triLine[] = {new Line(a, b), new Line(b, c), new Line(a, c)};
        for (Line tri_line : triLine) {
            if (tri_line.equal(l)) {
                return true;
            }
        }
        return false;
    }

    //判断两个三角形是否为同一个
    public boolean equal(Triangle T) {
        return ((T.getA().equal(a) && T.getB().equal(b) && T.getC().equal(c)) || (T.getA().equal(a) && T.getB().equal(c) && T.getC().equal(b))
                || (T.getA().equal(b) && T.getB().equal(a) && T.getC().equal(c)) || (T.getA().equal(b) && T.getB().equal(c) && T.getC().equal(a))
                || (T.getA().equal(c) && T.getB().equal(a) && T.getC().equal(b)) || (T.getA().equal(c) && T.getB().equal(b) && T.getC().equal(a)))
                ;
    }

    //判断两个三角形是否相交
    public boolean intersect(Triangle T) {
        boolean isIntersect = false;
        Line triLineA[] = {new Line(this.getA(), this.getB()), new Line(this.getA(), this.getC()), new Line(this.getB(), this.getC())};
        Line triLineB[] = {new Line(T.getA(), T.getB()), new Line(T.getA(), T.getC()), new Line(T.getB(), T.getC())};
        for (Line lineA : triLineA) {
            for (Line lineB : triLineB) {
                if ((!lineA.equal(lineB)) && lineA.intersectOld(lineB)) {
                    isIntersect = true;
                    break;
                }
            }
        }
        return isIntersect;
    }

    //判断三角形是否有这个点
    public boolean includePoint(Point p){
        return this.a.equal(p)||this.b.equal(p)||this.c.equal(p);
    }
}

