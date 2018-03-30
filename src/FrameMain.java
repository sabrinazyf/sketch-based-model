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
    private JButton nextButton;
    private JButton cutButton;
    private JButton clearButton;
    private JPanel mainPanel;
    private JPanel glPanel;
    private JButton downButton;
    private JButton rightButton;
    private JButton upButton;
    private JButton leftButton;
    private JButton saveButton;
    private JLabel modeLabel;

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

        //清空
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                canvas.clearAll();
                finishDrawButton.setText("Finish Draw");
                cutButton.setText("Cut Mode");
                modeLabel.setText("Draw Mode");
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
                    if (canvas.getRenderMode() >= 0) {
                        canvas.setRenderMode(7);
                    }
                } else {
                    canvas.clearAll();
                    finishDrawButton.setText("Finish Draw");
                    modeLabel.setText("Draw Mode");
                }
            }
        });
        cutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (canvas.getRenderMode() >= 0) {
                    canvas.clearPointList();
                    canvas.setRenderMode(-2);
                    cutButton.setText("Finish Cut");
                    modeLabel.setText("Cut Mode");
                } else if (canvas.getRenderMode() == -2) {
                    cutButton.setText("Cut Mode");
                    modeLabel.setText("Display Mode");
                    canvas.setRenderMode(7);
                }
            }
        });
        nextButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (canvas.getRenderMode() >= 0) {
                    canvas.showDetails();
                }
            }
        });
        upButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (canvas.getRenderMode() >= 5) {
                    canvas.changeRotate(1);
                }
            }
        });
        downButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (canvas.getRenderMode() >= 5) {
                    canvas.changeRotate(0);
                }
            }
        });
        leftButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (canvas.getRenderMode() >= 5) {
                    canvas.changeRotate(2);
                }
            }
        });
        rightButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (canvas.getRenderMode() >= 5) {
                    canvas.changeRotate(3);
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
