import android.graphics.Canvas;
import android.content.Context;
import android.app.Activity;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;

import com.bumptech.glide.request.target.FixedSizeDrawable;

public class Harness
{
    private static int nd$int()
    {
        return 1;
    }

    private static void assume(boolean b)
    {
    }

    public static void main(String[] args)
    {
        Canvas c = new Canvas();
        Picture p = new Picture();
        PictureDrawable pDrawable = new MockDrawable(p);
        int width = nd$int();
        int height = nd$int();

        assume(width > 0 && height > 0);

        int n = nd$int();
        assume(n > 0);

        for (int i = 0; i < n; ++i)
        {
            FixedSizeDrawable d1 = new FixedSizeDrawable(pDrawable, width, height);
            FixedSizeDrawable d2 = new FixedSizeDrawable(d1, width, height);
            d2.draw(c);
            d1.draw(c);
        }
    }

    private static class MockDrawable extends PictureDrawable
    {
        public MockDrawable(Picture p)
        {
            super(p);
        }

        @Override
        public void draw(Canvas c)
        {
            c.save();
            super.draw(c);
            c.restore();
        }
    }
}
