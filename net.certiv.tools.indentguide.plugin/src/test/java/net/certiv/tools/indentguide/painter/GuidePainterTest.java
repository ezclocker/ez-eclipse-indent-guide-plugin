package net.certiv.tools.indentguide.painter;

import static net.certiv.tools.indentguide.TestSupport.TABWIDTH;
import static net.certiv.tools.indentguide.TestSupport.loadResource;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import net.certiv.tools.indentguide.TestSupport;
import net.certiv.tools.indentguide.util.Utils;

class GuidePainterTest {

	static final TestSupport TS = new TestSupport();
	String src1;
	Map<String, List<String>> map;

	@BeforeEach
	void setup() {
		TS.setUp();
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

	@ParameterizedTest
	@CsvFileSource(resources = "/painter_skip_logic_src1.csv", numLinesToSkip = 1, quoteCharacter = Utils.MARK)
	void testSkipLogic(int idx, int num, boolean blank, boolean block, boolean drawComment,
			boolean drawBlankLn, boolean drawLeadEdge, String draw) throws Exception {

		List<Boolean> draws = TestSupport.convertToBools(draw);

		// GuidePainter painter = new GuidePainter(TS.viewer, map, drawComment,
		// drawBlankLn, drawLeadEdge);

		TS.widget.setText(src1);
		Line ln = new Line(TS.viewer, TS.widget, map, num, TABWIDTH);
		assertEquals(blank, ln.blank, "Blank");
		assertEquals(block, ln.block, "Block");
		assertEquals(draws.size(), ln.stopCnt(), "Stop cnt");

		for (int stopIdx = 0; stopIdx < ln.stopCnt(); stopIdx++) {
			Pos pos = ln.stop(stopIdx);
			// boolean skip = painter.skipPos(ln, pos);
			boolean skip = LineRules.skipPos(ln, pos, drawLeadEdge, drawBlankLn, drawComment);
			assertEquals(draws.get(stopIdx), !skip); // skip == do not draw
		}
	}

	// @ParameterizedTest
	// @CsvFileSource(resources = "/painter_blank_logic.csv", numLinesToSkip = 1)
	// void testDrawLogic(int idx, boolean draw, boolean comment, boolean blank, boolean
	// drawComment,
	// boolean drawBlankLn, boolean drawLeadEdge, boolean first, boolean only) {
	//
	// assertEquals(!draw, blank && !(drawBlankLn && (!first || drawLeadEdge && first &&
	// !only)));
	// }
}
