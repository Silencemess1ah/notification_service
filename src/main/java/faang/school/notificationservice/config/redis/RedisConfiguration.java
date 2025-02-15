package faang.school.notificationservice.config.redis;

import faang.school.notificationservice.listener.AchievementEventListener;
import faang.school.notificationservice.listener.SkillAcquiredEventMessageListener;
import faang.school.notificationservice.listener.comment.NewCommentEventListener;
import faang.school.notificationservice.listener.follower.FollowerEventListener;
import faang.school.notificationservice.listener.goal.GoalCompletedEventListener;
import faang.school.notificationservice.listener.like.LikePostEventListener;
import faang.school.notificationservice.listener.recommendation.RecommendationEventListener;
import faang.school.notificationservice.listener.profile.ProfileViewEventListener;
import faang.school.notificationservice.listener.projectfollower.ProjectFollowerMessageListener;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.util.Pair;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfiguration {

    private final RedisProperties redisProperties;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(List<Pair<MessageListenerAdapter, ChannelTopic>> requesters,
            JedisConnectionFactory jedisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnectionFactory);
        requesters.forEach(
                (requester) -> container.addMessageListener(requester.getFirst(), requester.getSecond()));

        return container;
    }

    @Bean
    ChannelTopic goalCompletedEventTopic() {
        return new ChannelTopic(redisProperties.getChannels().getGoalCompletedEvent());
    }

    @Bean
    ChannelTopic recommendationRequestEventTopic() {
        return new ChannelTopic(redisProperties.getChannels().getRecommendationRequestEvent());
    }

    @Bean
    public Pair<MessageListenerAdapter, ChannelTopic> recommendationRequestEventPair(
            MessageListenerAdapter recommendationEventListenerAdapter,
            ChannelTopic recommendationRequestEventTopic) {
        return Pair.of(recommendationEventListenerAdapter, recommendationRequestEventTopic);
    }

    @Bean
    MessageListenerAdapter recommendationEventListenerAdapter(RecommendationEventListener recommendationEventListener) {
        return new MessageListenerAdapter(recommendationEventListener);
    }

    @Bean
    MessageListenerAdapter goalCompletedMessageListener(GoalCompletedEventListener goalCompletedEventListener) {
        return new MessageListenerAdapter(goalCompletedEventListener);
    }

    @Bean
    public ChannelTopic newCommentEventTopic() {
        return new ChannelTopic(redisProperties.getChannels().getNewComment());
    }

    @Bean
    public MessageListenerAdapter newCommentMessageListener(NewCommentEventListener newCommentEventListener) {
        return new MessageListenerAdapter(newCommentEventListener);
    }

    @Bean
    public ChannelTopic followerTopic() {
        return new ChannelTopic(redisProperties.getChannels().getFollower());
    }

    @Bean
    public MessageListenerAdapter followerMessageListener(FollowerEventListener followerEventListener) {
        return new MessageListenerAdapter(followerEventListener);
    }

    @Bean
    public ChannelTopic likePostTopic() {
        return new ChannelTopic(redisProperties.getChannels().getLikePostChannel());
    }

    @Bean
    public MessageListenerAdapter likePostMessageListener(LikePostEventListener likePostEventListener) {
        return new MessageListenerAdapter(likePostEventListener);
    }

    @Bean
    public MessageListenerAdapter profileViewMessageListener(ProfileViewEventListener profileViewEventListener) {
        return new MessageListenerAdapter(profileViewEventListener);
    }

    @Bean
    public ChannelTopic profileViewTopic(){
        return new ChannelTopic(redisProperties.getChannels().getProfileView());
    }

    @Bean
    public ChannelTopic skillAcquiredTopic() {
        return new ChannelTopic(redisProperties.getChannels().getSkillAcquiredChannel());
    }

    @Bean
    public MessageListenerAdapter skillAcquiredListener(
            SkillAcquiredEventMessageListener skillAcquiredEventMessageListener) {
        return new MessageListenerAdapter(skillAcquiredEventMessageListener);
    }

    @Bean
    public ChannelTopic achievementEventTopic() {
        return new ChannelTopic(redisProperties.getChannels().getAchievementEvent());
    }

    @Bean
    public MessageListenerAdapter achievementMessageListener(AchievementEventListener achievementEventListener) {
        return new MessageListenerAdapter(achievementEventListener);
    }



    @Bean
    public Pair<MessageListenerAdapter, ChannelTopic> skillAcquiredPair(MessageListenerAdapter skillAcquiredListener,
            ChannelTopic skillAcquiredTopic) {
        return Pair.of(skillAcquiredListener, skillAcquiredTopic);
    }

    @Bean
    public ChannelTopic projectFollowerTopic() {
        return new ChannelTopic(redisProperties.getChannels().getProjectChannel());
    }

    @Bean
    public MessageListenerAdapter projectFollowerMessageListenerAdapter(
            ProjectFollowerMessageListener projectFollowerMessageListener) {
        return new MessageListenerAdapter(projectFollowerMessageListener);
    }

    @Bean
    public Pair<MessageListenerAdapter, ChannelTopic> followerPair(MessageListenerAdapter followerMessageListener,
            ChannelTopic followerTopic) {
        return Pair.of(followerMessageListener, followerTopic);
    }

    @Bean
    public Pair<MessageListenerAdapter, ChannelTopic> goalCompletedPair(
            MessageListenerAdapter goalCompletedMessageListener,
            ChannelTopic goalCompletedEventTopic) {
        return Pair.of(goalCompletedMessageListener, goalCompletedEventTopic);
    }

    @Bean
    public Pair<MessageListenerAdapter, ChannelTopic> likePostPair(MessageListenerAdapter likePostMessageListener,
            ChannelTopic likePostTopic) {
        return Pair.of(likePostMessageListener, likePostTopic);
    }

    @Bean
    public Pair<MessageListenerAdapter, ChannelTopic> newCommentPair(MessageListenerAdapter newCommentMessageListener,
            ChannelTopic newCommentEventTopic) {
        return Pair.of(newCommentMessageListener, newCommentEventTopic);
    }

    @Bean
    public Pair<MessageListenerAdapter, ChannelTopic> achievementPair(MessageListenerAdapter achievementMessageListener,
            ChannelTopic achievementEventTopic) {
        return Pair.of(achievementMessageListener, achievementEventTopic);
    }

    @Bean
    public Pair<MessageListenerAdapter, ChannelTopic> profileViewPair(MessageListenerAdapter profileViewMessageListener,
                                                                      ChannelTopic profileViewTopic) {
        return Pair.of(profileViewMessageListener, profileViewTopic);
    }

    @Bean
    public Pair<MessageListenerAdapter, ChannelTopic> projectPair(
            MessageListenerAdapter projectFollowerMessageListenerAdapter,
            ChannelTopic projectFollowerTopic) {
        return Pair.of(projectFollowerMessageListenerAdapter, projectFollowerTopic);
    }
}
