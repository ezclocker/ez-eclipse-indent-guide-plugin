package net.certiv.tools.indentguide;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Shell;

import net.certiv.tools.indentguide.util.Utils;

public class TestSupport {

	public static final boolean isWin = SWT.getPlatform().startsWith("win32");
	public static final boolean isCocoa = SWT.getPlatform().startsWith("cocoa");
	public static final boolean isGTK = SWT.getPlatform().equals("gtk");
	public static final boolean isWinOS = System.getProperty("os.name").startsWith("Windows");
	public static final boolean isLinux = System.getProperty("os.name").equals("Linux");

	public static final int TABWIDTH = 4;

	public final String[] fontnames = { //
			"Consolas", "Courier New", "Menlo", //
			"Fira Code", "Source Code Pro", "Liberation Mono" //
	};

	public final String DummyText = "This is some dummy text. Sufficient in length to force the scroll bars to appear.";
	public final String[] prefixes0 = { "", "//" };
	public final String type0 = Utils.DefContentType;
	public final String[] prefixes1 = { "//" };
	public final String type1 = Utils.JavaContentType;
	public final String[] prefixes2 = { "<!--", null };
	public final String type2 = "__html_text";

	public Shell shell;
	public ISourceViewer viewer;
	public StyledText widget;

	public void setUp() {
		shell = new Shell();
		shell.setSize(200, 200);
		shell.setLayout(new FillLayout());

		viewer = new SourceViewer(shell, null, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.setDefaultPrefixes(prefixes0, type0);
		viewer.setDefaultPrefixes(prefixes1, type1);
		viewer.setDefaultPrefixes(prefixes2, type2);

		widget = viewer.getTextWidget();

		FontData fd = new FontData(fontnames[3], 11, SWT.NORMAL);
		Font font = new Font(widget.getDisplay(), fd);

		widget.setFont(font);
		widget.setTabs(TABWIDTH);
		widget.setText(DummyText);
	}

	public void tearDown() {
		if (shell != null) shell.dispose();
	}

	public static String loadResource(Class<?> cls, String name) throws Exception {
		try (InputStream is = cls.getClassLoader().getResourceAsStream(name)) {
			return new String(is.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	public static List<Boolean> convertToBools(String in) {
		if (in == null || !(in instanceof String)) {
			throw new IllegalArgumentException("Input value must be a 'String'.");
		}
		String val = (String) in;
		if (val.isBlank()) return List.of();

		String[] elems = val.split("\\s*,\\s*");
		return Arrays.stream(elems).map(e -> Boolean.valueOf(e)).toList();
	}

}
