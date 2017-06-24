package com.isoft.zend.ext.date;

public class CharStream {

	private char buf[];
	private int pos;
	private int mark = 0;

	private int count;

	public CharStream(char buf[]) {
		this.buf = buf;
		this.pos = 0;
		this.count = buf.length;
	}

	public CharStream(char buf[], int offset, int length) {
		this.buf = buf;
		this.pos = offset;
		this.count = Math.min(offset + length, buf.length);
		this.mark = offset;
	}

	public synchronized int readInt() {
		return (((buf[pos++] & 0x0FF) << 24) + ((buf[pos++] & 0x0FF) << 16)
				+ ((buf[pos++] & 0x0FF) << 8) + ((buf[pos++] & 0x0FF) << 0));
	}

	public synchronized long skip(long n) {
		if (pos + n > count) {
			n = count - pos;
		}
		if (n < 0) {
			return 0;
		}
		pos += n;
		return n;
	}

	public synchronized int available() {
		return count - pos;
	}

	public void mark(int readAheadLimit) {
		mark = pos;
	}

	public synchronized void reset() {
		pos = mark;
	}
}