/******************************************************************************
 * Copyright (c) 2006-2024 The IndentGuide Authors.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License.  A copy of the MIT License is included this
 * distribution and is available at https://opensource.org/licenses/MIT.
 *****************************************************************************/
package net.certiv.tools.indentguide.painter;

import java.util.Objects;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;

public class Pos {

	public static final Pos P0 = Pos.at(0, 0, 0, 1);

	/** Stop index in line (0..n). */
	public final int stop;

	/** Char position in line (0..n); unexpanded. */
	public final int pos;
	/** Visual column in line (0..n); expanded. */
	public final int col;

	/** Location (X pixel offset) in widget line. */
	public final int loc;

	/**
	 * @param widget containing widget
	 * @param lnNum  line number
	 * @param stop   stop index in line (0..n)
	 * @param pos    char position in line (0..n)
	 * @param col    visual column in line (0..n); pos expanded
	 * @return stop position
	 */
	public static Pos at(StyledText widget, int lnNum, int stop, int pos, int col) {
		int offset = widget.getOffsetAtLine(lnNum);
		Point loc = widget.getLocationAtOffset(offset + pos);
		return new Pos(stop, pos, col, loc.x);
	}

	/**
	 * @param stop stop index in line (0..n)
	 * @param pos  char position in line (0..n)
	 * @param col  visual column in line (0..n); pos expanded
	 * @param loc  X pixel offset in line
	 * @return stop position
	 */
	private static Pos at(int stop, int pos, int col, int loc) {
		return new Pos(stop, pos, col, loc);
	}

	// --------------------------------

	private Pos(int stop, int pos, int col, int loc) {
		this.stop = stop;
		this.pos = pos;
		this.col = col;
		this.loc = loc;
	}

	@Override
	public int hashCode() {
		return Objects.hash(col, pos, loc);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Pos other = (Pos) obj;
		return col == other.col && pos == other.pos && loc == other.loc;
	}

	@Override
	public String toString() {
		return String.format("(%s,%s)", pos, col);
	}
}
