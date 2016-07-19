package pl.czak.tvtalk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;

public class SlideshowActivity extends Activity {
    // Views
    private ImageView imageView;

    // PDF file related
    ParcelFileDescriptor fileDescriptor;
    PdfRenderer renderer;
    PdfRenderer.Page currentPage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        imageView = (ImageView) findViewById(R.id.image_view);

        try {
            openRenderer(this);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }

        showPage(1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            closeRenderer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                showPage(currentPage.getIndex() - 1);
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                showPage(currentPage.getIndex() + 1);
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    private void openRenderer(Context context) throws IOException {
        fileDescriptor = context.getAssets().openFd("slides.pdf").getParcelFileDescriptor();
        renderer = new PdfRenderer(fileDescriptor);
    }

    private void closeRenderer() throws IOException {
        if (currentPage != null) {
            currentPage.close();
        }
        renderer.close();
        fileDescriptor.close();
    }

    private void showPage(int index) {
        if (index < 0 || index >= renderer.getPageCount()) {
            return;
        }

        if (currentPage != null) {
            currentPage.close();
        }

        currentPage = renderer.openPage(index);
        Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        imageView.setImageBitmap(bitmap);
    }
}
