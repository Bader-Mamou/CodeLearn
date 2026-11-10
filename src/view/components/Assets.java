package view.components;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

/** Loads optional illustration assets and prepares them for dark backgrounds. */
public final class Assets {

    private Assets() {}

    /** Tries the project root, the assets folder and the classpath. */
    public static Image load(String name) {
        try {
            File direct = new File(name);
            if (direct.isFile()) {
                return ImageIO.read(direct);
            }
            File assets = new File("assets", name);
            if (assets.isFile()) {
                return ImageIO.read(assets);
            }
            File source = new File("src/assets", name);
            if (source.isFile()) {
                return ImageIO.read(source);
            }
            try (InputStream stream = Assets.class.getResourceAsStream("/" + name)) {
                if (stream != null) {
                    return ImageIO.read(stream);
                }
            }
        } catch (Exception ignored) {
            // the UI falls back to a vector illustration
        }
        return null;
    }

    /**
     * Removes the flat white background of an illustration while keeping soft
     * antialiased edges. The result blends naturally into the dark sidebar.
     */
    public static Image withTransparentBackground(Image source) {
        if (source == null) {
            return null;
        }
        int w = source.getWidth(null);
        int h = source.getHeight(null);
        if (w <= 0 || h <= 0) {
            return source;
        }
        BufferedImage in = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = in.createGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();

        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = in.getRGB(x, y);
                int a = (argb >>> 24) & 0xff;
                int r = (argb >>> 16) & 0xff;
                int gr = (argb >>> 8) & 0xff;
                int b = argb & 0xff;
                int min = Math.min(r, Math.min(gr, b));
                int max = Math.max(r, Math.max(gr, b));
                if (min >= 248 || (min >= 236 && max - min <= 8)) {
                    int fade = Math.max(0, Math.min(255, (248 - min) * 18));
                    a = a * fade / 255;
                }
                out.setRGB(x, y, (a << 24) | (r << 16) | (gr << 8) | b);
            }
        }
        return out;
    }
}
