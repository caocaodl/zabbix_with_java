package com.isoft.zend.ext.standard;

import static java.lang.Character.isDigit;
import static java.lang.Character.isWhitespace;
import static java.lang.Character.toUpperCase;

public class StrnatCmp {

	private StrnatCmp() {
	}
	
	private static int compare_right(tref tRef, char[] achs, int aend, char[] bchs, int bend) {
		int bias = 0;
		 /* The longest run of digits wins.  That aside, the greatest 
			 value wins, but we can't know that it will until we've scanned 
			 both numbers to know that they have the same magnitude, so we 
			 remember it in BIAS. */  
		for(;; tRef.ap++, tRef.bp++) {
			if ((tRef.ap == aend || !isDigit(achs[tRef.ap])) &&(tRef.bp == bend || !isDigit(bchs[tRef.bp])))  
				return bias;
			else if (tRef.ap == aend || !isDigit(achs[tRef.ap]))
				return -1;
			else if (tRef.bp == bend || !isDigit(bchs[tRef.bp]))
				return 1;
			else if (achs[tRef.ap] < bchs[tRef.bp]) {
				if (bias == 0) bias = -1;
			} else if (achs[tRef.ap] > bchs[tRef.bp]) {
				if (bias == 0) bias = 1;
			}
		}
	}

	private static int compare_left(tref tRef, char[] achs, int aend, char[] bchs, int bend) {
		/*	Compare two left-aligned numbers: the first to have a 
			different value wins. */
		for (;; tRef.ap++, tRef.bp++) {
			if ((tRef.ap == aend || !isDigit(achs[tRef.ap])) && (tRef.bp == bend || !isDigit(bchs[tRef.bp])))
				return 0;
			else if (tRef.ap == aend || !isDigit(achs[tRef.ap]))
				return -1;
			else if (tRef.bp == bend || !isDigit(bchs[tRef.bp]))
				return 1;
			else if (achs[tRef.ap] < bchs[tRef.bp]) {
				return -1;
			} else if (achs[tRef.ap] > bchs[tRef.bp]) {
				return 1;
			}
		}
	}
	
	protected static int strnatcmp_ex(String left, String right, boolean fold_case) {
		if (left == null) {
			return -1;
		}
		if (right == null) {
			return 1;
		}
		if (left.length() == 0) {
			return -1;
		}
		if (right.length() == 0) {
			return 1;
		} 
	
		boolean fractional, leading = true;
		int result;
		tref tRef = new tref();
		tRef.ap = 0;
		tRef.bp = 0;
		char[] achs = left.toCharArray();
		char[] bchs = right.toCharArray();
		int aend = achs.length, bend = bchs.length;
		char ca,cb;
		//Character.isDigit(ch)
		while(true){
			ca = achs[tRef.ap];
			cb = bchs[tRef.bp];
			/* skip over leading zeros */
			while (leading && ca == '0' && (tRef.ap+1 < aend) && isDigit(achs[tRef.ap+1])) {
				ca = achs[++tRef.ap];
			}
			while (leading && cb == '0' && (tRef.bp+1 < bend) && isDigit(achs[tRef.bp+1])) {
				cb = bchs[++tRef.bp];
			}
			leading = false;
			/* Skip consecutive whitespace */
			while (isWhitespace(ca) && (tRef.ap+1 < aend)){
				ca = achs[++tRef.ap];
			}
			while (isWhitespace(cb) && (tRef.bp+1 < bend)){
				cb = bchs[++tRef.bp];
			}
			/* process run of digits */
			if (isDigit(ca)  &&  isDigit(cb)) {
				fractional = (ca == '0' || cb == '0');
				if (fractional)
					result = compare_left(tRef, achs, aend, bchs, bend);
				else
					result = compare_right(tRef, achs, aend, bchs, bend);
				if (result != 0)
					return result;
				else if (tRef.ap == aend && tRef.bp == bend)
					/* End of the strings. Let caller sort them out. */
					return 0;
				else {
					/* Keep on comparing from the current point. */
					ca = achs[tRef.ap];  cb = bchs[tRef.bp]; 
				}
			}
			if (fold_case) {
				ca = toUpperCase(ca);
				cb = toUpperCase(cb);
			}
			if (ca < cb)
				return -1;
			else if (ca > cb)
				return 1;
			++tRef.ap; ++tRef.bp;
			if (tRef.ap >= aend && tRef.bp >= bend)
				/*The strings compare the same.  Perhaps the caller 
				   will want to call strcmp to break the tie. */
				return 0;
			else if (tRef.ap >= aend)
				return -1;
			else if (tRef.bp >= bend)
				return 1;
		}
	}

	private static class tref {
		private int ap, bp;
	}
}
