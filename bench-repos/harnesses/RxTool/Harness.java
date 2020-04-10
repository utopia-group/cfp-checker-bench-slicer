import com.tamsiree.rxui.view.loadingview.style.MultiplePulse;
import com.tamsiree.rxui.view.loadingview.style.MultiplePulseRing;
import com.tamsiree.rxui.view.loadingview.style.DoubleBounce;

import android.graphics.Canvas;

public class Harness
{
    public static void main(String[] args)
    {
        MultiplePulse mp = new MultiplePulse();
        MultiplePulseRing mpr = new MultiplePulseRing();
        DoubleBounce db = new DoubleBounce();
        Canvas c = new Canvas();

        mp.draw(c);
        db.draw(c);
        mpr.draw(c);
    }
}
