package com.isoft.struts2.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.opensymphony.xwork2.util.TextParseUtil;

public class ContentParseUtil extends TextParseUtil {
    
    /**
     * Returns a set from semicolon delimted Strings.
     * @param s The String to parse.
     * @return A set from semicolon delimted Strings.
     */
    public static Set<String> semicolonDelimitedStringToSet(String s) {
        Set<String> set = new HashSet<String>();
        String[] split = s.split(";");
        for (String aSplit : split) {
            String trimmed = aSplit.trim();
            if (trimmed.length() > 0)
                set.add(trimmed);
        }
        return set;
    }
    
    /**
     * Returns a set from colon delimted Strings.
     * @param s The String to parse.
     * @return A set from colon delimted Strings.
     */
    public static List<String> colonDelimitedStringToSet(String s) {
        List<String> set = new ArrayList<String>();
        String[] split = s.split(":");
        for (String aSplit : split) {
            String trimmed = aSplit.trim();
            set.add(trimmed);                
        }
        return set;
    }
    
    /**
     * Returns a set from parentheses delimted Strings.
     * @param s The String to parse.
     * @return A set from parentheses delimted Strings.
     */
    public static List<String> parenthesesDelimitedStringToSet(String s) {
        List<String> set = new ArrayList<String>();
        String[] split = s.split("\\(|\\)");
        for (String aSplit : split) {
            String trimmed = aSplit.trim();
            if(trimmed.length()>0){
                set.add(trimmed);
            }
        }
        return set;
    }
    
    /**
     * Returns a set from equal delimted Strings.
     * @param s The String to parse.
     * @return A set from equal delimted Strings.
     */
    public static List<String> equalDelimitedStringToSet(String s) {
        List<String> set = new ArrayList<String>();
        String[] split = s.split("=");
        for (String aSplit : split) {
            String trimmed = aSplit.trim();
            set.add(trimmed);
        }
        return set;
    }
}
