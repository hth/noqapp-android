package com.noqapp.android.client.utils;

import com.noqapp.android.common.model.types.QueueStatusEnum;
import com.noqapp.android.common.utils.Formatter;

/**
 * This is a utile class for token status.
 */
public class TokenStatusUtils {
  private static final String TAG = TokenStatusUtils.class.getSimpleName();

  // Private constructor
  private TokenStatusUtils () {
  }

  /**
   * Calculate the estimated wait time
   * @param avgServiceTime Average handling time for a token
   * @param positionInQueue assigned token position in queue
   * @param queueStatus current status of queue
   * @return the estimated wait time
   */
  public static String calculateEstimatedWaitTime(long avgServiceTime, int positionInQueue,
                                                  QueueStatusEnum queueStatus, int startHour) {
    if(avgServiceTime > 0 && positionInQueue > 0 ) {
      if(queueStatus == QueueStatusEnum.S) {
        long timeToStoreStartInMilli = Formatter.computeTimeToStoreStart(startHour);
        return GetTimeAgoUtils.getTimeAgo((positionInQueue * avgServiceTime) + timeToStoreStartInMilli );
      } else {
        return GetTimeAgoUtils.getTimeAgo(positionInQueue * avgServiceTime);
      }
    }
    return null;
  }



}
