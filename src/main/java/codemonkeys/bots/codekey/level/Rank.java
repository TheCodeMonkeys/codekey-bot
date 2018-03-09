package codemonkeys.bots.codekey.level;

/**
 * Created by thvardhan from codemonkeys discord server
 * https://discord.gg/PAH8y8W on 9/23/17.
 */
public enum Rank {
	NO_RANK(0x0, 0l), KILOBYTE(0x2ecc71, 219278753849671690l), MEGABYTE(0x37c289, 219278815866781697l), GIGABYTE(0x40b8a2, 219278838750773249l), TERABYTE(0x4aafba, 219278855372931072l), PETABYTE(0x53a5d3, 219278875203600384l), EXABYTE(0x5d9cec, 241334568039219200l), UNKNOWN(0x0, 0l), STAFF(0x8067b7, 219266745729286145l);

	private final int color;
	private final long id;

	Rank(int color, long id) {
		this.color = color;
		this.id = id;
	}

	public int getColor() {
		return color;
	}

	public long getID() {
		return id;
	}

}
