import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import com.jogamp.opengl.util.FPSAnimator;

public class FrameMain {
    private JButton firstButton;
    private static String TITLE = "JFrame Test";
    private static final int CANVAS_WIDTH = 1900;
    private static final int CANVAS_HEIGHT = 1900;
    private static final int FPS = 30;
    private JButton thirdButton;
    private JButton fourthButton;
    private JButton secondButton;
    private JPanel mainPanel;
    private JPanel glPanel;
    private JLabel modeLabel;
    private JButton fifthButton;
    private JButton sixthButton;
    private static final int SHOW_OUTSIDELINE_MODE = 0;
    private static final int SHOW_CATLINE_MODE = 1;
    private static final int SHOW_FANLINE_MODE = 2;
    private static final int SHOW_AXIS_MODE = 3;
    private static final int SHOW_FANRIGION_MODE = 4;
    private static final int SHOW_LIFT_MODE = 5;
    private static final int SHOW_TRIANGLE_MODE = 6;
    private static final int SHOW_MODEL_MODE = 7;
    private static final int DRAW_MODE = -1;
    private static final int EDIT_MODE = -4;

    public void BuildFrame(DrawPolygonRenderer canvas) {
        JFrame frame = new JFrame();
        canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);
//        FrameMain frameMain = new FrameMain();
        glPanel.add(canvas);
//        glPanel.add(canvas, BorderLayout.CENTER);
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

        //第一个按钮
        firstButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //show mode下为切换到draw mode
                if (canvas.getRenderMode() == SHOW_MODEL_MODE) {
                    canvas.setRenderMode(DRAW_MODE);
                    canvas.packup();
                    changeToDrawMode();
                } else if (canvas.getRenderMode() == DRAW_MODE) {
                    //draw mode下为结束绘图
                    canvas.beginBuilding();
                    canvas.setRenderMode(SHOW_MODEL_MODE);
                    changeToShowMode();
                } else if (canvas.getRenderMode() == EDIT_MODE) {
                    //edit mode下为结束编辑
                    canvas.setRenderMode(SHOW_MODEL_MODE);
                    changeToShowMode();
                    //finish
                }

            }
        });

        //第二个按钮
        secondButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //show mode下为进入编辑模式
                if (canvas.getRenderMode() == SHOW_MODEL_MODE) {
                    canvas.setRenderMode(EDIT_MODE);
                    canvas.packup();
                    changeToEditMode();
                } else if (canvas.getRenderMode() == DRAW_MODE) {
                    //draw mode下为清空当前绘图内容
                    canvas.clearPointList();
                } else if (canvas.getRenderMode() == EDIT_MODE) {
                    //edit mode下为显示当前选定模型的细节
                    canvas.showDetails();
                }
            }
        });

        //第三个按钮
        thirdButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //show mode下为撤销操作
                if (canvas.getRenderMode() == SHOW_MODEL_MODE) {
                    canvas.undo();
                } else if (canvas.getRenderMode() == DRAW_MODE) {
                    //draw mode下为不保存结束绘图
                    canvas.undo();
                    changeToShowMode();
                    canvas.setRenderMode(SHOW_MODEL_MODE);
                }

                //edit mode下为平滑当前选定模型
                if (canvas.getRenderMode() == EDIT_MODE) {
                    //smooth
                    canvas.smoothModel();
                }
            }
        });

        //第四个按钮
        fourthButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //show mode下为清空所有
                if (canvas.getRenderMode() == SHOW_MODEL_MODE) {
                    canvas.clearAll();
                }

                //edit mode下为选择颜色
//                if (canvas.getRenderMode() == EDIT_MODE) {
//                    //colour
//                }
            }
        });

        //第五个按钮
        fifthButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //show mode下为存储模型
//                if (canvas.getRenderMode() == SHOW_MODEL_MODE) {
//                    //save
//                }

                //edit mode下为删除选定模型
                if (canvas.getRenderMode() == EDIT_MODE) {
                    //delete
                    canvas.deleteModel();
                }
            }
        });

        //第六个按钮
        sixthButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //edit mode下为不保存退出
                if (canvas.getRenderMode() == EDIT_MODE) {
                    canvas.setRenderMode(SHOW_MODEL_MODE);
                    canvas.undo();
                    changeToShowMode();
                }
            }
        });

        changeToDrawMode();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setTitle(TITLE);
        frame.pack();
        frame.setVisible(true);
        animator.start();
    }


    private void changeToShowMode() {
        modeLabel.setText("Display Mode");

        firstButton.setVisible(true);
        firstButton.setText("Draw Mode");

        secondButton.setVisible(true);
        secondButton.setText("Edit Mode");

        thirdButton.setVisible(true);
        thirdButton.setText("Undo");

        fourthButton.setVisible(true);
        fourthButton.setText("Clear All");

        fifthButton.setVisible(true);
        fifthButton.setText("Save Model");

        sixthButton.setVisible(false);
    }

    private void changeToDrawMode() {
        modeLabel.setText("Draw Mode");

        firstButton.setVisible(true);
        firstButton.setText("Finish");

        secondButton.setVisible(true);
        secondButton.setText("Clear");

        thirdButton.setVisible(true);
        thirdButton.setText("Exit");

        fourthButton.setVisible(false);
        fifthButton.setVisible(false);
        sixthButton.setVisible(false);
    }

    private void changeToEditMode() {
        modeLabel.setText("Edit Mode");

        firstButton.setVisible(true);
        firstButton.setText("Finish");

        secondButton.setVisible(true);
        secondButton.setText("Details");

        thirdButton.setVisible(true);
        thirdButton.setText("Smooth");

        fourthButton.setVisible(true);
        fourthButton.setText("Colour");

        fifthButton.setVisible(true);
        fifthButton.setText("Delete Model");

        sixthButton.setVisible(true);
        sixthButton.setText("Exit");
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
