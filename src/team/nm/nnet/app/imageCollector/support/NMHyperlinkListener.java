package team.nm.nnet.app.imageCollector.support;

import java.awt.Desktop;
import java.net.URI;
import java.util.StringTokenizer;

import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class NMHyperlinkListener implements HyperlinkListener {
	public void hyperlinkUpdate(HyperlinkEvent evt) {
		if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			StringTokenizer st = new StringTokenizer(evt.getDescription(), " ");
			if (st.hasMoreTokens()) {
				String uri = st.nextToken();
				if (Desktop.isDesktopSupported()) {
					Desktop desktop = Desktop.getDesktop();
					try {
						desktop.browse(new URI(uri));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
