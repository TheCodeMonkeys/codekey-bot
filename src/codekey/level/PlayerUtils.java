package codekey.level;

import codekey.main.Main;
import net.dv8tion.jda.core.entities.Role;

import java.util.List;

/**
 * Created by thvardhan from codemonkeys discord server https://discord.gg/PAH8y8W on 9/23/17.
 */
public class PlayerUtils {

    public static boolean listContainsId(String id) {
        for (int i = 0; i < Main.players.size(); i++) {
            if (Main.players.get(i).getId().equals(id)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Adds a new entry to player based on their present rank
     *
     * @param ID
     * @param role
     */
    public static void addNewPlayerEntryWithRank(String ID, List<Role> role) {
        if (role.isEmpty())
            Main.players.add(new Player(ID, 0));
        else
            Main.players.add(new Player(ID, getExpFromRank(getRankFromRole(role.get(0).getId()))));
    }

    public static Rank getRankFromExp(int exp) {
        if (exp <= 76) return Rank.NO_RANK;
        if (exp > 76 && exp <= 464) return Rank.KILOBYTE;
        if (exp > 464 && exp <= 949) return Rank.MEGABYTE;
        if (exp > 949 && exp <= 1758) return Rank.TERABYTE;
        if (exp > 1758 && exp <= 2676) return Rank.PETABYTE;
        if (exp > 2676 && exp <= 3812) return Rank.EXABYTE;
        return Rank.GOD;
    }

    public static int getExpFromRank(Rank rank) {
        if (rank == Rank.NO_RANK) return 0;
        if (rank == Rank.MEGABYTE) return 76;
        if (rank == Rank.GIGABYTE) return 464;
        if (rank == Rank.TERABYTE) return 949;
        if (rank == Rank.PETABYTE) return 1758;
        if (rank == Rank.EXABYTE) return 2676;
        return 10000;
    }

    public static Rank getRankFromRole(String roleID) {
        if (roleID.equals("219278753849671690"))
            return Rank.KILOBYTE;
        else if (roleID.equals("219278815866781697"))
            return Rank.MEGABYTE;
        else if (roleID.equals("219278838750773249"))
            return Rank.GIGABYTE;
        else if (roleID.equals("219278855372931072"))
            return Rank.TERABYTE;
        else if (roleID.equals("219278875203600384"))
            return Rank.PETABYTE;
        else if (roleID.equals("241334568039219200"))
            return Rank.EXABYTE;
        else return Rank.GOD;
    }

}
