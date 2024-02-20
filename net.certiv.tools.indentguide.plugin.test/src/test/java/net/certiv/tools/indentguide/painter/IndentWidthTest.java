package net.certiv.tools.indentguide.painter;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.tools.indentguide.TestSupport;
import net.certiv.tools.indentguide.util.Utils;

class IndentWidthTest {

	static final TestSupport TS = new TestSupport();

	@BeforeEach
	void setup() {
		TS.setUp();
	}

	@AfterEach
	void tearDown() {
		TS.tearDown();
	}

	@Test
	void test_stringExtent() {
		GC gc = new GC(TS.shell);

		try {
			for (int size = 7; size < 15; size++) {
				for (String name : TS.fontnames) {
					FontData fd = new FontData(name, size, SWT.NORMAL);
					Font font = new Font(TS.widget.getDisplay(), fd);
					TS.widget.setFont(font);
					gc.setFont(font);

					Map<Integer, Integer> widths0 = new LinkedHashMap<>();
					Map<Integer, Integer> widths1 = new LinkedHashMap<>();
					for (int col = 1; col <= 10; col++) {
						Point p = gc.stringExtent(Utils.SPACE);
						widths0.put(col, p.x * col);

						p = gc.stringExtent(Utils.SPACE.repeat(col));
						widths1.put(col, p.x);
					}
					// System.out.printf("Font %-20s: size [%02d] multiply widths %s\n",
					// name, size, widths0);
					// System.out.printf("Font %-20s: size [%02d] *repeat* widths %s\n",
					// name, size, widths1);

					gc.setFont(null);
					TS.widget.setFont(null);
					font.dispose();

					assertTrue(widths0.equals(widths1), String.format("Font %s did not match.", name));
				}
			}

		} finally {
			gc.dispose();
			gc = null;
		}
	}
}
