package com.box.common.extension.spanner;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.text.style.ImageSpan;

public class CenteredImageSpan extends ImageSpan {
    public CenteredImageSpan(Context context, Bitmap b) {
        super(context, b);
    }

    public CenteredImageSpan(Drawable d, int verticalAlignment) {
        super(d, verticalAlignment);
    }

    public CenteredImageSpan(Context context, Bitmap b, int verticalAlignment) {
        super(context, b, verticalAlignment);
    }

    public CenteredImageSpan(Drawable d) {
        super(d);
    }

    public CenteredImageSpan(Drawable d, String source) {
        super(d, source);
    }

    public CenteredImageSpan(Drawable d, String source, int verticalAlignment) {
        super(d, source, verticalAlignment);
    }

    public CenteredImageSpan(Context context, Uri uri) {
        super(context, uri);
    }

    public CenteredImageSpan(Context context, Uri uri, int verticalAlignment) {
        super(context, uri, verticalAlignment);
    }

    public CenteredImageSpan(Context context, int resourceId) {
        super(context, resourceId);
    }

    public CenteredImageSpan(Context context, int resourceId, int verticalAlignment) {
        super(context, resourceId, verticalAlignment);
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
        // image to draw
        Drawable b = getDrawable();
        // font metrics of text to be replaced
        Paint.FontMetricsInt fm = paint.getFontMetricsInt();
        int transY = (y + fm.descent + y + fm.ascent) / 2 - b.getBounds().bottom / 2;

        canvas.save();
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }
}