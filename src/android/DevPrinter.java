package cordova.plugin.devbluetoothprinter;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.io.IOException;
import java.io.OutputStream;

public class DevPrinter {

    private final static char ESC_CHAR = 0x1B;
    private final static char GS = 0x1D;
    private final static byte[] LINE_FEED = new byte[]{0x0A};
    private final static byte[] CUT_PAPER = new byte[]{GS, 0x56, 0x00};
    private final static byte[] INIT_PRINTER = new byte[]{ESC_CHAR, 0x40};
    private final static byte[] SET_PRINT_MODE = new byte[]{ESC_CHAR, 0x21, 0x00};
    private final static byte[] UNDERLINED_MODE = new byte[]{ESC_CHAR, 0x2D, 0x32};
    private final static byte[] EMPHASIZED_MODE_ON = new byte[]{ESC_CHAR, 0x45, 0x01};
    private final static byte[] EMPHASIZED_MODE_OFF = new byte[]{ESC_CHAR, 0x45, 0x00};
    private final static byte[] DSTRIKE_MODE_ON = new byte[]{ESC_CHAR, 0x47, 0x01};
    private final static byte[] DSTRIKE_MODE_OFF = new byte[]{ESC_CHAR, 0x47, 0x00};
    private final static byte[] ALIGN_LEFT = new byte[]{ESC_CHAR, 0x61, 0x30};
    private final static byte[] ALIGN_CENTER = new byte[]{ESC_CHAR, 0x61, 0x31};
    private final static byte[] ALIGN_RIGHT = new byte[]{ESC_CHAR, 0x61, 0x32};
    private final static byte[] UPSIDE_ON = new byte[]{ESC_CHAR, 0x7B, 0x01};
    private final static byte[] UPSIDE_OFF = new byte[]{ESC_CHAR, 0x7B, 0x00};
    private final static byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33};
    private final static byte[] SET_LINE_SPACE_24 = new byte[]{ESC_CHAR, 0x33, 24};
    private final static byte[] SET_LINE_SPACE_30 = new byte[]{ESC_CHAR, 0x33, 30};
    private final static byte FONT_POINT = 0x11;
    private final static String DEFAULT_TYPE = "SERIAL", SERIAL_TYPE = "SERIAL", USB_TYPE = "USB";
    private final static String[] PRINTER = {"ESC/POS Epson Printers"};
    
    private OutputStream os;
    
    DevPrinter(OutputStream os) {
        this.os = os;
    }


    public void printImage(Bitmap image) throws IOException {

         image = scaleDown(image, 384, false);
        int[][] pixels = getPixelsSlow(image);
        os.write(INIT_PRINTER);
        os.write(SET_LINE_SPACE_24);
        for (int y = 0; y < pixels.length; y += 24) {
            os.write(SELECT_BIT_IMAGE_MODE);// bit mode
            os.write(new byte[]{(byte) (0x00ff & pixels[y].length), (byte) ((0xff00 & pixels[y].length) >> 8)});// width, low & high
            for (int x = 0; x < pixels[y].length; x++) {
                // For each vertical line/slice must collect 3 bytes (24 bytes)
                os.write(recollectSlice(y, x, pixels));
            }

            os.write(PrinterCommands.FEED_LINE);
        }
        os.write(SET_LINE_SPACE_30);
        os.write(SET_LINE_SPACE_30);
        os.write(SET_LINE_SPACE_30);
    }

    private int[][] getPixelsSlow(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[][] result = new int[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                result[row][col] = image.getPixel(col,row);
            }
        }
        return result;
    }

    private byte[] recollectSlice(int y, int x, int[][] img) {
        byte[] slices = new byte[] {0, 0, 0};
        for (int yy = y, i = 0; yy < y + 24 && i < 3; yy += 8, i++) {
            byte slice = 0;
            for (int b = 0; b < 8; b++) {
                int yyy = yy + b;
                if (yyy >= img.length) {
                    continue;
                }
                int col = img[yyy][x];
                boolean v = shouldPrintColor(col);
                slice |= (byte) ((v ? 1 : 0) << (7 - b));
            }
            slices[i] = slice;
        }
        return slices;
    }
    private boolean shouldPrintColor(int col) {
        final int threshold = 127;
        int a, r, g, b, luminance;
        a = (col >> 24) & 0xff;
        if (a != 0xff) {// Ignore transparencies
            return false;
        }
        r = (col >> 16) & 0xff;
        g = (col >> 8) & 0xff;
        b = col & 0xff;
        luminance = (int) (0.299 * r + 0.587 * g + 0.114 * b);
        return luminance < threshold;
    }
    private Bitmap scaleDown(Bitmap realImage, float maxImageSize,boolean filter) {
        float ratio = maxImageSize / realImage.getWidth();
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());
        int[] colors = new int[1000];
        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }
}
