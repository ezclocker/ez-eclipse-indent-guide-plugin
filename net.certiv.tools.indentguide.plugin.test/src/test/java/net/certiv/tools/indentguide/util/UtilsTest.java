package net.certiv.tools.indentguide.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.certiv.tools.indentguide.TestSupport;

class UtilsTest {

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
	void testPrefixes() {
		Map<String, List<String>> map = assertDoesNotThrow(() -> Utils.prefixesFor(TS.viewer));

		assertTrue(!map.isEmpty());
		map.forEach((k, v) -> assertTrue(v.size() == 1));
	}
}
