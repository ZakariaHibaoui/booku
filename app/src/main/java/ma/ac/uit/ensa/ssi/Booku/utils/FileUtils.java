package ma.ac.uit.ensa.ssi.Booku.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    // Method to save a binary file from a byte array to the app's data directory
    public static void write(Context context, String fileName, byte[] data)
    throws IOException {
        // Get the app's data directory
        File dir = context.getFilesDir();

        // Create a new file in the app's data directory
        File file = new File(dir, fileName);

        FileOutputStream fos = fos = new FileOutputStream(file);
        fos.write(data); // Write the byte array to the file
        fos.flush(); // Ensure all data is written

        if (fos != null) {
            fos.close(); // Close the stream
        }
    }

    public static byte[] read(Context context, String fileName) {
        File dir = context.getFilesDir();
        File file = new File(dir, fileName);

        FileInputStream fis = null;
        byte[] data = null;

        try {
            fis = new FileInputStream(file);
            data = new byte[(int) file.length()]; // Create a byte array of the file's length
            fis.read(data); // Read the file into the byte array
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return data; // Return the byte array
    }

    public static boolean delete(Context context, String fileName) {
        // Get the app's internal storage directory
        File dir = context.getFilesDir();

        // Create a File object for the specified file
        File file = new File(dir, fileName);

        // Delete the file and return the result
        return file.delete();
    }

    public static void setImageFromPath(Context ctx, ImageView imageView, String imageName, int defaultImageResId) {
        File dir = ctx.getFilesDir();
        File file = new File(dir, imageName);
        // Try to load the image from the specified path
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        if (bitmap != null) {
            // If the bitmap is successfully loaded, set it to the ImageView
            imageView.setImageBitmap(bitmap);
        } else {
            // If loading fails, set the default image
            imageView.setImageResource(defaultImageResId);
        }
    }
}
