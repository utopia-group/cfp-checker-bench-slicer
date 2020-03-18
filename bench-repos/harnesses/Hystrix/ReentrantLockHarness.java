import com.netflix.hystrix.util.HystrixRollingPercentile;
import com.netflix.hystrix.strategy.properties.HystrixProperty;

public class ReentrantLockHarness
{
    private static int nd$int()
    {
        return 0;
    }

    private static boolean nd$boolean()
    {
        return false;
    }

    static void assume(boolean b)
    {

    }

    public static void main(String[] args)
    {
        HystrixProperty<Boolean> property = HystrixProperty.Factory.asProperty(nd$boolean());
        int timeInMilliseconds = nd$int(), numberOfBuckets = nd$int(), bucketDataLength = nd$int();
        assume(timeInMilliseconds > 0);
        assume(numberOfBuckets > 0);
        assume(bucketDataLength > 0);
        HystrixRollingPercentile rollingPercentile = new HystrixRollingPercentile(timeInMilliseconds, numberOfBuckets, bucketDataLength, property);

        rollingPercentile.getMean();
    }
}
