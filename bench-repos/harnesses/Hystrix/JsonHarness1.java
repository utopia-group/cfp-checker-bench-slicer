import java.util.Collections;
import com.netflix.hystrix.metric.HystrixRequestEvents;
import com.netflix.hystrix.serial.SerialHystrixRequestEvents;

public class JsonHarness1
{
    private static int nd$int()
    {
        return 1;
    }

    public static void main(String[] args)
    {
        // We over-approximate collections, so an emptySet should be fine.
        HystrixRequestEvents requestEvents = new HystrixRequestEvents(Collections.emptySet());
        SerialHystrixRequestEvents.toJsonString(requestEvents);
    }
}
