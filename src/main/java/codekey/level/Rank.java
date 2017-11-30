package codekey.level;

/**
 * Created by thvardhan from codemonkeys discord server
 * https://discord.gg/PAH8y8W on 9/23/17.
 */
public enum Rank {
	NO_RANK(0x0), KILOBYTE(0x2ecc71), MEGABYTE(0x37c289), GIGABYTE(0x40b8a2), TERABYTE(0x4aafba), PETABYTE(0x53a5d3), EXABYTE(0x5d9cec), UNKNOWN(0x0), STAFF(0x8067b7);

	private final int color;

	Rank(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}

}
