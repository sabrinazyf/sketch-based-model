public class Point {
    private double x;
    private double y;
    private double z;

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }


    Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        //System.out.println("Add Point: ("+a+", "+b+", "+c+") Successful.");
    }

    public void setPoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }


    //计算该点与另一点的点积
    public double dotProduct(Point a) {
        return a.getX() * this.x + a.getY() * this.y + a.getZ() * this.z;
    }

    //计算该点与另一点的向量积
    public Point crossProduct(Point a) {
        double x = this.y * a.getZ() - this.z * a.getY();
        double y = -1.0 * (this.x * a.getZ() - this.z * a.getX());
        double z = this.x * a.getY() - this.y * a.getX();
        return new Point(x, y, z);
    }

    //计算向量模长
    private double norm() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    //计算该点与另一点的距离
    public double distance(Point a) {
        return Math.sqrt((this.x - a.getX()) * (this.x - a.getX()) + (this.y - a.getY()) * (this.y - a.getY()) + (this.z - a.getZ()) * (this.z - a.getZ()));
    }

    //两点是否为同一点
    public boolean equal(Point a) {
        return (Math.abs(a.getX() - this.x) <= 0.000000000001 && Math.abs(a.getY() - this.y) <= 0.000000000001 && Math.abs(a.getZ() - this.z) <= 0.000000000001
                || Math.abs(distance(a)) <= 0.000000000001);
    }

    //Z轴对称点
    public Point mirrorPoint(){
        return new Point(this.x,this.y,-1.0*this.z);
    }
}
