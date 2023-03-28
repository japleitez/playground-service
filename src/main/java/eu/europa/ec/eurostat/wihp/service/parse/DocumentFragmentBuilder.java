package eu.europa.ec.eurostat.wihp.service.parse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.commons.lang3.Validate;
import org.apache.html.dom.HTMLDocumentImpl;
import org.jsoup.nodes.Attribute;
import org.jsoup.parser.Parser;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;
import org.w3c.dom.Comment;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public final class DocumentFragmentBuilder {

    private DocumentFragmentBuilder() {}

    public static DocumentFragment getDocumentFragment(final String url, final String html) {
        Validate.notNull(url, "URL must be set");
        Validate.notNull(html, "HTML content must be set");
        return DocumentFragmentBuilder.fromJsoup(Parser.htmlParser().parseInput(html, url));
    }

    public static DocumentFragment getDocumentFragment(final String url, final InputStream inputStream) throws IOException {
        String html = new String(Objects.requireNonNull(inputStream).readAllBytes(), StandardCharsets.UTF_8);
        return DocumentFragmentBuilder.fromJsoup(Parser.htmlParser().parseInput(html, url));
    }

    public static DocumentFragment fromJsoup(org.jsoup.nodes.Document jsoupDocument) {
        HTMLDocumentImpl htmlDoc = new HTMLDocumentImpl();
        htmlDoc.setErrorChecking(false);
        DocumentFragment fragment = htmlDoc.createDocumentFragment();
        org.jsoup.nodes.Element rootEl = jsoupDocument.child(0); // skip the
        // #root node
        NodeTraversor.traverse(new W3CBuilder(htmlDoc, fragment), rootEl);
        return fragment;
    }

    /**
     * Implements the conversion by walking the input.
     */
    protected static class W3CBuilder implements NodeVisitor {

        private final HTMLDocumentImpl doc;
        private final DocumentFragment fragment;

        private Element dest;

        public W3CBuilder(HTMLDocumentImpl doc, DocumentFragment fragment) {
            this.fragment = fragment;
            this.doc = doc;
        }

        public void head(org.jsoup.nodes.Node source, int depth) {
            if (source instanceof org.jsoup.nodes.Element) {
                org.jsoup.nodes.Element sourceEl = (org.jsoup.nodes.Element) source;
                Element el = doc.createElement(sourceEl.tagName());
                copyAttributes(sourceEl, el);
                if (dest == null) { // sets up the root
                    fragment.appendChild(el);
                } else {
                    dest.appendChild(el);
                }
                dest = el; // descend
            } else if (source instanceof org.jsoup.nodes.TextNode) {
                org.jsoup.nodes.TextNode sourceText = (org.jsoup.nodes.TextNode) source;
                Text text = doc.createTextNode(sourceText.getWholeText());
                dest.appendChild(text);
            } else if (source instanceof org.jsoup.nodes.Comment) {
                org.jsoup.nodes.Comment sourceComment = (org.jsoup.nodes.Comment) source;
                Comment comment = doc.createComment(sourceComment.getData());
                dest.appendChild(comment);
            } else if (source instanceof org.jsoup.nodes.DataNode) {
                org.jsoup.nodes.DataNode sourceData = (org.jsoup.nodes.DataNode) source;
                Text node = doc.createTextNode(sourceData.getWholeData());
                dest.appendChild(node);
            } else {
                // unhandled
            }
        }

        public void tail(org.jsoup.nodes.Node source, int depth) {
            if (source instanceof org.jsoup.nodes.Element && dest.getParentNode() instanceof Element) {
                dest = (Element) dest.getParentNode(); // undescend. cromulent.
            }
        }

        private void copyAttributes(org.jsoup.nodes.Node source, Element el) {
            for (Attribute attribute : source.attributes()) {
                // valid xml attribute names are: ^[a-zA-Z_:][-a-zA-Z0-9_:.]
                String key = attribute.getKey().replaceAll("[^-a-zA-Z0-9_:.]", "");
                if (key.matches("[a-zA-Z_:][-a-zA-Z0-9_:.]*")) el.setAttribute(key, attribute.getValue());
            }
        }
    }
}
