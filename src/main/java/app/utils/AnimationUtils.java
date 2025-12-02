package app.utils;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnimationUtils {
    
    /**
     * Anima el cambio de color de un componente gradualmente
     */
    public static void animateColor(JComponent component, Color fromColor, Color toColor, int duration) {
        Timer timer = new Timer(16, null);
        long startTime = System.currentTimeMillis();
        
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                float progress = Math.min(1.0f, elapsed / (float) duration);
                
                int r = (int) (fromColor.getRed() + (toColor.getRed() - fromColor.getRed()) * progress);
                int g = (int) (fromColor.getGreen() + (toColor.getGreen() - fromColor.getGreen()) * progress);
                int b = (int) (fromColor.getBlue() + (toColor.getBlue() - fromColor.getBlue()) * progress);
                
                component.setBackground(new Color(r, g, b));
                component.repaint();
                
                if (progress >= 1.0f) {
                    timer.stop();
                }
            }
        });
        timer.start();
    }
    
    /**
     * Anima el cambio de opacidad de un componente (fade in/out)
     */
    public static void fadeIn(JComponent component, int duration) {
        component.setVisible(true);
        Timer timer = new Timer(16, null);
        long startTime = System.currentTimeMillis();
        
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                float progress = Math.min(1.0f, elapsed / (float) duration);
                
                component.setVisible(true);
                component.repaint();
                
                if (progress >= 1.0f) {
                    timer.stop();
                }
            }
        });
        timer.start();
    }
    
    /**
     * Anima el movimiento de un componente
     */
    public static void slideIn(JComponent component, Point fromPoint, Point toPoint, int duration) {
        Timer timer = new Timer(16, null);
        long startTime = System.currentTimeMillis();
        
        timer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsed = System.currentTimeMillis() - startTime;
                float progress = Math.min(1.0f, elapsed / (float) duration);
                
                // Easing function (ease-out)
                float easedProgress = 1.0f - (float) Math.pow(1.0 - progress, 3);
                
                int x = (int) (fromPoint.x + (toPoint.x - fromPoint.x) * easedProgress);
                int y = (int) (fromPoint.y + (toPoint.y - fromPoint.y) * easedProgress);
                
                component.setLocation(x, y);
                component.repaint();
                
                if (progress >= 1.0f) {
                    timer.stop();
                }
            }
        });
        timer.start();
    }
    
    /**
     * Crea un efecto de "pulse" en un botón
     */
    public static void pulseButton(JButton button, Color baseColor) {
        Timer timer = new Timer(50, null);
        final boolean[] expanding = {true};
        final float[] scale = {1.0f};
        
        timer.addActionListener(e -> {
            if (expanding[0]) {
                scale[0] += 0.05f;
                if (scale[0] >= 1.1f) {
                    expanding[0] = false;
                }
            } else {
                scale[0] -= 0.05f;
                if (scale[0] <= 1.0f) {
                    scale[0] = 1.0f;
                    timer.stop();
                    button.setBackground(baseColor);
                    return;
                }
            }
            
            // Cambiar brillo del color
            float brightness = 0.8f + (scale[0] - 1.0f) * 2.0f;
            int r = Math.min(255, (int)(baseColor.getRed() * brightness));
            int g = Math.min(255, (int)(baseColor.getGreen() * brightness));
            int b = Math.min(255, (int)(baseColor.getBlue() * brightness));
            
            button.setBackground(new Color(r, g, b));
            button.repaint();
        });
        timer.start();
    }
    
    /**
     * Shake animation para errores
     */
    public static void shake(JComponent component) {
        Point originalLocation = component.getLocation();
        Timer timer = new Timer(50, null);
        final int[] offset = {0};
        final boolean[] goingRight = {true};
        final int[] count = {0};
        
        timer.addActionListener(e -> {
            if (count[0] >= 8) {
                component.setLocation(originalLocation);
                timer.stop();
                return;
            }
            
            if (goingRight[0]) {
                offset[0] += 10;
                if (offset[0] >= 10) {
                    goingRight[0] = false;
                    count[0]++;
                }
            } else {
                offset[0] -= 10;
                if (offset[0] <= -10) {
                    goingRight[0] = true;
                    count[0]++;
                }
            }
            
            component.setLocation(originalLocation.x + offset[0], originalLocation.y);
            component.repaint();
        });
        timer.start();
    }
    
    /**
     * Animación de carga (spinner personalizado)
     */
    public static JPanel createLoadingSpinner(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(100, 100, 100));
        
        panel.add(label, BorderLayout.CENTER);
        return panel;
    }
}

