package de.oberien.adbremotecontrol.view;

import de.oberien.adbremotecontrol.adb.AdbDevice;
import de.oberien.adbremotecontrol.adb.AndroidKeyEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ScreenPanel extends JPanel {
    private BufferedImage screenshot;
    private int startX;
    private int startY;

    public ScreenPanel(AdbDevice device) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                double scale = getScale();
                double x = e.getX() * scale;
                double y = e.getY() * scale;
                device.click((int) x, (int) y);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                double scale = getScale();
                double downX = startX / scale;
                double downY = startY / scale;
                double upX = e.getX() / scale;
                double upY = e.getY() / scale;
                device.swipe((int) downX, (int) downY, (int) upX, (int) upY);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                requestFocus();
                requestFocusInWindow();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                AndroidKeyEvent key = AndroidKeyEvent.fromAwtKeycode(e.getKeyCode());
                if (key != null) {
                    device.type(key);
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() >= 32 && e.getKeyChar() < 128) {
                    device.text(String.valueOf(e.getKeyChar()));
                }
            }
        });
    }

    private double getScale() {
        double scaleX = (double) getWidth() / screenshot.getWidth();
        double scaleY = (double) getHeight() / screenshot.getHeight();
        return Math.min(scaleX, scaleY);
    }

    public void setScreenshot(BufferedImage screenshot) {
        this.screenshot = screenshot;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (screenshot == null) {
            return;
        }

        double scale = getScale();
        double scaledWidth = screenshot.getWidth() * scale;
        double scaledHeight = screenshot.getHeight() * scale;

        g.drawImage(screenshot, 0, 0, (int) scaledWidth, (int) scaledHeight, null);
    }
}
