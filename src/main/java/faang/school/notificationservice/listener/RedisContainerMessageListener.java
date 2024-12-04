package faang.school.notificationservice.listener;

import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

public interface RedisContainerMessageListener {

    MessageListenerAdapter getAdapter();
    ChannelTopic getTopic();
}
