/******************************************************************************
 * Copyright (c) 2006-2024 The IndentGuide Authors.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License.  A copy of the MIT License is included this
 * distribution and is available at https://opensource.org/licenses/MIT.
 *****************************************************************************/
package net.certiv.tools.indentguide.painter;

/** Rules controlling whether to skip drawing a given position */
public class LineRules {

	public static boolean skipPos(Line line, Pos pos, boolean drawLeadEdge, boolean drawBlankLn,
			boolean drawComment) {

		boolean first = pos == line.firstStop();
		boolean last = pos == line.lastStop();

		if (line.block) {
			// skip first visible character
			if (pos.col == line.textCol()) return true;

			// skip first where only unless drawComment or drawLeadEdge
			if (line.noDents && !(drawComment || drawLeadEdge)) return true;

			// skip first where not only unless drawLeadEdge
			if (first && !line.noDents && !drawLeadEdge) return true;

			// skip last where !only unless drawComment
			if (last && !line.noDents && !drawComment) return true;

		} else if (line.blank) {
			// skip first where only and zero
			if (first && line.noDents && line.delta0) return true;

			// skip last where not only and zero
			if (last && !line.noDents && line.delta0) return true;

			// skip first where not zero unless drawBlankLn and drawLeadEdge
			if (first && !line.delta0 && !(drawBlankLn && drawLeadEdge)) return true;

			// skip first where zero and multi dents unless drawBlankLn and
			// drawLeadEdge
			if (first && line.delta0 && !line.noDents && !(drawBlankLn && drawLeadEdge)) return true;

		} else {
			// skip draw on top of first visible character
			if (pos.col == line.textCol()) return true;

			// skip first unless drawLeadEdge
			if (first && !drawLeadEdge) return true;
		}

		return false;
	}
}
