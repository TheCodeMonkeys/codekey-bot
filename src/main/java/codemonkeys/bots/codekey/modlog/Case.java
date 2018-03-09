package codemonkeys.bots.codekey.modlog;

import io.discloader.discloader.entity.message.IMessage;
import io.discloader.discloader.entity.user.IUser;
import io.discloader.discloader.entity.util.SnowflakeUtil;

public class Case {

	/**
	 * {@code 0} means banned.<br>
	 * {@code 1} means kicked.<br>
	 * {@code 2} means unbanned.<br>
	 * {@code 3} means unknown.
	 */
	public byte status = 3;
	public long caseNumber = 0;
	public User user, moderator;
	public String msgID, reason;

	public Case(long caseNumber, byte status, String reason, IUser user, IUser moderator, IMessage message) {
		this.caseNumber = caseNumber;
		this.status = status;
		this.user = new User(user);
		this.moderator = new User(moderator);
		msgID = SnowflakeUtil.asString(message);
	}

	public class User {
		public String id, name;

		public User(IUser data) {
			id = SnowflakeUtil.asString(data);
			name = data.toString();
		}
	}
}
