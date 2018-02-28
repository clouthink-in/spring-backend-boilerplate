package in.clouthink.synergy.team.engine.actor;

import in.clouthink.synergy.account.domain.model.User;

import java.io.Serializable;

/**
 * @auther dz
 */
public class PrintActivityRequest implements Serializable {

    private final String activityId;

    private final User user;

    public PrintActivityRequest(String activityId, User user) {
        this.activityId = activityId;
        this.user = user;
    }

    public String getActivityId() {
        return activityId;
    }

    public User getUser() {
        return user;
    }
}