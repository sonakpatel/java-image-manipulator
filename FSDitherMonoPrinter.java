import java.awt.Color;
import java.lang.Math.*;

/**
 * Reduces image to black and white dots using Floyd-Steinberg dithering
 *
 * @author  Sonak Patel
 * @version 1st March 2011
 */

public class FSDitherMonoPrinter extends Filter
{
    //Threshold as a float to set a cut off point between whether an intensity is either closer to white or black
    private final float THRESHOLD = 0.5f;
    //Holds the two possible colours for mono dithering, Black or White
    private RGBTriple[] RGBPalette = {
        new RGBTriple(0,0,0),   //black
        new RGBTriple(255,255,255)  //white
    };

    /**
     * Constructor
     * @param name The name of the filter
     */

    public FSDitherMonoPrinter(String name)
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
        int height = image.getHeight();
        int width = image.getWidth();
        Color[][] pixelArray = new Color[width] [height];
        Color[][] dotArray = new Color[width] [height];

        for (int y=0; y< height; y++) {
            for (int x=0; x< width; x++) {
                pixelArray[x][y] = image.getPixel(x,y);
            }
        }

        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                dotArray[x][y] = getNearestColor( pixelArray[x][y] );
                int[] dith_error = colorDiff ( pixelArray[x][y] , dotArray[x][y] );

                if (x+1 != width){
                    pixelArray[x+1][y] =  colorMix( pixelArray[x+1][y], dith_error, (7.0 / 16));
                }
                if (y+1 != height){
                    if (x > 0){
                        pixelArray[x-1][y+1] = colorMix( pixelArray[x-1][y+1], dith_error, (3.0/16) );
                    }
                    pixelArray[x][y+1] = colorMix( pixelArray[x][y+1], dith_error, (5.0/16) );
                    if (x+1 != width){
                        pixelArray[x+1][y+1] = colorMix(pixelArray[x+1][y+1], dith_error, (1.0/16) );
                    }
                }
                image.setPixel( x, y, dotArray[x][y] );
            }
        }

        return image;
    }

    /**
     * Comparison of the intensity with the threshold
     * @param  pixel  The individual pixel, of which it's intensity is to be compared with the threshold
     * @return  Black or White returned as a colour depending on which side of the intensity the colour's intensity falls on
     */
    private Color getNearestColor (Color pixel){
        if ( instensity( pixel.getRed(), pixel.getBlue(), pixel.getGreen() ) < THRESHOLD ){
            return RGBPalette[0].getColor();
        } else {
            return RGBPalette[1].getColor();
        }
    }

    /**
     * A value of the average intensity of the colour represented by RGB values
     * @param  red green blue  RGB values
     * @return  Float representation of the intensity
     */
    private float instensity (int red, int green, int blue ){
        //System.out.println("Red: " + red + ", Blue: " + blue + ", Green: " + green);
        return (float)(red + green + blue) / (255 * 3);
    }

    /**
     * Get the difference in colour from the two that are passed in to retrieve the dithering error
     * @param  pixelCol The colour of the current pixel
     * @param  dotCol The colour of the nearest available present in dotarray
     * @return  Array of int's containing the RGB value of the colour difference
     */
    public int[] colorDiff(Color pixelCol, Color dotCol)
    {
        int red = pixelCol.getRed() - dotCol.getRed();
        int green =  pixelCol.getGreen() - dotCol.getGreen();
        int blue =  pixelCol.getBlue() - dotCol.getBlue();

        return new int[] {red ,green ,blue };
    }

    /**
     * Combine two colours together for a specific pixel ahead of the buffer
     * @param  pixelCol The colour of the current pixel
     * @param  error The array of RGB values representing the error to be applied
     * @param  offset The offset value to be applied to the error
     * @return  The new colour to be set
     */
    public Color colorMix(Color pixelCol, int[] error, double offset)
    {
        int red = Math.min( pixelCol.getRed() + (int)( offset*(error[0]) ) ,255);
        int green = Math.min( pixelCol.getGreen() + (int)( offset*(error[1]) ),255);
        int blue  = Math.min( pixelCol.getBlue() + (int)( offset*(error[2]) ),255);

        if(red < 0) red = 0;
        if(green < 0) green = 0;
        if(blue < 0) blue = 0;

        return new Color (red,green,blue);
    }
}

