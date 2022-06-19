import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class reads bytes in a 100x100 bitmap and converts the data into .PPM images (and one .PGM image) with
 * visual alterations including: mirroring, color inverting, converting to black and white, and stretching
 * vertically by 200% (nearest neighbor).
 * @author Mark Valentino
 *
 */
public class ConvertToPPM {
	
    static int imageWidth = 0;
    static int imageHeight = 0;
     
    public static void main(String[] args) {
    	
    	// Image is 100x100 pixels.
    	File image = new File("tiger.bmp");
        FileInputStream fin = null;

        try {
            fin = new FileInputStream(image);
            // Holds signed byte values of the entire image.
            byte fileContent[] = new byte[(int)image.length() ];
            // Holds ASCII printable hex values of image data minus the header.
            ArrayList<Integer> rgbValues = new ArrayList<>();
             
            fin.read(fileContent);
            
            /* Extracts RGB hex values from the image. The order in which data is extracted
             * results in a mirrored image.
             */
            for (int i = 0; i < fileContent.length ; i++) {
            	if (i == 18) {
            		imageWidth = fileContent[i];
            	}
            	if (i == 22) {
            		imageHeight = fileContent[i];
            	}
            	// Byte at index 53 is where the bitmap file header ends.
            	if (i > 53) {
            		// Lambda function converts bytes from signed to unsigned.
            		rgbValues.add(fileContent[i] < 0 ? 256 + fileContent[i] : fileContent[i]);
            	}
            }
            
            // Writes a mirrored image to PPM file.
            writeToFile(rgbValues, "tiger.ppm");
            // Writes a color inverted image to a PPM file.
            writeToFile(invertColors(rgbValues), "tiger_inverted.ppm");
            // Writes black and white image to PGM file.
            convertToBlackandWhite(rgbValues, "tiger_baw.pgm");
            // Writes vertically stretched image to PPM file.
            stretchImage(rgbValues, "tiger_stretched.ppm");
            
        }
        
        catch (FileNotFoundException e) {
            System.out.println("File not found" + e);
        }
        
        catch (IOException ioe) {
            System.out.println("Exception while reading file " + ioe);
        }
    }
    
    /**
     * Method to write RGB data to a PPM file.
     * @param rgbValues the ArrayList of RGB values to write.
     * @param name the name of the file to be written.
     */
    public static void writeToFile(ArrayList<Integer> rgbValues, String name) {
    	try {
    		FileWriter fw = new FileWriter(name);
    		// Writes the file header of PPM image.
    		fw.write("P3\n" + imageWidth + " " +imageHeight + "\n255\n");
    		/* rgbValues is indexed from the highest to the lowest index because
    		 * pixels in the read bitmap image are stored upsidedown.
    		 */
    		for (int i = rgbValues.size() - 1; i >= 0 ; i--) {
    			/* PPM image needs a new line per 100 pixels. There are 3 ints
    			 * that hold data for one pixel which are the red, green, and blue
    			 * color channels.
    			 */
    			if (i % (imageWidth * 3)  == 0) {
    				fw.write("\n");
    			}
    			fw.write(rgbValues.get(i) + " ");
    		}
            fw.close();
            System.out.println("Successfully wrote " + name);
          }
    	
    	catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }
    
    /**
     * Method to invert the colors of RGB values in an ArrayList.
     * @param rgbValues the ArrayList of RGB values to invert
     * @return invertedRGBValues the resulting ArrayList to be returned.
     */
    public static ArrayList<Integer> invertColors(ArrayList<Integer> rgbValues) {
    	ArrayList<Integer> invertedRGBValues = new ArrayList<Integer>();
    	for (int i = 0; i < rgbValues.size(); i++) {
    		invertedRGBValues.add(Math.abs(rgbValues.get(i) - 255)); 
    	}
    	return invertedRGBValues;
    }
    
    /**
     * Method to convert rgbValues to black and white and print a .PGM image.
     * @param rgbValues the ArrayList of RGB values to convert
     * @param name the name of the file to be written.
     */
    public static void convertToBlackandWhite(ArrayList<Integer> rgbValues, String name) {
    	ArrayList<Integer> bawRGBValues = new ArrayList<Integer>();
    	int tempRed = 0;
    	int tempGreen = 0;
    	int tempBlue = 0;
    	// Average value of all color channels per pixel.
    	int average;
    	
    	for (int i = 2; i < rgbValues.size(); i+=3) {
        	tempRed = rgbValues.get(i-2);
        	tempGreen = rgbValues.get(i-1);
        	tempBlue = rgbValues.get(i);
    		average = (tempRed + tempGreen + tempBlue) / 3;
    		bawRGBValues.add(average);
    	}
    	
    	try {
    		FileWriter fw = new FileWriter(name);
    		// Writes the file header of PPM image.
    		fw.write("P2\n" + imageWidth + " " +imageHeight + "\n255\n");
  
    		for (int i = bawRGBValues.size() - 1; i >= 0 ; i--) {
    			fw.write(bawRGBValues.get(i) + " ");
    			/* PPM image needs a new line per 100 pixels. Only one int
    			 * needed per pixel since the result will be black and white.
    			 */
    			if (i % imageWidth == 0) {
    				fw.write("\n");
    			}
    		}
            fw.close();
            System.out.println("Successfully wrote " + name);
          }
    	
    	catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }	
    }
    
    /**
     * Method to stretch (by 200% vertically) and write RGB data to a PPM file.
     * @param rgbValues the ArrayList of RGB values to write and scale.
     * @param name the name of the file to be written.
     */
    public static void stretchImage(ArrayList<Integer> rgbValues, String name) {
    	try {
    		String tempString = "";
    		FileWriter fw = new FileWriter(name);
    		// Writes the file header of PPM image.
    		fw.write("P3\n" + imageWidth + " " + imageHeight*2 + "\n255\n");
    		/* rgbValues is indexed from the highest to the lowest index because
    		 * pixels in the read bitmap image are stored upsidedown.
    		 */
    		for (int i = rgbValues.size() - 1; i >= 0 ; i--) {
    			tempString += rgbValues.get(i) + " ";
    			/* PPM image needs a new line per 100 pixels. There are 3 ints
    			 * that hold data for one pixel which are the red, green, and blue
    			 * color channels.
    			 */
    			if (i % (imageWidth * 3) == 0) {
    				// write function called twice to stretch image.
    				fw.write( tempString + "\n");
    				fw.write( tempString + "\n");
    				tempString = "";
    			}
    		}
            fw.close();
            System.out.println("Successfully wrote " + name);
          }
    	
    	catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
          }
    }
}
