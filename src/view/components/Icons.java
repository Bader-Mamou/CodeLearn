package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Small vector icon set used by the CodeLearn UI.
 *
 * <p>Icons are painted with Java2D so they stay crisp at every size and do not
 * depend on emoji fonts or external image files.</p>
 */
public final class Icons {

    private Icons() {}

    /** Returns a lightweight component that paints one icon. */
    public static JComponent view(String name, int size, Color color) {
        JComponent icon = new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                Icons.paint(g, name, 0, 0, Math.min(getWidth(), getHeight()), color);
            }
        };
        icon.setOpaque(false);
        icon.setPreferredSize(new Dimension(size, size));
        icon.setMinimumSize(new Dimension(size, size));
        icon.setMaximumSize(new Dimension(size, size));
        return icon;
    }

    /** Paints {@code name} inside a square of {@code size} pixels at (x, y). */
    public static void paint(Graphics g, String name, int x, int y, int size, Color color) {
        if (size <= 0) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        g2.translate(x, y);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(Math.max(1.4f, size * 0.085f),
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        float s = size;
        switch (name == null ? "" : name) {
            case "home":
                Path2D roof = new Path2D.Float();
                roof.moveTo(s * 0.12f, s * 0.48f);
                roof.lineTo(s * 0.50f, s * 0.14f);
                roof.lineTo(s * 0.88f, s * 0.48f);
                g2.draw(roof);
                Path2D body = new Path2D.Float();
                body.moveTo(s * 0.21f, s * 0.44f);
                body.lineTo(s * 0.21f, s * 0.84f);
                body.lineTo(s * 0.79f, s * 0.84f);
                body.lineTo(s * 0.79f, s * 0.44f);
                g2.draw(body);
                g2.draw(new RoundRectangle2D.Float(s * 0.40f, s * 0.57f,
                        s * 0.20f, s * 0.27f, s * 0.05f, s * 0.05f));
                break;

            case "book":
            case "bookOpen":
                Path2D left = new Path2D.Float();
                left.moveTo(s * 0.50f, s * 0.26f);
                left.curveTo(s * 0.41f, s * 0.18f, s * 0.25f, s * 0.16f, s * 0.13f, s * 0.19f);
                left.lineTo(s * 0.13f, s * 0.78f);
                left.curveTo(s * 0.27f, s * 0.75f, s * 0.41f, s * 0.79f, s * 0.50f, s * 0.87f);
                Path2D right = new Path2D.Float();
                right.moveTo(s * 0.50f, s * 0.26f);
                right.curveTo(s * 0.59f, s * 0.18f, s * 0.75f, s * 0.16f, s * 0.87f, s * 0.19f);
                right.lineTo(s * 0.87f, s * 0.78f);
                right.curveTo(s * 0.73f, s * 0.75f, s * 0.59f, s * 0.79f, s * 0.50f, s * 0.87f);
                g2.draw(left);
                g2.draw(right);
                g2.draw(new Line2D.Float(s * 0.50f, s * 0.26f, s * 0.50f, s * 0.87f));
                break;

            case "exercise":
                g2.draw(new RoundRectangle2D.Float(s * 0.19f, s * 0.16f,
                        s * 0.62f, s * 0.72f, s * 0.10f, s * 0.10f));
                g2.draw(new RoundRectangle2D.Float(s * 0.34f, s * 0.08f,
                        s * 0.32f, s * 0.15f, s * 0.05f, s * 0.05f));
                Path2D tick = new Path2D.Float();
                tick.moveTo(s * 0.33f, s * 0.48f);
                tick.lineTo(s * 0.44f, s * 0.59f);
                tick.lineTo(s * 0.67f, s * 0.35f);
                g2.draw(tick);
                g2.draw(new Line2D.Float(s * 0.33f, s * 0.71f, s * 0.67f, s * 0.71f));
                break;

            case "test":
            case "check":
                g2.fill(new Ellipse2D.Float(s * 0.06f, s * 0.06f, s * 0.88f, s * 0.88f));
                g2.setColor(Color.WHITE);
                g2.setStroke(new BasicStroke(Math.max(1.8f, size * 0.11f),
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                Path2D check = new Path2D.Float();
                check.moveTo(s * 0.28f, s * 0.51f);
                check.lineTo(s * 0.44f, s * 0.67f);
                check.lineTo(s * 0.73f, s * 0.34f);
                g2.draw(check);
                break;

            case "progress":
                g2.fill(new RoundRectangle2D.Float(s * 0.16f, s * 0.58f,
                        s * 0.15f, s * 0.27f, s * 0.06f, s * 0.06f));
                g2.fill(new RoundRectangle2D.Float(s * 0.42f, s * 0.40f,
                        s * 0.15f, s * 0.45f, s * 0.06f, s * 0.06f));
                g2.fill(new RoundRectangle2D.Float(s * 0.68f, s * 0.18f,
                        s * 0.15f, s * 0.67f, s * 0.06f, s * 0.06f));
                break;

            case "profile":
                g2.draw(new Ellipse2D.Float(s * 0.37f, s * 0.14f, s * 0.26f, s * 0.26f));
                g2.draw(new Arc2D.Float(s * 0.16f, s * 0.46f, s * 0.68f,
                        s * 0.62f, 0, 180, Arc2D.OPEN));
                break;

            case "logout":
                g2.draw(new RoundRectangle2D.Float(s * 0.13f, s * 0.13f,
                        s * 0.31f, s * 0.74f, s * 0.06f, s * 0.06f));
                g2.draw(new Line2D.Float(s * 0.47f, s * 0.50f, s * 0.87f, s * 0.50f));
                Path2D arrow = new Path2D.Float();
                arrow.moveTo(s * 0.70f, s * 0.33f);
                arrow.lineTo(s * 0.87f, s * 0.50f);
                arrow.lineTo(s * 0.70f, s * 0.67f);
                g2.draw(arrow);
                break;

            case "lock":
                g2.draw(new Arc2D.Float(s * 0.32f, s * 0.17f, s * 0.36f,
                        s * 0.48f, 0, 180, Arc2D.OPEN));
                g2.fill(new RoundRectangle2D.Float(s * 0.19f, s * 0.43f,
                        s * 0.62f, s * 0.43f, s * 0.09f, s * 0.09f));
                g2.setColor(new Color(255, 255, 255, 210));
                g2.fill(new Ellipse2D.Float(s * 0.45f, s * 0.55f,
                        s * 0.10f, s * 0.10f));
                g2.fill(new RoundRectangle2D.Float(s * 0.48f, s * 0.61f,
                        s * 0.04f, s * 0.12f, s * 0.03f, s * 0.03f));
                break;

            case "play":
                Path2D play = new Path2D.Float();
                play.moveTo(s * 0.30f, s * 0.16f);
                play.lineTo(s * 0.85f, s * 0.50f);
                play.lineTo(s * 0.30f, s * 0.84f);
                play.closePath();
                g2.fill(play);
                break;

            case "trophy":
                Path2D cup = new Path2D.Float();
                cup.moveTo(s * 0.28f, s * 0.17f);
                cup.lineTo(s * 0.72f, s * 0.17f);
                cup.lineTo(s * 0.67f, s * 0.54f);
                cup.curveTo(s * 0.63f, s * 0.70f, s * 0.37f, s * 0.70f, s * 0.33f, s * 0.54f);
                cup.closePath();
                g2.fill(cup);
                g2.setStroke(new BasicStroke(Math.max(1.3f, size * 0.07f),
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.draw(new Arc2D.Float(s * 0.12f, s * 0.22f, s * 0.24f,
                        s * 0.24f, 90, -180, Arc2D.OPEN));
                g2.draw(new Arc2D.Float(s * 0.64f, s * 0.22f, s * 0.24f,
                        s * 0.24f, 90, 180, Arc2D.OPEN));
                g2.fill(new RoundRectangle2D.Float(s * 0.46f, s * 0.62f,
                        s * 0.08f, s * 0.14f, s * 0.03f, s * 0.03f));
                g2.fill(new RoundRectangle2D.Float(s * 0.31f, s * 0.78f,
                        s * 0.38f, s * 0.09f, s * 0.04f, s * 0.04f));
                break;

            case "clock":
                g2.draw(new Ellipse2D.Float(s * 0.10f, s * 0.10f, s * 0.80f, s * 0.80f));
                g2.draw(new Line2D.Float(s * 0.50f, s * 0.47f, s * 0.50f, s * 0.26f));
                g2.draw(new Line2D.Float(s * 0.50f, s * 0.47f, s * 0.68f, s * 0.58f));
                break;

            case "flame":
                Path2D flame = new Path2D.Float();
                flame.moveTo(s * 0.50f, s * 0.94f);
                flame.curveTo(s * 0.22f, s * 0.94f, s * 0.07f, s * 0.74f,
                        s * 0.16f, s * 0.53f);
                flame.curveTo(s * 0.22f, s * 0.39f, s * 0.35f, s * 0.31f,
                        s * 0.38f, s * 0.10f);
                flame.curveTo(s * 0.55f, s * 0.23f, s * 0.59f, s * 0.37f,
                        s * 0.51f, s * 0.51f);
                flame.curveTo(s * 0.63f, s * 0.48f, s * 0.70f, s * 0.36f,
                        s * 0.67f, s * 0.25f);
                flame.curveTo(s * 0.85f, s * 0.40f, s * 0.93f, s * 0.59f,
                        s * 0.83f, s * 0.75f);
                flame.curveTo(s * 0.75f, s * 0.87f, s * 0.63f, s * 0.94f,
                        s * 0.50f, s * 0.94f);
                flame.closePath();
                g2.fill(flame);
                break;

            case "chart":
                g2.fill(new RoundRectangle2D.Float(s * 0.10f, s * 0.54f,
                        s * 0.17f, s * 0.33f, s * 0.05f, s * 0.05f));
                g2.fill(new RoundRectangle2D.Float(s * 0.41f, s * 0.34f,
                        s * 0.17f, s * 0.53f, s * 0.05f, s * 0.05f));
                g2.fill(new RoundRectangle2D.Float(s * 0.72f, s * 0.16f,
                        s * 0.17f, s * 0.71f, s * 0.05f, s * 0.05f));
                break;

            case "gear":
                Area gear = new Area(starPolygon(s * 0.5f, s * 0.5f,
                        8, s * 0.46f, s * 0.35f));
                gear.subtract(new Area(new Ellipse2D.Float(s * 0.34f, s * 0.34f,
                        s * 0.32f, s * 0.32f)));
                g2.fill(gear);
                break;

            case "diamond":
                Path2D gem = new Path2D.Float();
                gem.moveTo(s * 0.50f, s * 0.10f);
                gem.lineTo(s * 0.87f, s * 0.40f);
                gem.lineTo(s * 0.50f, s * 0.90f);
                gem.lineTo(s * 0.13f, s * 0.40f);
                gem.closePath();
                g2.fill(gem);
                g2.setColor(new Color(255, 255, 255, 150));
                g2.draw(new Line2D.Float(s * 0.13f, s * 0.40f, s * 0.87f, s * 0.40f));
                g2.draw(new Line2D.Float(s * 0.50f, s * 0.10f, s * 0.50f, s * 0.90f));
                g2.draw(new Line2D.Float(s * 0.31f, s * 0.40f, s * 0.50f, s * 0.10f));
                g2.draw(new Line2D.Float(s * 0.69f, s * 0.40f, s * 0.50f, s * 0.10f));
                break;

            default:
                g2.fill(new Ellipse2D.Float(s * 0.30f, s * 0.30f, s * 0.40f, s * 0.40f));
                break;
        }
        g2.dispose();
    }

    private static Polygon starPolygon(float cx, float cy, int points,
                                       float outer, float inner) {
        int[] xs = new int[points * 2];
        int[] ys = new int[points * 2];
        for (int i = 0; i < points * 2; i++) {
            double angle = Math.PI * i / points - Math.PI / 2;
            float radius = (i % 2 == 0) ? outer : inner;
            xs[i] = Math.round(cx + (float) Math.cos(angle) * radius);
            ys[i] = Math.round(cy + (float) Math.sin(angle) * radius);
        }
        return new Polygon(xs, ys, xs.length);
    }
}
