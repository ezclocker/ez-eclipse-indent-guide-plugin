package net.certiv.tools.indentguide.painter;

import static net.certiv.tools.indentguide.TestSupport.TABWIDTH;
import static net.certiv.tools.indentguide.TestSupport.loadResource;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import net.certiv.tools.indentguide.TestSupport;
import net.certiv.tools.indentguide.util.Utils;

class LineTest {

	static final TestSupport TS = new TestSupport();
	String src0;
	String src1;
	Map<String, List<String>> map;

	@BeforeEach
	void setup() {
		TS.setUp();
		if (src0 == null) {
			src0 = assertDoesNotThrow(() -> loadResource(getClass(), "TestSrc0.txt"));
		}
		if (src1 == null) {
			src1 = assertDoesNotThrow(() -> loadResource(getClass(), "TestSrc1.txt"));
		}
		if (map == null) {
			map = assertDoesNotThrow(() -> Utils.prefixesFor(TS.viewer));
		}
	}

	@AfterEach
	void tearDown() {
		TS.tearDown();
	}

	@Test
	void testBase() {
		assertEquals(1, TS.widget.getLineCount());
		assertEquals(TS.DummyText, TS.widget.getLine(0));
	}

	@Test
	void testLine() {
		Line ln = new Line(TS.viewer, TS.widget, map, 0, TABWIDTH);
		assertFalse(ln.blank);
		assertEquals(0, ln.textCol());
		assertEquals(1, ln.stopCnt());

		TS.widget.setText(Utils.TAB + TS.DummyText);
		ln = new Line(TS.viewer, TS.widget, map, 0, TABWIDTH);
		assertFalse(ln.blank);
		assertEquals(4, ln.textCol());
		assertEquals(2, ln.stopCnt());

		TS.widget.setText("  ");
		ln = new Line(TS.viewer, TS.widget, map, 0, TABWIDTH);
		assertTrue(ln.blank);
		assertEquals(2, ln.textCol());
		assertEquals(1, ln.stopCnt());

		TS.widget.setText("  " + TS.DummyText);
		ln = new Line(TS.viewer, TS.widget, map, 0, TABWIDTH);
		assertFalse(ln.blank);
		assertEquals(2, ln.textCol());
		assertEquals(1, ln.stopCnt());
	}

	@ParameterizedTest
	@CsvFileSource(resources = "/line_comments.csv", quoteCharacter = Utils.MARK)
	void testComment(String txt) {
		TS.widget.setText(txt);
		Line ln = new Line(TS.viewer, TS.widget, map, 0, TABWIDTH);

		assertTrue(ln.block, "Comment");
	}

	@ParameterizedTest
	@CsvFileSource(resources = "/line_single.csv", quoteCharacter = Utils.MARK)
	void testSingle(String txt, int len, boolean blank, int cnt, String last, int beg) {
		TS.widget.setText(txt);
		Line ln = new Line(TS.viewer, TS.widget, map, 0, TABWIDTH);

		assertEquals(blank, ln.blank, "Blank");
		assertEquals(beg, ln.textCol(), "Beg");
		assertEquals(len, ln.info.txt.length(), "Length");
		assertEquals(cnt, ln.stopCnt(), "Stop count");
	}

	@ParameterizedTest
	@CsvFileSource(resources = "/line_multi_src0.csv", numLinesToSkip = 1)
	void testMultiSrc0(int idx, int num, boolean blank, boolean block, boolean cmt0, int real, int cnt,
			int delta, int beg) {

		TS.widget.setText(src0);
		Line ln = new Line(TS.viewer, TS.widget, map, num, TABWIDTH);

		assertEquals(blank, ln.blank, "Blank");
		assertEquals(block, ln.block, "Block");
		assertEquals(cmt0, ln.cmt0, "Cmt0");
		assertEquals(real, ln.info.num, "Real");
		assertEquals(cnt, ln.stopCnt(), "StopCnt");
		assertEquals(delta, ln.delta, "Delta");
		assertEquals(beg, ln.textCol(), "Beg");
	}

	@ParameterizedTest
	@CsvFileSource(resources = "/line_multi_src1.csv", numLinesToSkip = 1)
	void testMultiSrc1(int idx, int num, boolean blank, boolean block, boolean cmt0, int real, int cnt,
			int delta, int beg) {

		TS.widget.setText(src1);
		Line ln = new Line(TS.viewer, TS.widget, map, num, TABWIDTH);

		assertEquals(blank, ln.blank, "Blank");
		assertEquals(block, ln.block, "Block");
		assertEquals(cmt0, ln.cmt0, "Cmt0");
		assertEquals(real, ln.info.num, "Real");
		assertEquals(cnt, ln.stopCnt(), "Stop");
		assertEquals(delta, ln.delta, "Delta");
		assertEquals(beg, ln.textCol(), "Beg");
	}
}
