package org.apache.hadoop.mapred;

import java.io.StringWriter;

import org.apache.hadoop.mapred.QueueManager;
import org.apache.hadoop.mapred.Queue;
import org.apache.hadoop.conf.Configuration;

public class Harness
{
    public static void main(String[] args) throws Exception
    {
        // Queue q = new Queue();
        StringWriter writer = new StringWriter();
        // q.addChild(new Queue());
        Configuration conf = new Configuration();
        // conf.set(QueueManager.QUEUE_CONF_PROPERTY_NAME_PREFIX + "first.acl-submit-job", "user1,user2 group1,group2");
        // conf.set(QueueManager.QUEUE_CONF_PROPERTY_NAME_PREFIX + "first.state", "running");
        // conf.set(QueueManager.QUEUE_CONF_PROPERTY_NAME_PREFIX + "second.state", "stopped");
        QueueManager.dumpConfiguration(writer, conf);
    }
}
