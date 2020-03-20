import java.util.Map;
import java.util.HashMap;

import com.netflix.hystrix.metric.sample.HystrixUtilization;
import com.netflix.hystrix.metric.sample.HystrixCommandUtilization;
import com.netflix.hystrix.metric.sample.HystrixThreadPoolUtilization;
import com.netflix.hystrix.serial.SerialHystrixUtilization;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;

public class JsonHarness2
{
    public static void main(String[] args)
    {
        // We over-approximate collections, so empty maps should be fine.
        Map<HystrixCommandKey, HystrixCommandUtilization> commandUtilizationMap = new HashMap<>();
        Map<HystrixThreadPoolKey, HystrixThreadPoolUtilization> threadPoolUtilizationMap = new HashMap<>();
        HystrixUtilization utilization = new HystrixUtilization(commandUtilizationMap, threadPoolUtilizationMap);
        SerialHystrixUtilization.toJsonString(utilization);
    }
}
