package android.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;

import com.google.android.exoplayer2.testutil.HostActivity;

public class ActivityHarness
{
    private static int nd$int()
    {
        return 1;
    }

    public static void activityHarness(Activity a)
    {
        boolean onCreate = true, onStart = false;
        boolean onResume = false, onPause = false;
        boolean onStop = false, onDestroy = false;
        boolean onRestart = false;

        while (true)
        {
            if (onCreate)
            {
                a.onCreate(new Bundle(), new PersistableBundle());
                onCreate = false;
                onStart = true;
            }
            else if (onStart)
            {
                a.onStart();
                onStart = false;
                onResume = true;
            }
            else if (onResume)
            {
                a.onResume();
                onResume = false;
                onPause = true;
            }
            else if (onPause)
            {
                a.onPause();
                onPause = false;

                int nextState = nd$int();
                if (nextState == 1)
                {
                    onStop = true;
                }
                else if (nextState == 2)
                {
                    onResume = true;
                }
                else
                {
                    onCreate = true;
                }
            }
            else if (onStop)
            {
                a.onStop();
                onStop = false;

                int nextState = nd$int();
                if (nextState == 1)
                {
                    onDestroy = true;
                }
                else if (nextState == 2)
                {
                    onRestart = true;
                }
                else
                {
                    onCreate = true;
                }
            }
            else if (onRestart)
            {
                a.onRestart();
                onRestart = false;
                onStart = true;
            }
            else if (onDestroy)
            {
                a.onDestroy();
                onDestroy = false;
                break;
            }
        }
    }

    public static void main(String args[])
    {
        activityHarness(new HostActivity());
    }
}
