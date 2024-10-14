package ma.ac.uit.ensa.ssi.Booku.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutionException;


public class ImagePicker {
    public static final int PICK_IMAGE = 103;

    private static boolean requestPermission(Activity ctx) {
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ctx, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            return false;
        }
        return true;
    }

    public static void openImagePicker(Activity ctx) {
        if (!requestPermission(ctx)) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ctx.startActivityForResult(intent, PICK_IMAGE);
    }

    public static byte[] to_bytes(ImageView imageView) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    public static byte[] downloadImage(String imageUrl, Context ctx)
            throws ExecutionException, InterruptedException {
        // Use Glide to download the image
        FutureTarget<Bitmap> futureTarget = Glide.with(ctx)
                .asBitmap()
                .load(imageUrl)
                .submit();

        Bitmap bitmap = futureTarget.get();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
}
