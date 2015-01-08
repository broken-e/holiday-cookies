package com.trey.xmas2014;

import com.badlogic.gdx.utils.StringBuilder;

/**
 * the "Common" class for non-oop style helper things
 * 
 * @author trey miller
 */
public class C {
	public static final boolean DEBUG = true;
	/** badlogic StringBuilder for good GC reduction. ALWAYS do sb.setLength(0) before using!!! */
	public static final StringBuilder sb = new StringBuilder();

	public static final CharSequence toSb(float f) {
		sb.setLength(0);
		sb.append(f);
		return sb;
	}
}
