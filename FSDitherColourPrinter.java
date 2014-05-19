import java.awt.Color;
import java.lang.Math.*;

/**
 * Reduces image to dots of colours available with typical colour printer.
 *
 * Available colours (and RGB values) are:
 *   black (0,0,0);  white (255,255,255)
 *   cyan (0,255,255);  magenta (255,0,255);  yellow (255,255,0)
 *   red (255,0,0);  green (0,255,0);  blue (0,0,255)
 *
 * Colours used have been that of the CMY colour model; Cyan, Magenta and Yellow
 *
 * @author  Sonak Patel
 * @version 1st March 2011
 */

public class FSDitherColourPrinter extends Filter
{
    //Declaration of the RGB Palette for use in this class
    private RGBTriple[] RGBPalette = {
            new RGBTriple(0,0,0),//Black
            new RGBTriple(0,255,255), //Cyan
            new RGBTriple(255,0,255), //Magenta
            new RGBTriple(255,255,0), //Yellow
            new RGBTriple(255,255,255) //White
        };

    /**
     * Constructor
     * @param name The name of the filter
     */
    public FSDitherColourPrinter(String name)
    {
        super(name);
    }

    /**
     * Apply this filter to an image
     * @param  image  The image to be changed by this filter
     * @return  The filtered image
     */
    public ColorImage apply(ColorImage image)
    {
        int height = image.getHeight();
        int width = image.getWidth();
        Color[][] pixelArray = new Color[width] [height];
        Color[][] dotArray = new Color[width] [height];

        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                pixelArray[x][y] = image.getPixel(x,y);
            }
        }

        for (int y=0; y<height; y++) {
            for (int x=0; x<width; x++) {
                dotArray[x][y] = getNearestColor( pixelArray[x][y] );
                int[] dith_error = colorDiff ( pixelArray[x][y] , dotArray[x][y] );

                if (x+1 != width){
                    pixelArray[x+1][y] =  colorMix( pixelArray[x+1][y], dith_error, (7.0/16)  );
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
     * Get the nearest available colour from the palette
     * @param  pixel  The individual pixel to be compared with the palette
     * @return  The nearest colour
     */
    private Color getNearestColor (Color pixel){
        int bestMatch = 0;
        int maxDist = 3*(255*255)+1;
        int newDist = 0;

        //Euclidean distance = sqrt[ (C1r - C2r)^2 + (C1g - C2g)^2 + (C1b - C2b)^2 ]
        for(int i = 0; i < RGBPalette.length; i++ ){
            int dist_red   = pixel.getRed()   - RGBPalette[i].getRed();
            int dist_green = pixel.getGreen() - RGBPalette[i].getGreen();
            int dist_blue  = pixel.getBlue()  - RGBPalette[i].getBlue();

            newDist = dist_red*dist_red + dist_green*dist_green + dist_blue*dist_blue;

            if (newDist == 0){
                return  RGBPalette[i].getColor();
            } else if (newDist < maxDist){
                maxDist = newDist;
                bestMatch = i;
            }
        }
        return  RGBPalette[bestMatch].getColor();
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
        int red = Math.min( pixelCol.getRed() + (int)( error[0]*offset ) ,255);
        int green = Math.min( pixelCol.getGreen() + (int)( error[1]*offset ),255);
        int blue  = Math.min( pixelCol.getBlue() + (int)( error[2]*offset),255);

        if(red < 0) red = 0;
        if(green < 0) green = 0;
        if(blue < 0) blue = 0;

        return new Color (red,green,blue);
    }

}

