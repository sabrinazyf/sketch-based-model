import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.common.nio.Buffers;

import java.awt.event.*;
import java.nio.FloatBuffer;
import java.util.LinkedList;

import static com.jogamp.opengl.GL2.*; // GL2 constants

@SuppressWarnings("serial")
public class DrawPolygonRenderer extends GLCanvas implements GLEventListener, MouseListener, KeyListener, MouseMotionListener, MouseWheelListener {
    private static final int SHOW_OUTSIDELINE_MODE = 0;
    private static final int SHOW_CATLINE_MODE = 1;
    private static final int SHOW_FANLINE_MODE = 2;
    private static final int SHOW_AXIS_MODE = 3;
    private static final int SHOW_FANRIGION_MODE = 4;
    private static final int SHOW_LIFT_MODE = 5;
    private static final int SHOW_TRIANGLE_MODE = 6;
    private static final int SHOW_MODEL_MODE = 7;
    private static final int DRAW_MODE = -1;
    private static final int CUT_MODE = -2;
    private static final int CUT_SELECT_MODE = -3;
    private float rotateX;
    private float rotateY;
    private float middlePivot[] = {0f, 0f};


    private int modelCount = 0;
    private LinkedList<Model> modelList = new LinkedList<>();
    private int modelColor[] = new int[0];
    private int thisModel = 0;

    private GLU glu;
    //    private Polygon renderPolygon = new Polygon();
    private Model renderModel = new Model();
    private Model lastModel = new Model();

    private boolean canUndo = false;
    private int renderMode;

    private boolean mouseLeftDown;
    private int mouseX, mouseY;
    private int lastMouseX, lastMouseY;
    private GL2 gl;
    private LinkedList<Point> pointList;

    //设定显示模式
    public void setRenderMode(int mode) {
        renderMode = mode;
    }

    //获取显示模式
    public int getRenderMode() {
        return renderMode;
    }

    //初始化函数
    public DrawPolygonRenderer() {
        renderMode = DRAW_MODE;
        this.addGLEventListener(this);
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        mouseLeftDown = false;
        pointList = new LinkedList<>();
        gl = drawable.getGL().getGL2();
        glu = new GLU();
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LEQUAL);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glShadeModel(GL_SMOOTH);
        gl.glEnable(GL_COLOR_MATERIAL);

//        tGLCapabilities.setSampleBuffers(true);
//        tGLCapabilities.setNumSamples(getNumberOfSamplingBuffers());
//        gl.glEnable(GL2.GL_LINE_SMOOTH);
//        gl.glEnable(GL2.GL_POLYGON_SMOOTH);
        gl.glHint(GL2.GL_POLYGON_SMOOTH_HINT, GL2.GL_NICEST);
//        gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_DONT_CARE);
//        gl.glEnable(GL_BLEND);
//        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

        if (height == 0) height = 1;
        System.out.println(width + ", " + height);

        // Set the view port (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
//        glu.gluPerspective(60.0, aspect, 1.0, 10000.0);
        gl.glOrtho(-1.5, 1.5, -1.5, 1.5, -10, 10);
        // Enable the model-view transform
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();//reset
    }


    @Override
    public void display(GLAutoDrawable drawable) {
        gl = drawable.getGL().getGL2();
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//        gl.glShadeModel(GL_SMOOTH);
        gl.glLoadIdentity(); // reset the model-view matrix
        selectMode(gl);

    }

    public void smoothModel() {
        for (int i = 0; i < modelCount; i++) {
            for (int j = 0; j < 3; j++) {
                modelList.get(i).smooth(1, 0);
            }
        }
    }

    //具体的选择模式
    private void selectMode(GL2 gl) {
        if (renderMode == SHOW_MODEL_MODE) {
            gl.glTranslatef(0.0f, 0.0f, -1.0f);
        } else {
            gl.glTranslatef(0.0f, 0.0f, -1.0f);
            rotateX = 0.0f;
            rotateY = 0.0f;
        }
        if (modelCount == 0) {
            if (renderMode == DRAW_MODE) {
                drawLine(gl, 0, 0, 0);
            }
        }
        gl.glPushMatrix();
        if (renderMode == SHOW_MODEL_MODE) {
//            float pivot[] = new float[2];
//            for (int modelI = 0; modelI < modelCount; modelI++) {
//                pivot[0] += (float) modelList.get(modelI).getBasedPolygon().getMiddlePoint().getX();
//                pivot[1] += (float) modelList.get(modelI).getBasedPolygon().getMiddlePoint().getY();
//            }
//            pivot[0] /= modelCount;
//            pivot[1] /= modelCount;
//            System.out.println("old: " + (float) modelList.get(0).getBasedPolygon().getMiddlePoint().getX() + ", " + (float) modelList.get(0).getBasedPolygon().getMiddlePoint().getY());
//            System.out.println("new: " + pivot[0] + ", " + pivot[1]);
//            System.out.println("now: " + (float) modelList.get(modelCount - 1).getBasedPolygon().getMiddlePoint().getX() + ", " + (float) modelList.get(modelCount - 1).getBasedPolygon().getMiddlePoint().getY());
            gl.glTranslatef(middlePivot[0], middlePivot[1], 0.0f);
            gl.glRotatef(rotateX, middlePivot[0], 0.0f, 0.0f);
//            gl.glRotatef(rotateX, 0.0f, 0.0f, pivot[2]);
            gl.glRotatef(rotateY, 0.0f, middlePivot[1], 0.0f);
            gl.glTranslatef(-1 * middlePivot[0], -1 * middlePivot[1], 0.0f);
        }
        for (int modelI = 0; modelI < modelCount; modelI++) {
            Polygon renderPolygon = modelList.get(modelI).getBasedPolygon();

            // 绘制原多边形
            if (renderMode >= 0 && renderMode != SHOW_CATLINE_MODE && renderMode != SHOW_MODEL_MODE) {
                drawOutLine(gl, renderPolygon);
            }
            select2DMode(gl, renderPolygon);
            select3DMode(gl, renderPolygon, modelI);

            if (renderMode == SHOW_MODEL_MODE || renderMode == CUT_MODE || renderMode == CUT_SELECT_MODE || (renderMode == DRAW_MODE && modelCount > 0)) {
                gl.glEnable(GL_MULTISAMPLE);
                gl.glEnable(GL2.GL_LIGHTING);
                gl.glEnable(GL2.GL_LIGHT0);
//            gl.glEnable( GL2.GL_NORMALIZE );

                // weak RED ambient
                float[] ambientLight = {0f, 0.f, 0.f, 0f};
                gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, ambientLight, 0);

                // multicolor diffuse
                float[] diffuseLight = {1f, 1f, 1f, 0f};
                gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, diffuseLight, 0);

                float specular[] = {1.0f, 1.0f, 1.0f, 1.0f};
                gl.glLightfv(GL_LIGHT0, GL_SPECULAR, specular, 0);

                float specref[] = {1.0f, 1.0f, 1.0f, 1.0f};
                // 设置多边形正面的镜面反射属性
                gl.glMaterialfv(GL_FRONT, GL_SPECULAR, specref, 0);
                // 指定镜面指数
                gl.glMateriali(GL_FRONT, GL_SHININESS, 128);

                FloatBuffer triCoordBuffer = Buffers.newDirectFloatBuffer(modelList.get(modelI).getTriCoords());
                FloatBuffer normalCoordBuffer = Buffers.newDirectFloatBuffer(modelList.get(modelI).getNormalCoords());

                if (renderMode == CUT_SELECT_MODE) {
                    triCoordBuffer = Buffers.newDirectFloatBuffer(modelList.get(modelI).getRightTriCoords());
                    normalCoordBuffer = Buffers.newDirectFloatBuffer(modelList.get(modelI).getRightTriNormal());
                }

                gl.glVertexPointer(3, GL2.GL_FLOAT, 0, triCoordBuffer);  // Set data type and location, first Tri.
                gl.glNormalPointer(GL2.GL_FLOAT, 0, normalCoordBuffer);
                gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
                gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
                gl.glColor3f(131 / 255.0f, 205 / 255.0f, 1.0f);
                int size = modelList.get(modelI).getTriCoords().length / 3;
                if (renderMode == CUT_SELECT_MODE) {
                    size = modelList.get(modelI).getRightTriCoords().length / 3;
                }
                gl.glDrawArrays(GL2.GL_TRIANGLES, 0, size); // Draw the first cube!
                gl.glDisable(GL2.GL_LIGHTING);
                gl.glDisable(GL2.GL_LIGHT0);
                gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
                gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
            }

            if (renderMode == CUT_MODE) {
                gl.glPopMatrix();
                double pivot[] = {modelList.get(modelI).getBasedPolygon().getMiddlePoint().getX(), modelList.get(modelI).getBasedPolygon().getMiddlePoint().getY()};
                drawLine(gl, pivot[0], pivot[1], 0);
                drawOutLine(gl, renderPolygon);
            }
            if (renderMode == CUT_SELECT_MODE) {
                gl.glColor3f(1.0f, 0.0f, 0.0f);
                for (int i = 0; i < modelList.get(modelI).getRightPolygon().getPolygonPoint().size() - 1; i++) {
                    gl.glEnable(GL_LINE_SMOOTH);
                    gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
                    gl.glLineWidth(8.0f);
                    gl.glBegin(GL2.GL_LINES);
                    gl.glVertex3f((float) modelList.get(modelI).getRightPolygon().getPolygonPoint().get(i).getX(), (float) modelList.get(modelI).getRightPolygon().getPolygonPoint().get(i).getY(), (float) modelList.get(modelI).getBiggestZ());
                    gl.glVertex3f((float) modelList.get(modelI).getRightPolygon().getPolygonPoint().get(i + 1).getX(), (float) modelList.get(modelI).getRightPolygon().getPolygonPoint().get(i + 1).getY(), (float) modelList.get(modelI).getBiggestZ());
                    gl.glEnd();
                }
                gl.glEnable(GL_LINE_SMOOTH);
                gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
                gl.glLineWidth(8.0f);
                gl.glBegin(GL2.GL_LINES);
                gl.glVertex3f((float) modelList.get(modelI).getRightPolygon().getPolygonPoint().getLast().getX(), (float) modelList.get(modelI).getRightPolygon().getPolygonPoint().getLast().getY(), (float) modelList.get(modelI).getBiggestZ());
                gl.glVertex3f((float) modelList.get(modelI).getRightPolygon().getPolygonPoint().getFirst().getX(), (float) modelList.get(modelI).getRightPolygon().getPolygonPoint().getFirst().getY(), (float) modelList.get(modelI).getBiggestZ());
                gl.glEnd();
            }

            if (renderMode == DRAW_MODE) {
                drawLine(gl, 0, 0, 0);
            }
        }
    }

    //模式大于0时，2D情况下的选择模式
    private void select2DMode(GL2 gl, Polygon renderPolygon) {
        // 绘制CAT
        if (renderMode == SHOW_CATLINE_MODE) {

            for (int i = 0; i < renderPolygon.getCATTriangle().size(); i++) {
                gl.glColor3f(0.0f, 0.0f, 0.0f);
                gl.glEnable(GL_LINE_SMOOTH);
                gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
                gl.glLineWidth(4.0f);
                gl.glBegin(GL2.GL_LINES);
                gl.glVertex3f((float) renderPolygon.getCATTriangle().get(i).getA().getX(), (float) renderPolygon.getCATTriangle().get(i).getA().getY(), (float) renderPolygon.getCATTriangle().get(i).getA().getZ());
                gl.glVertex3f((float) renderPolygon.getCATTriangle().get(i).getB().getX(), (float) renderPolygon.getCATTriangle().get(i).getB().getY(), (float) renderPolygon.getCATTriangle().get(i).getB().getZ());
                gl.glEnd();

                gl.glColor3f(0.0f, 0.0f, 0.0f);
                gl.glEnable(GL_LINE_SMOOTH);
                gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
                gl.glLineWidth(4.0f);
                gl.glBegin(GL2.GL_LINES);
                gl.glVertex3f((float) renderPolygon.getCATTriangle().get(i).getA().getX(), (float) renderPolygon.getCATTriangle().get(i).getA().getY(), (float) renderPolygon.getCATTriangle().get(i).getA().getZ());
                gl.glVertex3f((float) renderPolygon.getCATTriangle().get(i).getC().getX(), (float) renderPolygon.getCATTriangle().get(i).getC().getY(), (float) renderPolygon.getCATTriangle().get(i).getC().getZ());
                gl.glEnd();

                gl.glColor3f(0.0f, 0.0f, 0.0f);
                gl.glEnable(GL_LINE_SMOOTH);
                gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
                gl.glLineWidth(4.0f);
                gl.glBegin(GL2.GL_LINES);
                gl.glVertex3f((float) renderPolygon.getCATTriangle().get(i).getC().getX(), (float) renderPolygon.getCATTriangle().get(i).getC().getY(), (float) renderPolygon.getCATTriangle().get(i).getC().getZ());
                gl.glVertex3f((float) renderPolygon.getCATTriangle().get(i).getB().getX(), (float) renderPolygon.getCATTriangle().get(i).getB().getY(), (float) renderPolygon.getCATTriangle().get(i).getB().getZ());
                gl.glEnd();
//                gl.glDisable(GL_LINE_STIPPLE);
            }

        }

        if (renderMode == SHOW_FANLINE_MODE || renderMode == SHOW_AXIS_MODE || renderMode == SHOW_FANRIGION_MODE) {
            for (int i = 0; i < renderPolygon.getFanLine().size(); i++) {
                gl.glEnable(GL_LINE_SMOOTH);
                gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
                gl.glLineWidth(4.0f);

                gl.glBegin(GL2.GL_LINES);
                gl.glColor3f(0.0f, 0.0f, 1.0f);
                gl.glVertex3f((float) renderPolygon.getFanLine().get(i).getStart().getX(), (float) renderPolygon.getFanLine().get(i).getStart().getY(), (float) renderPolygon.getFanLine().get(i).getStart().getZ());
                gl.glVertex3f((float) renderPolygon.getFanLine().get(i).getEnd().getX(), (float) renderPolygon.getFanLine().get(i).getEnd().getY(), (float) renderPolygon.getFanLine().get(i).getEnd().getZ());
                gl.glEnd();
            }
            if (renderMode == SHOW_AXIS_MODE || renderMode == SHOW_FANRIGION_MODE) {
                for (int i = 0; i < renderPolygon.getPolygonAxis().size(); i++) {
                    gl.glEnable(GL_LINE_STIPPLE);
                    gl.glEnable(GL_LINE_SMOOTH);
                    gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
                    gl.glLineWidth(5.0f);
                    gl.glLineStipple(2, (short) 0x33FF);

                    gl.glBegin(GL2.GL_LINES);
                    gl.glColor3f(1.0f, 0.0f, 0.0f);
                    gl.glVertex3f((float) renderPolygon.getPolygonAxis().get(i).getStart().getX(), (float) renderPolygon.getPolygonAxis().get(i).getStart().getY(), (float) renderPolygon.getPolygonAxis().get(i).getStart().getZ());
                    gl.glVertex3f((float) renderPolygon.getPolygonAxis().get(i).getEnd().getX(), (float) renderPolygon.getPolygonAxis().get(i).getEnd().getY(), (float) renderPolygon.getPolygonAxis().get(i).getEnd().getZ());
                    gl.glEnd();
                    gl.glDisable(GL_LINE_STIPPLE);
                }
                for (Point p : renderPolygon.getEndPoint()) {
                    gl.glColor3f(0.0f, 1.0f, 1.0f);
                    gl.glPointSize(10.0f);//在绘制之前要设置要相关参数，这里设置点的大小为5像素
                    gl.glBegin(GL_POINTS);
                    gl.glVertex3f((float) p.getX(), (float) p.getY(), (float) p.getZ()); //OpenGl内的点是齐次坐标的四元组，缺省的z坐标为0.0f，w为1.0f，所以该点为(1, 2, 0, 1)
                    gl.glEnd();
                }
                if (renderMode == SHOW_FANRIGION_MODE) {
                    for (int i = 0; i < renderPolygon.getFanRegionTriangle().size(); i++) {
                        gl.glColor3f(0.0f, 1.0f, 0.0f);
                        gl.glEnable(GL_LINE_SMOOTH);
                        gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
                        gl.glLineWidth(3.0f);
                        gl.glBegin(GL2.GL_LINES);
                        gl.glVertex3f((float) renderPolygon.getFanRegionTriangle().get(i).getA().getX(), (float) renderPolygon.getFanRegionTriangle().get(i).getA().getY(), (float) renderPolygon.getFanRegionTriangle().get(i).getA().getZ());
                        gl.glVertex3f((float) renderPolygon.getFanRegionTriangle().get(i).getB().getX(), (float) renderPolygon.getFanRegionTriangle().get(i).getB().getY(), (float) renderPolygon.getFanRegionTriangle().get(i).getB().getZ());
                        gl.glEnd();

                        gl.glColor3f(0.0f, 1.0f, 0.0f);
                        gl.glEnable(GL_LINE_SMOOTH);
                        gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
                        gl.glLineWidth(3.0f);
                        gl.glBegin(GL2.GL_LINES);
                        gl.glVertex3f((float) renderPolygon.getFanRegionTriangle().get(i).getA().getX(), (float) renderPolygon.getFanRegionTriangle().get(i).getA().getY(), (float) renderPolygon.getFanRegionTriangle().get(i).getA().getZ());
                        gl.glVertex3f((float) renderPolygon.getFanRegionTriangle().get(i).getC().getX(), (float) renderPolygon.getFanRegionTriangle().get(i).getC().getY(), (float) renderPolygon.getFanRegionTriangle().get(i).getC().getZ());
                        gl.glEnd();

                        gl.glColor3f(0.0f, 1.0f, 0.0f);
                        gl.glEnable(GL_LINE_SMOOTH);
                        gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
                        gl.glLineWidth(3.0f);
                        gl.glBegin(GL2.GL_LINES);
                        gl.glVertex3f((float) renderPolygon.getFanRegionTriangle().get(i).getC().getX(), (float) renderPolygon.getFanRegionTriangle().get(i).getC().getY(), (float) renderPolygon.getFanRegionTriangle().get(i).getC().getZ());
                        gl.glVertex3f((float) renderPolygon.getFanRegionTriangle().get(i).getB().getX(), (float) renderPolygon.getFanRegionTriangle().get(i).getB().getY(), (float) renderPolygon.getFanRegionTriangle().get(i).getB().getZ());
                        gl.glEnd();
//                gl.glDisable(GL_LINE_STIPPLE);
                    }
                }
            }

        }
    }

    //模式大于0时，3D情况下的选择模式
    private void select3DMode(GL2 gl, Polygon renderPolygon, int modelI) {
        if (renderMode == SHOW_TRIANGLE_MODE || renderMode == SHOW_LIFT_MODE) {
            for (int i = 0; i < modelList.get(modelI).getModelTriangle().size(); i++) {
                if (renderMode != SHOW_LIFT_MODE) {
                    selectColor(i, gl);
                    gl.glBegin(GL2.GL_TRIANGLES);
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangle().get(i).getA().getX(), (float) modelList.get(modelI).getModelTriangle().get(i).getA().getY(), (float) modelList.get(modelI).getModelTriangle().get(i).getA().getZ());
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangle().get(i).getB().getX(), (float) modelList.get(modelI).getModelTriangle().get(i).getB().getY(), (float) modelList.get(modelI).getModelTriangle().get(i).getB().getZ());
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangle().get(i).getC().getX(), (float) modelList.get(modelI).getModelTriangle().get(i).getC().getY(), (float) modelList.get(modelI).getModelTriangle().get(i).getC().getZ());
                    gl.glEnd();
                }
                if (renderMode != SHOW_MODEL_MODE) {
                    gl.glColor3f(0.0f, 0.0f, 0.0f);
                    gl.glEnable(GL_LINE_SMOOTH);
                    gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
                    gl.glLineWidth(2.0f);
                    gl.glBegin(GL2.GL_LINES);
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangle().get(i).getA().getX(), (float) modelList.get(modelI).getModelTriangle().get(i).getA().getY(), (float) modelList.get(modelI).getModelTriangle().get(i).getA().getZ());
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangle().get(i).getB().getX(), (float) modelList.get(modelI).getModelTriangle().get(i).getB().getY(), (float) modelList.get(modelI).getModelTriangle().get(i).getB().getZ());
                    gl.glEnd();

                    gl.glColor3f(0.0f, 0.0f, 0.0f);
                    gl.glEnable(GL_LINE_SMOOTH);
                    gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
                    gl.glLineWidth(2.0f);
                    gl.glBegin(GL2.GL_LINES);
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangle().get(i).getC().getX(), (float) modelList.get(modelI).getModelTriangle().get(i).getC().getY(), (float) modelList.get(modelI).getModelTriangle().get(i).getC().getZ());
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangle().get(i).getA().getX(), (float) modelList.get(modelI).getModelTriangle().get(i).getA().getY(), (float) modelList.get(modelI).getModelTriangle().get(i).getA().getZ());
                    gl.glEnd();

                    gl.glColor3f(0.0f, 0.0f, 0.0f);
                    gl.glEnable(GL_LINE_SMOOTH);
                    gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
                    gl.glLineWidth(2.0f);
                    gl.glBegin(GL2.GL_LINES);
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangle().get(i).getC().getX(), (float) modelList.get(modelI).getModelTriangle().get(i).getC().getY(), (float) modelList.get(modelI).getModelTriangle().get(i).getC().getZ());
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangle().get(i).getB().getX(), (float) modelList.get(modelI).getModelTriangle().get(i).getB().getY(), (float) modelList.get(modelI).getModelTriangle().get(i).getB().getZ());
                    gl.glEnd();

                    gl.glColor3f(1.0f, 1.0f, 0.0f);
                    gl.glPointSize(5.0f);//在绘制之前要设置要相关参数，这里设置点的大小为5像素
                    gl.glBegin(GL_POINTS);
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangle().get(i).getA().getX(), (float) modelList.get(modelI).getModelTriangle().get(i).getA().getY(), (float) modelList.get(modelI).getModelTriangle().get(i).getA().getZ());
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangle().get(i).getB().getX(), (float) modelList.get(modelI).getModelTriangle().get(i).getB().getY(), (float) modelList.get(modelI).getModelTriangle().get(i).getB().getZ());
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangle().get(i).getC().getX(), (float) modelList.get(modelI).getModelTriangle().get(i).getC().getY(), (float) modelList.get(modelI).getModelTriangle().get(i).getC().getZ());
                    gl.glEnd();
                }
            }
            for (int i = 0; i < modelList.get(modelI).getModelTriangleLink().size(); i++) {
                if (renderMode != SHOW_LIFT_MODE) {
                    selectColor(i, gl);
                    gl.glBegin(GL2.GL_TRIANGLES);
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangleLink().get(i).getA().getX(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getA().getY(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getA().getZ());
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangleLink().get(i).getB().getX(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getB().getY(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getB().getZ());
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangleLink().get(i).getC().getX(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getC().getY(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getC().getZ());
                    gl.glEnd();
                }

                if (renderMode != SHOW_MODEL_MODE) {
                    gl.glColor3f(0.0f, 0.0f, 0.0f);
                    gl.glEnable(GL_LINE_SMOOTH);
                    gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
                    gl.glLineWidth(2.0f);
                    gl.glBegin(GL2.GL_LINES);
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangleLink().get(i).getA().getX(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getA().getY(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getA().getZ());
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangleLink().get(i).getB().getX(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getB().getY(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getB().getZ());
                    gl.glEnd();

                    gl.glColor3f(0.0f, 0.0f, 0.0f);
                    gl.glEnable(GL_LINE_SMOOTH);
                    gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
                    gl.glLineWidth(2.0f);
                    gl.glBegin(GL2.GL_LINES);
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangleLink().get(i).getC().getX(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getC().getY(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getC().getZ());
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangleLink().get(i).getA().getX(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getA().getY(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getA().getZ());
                    gl.glEnd();

                    gl.glColor3f(0.0f, 0.0f, 0.0f);
                    gl.glEnable(GL_LINE_SMOOTH);
                    gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
                    gl.glLineWidth(2.0f);
                    gl.glBegin(GL2.GL_LINES);
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangleLink().get(i).getC().getX(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getC().getY(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getC().getZ());
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangleLink().get(i).getB().getX(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getB().getY(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getB().getZ());
                    gl.glEnd();

                    gl.glColor3f(1.0f, 1.0f, 0.0f);
                    gl.glPointSize(5.0f);//在绘制之前要设置要相关参数，这里设置点的大小为5像素
                    gl.glBegin(GL_POINTS);
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangleLink().get(i).getA().getX(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getA().getY(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getA().getZ());
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangleLink().get(i).getB().getX(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getB().getY(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getB().getZ());
                    gl.glVertex3f((float) modelList.get(modelI).getModelTriangleLink().get(i).getC().getX(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getC().getY(), (float) modelList.get(modelI).getModelTriangleLink().get(i).getC().getZ());
                    gl.glEnd();
                }
            }

        }

        if (renderMode >= 0 && renderMode != SHOW_MODEL_MODE) {
            for (Point p : renderPolygon.getPolygonPoint()) {
                gl.glColor3f(1.0f, 1.0f, 0.0f);
                gl.glPointSize(10.0f);//在绘制之前要设置要相关参数
                gl.glBegin(GL_POINTS);
                gl.glVertex3f((float) p.getX(), (float) p.getY(), (float) p.getZ());
                gl.glEnd();
            }
            Point p = renderPolygon.getPolygonPoint().getFirst();
            gl.glColor3f(1.0f, 0.0f, 1.0f);
            gl.glPointSize(15.0f);//在绘制之前要设置要相关参数，这里设置点的大小为5像素
            gl.glBegin(GL_POINTS);
            gl.glVertex3f((float) p.getX(), (float) p.getY(), (float) p.getZ()); //OpenGl内的点是齐次坐标的四元组，缺省的z坐标为0.0f，w为1.0f
            gl.glEnd();
            p = renderPolygon.getPolygonPoint().getLast();
            gl.glColor3f(1.0f, 0.0f, 0.0f);
            gl.glPointSize(10.0f);//在绘制之前要设置要相关参数，这里设置点的大小为5像素
            gl.glBegin(GL_POINTS);
            gl.glVertex3f((float) p.getX(), (float) p.getY(), (float) p.getZ()); //OpenGl内的点是齐次坐标的四元组，缺省的z坐标为0.0f，w为1.0f
            gl.glEnd();
        }
    }

    //用旧方法绘制模型边框
    private void drawOutLine(GL2 gl, Polygon renderPolygon) {
        for (int i = 0; i < renderPolygon.getPolygonLine().size(); i++) {
            gl.glColor3f(0.0f, 0.0f, 0.0f);
            if (renderMode == CUT_MODE) {
                gl.glColor3f(1.0f, 0.0f, 0.0f);
            }
            gl.glEnable(GL_LINE_SMOOTH);
            gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
            gl.glLineWidth(8.0f);
            gl.glBegin(GL2.GL_LINES);
            gl.glVertex3f((float) renderPolygon.getPolygonLine().get(i).getStart().getX(), (float) renderPolygon.getPolygonLine().get(i).getStart().getY(), (float) renderPolygon.getPolygonLine().get(i).getStart().getZ());
            gl.glVertex3f((float) renderPolygon.getPolygonLine().get(i).getEnd().getX(), (float) renderPolygon.getPolygonLine().get(i).getEnd().getY(), (float) renderPolygon.getPolygonLine().get(i).getEnd().getZ());
            gl.glEnd();
//                gl.glFlush();
        }
    }

    //绘图模式中绘出最后一步
    private void drawLastLine(GL2 gl) {
        int lastIndex = pointList.size();
        for (int i = pointList.size() - 1; i > 2; i--) {
            Line judgeLine = new Line(pointList.get(i), pointList.getFirst());
            boolean isIntersect = false;
            for (int j = 1; j < i - 2; j++) {
                isIntersect = judgeLine.intersectOld(new Line(pointList.get(j), pointList.get(j + 1)));
                if (isIntersect) {
//                    System.out.println(true+" "+i+" "+j);
                    break;
                }
            }
            if (!isIntersect) {
                lastIndex = i;
//                System.out.println(i);
                break;
            }
        }
        LinkedList<Point> pointListCopy = new LinkedList<>();
        for (int j = 0; j <= lastIndex; j++) {
            pointListCopy.add(pointList.get(j));
        }

        double heightz = 0;
        for (int i = 0; i < modelCount; i++) {
            if (heightz <= modelList.get(i).getBiggestZ()) {
                heightz = modelList.get(i).getBiggestZ() * 10;
            }
        }

        pointList.clear();
        pointList.addAll(pointListCopy);
        drawSketchLine(gl);
        gl.glEnable(GL_BLEND);
        gl.glEnable(GL_LINE_SMOOTH);
        gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glLineWidth(5.0f);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex3f((float) pointList.getFirst().getX(), (float) pointList.getFirst().getY(), (float) heightz);
        gl.glVertex3f((float) pointList.get(lastIndex - 1).getX(), (float) pointList.get(lastIndex - 1).getY(), (float) heightz);
        gl.glEnd();
    }

    //画边框
    private void drawSketchLine(GL2 gl) {
        double heightz = 0;
        for (int i = 0; i < modelCount; i++) {
            if (heightz <= modelList.get(i).getBiggestZ()) {
                heightz = modelList.get(i).getBiggestZ() * 10;
            }
        }
        for (int i = 0; i < pointList.size() - 2; i++) {
            gl.glEnable(GL_BLEND);
            gl.glEnable(GL_LINE_SMOOTH);
            gl.glHint(GL_LINE_SMOOTH_HINT, GL_FASTEST);
            gl.glColor3f(1.0f, 0.0f, 0.0f);
            gl.glLineWidth(5.0f);
            gl.glBegin(GL2.GL_LINES);
            gl.glVertex3f((float) pointList.get(i).getX(), (float) pointList.get(i).getY(), (float) heightz);
            gl.glVertex3f((float) pointList.get(i + 1).getX(), (float) pointList.get(i + 1).getY(), (float) heightz);
            gl.glEnd();
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }

    @Override
    public void mouseClicked(MouseEvent mouse) {

    }

    //鼠标点击事件
    @Override
    public void mousePressed(MouseEvent mouse) {
        if (mouse.getButton() == MouseEvent.BUTTON1) {
            if (renderMode == DRAW_MODE || renderMode == CUT_MODE) {
                pointList.clear();
            }
            mouseLeftDown = true;
            mouseX = mouse.getX();
            mouseY = mouse.getY();

//            if (renderMode == SHOW_MODEL_MODE) {
//                int Xmove = lastMouseX - mouseX;
//                int Ymove = lastMouseY - mouseY;
//                rotateX += (Xmove%360)/20.0;
//                rotateY += (Ymove%360)/20.0;
//                if (rotateX > 360f) rotateX = rotateX - 360;
//                if (rotateX < 0f) rotateX = rotateX + 360;
//                if (rotateY > 360f) rotateY = rotateY - 360;
//                if (rotateY < 0f) rotateY = rotateY + 360;
//            }

            lastMouseX = mouseX;
            lastMouseY = mouseY;
//            System.out.println(mouseX + "," + mouseY);
        }
    }

    //鼠标松开事件
    @Override
    public void mouseReleased(MouseEvent mouse) {
        mouseLeftDown = false;
    }

    @Override
    public void mouseEntered(MouseEvent mouse) {
    }

    @Override
    public void mouseExited(MouseEvent mouse) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mouse) {
    }

    //鼠标拖拽事件
    @Override
    public void mouseDragged(MouseEvent mouse) {
        mouseX = mouse.getX();
        mouseY = mouse.getY();
        if (renderMode == SHOW_MODEL_MODE) {
            int Ymove = lastMouseX - mouseX;
            int Xmove = lastMouseY - mouseY;
            if (Math.abs(Ymove) <= 200) Ymove = 0;
            if (Math.abs(Xmove) <= 200) Xmove = 0;
//            if (rotateY < 90 || (rotateY > 270 && rotateY < 360)) {
//                rotateX += (Xmove % 360) / 100.0;
//            } else {
//                rotateX -= (Xmove % 360) / 100.0;
//            }

            if (rotateX < 90 || (rotateX > 270 && rotateX < 360)) {
                rotateY -= (Ymove % 360) / 100.0;

            } else {
                rotateY += (Ymove % 360) / 100.0;
            }

            rotateX -= (Xmove % 360) / 100.0;
//            rotateY -= (Ymove % 360) / 120.0;

            if (rotateX > 360f) rotateX = rotateX - 360;
            if (rotateX < 0f) rotateX = rotateX + 360;
            if (rotateY > 360f) rotateY = rotateY - 360;
            if (rotateY < 0f) rotateY = rotateY + 360;
        }
//            System.out.println(mouseX + "," + mouseY);
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component
     * but no buttons have been pushed.
     */
    public void mouseMoved(MouseEvent e) {
    }

    //废弃了的选择颜色
    private void selectColor(int i, GL2 gl) {
//        if (i % 3 == 0) {
//            gl.glColor3f(131 / 255.0f, 205 / 255.0f, 1.0f);
//        } else if (i % 3 == 1) {
//            gl.glColor3f(164 / 255.0f, 230 / 255.0f, 1.0f);
//        } else {
//            gl.glColor3f(216 / 255.0f, 230 / 255.0f, 1.0f);
//        }
        gl.glColor3f(131 / 255.0f, 205 / 255.0f, 1.0f);
//                    gl.glColor3f(164 / 255.0f, 230 / 255.0f, 1.0f);
//                    gl.glColor3f(216 / 255.0f, 230 / 255.0f, 1.0f);


    }

    public void keyTyped(KeyEvent e) {
    }

    //仍然保留下来了的方向键控制模型转向
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            changeRotate(1);
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            changeRotate(0);
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            changeRotate(2);
        }
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            changeRotate(3);
        }

    }

    //改变模型方向
    private void changeRotate(int type) {
        //up
        if (type == 0) rotateX -= 2.5f;
        //down
        if (type == 1) rotateX += 2.5f;
        //left
        if (type == 2) rotateY -= 2.5f;
        //right
        if (type == 3) rotateY += 2.5f;

        if (rotateX > 360f) rotateX = rotateX - 360;
        if (rotateX < 0f) rotateX = rotateX + 360;
        if (rotateY > 360f) rotateY = rotateY - 360;
        if (rotateY < 0f) rotateY = rotateY + 360;
    }

    //清空重绘
    public void clearAll() {
        pointList.clear();
        modelCount = 0;
        modelColor = new int[0];
        thisModel = 0;
        modelList = new LinkedList<>();
        renderModel = new Model();
        renderMode = DRAW_MODE;
    }

    //清空点集
    public void clearPointList() {
        pointList.clear();
    }

    //画线
    private void drawLine(GL2 gl, double x, double y, double z) {
        if (mouseLeftDown) {
            double heightz = 0;

//                for (int i = 0; i < modelCount; i++) {
//                    if (heightz <= modelList.get(i).getBiggestZ()) {
//                        heightz =  modelList.get(i).getBiggestZ() * 10;
//                    }
//                }
//

            drawSketchLine(gl);
            int viewport[] = new int[4];
            double modelViewMatrix[] = new double[16];
            double projectionMatrix[] = new double[16];
            double thisVertex[] = new double[3];
            double lastVertex[] = new double[3];
            int realY;
            int realLastY;
            gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
            gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, modelViewMatrix, 0);
            gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projectionMatrix, 0);
            realY = viewport[3] - mouseY;
            realLastY = viewport[3] - lastMouseY;

            this.glu.gluUnProject((double) mouseX, (double) realY, 0, modelViewMatrix, 0, projectionMatrix, 0, viewport, 0, thisVertex, 0);
            this.glu.gluUnProject((double) lastMouseX, (double) realLastY, 0, modelViewMatrix, 0, projectionMatrix, 0, viewport, 0, lastVertex, 0);
            Point thisPoint = new Point(thisVertex[0], thisVertex[1], heightz);
            Point lastPoint = new Point(lastVertex[0], lastVertex[1], heightz);
//            if (renderMode == CUT_MODE) {
//                thisPoint = rotatePoint(thisPoint, rotateX, 0, x, y, z);
//                thisPoint = rotatePoint(thisPoint, rotateY, 1, x, y, z);
//                lastPoint = rotatePoint(lastPoint, rotateX, 0, x, y, z);
//                lastPoint = rotatePoint(lastPoint, rotateY, 1, x, y, z);
//            }
            double dis = thisPoint.distance(lastPoint);
//                System.out.println(dis);
            if (dis > 0.005) {
                Line judgeLine = new Line(thisPoint, lastPoint);
                boolean isIntersect = false;
                if (pointList.size() > 3) {
                    for (int j = 0; j < pointList.size() - 2; j++) {
                        isIntersect = judgeLine.intersectOld(new Line(pointList.get(j), pointList.get(j + 1)));
                        if (isIntersect) {
                            mouseLeftDown = false;
                            drawLastLine(gl);
                        }
                    }
                }
                if (!isIntersect) {
                    pointList.add(thisPoint);
                    lastMouseX = mouseX;
                    lastMouseY = mouseY;
                }
            }
        } else {
            if (pointList.size() >= 2) {
                if (renderMode == DRAW_MODE) {
                    drawLastLine(gl);
                } else {
                    drawSketchLine(gl);
                }
            }
        }
    }

    //旋转一个点
    private Point rotatePoint(Point p, double angle, int rotateMode, double x, double y, double z) {
        //mode = 0: x
        //mode = 1: y
        //mode = 2: z
//        float pivot[] = {(float) renderModel.getBasedPolygon().getMiddlePoint().getX(), (float) renderModel.getBasedPolygon().getMiddlePoint().getY()};
        if (angle == 0) return p;
        double[][] rotateMatrix = {{0, 0, 0}, {0, 0, 0}, {0, 0, 0}};
        if (rotateMode == 0) {
            rotateMatrix[0][0] = 1;
            rotateMatrix[1][1] = Math.cos(Math.toRadians(angle));
            rotateMatrix[1][2] = -1 * Math.sin(Math.toRadians(angle));
            rotateMatrix[2][1] = Math.sin(Math.toRadians(angle));
            rotateMatrix[2][2] = Math.cos(Math.toRadians(angle));
        } else if (rotateMode == 1) {
            rotateMatrix[1][1] = 1;
            rotateMatrix[0][0] = Math.cos(Math.toRadians(angle));
            rotateMatrix[0][2] = Math.sin(Math.toRadians(angle));
            rotateMatrix[2][0] = -1 * Math.sin(Math.toRadians(angle));
            rotateMatrix[2][2] = Math.cos(Math.toRadians(angle));
        } else {
            rotateMatrix[2][2] = 1;
            rotateMatrix[1][1] = Math.cos(Math.toRadians(angle));
            rotateMatrix[1][0] = Math.sin(Math.toRadians(angle));
            rotateMatrix[0][1] = -1 * Math.sin(Math.toRadians(angle));
            rotateMatrix[2][2] = Math.cos(Math.toRadians(angle));
        }
        double newx = x + rotateMatrix[0][0] * (p.getX() + x) + rotateMatrix[0][1] * (p.getY() + y) + rotateMatrix[0][2] * (p.getZ() + z);
        double newy = y + rotateMatrix[1][0] * (p.getX() + x) + rotateMatrix[1][1] * (p.getY() + y) + rotateMatrix[1][2] * (p.getZ() + z);
        double newz = z + rotateMatrix[2][0] * (p.getX() + x) + rotateMatrix[2][1] * (p.getY() + y) + rotateMatrix[2][2] * (p.getZ() + z);

        return new Point(newx, newy, newz);
    }

    //根据边框点集建模
    public void beginBuilding() {
        System.out.println(pointList.size());
        int index = 0;
        Polygon renderPolygon = new Polygon();
        renderPolygon.clearAll();
        renderPolygon.addPolygonPoint(pointList.getFirst());
        for (int i = 1; i < pointList.size() - 1; i++) {
            double dis = pointList.get(i).distance(pointList.get(index));
            if (dis >= 0.1) {
                int count = (int) (dis / 0.1);
                if (count >= 2) {
                    for (int p = 1; p < count; p++) {
                        double radio = p / (count * 1.0);
                        double newX = radio * pointList.get(i).getX() + (1 - radio) * pointList.get(index).getX();
                        double newY = radio * pointList.get(i).getY() + (1 - radio) * pointList.get(index).getY();
                        renderPolygon.addPolygonPoint(new Point(newX, newY, 0));
                    }
                }
                index = i;
                renderPolygon.addPolygonPoint(pointList.get(i));
            }
        }

        double dis = renderPolygon.getPolygonPoint().getFirst().distance(pointList.getLast());
        if (dis >= 0.3) {
            int div = (int) (dis / 0.1);
            System.out.println("div = " + div);
            if (div < 2) {
                div = 2;
            }
            for (int i = 1; i < div; i++) {
                double plusCount = i / (div * 1.0);
                double disX = pointList.getFirst().getX() * plusCount + pointList.getLast().getX() * (1 - plusCount);
                double disY = pointList.getFirst().getY() * plusCount + pointList.getLast().getY() * (1 - plusCount);
                renderPolygon.addPolygonPoint(new Point(disX, disY, 0));
            }
        }
//            renderPolygon.addPolygonPoint(pointList.getLast());

        if (pointList.size() < 3 || renderPolygon.getPolygonPoint().size() < 3) {
            System.out.println("Point List Failed");
            pointList.clear();
            renderMode = DRAW_MODE;
        } else {
//            renderModel.clearAll();
            renderModel = new Model();
            renderModel.setBasedPolygon(renderPolygon);
            renderModel.buildSketchModel();
            System.out.println("Point List OK");
            modelList.add(renderModel);
            modelCount++;
            if (modelCount == 1) {
                middlePivot[0] = (float) renderModel.getBasedPolygon().getMiddlePoint().getX();
                middlePivot[1] = middlePivot[1] + (float) renderModel.getBasedPolygon().getMiddlePoint().getY();
            } else {
                middlePivot[0] = 0.5f * (middlePivot[0] + (float) renderModel.getBasedPolygon().getMiddlePoint().getX());
                middlePivot[1] = 0.5f * (middlePivot[1] + (float) renderModel.getBasedPolygon().getMiddlePoint().getY());
            }
            thisModel = modelCount - 1;
            renderMode = SHOW_MODEL_MODE;
        }
        pointList.clear();
    }

    //更改大于0时的显示模式
    public void showDetails() {
        if (renderMode < 8) {
            if (renderMode == 7) {
                renderMode = 0;
            } else {
                renderMode++;
            }
        }
    }

    //Undo，返回到上一个可渲染的模型中
    public void undo() {
        if (canUndo) {
            renderModel = lastModel;
            lastModel = new Model();
            canUndo = false;
        }
    }

    //完成Cut操作后的选择
    public boolean selectCut() {
        LinkedList<Point> newPointList = new LinkedList<>();
        int index = 0;
        for (int i = 1; i < pointList.size() - 1; i++) {
            pointList.set(i, new Point(pointList.get(i).getX(), pointList.get(i).getY(), 0));
            pointList.set(index, new Point(pointList.get(index).getX(), pointList.get(index).getY(), 0));
            double dis = pointList.get(i).distance(pointList.get(index));
            if (dis >= 0.1) {
                index = i;
                newPointList.add(pointList.get(i));
            }
        }
        pointList.clear();
        pointList.addAll(newPointList);

        int crossIndexX0 = 0;
        int crossIndexY0 = 0;
        int crossIndexX1 = 0;
        int crossIndexY1 = 0;

        Point cutPoint0 = new Point(0, 0, 0);
        Point cutPoint1 = new Point(0, 0, 0);

        newPointList.clear();
        LinkedList<Point> modelPoint = renderModel.getBasedPolygon().getPolygonPoint();
        int crossCount = 0;
        for (int i = 0; i < pointList.size(); i++) {
            int lastIndex = i + 1;
            if (i == pointList.size() - 1) {
                lastIndex = 0;
            }
            Line cutLine = new Line(pointList.get(i), pointList.get(lastIndex));
            boolean isCross = false;
//            boolean isOutside = true;
            for (int j = 0; j < modelPoint.size(); j++) {
                int polyLastIndex = j + 1;
                if (j == modelPoint.size() - 1) {
                    polyLastIndex = 0;
                }
                Line modelLine = new Line(modelPoint.get(j), modelPoint.get(polyLastIndex));
                if (modelLine.intersect2D(cutLine)) {
                    isCross = true;
                    crossCount++;
                    if (crossCount == 1) {
                        crossIndexX0 = j;
                        crossIndexY0 = polyLastIndex;
                        cutPoint0 = PointcalculateIntersectionPoint(cutLine.getStart(), cutLine.getEnd(), modelLine.getStart(), modelLine.getEnd());
                        newPointList.add(cutPoint0);
                        newPointList.add(cutLine.getEnd());
                    }
                    if (crossCount == 2) {
                        crossIndexX1 = j;
                        crossIndexY1 = polyLastIndex;

                        boolean outsideLine = false;
                        for (int t = 1; t < newPointList.size() - 1; t++) {
                            if (!renderModel.getBasedPolygon().inPolygon(newPointList.get(t))) {
                                outsideLine = true;
                                break;
                            }
                        }

                        if (outsideLine) {
                            crossCount = 1;
                            crossIndexX0 = j;
                            crossIndexY0 = polyLastIndex;
                            cutPoint0 = PointcalculateIntersectionPoint(cutLine.getStart(), cutLine.getEnd(), modelLine.getStart(), modelLine.getEnd());
                            newPointList.clear();
                            newPointList.add(cutPoint0);
                            newPointList.add(cutLine.getEnd());
                        } else {
                            cutPoint1 = PointcalculateIntersectionPoint(cutLine.getStart(), cutLine.getEnd(), modelLine.getStart(), modelLine.getEnd());
                            newPointList.add(cutLine.getStart());
                            newPointList.add(cutPoint1);
                        }
                    }
                    break;
                }
            }
            if (crossCount >= 2) {
                break;
            }
            if (!isCross && crossCount == 1) {
                newPointList.add(cutLine.getStart());
                newPointList.add(cutLine.getEnd());
            }
        }
        pointList.clear();
        if (crossCount == 2) {
            if (newPointList.size() < 4) {
                return false;
            } else {
                boolean outsideLine = false;
                for (int i = 1; i < newPointList.size() - 1; i++) {
                    if (!renderModel.getBasedPolygon().inPolygon(newPointList.get(i))) {
                        outsideLine = true;
                        break;
                    }
                }
                if (outsideLine) {
                    return false;
                } else {
                    pointList = newPointList;
                    renderModel.divideModel(crossIndexX0, crossIndexY0, crossIndexX1, crossIndexY1, pointList);
                    return true;
                }
            }
        } else {
            return false;
        }
    }

    //计算交点配套
    private double calculateVectorProduct(Point P1, Point P2, Point P3, Point P4) {

        return (P2.getX() - P1.getX()) * (P4.getY() - P3.getY()) - (P2.getY() - P1.getY()) * (P4.getX() - P3.getX());

    }

    //计算线段AB与CD的交点
    private Point PointcalculateIntersectionPoint(Point A, Point B, Point C, Point D) {

        double t1 = calculateVectorProduct(C, D, A, B);

        double t2 = calculateVectorProduct(A, B, A, C);

        double x = C.getX() + (D.getX() - C.getX()) * t2 / t1;

        double y = C.getY() + (D.getY() - C.getY()) * t2 / t1;

        return new Point(x, y, 0);

    }

    public void keyReleased(KeyEvent e) {
    }
}
