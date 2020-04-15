import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;

import com.facebook.litho.MatrixDrawable;

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
        MatrixDrawable<Drawable> d = new MatrixDrawable<>();
        d.mount(pDrawable);

        int n = nd$int();
        assume(n > 0);

        for (int i = 0; i < n; ++i)
        {
            MatrixDrawable<Drawable> curr = new MatrixDrawable<>();
            d = new MatrixDrawable<>();
            d.mount(curr);
        }

        pDrawable.draw(c);
        d.draw(c);
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
