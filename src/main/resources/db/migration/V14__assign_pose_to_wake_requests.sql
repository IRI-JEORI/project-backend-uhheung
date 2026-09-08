ALTER TABLE wake_requests
    ADD COLUMN pose_id BIGINT NULL AFTER receiver_id;

SET @previous_time_zone = @@SESSION.time_zone;
SET SESSION time_zone = '+00:00';

UPDATE wake_requests wake_request
JOIN daily_poses daily_pose
  ON daily_pose.wake_group_id = wake_request.wake_group_id
 AND daily_pose.pose_date = DATE(CONVERT_TZ(wake_request.requested_at, '+00:00', '+09:00'))
SET wake_request.pose_id = daily_pose.pose_id
WHERE wake_request.pose_id IS NULL;

SET SESSION time_zone = @previous_time_zone;

ALTER TABLE wake_requests
    ADD CONSTRAINT fk_wake_requests_pose FOREIGN KEY (pose_id) REFERENCES poses(id);
