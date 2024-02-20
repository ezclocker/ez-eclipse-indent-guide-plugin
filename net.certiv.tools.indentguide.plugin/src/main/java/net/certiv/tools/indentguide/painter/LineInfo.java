/******************************************************************************
 * Copyright (c) 2006-2024 The IndentGuide Authors.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License.  A copy of the MIT License is included this
 * distribution and is available at https://opensource.org/licenses/MIT.
 *****************************************************************************/
package net.certiv.tools.indentguide.painter;

import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.swt.custom.StyledText;

import net.certiv.tools.indentguide.util.Utils;

class LineInfo {

	/** Tab stop positions */
	final LinkedList<Pos> stops = new LinkedList<>();

	/** Line number (0..n) */
	final int num;
	/** Text begin column */
	int beg;
	/** Text content */
	String txt = Utils.EMPTY;

	// --------------------------------

	/**
	 * @param widget   text control
	 * @param num      line number
	 * @param tabWidth defined tab width
	 */
	LineInfo(StyledText widget, int num, int tabWidth) {
		this.num = num;
		stops.add(Pos.P0);

		txt = widget.getLine(num);
		for (int pos = 0, col = 0, len = txt.length(); pos < len; pos++) {
			int ch = txt.codePointAt(pos);
			switch (ch) {
				case Utils.SPC:
					beg = col += Character.charCount(ch);
					if (col % tabWidth == 0) stops.add(Pos.at(widget, num, stopCnt(), pos + 1, col));
					break;

				case Utils.TAB:
					beg = col += tabWidth - (col % tabWidth);
					stops.add(Pos.at(widget, num, stopCnt(), pos + 1, col));
					break;

				default:
					beg = col;
					return;
			}
		}
	}

	/**
	 * Return the last stop position.
	 *
	 * @return last stop position
	 */
	Pos lastStop() {
		return stops.peekLast();
	}

	/**
	 * Remove the last indentation stop position. Does not remove the column zero stop.
	 */
	void removeLast() {
		if (stops.size() > 1) {
			stops.removeLast();
		}
	}

	/**
	 * Return the total number of stop positions.
	 *
	 * @return stop position count
	 */
	int stopCnt() {
		return stops.size();
	}

	public Iterator<Pos> iterator() {
		return stops.iterator();
	}
}
