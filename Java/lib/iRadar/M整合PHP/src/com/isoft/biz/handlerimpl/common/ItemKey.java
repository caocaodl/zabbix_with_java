package com.isoft.biz.handlerimpl.common;

import java.util.ArrayList;
import java.util.List;

public class ItemKey {
	private String key;
	private int keyByteCnt;
	private int currentByte;
	private boolean isValid = true;
	private String error;
	private List<String> parameters = new ArrayList();
	private String keyId = "";

	public ItemKey(String key) {
		this.key = key;
		this.keyByteCnt = key != null ? this.key.length() : 0;
		if (this.keyByteCnt == 0) {
			this.isValid = false;
			this.error = "Key cannot be empty.";
		} else {
			parseKeyId();
			if (this.isValid) {
				parseKeyParameters();
			}
		}
	}

	public boolean isValid() {
		return isValid;
	}

	public String getError() {
		return error;
	}

	public List<String> getParameters() {
		return parameters;
	}

	public String getKeyId() {
		return keyId;
	}

	private void parseKeyId() {
		for (this.currentByte = 0; this.currentByte < this.keyByteCnt; this.currentByte++) {
			if (!isKeyIdChar(this.key.charAt(this.currentByte))) {
				break;
			}
		}
		if (this.currentByte == 0) {
			this.isValid = false;
			this.error = "Invalid item key format.";
		} else {
			this.keyId = this.key.substring(0, this.currentByte);
		}
	}
	
	private void parseKeyParameters() {
		if(this.currentByte == this.keyByteCnt){
			return;
		}
		if(this.key.charAt(this.currentByte)!='['){
			this.isValid = false;
			this.error = "Invalid item key format.";
			return;
		}
		// let the parsing begin!
		int state = 0; // 0 - initial, 1 - inside quoted param, 2 - inside unquoted param
		int nestLevel = 0;
		int currParamNo = 0;
		this.parameters.add(currParamNo, "");
		for (this.currentByte++; this.currentByte < this.keyByteCnt; this.currentByte++) {
			switch (state) {
			case 0: // initial state
				if(this.key.charAt(this.currentByte) == ','){
					if (nestLevel == 0) {
						currParamNo++;
						this.parameters.add(currParamNo,"");
					} else {
						this.parameters.add(currParamNo,this.parameters.remove(currParamNo) + this.key.charAt(this.currentByte));
					}
				} 
				// Zapcat: '][' is treated as ','
				else if (this.key.charAt(this.currentByte) == ']'
						&& (this.currentByte < this.keyByteCnt - 1)
						&& this.key.charAt(this.currentByte) == '['
						&& nestLevel == 0) {
					currParamNo++;
					this.parameters.remove(currParamNo);
					this.parameters.add(currParamNo, "");
					this.currentByte++;
				}
				// entering quotes
				else if(this.key.charAt(this.currentByte) == '"'){
					state = 1;
					// in key[["a"]] param is "a"
					if (nestLevel > 0) {
						this.parameters.add(currParamNo,this.parameters.remove(currParamNo) + this.key.charAt(this.currentByte));
					}
				}
				// next nesting level
				else if(this.key.charAt(this.currentByte) == '['){
					if (nestLevel > 0) {
						this.parameters.add(currParamNo,this.parameters.remove(currParamNo) + this.key.charAt(this.currentByte));
					}
					nestLevel++;
				}
				// one of the nested sets ended
				else if(this.key.charAt(this.currentByte) == ']' && nestLevel > 0){
					nestLevel--;
					if (nestLevel > 0) {
						this.parameters.add(currParamNo,this.parameters.remove(currParamNo) + this.key.charAt(this.currentByte));
					}
					// skipping spaces
					while ((this.currentByte < this.keyByteCnt - 1) && this.key.charAt(this.currentByte+1) == ' ') {
						this.currentByte++;
						if (nestLevel > 0) {
							this.parameters.add(currParamNo,this.parameters.remove(currParamNo) + this.key.charAt(this.currentByte));
						}
					}
					// all nestings are closed correctly
					if (nestLevel == 0 && (this.currentByte < this.keyByteCnt - 1) && this.key.charAt(this.currentByte+1) == ']' && !(this.currentByte < this.keyByteCnt - 2)) {
						return;
					}
					if (!(this.currentByte < this.keyByteCnt - 1) || this.key.charAt(this.currentByte+1) != ','
						&& !(nestLevel > 0 && (this.currentByte < this.keyByteCnt - 1) && this.key.charAt(this.currentByte+1) == ']')
						&& this.key.charAt(this.currentByte+1) != ']' // Zapcat - '][' is the same as ','
						&& this.key.charAt(this.currentByte+2) != '[') {
						this.isValid = false;
						this.error = "Incorrect syntax near \""+this.key.substring(this.currentByte)+"\"";
						return;
					}
				}
				// looks like we have reached final ']'
				else if (this.key.charAt(this.currentByte) == ']' && nestLevel == 0) {
					if (!(this.currentByte < this.keyByteCnt - 1)) {
						return;
					}

					// nothing else is allowed after final ']'
					this.isValid = false;
					this.error = "Incorrect usage of bracket symbols. \""+this.key.substring(this.currentByte+1)+"\" found after final bracket.";
					return;
				}
				else if (this.key.charAt(this.currentByte) != ' ') {
					state = 2;
					// this is a first symbol of unquoted param
					this.parameters.add(currParamNo,this.parameters.remove(currParamNo) + this.key.charAt(this.currentByte));
				}
				else if (nestLevel > 0) {
					this.parameters.add(currParamNo,this.parameters.remove(currParamNo) + this.key.charAt(this.currentByte));
				}
				break;
			case 1://quoted
				// ending quote is reached
				if (this.key.charAt(this.currentByte) == '"' && this.key.charAt(this.currentByte-1) != '\\') {
					// skipping spaces
					while ((this.currentByte < this.keyByteCnt - 1) && this.key.charAt(this.currentByte+1) == ' ') {
						this.currentByte++;
						if (nestLevel > 0) {
							this.parameters.add(currParamNo,this.parameters.remove(currParamNo) + this.key.charAt(this.currentByte));
						}
					}
					
					// Zapcat
					if (nestLevel == 0 && (this.currentByte < this.keyByteCnt - 2)
							&& this.key.charAt(this.currentByte+1) == ']' && this.key.charAt(this.currentByte+2) == '[') {
						state = 0;
						break;
					}
					
					if (nestLevel == 0 && (this.currentByte < this.keyByteCnt - 1) && this.key.charAt(this.currentByte+1) == ']' && !(this.currentByte < this.keyByteCnt - 2)) {
						return;
					}
					else if (nestLevel == 0 && this.key.charAt(this.currentByte+1) == ']' && (this.currentByte < this.keyByteCnt - 2)) {
						// nothing else is allowed after final ']'
						this.isValid = false;
						this.error = "Incorrect usage of bracket symbols. \""+this.key.substring(this.currentByte + 1)+"\" found after final bracket.";
						return;
					}

					if ((!(this.currentByte < this.keyByteCnt - 1) || this.key.charAt(this.currentByte+1) != ',') // if next symbol is not ','
							&& !(nestLevel != 0 && (this.currentByte < this.keyByteCnt - 1) && this.key.charAt(this.currentByte+1) == ']')) {
						// nothing else is allowed after final ']'
						this.isValid = false;
						this.error = "Incorrect syntax near \""+this.key.substring(this.currentByte)+"\" at position \""+this.currentByte+"s\"";
						return;
					}

					// in key[["a"]] param is "a"
					if (nestLevel > 0) {
						this.parameters.add(currParamNo,this.parameters.remove(currParamNo) + this.key.charAt(this.currentByte));
					}
					state = 0;
				}
				//escaped quote (\")
				else if (this.key.charAt(this.currentByte) == '\\' && (this.currentByte < this.keyByteCnt - 1) && this.key.charAt(this.currentByte+1) == '"') {
					if (nestLevel > 0) {
						this.parameters.add(currParamNo,this.parameters.remove(currParamNo) + this.key.charAt(this.currentByte));
					}
				}
				else {
					this.parameters.add(currParamNo,this.parameters.remove(currParamNo) + this.key.charAt(this.currentByte));
				}
				break;
			case 2: // unquoted
				// Zapcat
				if (nestLevel == 0 && this.key.charAt(this.currentByte) == ']' && (this.currentByte < this.keyByteCnt - 1) && this.key.charAt(this.currentByte+1) =='[' ) {
					this.currentByte--;
					state = 0;
				}
				else if (this.key.charAt(this.currentByte) == ',' || (this.key.charAt(this.currentByte) == ']' && nestLevel > 0)) {
					this.currentByte--;
					state = 0;
				}
				else if (this.key.charAt(this.currentByte) == ']' && nestLevel == 0) {
					if (this.currentByte < this.keyByteCnt - 1) {
						// nothing else is allowed after final ']'
						this.isValid = false;
						this.error = "Incorrect usage of bracket symbols. \""+this.key.substring(this.currentByte+1)+"\" found after final bracket.";
						return;
					}
					else {
						return;
					}
				}
				else {
					this.parameters.add(currParamNo,this.parameters.remove(currParamNo) + this.key.charAt(this.currentByte));
				}
				break;
			}
//			System.out.println(state);
//			if(state!=0){
//				this.isValid = false;
//				this.error = "Invalid item key format.";
//			}
		}
	}

	public static boolean isKeyIdChar(char c) {
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
				|| (c >= '0' && c <= '9') || c == '.' || c == '-';
	}
}
