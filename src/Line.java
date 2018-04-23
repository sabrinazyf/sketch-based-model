import java.lang.Double;
import java.math.BigDecimal;

public class Line {

    private Point start;
    private Point end;
    private Triangle lineTriangle;

    Line(Point a, Point b) {
        start = a;
        end = b;
    }

    public Point getStart() {
        return start;
    }

    public Point getEnd() {
        return end;
    }


    public void setStart(Point start) {
        this.start = start;
    }

    public void setEnd(Point end) {
        this.end = end;
    }

    public void setLine(Point start, Point end) {
        this.start = start;
        this.end = end;
    }

    public Triangle getLineTriangle() {
        return lineTriangle;
    }

    public void setLineTriangle(Triangle lineTriangle) {
        this.lineTriangle = lineTriangle;
    }

    //两线段是否是同一条线段（不含方向）
    public boolean equal(Line a) {
        return ((Math.abs(a.getStart().getX() - this.start.getX()) <= 0.00001 && Math.abs(a.getStart().getY() - this.start.getY()) <= 0.00001
                && Math.abs(a.getEnd().getY() - this.end.getY()) <= 0.00001 && Math.abs(a.getEnd().getX() - this.end.getX()) <= 0.00001)
                || (Math.abs(a.getStart().getX() - this.end.getX()) <= 0.00001 && Math.abs(a.getStart().getY() - this.end.getY()) <= 0.00001
                && Math.abs(a.getEnd().getX() - this.start.getX()) <= 0.00001 && Math.abs(a.getEnd().getY() - this.start.getY()) <= 0.00001));
    }

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

    //判断z轴相同的两条线段是否相交，true为相交，false为不相交
    public boolean intersectOld(Line l) {
        Point a = l.getStart();
        Point b = l.getEnd();
        Point c = this.start;
        Point d = this.end;
//        if(a.equal(c)||a.equal(d)||b.equal(c)||b.equal(d)) {
//            return false;
//        }
        if (!(min(a.getX(), b.getX()) <= max(c.getX(), d.getX()) && min(c.getY(), d.getY()) <= max(a.getY(), b.getY())
                && min(c.getX(), d.getX()) <= max(a.getX(), b.getX()) && min(a.getY(), b.getY()) <= max(c.getY(), d.getY()))) {
            return false;
        }
        double u, v, w, z;//分别记录两个向量
        u = (c.getX() - a.getX()) * (b.getY() - a.getY()) - (b.getX() - a.getX()) * (c.getY() - a.getY());
        v = (d.getX() - a.getX()) * (b.getY() - a.getY()) - (b.getX() - a.getX()) * (d.getY() - a.getY());
        w = (a.getX() - c.getX()) * (d.getY() - c.getY()) - (d.getX() - c.getX()) * (a.getY() - c.getY());
        z = (b.getX() - c.getX()) * (d.getY() - c.getY()) - (d.getX() - c.getX()) * (b.getY() - c.getY());
        Double uv = u * v;
        Double wz = w * z;
        return (uv < Double.MIN_VALUE && wz < Double.MIN_VALUE);
//        return (uv.equals(0.0) && wz.equals(0.0));
    }

    private double mult(Point a, Point b, Point c) {
        return (a.getX() - c.getX()) * (b.getY() - c.getY()) - (b.getX() - c.getX()) * (a.getY() - c.getY());
    }

    public boolean intersect2D(Line l) {
        Point aa = l.getStart();
        Point bb = l.getEnd();
        Point cc = this.start;
        Point dd = this.end;
        if (max(aa.getX(), bb.getX()) < min(cc.getX(), dd.getX())) {
            return false;
        }
        if (max(aa.getY(), bb.getY()) < min(cc.getY(), dd.getY())) {
            return false;
        }
        if (max(cc.getX(), dd.getX()) < min(aa.getX(), bb.getX())) {
            return false;
        }
        if (max(cc.getY(), dd.getY()) < min(aa.getY(), bb.getY())) {
            return false;
        }
        if (mult(cc, bb, aa) * mult(bb, dd, aa) < 0) {
            return false;
        }
        if (mult(aa, dd, cc) * mult(dd, bb, cc) < 0) {
            return false;
        }
        return true;
    }


    //判断两条线段是否平行
    //x1/x2=y1/y2=z1/z2
    public boolean parallel(Line l) {
//        Point direct1 = new Point(this.start.getX() - this.end.getX(), this.start.getY() - this.end.getY(), this.start.getZ() - this.end.getZ());
        Point direct2 = new Point(l.getStart().getX() - l.getEnd().getX(), l.getStart().getY() - l.getEnd().getY(), l.getStart().getZ() - l.getEnd().getZ());
        BigDecimal direct1X = new BigDecimal(Double.toString(this.start.getX())).subtract(new BigDecimal(Double.toString(this.end.getX())));
        BigDecimal direct1Y = new BigDecimal(Double.toString(this.start.getY())).subtract(new BigDecimal(Double.toString(this.end.getY())));
        BigDecimal direct2X = new BigDecimal(Double.toString(l.getStart().getX())).subtract(new BigDecimal(Double.toString(l.getEnd().getX())));
        BigDecimal direct2Y = new BigDecimal(Double.toString(l.getStart().getY())).subtract(new BigDecimal(Double.toString(l.getEnd().getY())));

//        return (Math.abs(direct1.getX() * direct2.getY() - direct1.getY() * direct2.getX()) <= 1E-32
//                && Math.abs(direct1.getZ() * direct2.getY() - direct1.getY() * direct2.getZ()) <= 1E-32);
        return (direct1X.multiply(direct2Y)).equals(direct1Y.multiply(direct2X));
    }

    //返回线段长度
    public double length() {
        return start.distance(end);
    }

    //获取线段中点
    public Point getMiddlePoint() {
        return new Point(0.5 * (start.getX() + end.getX()), 0.5 * (start.getY() + end.getY()), 0.5 * (start.getZ() + end.getZ()));
    }

    public boolean haveSamePoint(Line l) {
        return true;
    }
}

