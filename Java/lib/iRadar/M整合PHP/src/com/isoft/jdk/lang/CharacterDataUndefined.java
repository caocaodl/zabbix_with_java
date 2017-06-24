package com.isoft.jdk.lang;


class CharacterDataUndefined extends CharacterData {

    int getProperties(int ch) {
        return 0;
    }

    int getType(int ch) {
	return Character.UNASSIGNED;
    }

    boolean isJavaIdentifierStart(int ch) {
		return false;
    }

    boolean isJavaIdentifierPart(int ch) {
		return false;
    }

    boolean isUnicodeIdentifierStart(int ch) {
		return false;
    }

    boolean isUnicodeIdentifierPart(int ch) {
		return false;
    }

    boolean isIdentifierIgnorable(int ch) {
		return false;
    }

    int toLowerCase(int ch) {
		return ch;
    }

    int toUpperCase(int ch) {
		return ch;
    }

    int toTitleCase(int ch) {
		return ch;
    }

    int digit(int ch, int radix) {
		return -1;
    }

    int getNumericValue(int ch) {
		return -1;
    }

    boolean isWhitespace(int ch) {
		return false;
    }

    byte getDirectionality(int ch) {
		return Character.DIRECTIONALITY_UNDEFINED;
    }

    boolean isMirrored(int ch) {
		return false;
    }

    static final CharacterData instance = new CharacterDataUndefined();
    private CharacterDataUndefined() {};
}

