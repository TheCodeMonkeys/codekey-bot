package codemonkeys.bots.codekey.modlog;

import io.discloader.discloader.entity.channel.IGuildChannel;
import io.discloader.discloader.entity.message.IMessage;
import io.discloader.discloader.entity.user.IUser;
import io.discloader.discloader.entity.util.SnowflakeUtil;

public class Case {

	/**
	 * {@code 0x0} means banned.<br>
	 * {@code 0x1} means kicked.<br>
	 * {@code 0x2} means unbanned.<br>
	 * {@code 0x3} means muted.<br>
	 * {@code 0x4} means unmuted.<br>
	 * {@code 0x5} means unknown.
	 */
	public byte status = 0x5;
	public long caseNumber = 0, msgID = 0;
	public long[] channelIDs;
	public User user, moderator;
	public String reason;

	public Case(long caseNumber, byte status, String reason, IUser user, IUser moderator, IMessage message, IGuildChannel... channels) {
		this.caseNumber = caseNumber;
		this.status = status;
		this.user = new User(user);
		this.moderator = new User(moderator);
		msgID = message.getID();
		channelIDs = new long[channels.length];
		for (int i = 0; i < channels.length; i++) {
			channelIDs[i] = channels[i].getID();
		}
	}

	public class User {
		public String id, name;

		public User(IUser data) {
			id = SnowflakeUtil.toString(data);
			name = data.toString();
		}
	}
}
