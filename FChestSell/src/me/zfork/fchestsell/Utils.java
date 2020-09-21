package me.zfork.fchestsell;

import java.util.regex.Pattern;

import org.bukkit.block.BlockFace;

public class Utils {
	
	public static final BlockFace[] SHOP_FACES = {BlockFace.SELF, BlockFace.DOWN, BlockFace.UP, BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH};
	public static final Pattern[] SHOP_SIGN_PATTERN = {
			Pattern.compile("^?[\\w -.]*$"), 
			Pattern.compile("^[1-9][0-9]*$"),
			Pattern.compile("(?i)^[\\d.bs(free) :]+$"),
            Pattern.compile("^[\\w? #:-]+$")
    };
	
	public static boolean isValidSign(String[] line) {
        return isValidPreparedSign(line) && (line[2].toUpperCase().contains("B") || line[2].toUpperCase().contains("S")) && !line[0].isEmpty();
    }
	
	public static boolean isValidPreparedSign(String[] lines) {
        for (int i = 0; i < 4; i++)
            if (!SHOP_SIGN_PATTERN[i].matcher(lines[i]).matches()) return false;
        return lines[2].indexOf(':') == lines[2].lastIndexOf(':');
    }

}
