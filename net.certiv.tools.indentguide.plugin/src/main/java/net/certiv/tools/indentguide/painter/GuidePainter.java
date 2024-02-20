/******************************************************************************
 * Copyright (c) 2006-2024 The IndentGuide Authors.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the MIT License.  A copy of the MIT License is included this
 * distribution and is available at https://opensource.org/licenses/MIT.
 *****************************************************************************/
package net.certiv.tools.indentguide.painter;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPaintPositionManager;
import org.eclipse.jface.text.IPainter;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import net.certiv.tools.indentguide.Activator;
import net.certiv.tools.indentguide.preferences.Pref;
import net.certiv.tools.indentguide.util.Utils;

/**
 * A painter for drawing visible indent guide lines.
 *
 * @see org.eclipse.jface.text.WhitespaceCharacterPainter
 * @see org.eclipse.ui.texteditor.ShowWhitespaceCharactersAction
 */
public class GuidePainter implements IPainter, PaintListener {

	private ITextViewer viewer;
	private Map<String, List<String>> prefixMap;
	private StyledText widget;

	private boolean advanced;
	private IPreferenceStore store;

	private boolean active;
	private int lineAlpha;
	private int lineStyle;
	private int lineWidth;
	private int lineShift;
	private Color lineColor;
	private boolean drawLeadEdge;
	private boolean drawBlankLn;
	private boolean drawComment;

	/**
	 * Creates a new painter for the given text viewer.
	 *
	 * @param viewer    text viewer using this painter
	 * @param prefixMap line prefixes for the associated partition type
	 */
	public GuidePainter(ITextViewer viewer, Map<String, List<String>> prefixMap) {
		this.viewer = viewer;
		this.prefixMap = prefixMap;
		widget = viewer.getTextWidget();
		advanced = Utils.setAdvanced(widget);
		store = Activator.getDefault().getPreferenceStore();

		loadPrefs();
	}

	@Override
	public void paint(int reason) {
		IDocument doc = viewer.getDocument();
		if (doc == null) {
			deactivate(false);
			return;
		}

		if (!active) {
			active = true;
			widget.addPaintListener(this);
			redrawAll();

		} else if (reason == CONFIGURATION || reason == INTERNAL) {
			redrawAll();

		} else if (reason == TEXT_CHANGE) { // redraw current line only
			try {
				int docOffset = Utils.docOffset(viewer, widget.getCaretOffset());
				IRegion region = doc.getLineInformationOfOffset(docOffset);
				int offset = Utils.widgetOffset(viewer, region.getOffset());
				int cnt = widget.getCharCount();
				int len = Math.min(region.getLength(), cnt - offset);
				if (offset >= 0 && len > 0) {
					widget.redrawRange(offset, len, true);
				}
			} catch (BadLocationException e) {}
		}
	}

	/** Request a redraw of all visible content. */
	public void redrawAll() {
		widget.redraw();
	}

	@Override
	public void paintControl(PaintEvent evt) {
		if (widget != null) {
			handleDrawRequest(evt.gc, evt.x, evt.y, evt.width, evt.height);
		}
	}

	// Draw characters in view range.
	private void handleDrawRequest(GC gc, int x, int y, int w, int h) {
		int begLine = widget.getLineIndex(y);
		int endLine = widget.getLineIndex(y + h - 1);

		// Activator.log("draw request @(%s:%s)", begLine + 1, endLine + 1);

		if (begLine <= endLine && begLine < widget.getLineCount()) {

			// collect state
			Color color = gc.getForeground();
			LineAttributes attributes = gc.getLineAttributes();
			Rectangle clipping = gc.getClipping();

			// adjust the client area
			Rectangle clientArea = widget.getClientArea();
			int leftMargin = widget.getLeftMargin();
			int rightMargin = widget.getRightMargin();
			clientArea.x += leftMargin;
			clientArea.width -= leftMargin + rightMargin;
			clipping.intersect(clientArea);
			gc.setClipping(clientArea);

			// draw guides
			gc.setForeground(lineColor);
			gc.setLineStyle(lineStyle);
			gc.setLineWidth(lineWidth);
			if (advanced) {
				int alpha = gc.getAlpha();
				gc.setAlpha(lineAlpha);
				drawLineRange(gc, begLine, endLine, x, w);
				gc.setAlpha(alpha);
			} else {
				drawLineRange(gc, begLine, endLine, x, w);
			}

			// restore state
			gc.setClipping(clipping);
			gc.setForeground(color);
			gc.setLineAttributes(attributes);
		}
	}

	/**
	 * Draw the given line range.
	 *
	 * @param gc      the GC
	 * @param begLine first line number
	 * @param endLine last line number (inclusive)
	 * @param x       the X-coordinate of the drawing range
	 * @param w       the width of the drawing range
	 */
	private void drawLineRange(GC gc, int begLine, int endLine, int x, int w) {
		int tabWidth = widget.getTabs();
		StyledTextContent content = widget.getContent();

		for (int lnNum = begLine; lnNum <= endLine; lnNum++) {
			int offset = widget.getOffsetAtLine(lnNum);
			int height = widget.getLineHeight(offset);
			int spacing = widget.getLineSpacing();

			int docLnNum = content.getLineAtOffset(offset); // 1..n
			if (!Utils.isFolded(viewer, docLnNum)) {
				Line line = new Line(viewer, widget, prefixMap, lnNum, tabWidth);

				for (Pos stop : line) {
					// if (skipPos(line, stop)) continue;
					if (LineRules.skipPos(line, stop, drawLeadEdge, drawBlankLn, drawComment)) continue;

					boolean asc = stop.col >= line.lastStopCol();
					Point pos = widget.getLocationAtOffset(offset);
					int hx = widget.getHorizontalBar().getSelection();
					draw(gc, pos, stop.loc + hx, spacing, height, asc);
				}
			}
		}
	}

	private void draw(GC gc, Point pos, int loc, int sp, int ht, boolean asc) {
		pos.x += loc + lineShift;
		if (asc) {
			gc.drawLine(pos.x, pos.y - sp, pos.x, pos.y + ht + sp);
		} else {
			gc.drawLine(pos.x, pos.y, pos.x, pos.y + ht + sp);
		}
	}

	public void loadPrefs() {
		lineAlpha = store.getInt(Pref.LINE_ALPHA);
		lineStyle = store.getInt(Pref.LINE_STYLE);
		lineWidth = store.getInt(Pref.LINE_WIDTH);
		lineShift = store.getInt(Pref.LINE_SHIFT);

		disposeLineColor();
		lineColor = Utils.getColor(store);

		drawLeadEdge = store.getBoolean(Pref.DRAW_LEAD_EDGE);
		drawBlankLn = store.getBoolean(Pref.DRAW_BLANK_LINE);
		drawComment = store.getBoolean(Pref.DRAW_COMMENT_BLOCK);
	}

	public boolean isActive() {
		return active;
	}

	public void activate(boolean redraw) {
		if (!active) {
			active = true;
			widget.addPaintListener(this);
			if (redraw) redrawAll();
		}
	}

	@Override
	public void deactivate(boolean redraw) {
		if (active) {
			active = false;
			widget.removePaintListener(this);
			if (redraw) redrawAll();
		}
	}

	@Override
	public void dispose() {
		store = null;
		viewer = null;
		widget = null;

		disposeLineColor();
	}

	private void disposeLineColor() {
		if (lineColor != null) {
			lineColor.dispose();
			lineColor = null;
		}
	}

	@Override
	public void setPositionManager(IPaintPositionManager manager) {}

	// void log(int delta, Line prevNb, Line currLn, Line nextNb) {
	// Activator.log(new MsgBuilder() //
	// .nl().append("Delta: %s", delta) //
	// .nl().append("PrevNb: %s", prevNb) //
	// .nl().append("CurrLn: %s", currLn) //
	// .nl().append("NextNb: %s", nextNb)) //
	// ;
	// }
}
