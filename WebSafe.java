
import java.awt.Color;
import java.lang.Math.*;
import java.util.ArrayList;

/**
 *
 * @author  add your name here
 * @version 1st March 2011
 */
public class WebSafe extends Filter {

    private ArrayList<RGBTriple> WebPalette = new ArrayList<RGBTriple>();
    
    /**
     * Constructor
     * @param name The name of the filter
     */
    public WebSafe(String name) {
        super(name);
            for (int red = 0; red <= 255; red+=51) {
                for (int green = 0; green <= 255; green+=51) {
                    for (int blue = 0; blue <= 255; blue+=51) {
                        WebPalette.add(new RGBTriple(red, green, blue));
                    }
                }
            }
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

                if (x+1 != width) { 
                    pixelArray[x+1][y] =  colorMix( pixelArray[x+1][y], dith_error, (7.0/16)  );
                }

                if (y+1 != height){
                    if (x > 0) { pixelArray[x-1][y+1] = colorMix( pixelArray[x-1][y+1], dith_error, (3.0/16) ); }
                    pixelArray[x][y+1] = colorMix( pixelArray[x][y+1], dith_error, (5.0/16) );
                    if (x+1 != width) { pixelArray[x+1][y+1] = colorMix(pixelArray[x+1][y+1], dith_error, (1.0/16) ); }

                }
                image.setPixel( x, y, dotArray[x][y] );
            }
        }

        return image;
    }

   private Color getNearestColor (Color pixel){
        int bestMatch = 0;
        int dist = 3*(255*255)+1;
        int newDist = 0;

        //Euclidean distance = sqrt[ (C1r - C2r)^2 + (C1g - C2g)^2 + (C1b - C2b)^2 ]
        for(int i = 0; i < WebPalette.size(); i++ ){
            int dist_red   = pixel.getRed()   - WebPalette.get(i).getRed();
            int dist_green = pixel.getGreen() - WebPalette.get(i).getGreen();
            int dist_blue  = pixel.getBlue()  - WebPalette.get(i).getBlue();

            newDist = dist_red*dist_red + dist_green*dist_green + dist_blue*dist_blue;

            if (newDist == 0){
                return  WebPalette.get(i).getColor();
            } else if (newDist < dist){
                dist = newDist;
                bestMatch = i;
            }
        }
        return  WebPalette.get(bestMatch).getColor();
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
