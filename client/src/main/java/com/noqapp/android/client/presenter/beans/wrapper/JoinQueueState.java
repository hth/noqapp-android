package com.noqapp.android.client.presenter.beans.wrapper;

/**
 * User: hitender
 * Date: 7/11/17 7:56 AM
 */

public class JoinQueueState {

    private boolean isJoinNotPossible;
    private String joinErrorMsg;

    public boolean isJoinNotPossible() {
        return isJoinNotPossible;
    }

    public JoinQueueState setJoinNotPossible(boolean joinNotPossible) {
        isJoinNotPossible = joinNotPossible;
        return this;
    }

    public String getJoinErrorMsg() {
        return joinErrorMsg;
    }

    public JoinQueueState setJoinErrorMsg(String joinErrorMsg) {
        this.joinErrorMsg = joinErrorMsg;
        return this;
    }
}
