import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.jogamp.opengl.util.FPSAnimator;

public class FrameMain {
    private JButton finishDrawButton;
    private static String TITLE = "JFrame Test";
    private static final int CANVAS_WIDTH = 1600;
    private static final int CANVAS_HEIGHT = 1600;
    private static final int FPS = 30;
    private JButton strainButton;
    private JButton cutButton;
    private JButton reshapeButton;
    private JPanel mainPanel;
    private JPanel glPanel;
    private JButton clearButton;
    private JButton resetButton;
    private JButton undoButton;
    private JButton saveButton;
    private JLabel modeLabel;
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

    public void BuildFrame(DrawPolygonRenderer canvas) {
        JFrame frame = new JFrame();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);
        FrameMain frameMain = new FrameMain();
        glPanel.add(canvas, BorderLayout.CENTER);
        frame.getContentPane().add(mainPanel, BorderLayout.CENTER);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 使用一个专用线程来运行stop()以确保animator会在程序退出前停止
                new Thread() {
                    @Override
                    public void run() {
                        if (animator.isStarted()) animator.stop();
                        System.exit(0);
                    }
                }.start();
            }
        });

        //变形模式按钮
        reshapeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        //切换到DrawMode/确认绘图内容
        finishDrawButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (canvas.getRenderMode() == -1) {
                    canvas.beginBuilding();
                    finishDrawButton.setText("Draw Mode");
                    modeLabel.setText("Display Mode");
                    resetButton.setText("Detail");
                    if (canvas.getRenderMode() >= 0) {
                        canvas.setRenderMode(7);
                    }
                } else {
                    canvas.clearAll();
                    finishDrawButton.setText("Finish Draw");
                    modeLabel.setText("Draw Mode");
                    resetButton.setText("Reset");
                }
            }
        });

        //剪切模式按钮
        cutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (canvas.getRenderMode() >= 0) {
                    canvas.clearPointList();
                    canvas.setRenderMode(CUT_MODE);
                    turnToCutMode();
                } else if (canvas.getRenderMode() == -2) {
                    cutButton.setText("Select");
                    modeLabel.setText("Select Region");
                    boolean successCut = canvas.selectCut();
                    if (successCut) {
                        canvas.setRenderMode(CUT_SELECT_MODE);
                    } else {
                        turnToCutMode();
                    }
                } else {
                    cutButton.setText("Cut Mode");
                    resetButton.setText("Detail");
                    modeLabel.setText("Display Mode");
                    canvas.setRenderMode(SHOW_MODEL_MODE);
                }
            }
        });

        //拉伸模式按钮
        strainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.smoothModel();
            }
        });

        //清空按钮
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.clearAll();
                finishDrawButton.setText("Finish Draw");
                cutButton.setText("Cut Mode");
                modeLabel.setText("Draw Mode");
            }
        });

        //undo按钮
        undoButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (canvas.getRenderMode() >= 0) {
                    canvas.undo();
                }
            }
        });

        //重设按钮,在显示模型时为细节按钮
        resetButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (canvas.getRenderMode() >= 0) {
                    canvas.showDetails();
                }
                if (canvas.getRenderMode() == DRAW_MODE) {
                    canvas.clearAll();
                }
                if (canvas.getRenderMode() == CUT_MODE) {
                    canvas.clearPointList();
                }
                if (canvas.getRenderMode() == CUT_SELECT_MODE) {
                    canvas.clearPointList();
                    canvas.setRenderMode(CUT_MODE);
                    turnToCutMode();
                }
            }
        });

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setTitle(TITLE);
        frame.pack();
        frame.setVisible(true);
        animator.start();
    }

    private void turnToCutMode() {
        cutButton.setText("Finish Cut");
        modeLabel.setText("Cut Mode");
        resetButton.setText("Reset");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                FrameMain renderer = new FrameMain();
                renderer.BuildFrame(new DrawPolygonRenderer());
            }
        });
    }

}
