import java.awt.Color;


/**
 *
 * @author Sonak
 * @version 1st March 2011
 */
public class BilinearSmallerX2 extends Filter
{
    private final float mag = 0.5f;
    /**
     * Constructor
     * @param name The name of the filter
     */

    public BilinearSmallerX2(String name)
    {
        super(name);
    }

    /**
     * Apply this filter to an image
     * @param image The image to be changed by this filter
     * @return The filtered image
     */

    public ColorImage apply(ColorImage image)
    {
        // Create new image with double size
        int height = image.getHeight();
        int width = image.getWidth();

        ColorImage bilinearImage = new ColorImage((int)(width*mag), (int)(height*mag));

        for (int ry=0; ry<(int)(height*mag); ry++) {
            for (int rx=0; rx<(int)(width*mag); rx++) {

                float sx = (float) rx/mag, sy = (float) ry/mag;
                int isx = (int) sx, isy = (int) sy;
                float fsx = sx-isx, fsy = sy-isy;

                float red_sum = 0, blue_sum = 0, green_sum = 0;

                float red1 = 0, red2 = 0, red3 = 0, red4 = 0;

                red1 = (1-fsx)*(1-fsy)*(image.getPixel(isx,isy).getRed());
                if (isx+1 != width)  red2 = (fsx)*(1-fsy)*(image.getPixel(isx+1,isy).getRed());
                if (isy+1 != height) red3 = (1-fsx)*(fsy)*(image.getPixel(isx,isy+1).getRed());
                if (isx+1 != width &&
                    isy+1 != height) red4 = (fsx)*(fsy)*image.getPixel(isx+1,isy+1).getRed();

                red_sum = red1+red2+red3+red4;

                float green1 = 0, green2 = 0, green3 = 0, green4 = 0;

                green1 = (1-fsx)*(1-fsy)*(image.getPixel(isx,isy).getGreen());
                if (isx+1 != width) green2 = (fsx)*(1-fsy)*(image.getPixel(isx+1,isy).getGreen());
                if (isy+1 != height) green3 = (1-fsx)*(fsy)*(image.getPixel(isx,isy+1).getGreen());
                if (isx+1 != width &&
                    isy+1 != height) green4 = (fsx)*(fsy)*(image.getPixel(isx+1,isy+1).getGreen());

                green_sum = green1+green2+green3+green4;

                float blue1 = 0, blue2 = 0, blue3 = 0, blue4 = 0;

                blue1 = (1-fsx)*(1-fsy)*(image.getPixel(isx,isy).getBlue());
                if (isx+1 != width) blue2 = (fsx)*(1-fsy)*(image.getPixel(isx+1,isy).getBlue());
                if (isy+1 != height) blue3 = (1-fsx)*(fsy)*(image.getPixel(isx,isy+1).getBlue());
                if (isx+1 != width &&
                    isy+1 != height) blue4 = (fsx)*(fsy)*(image.getPixel(isx+1,isy+1).getBlue());

                blue_sum = blue1+blue2+blue3+blue4;

                if(red_sum   > 255) red_sum = 255;
                if(green_sum > 255) green_sum = 255;
                if(blue_sum  > 255) blue_sum = 255;

                bilinearImage.setPixel(rx,ry,new Color((int)red_sum,(int)green_sum,(int)blue_sum));
            }
        }

        return bilinearImage;
    }


}