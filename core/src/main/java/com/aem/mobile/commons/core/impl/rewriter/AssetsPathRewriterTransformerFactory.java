package com.aem.mobile.commons.core.impl.rewriter;

import com.adobe.acs.commons.rewriter.AbstractTransformer;
import com.adobe.acs.commons.util.ParameterUtil;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.felix.scr.annotations.*;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.apache.sling.rewriter.ProcessingComponentConfiguration;
import org.apache.sling.rewriter.ProcessingContext;
import org.apache.sling.rewriter.Transformer;
import org.apache.sling.rewriter.TransformerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import java.io.IOException;
import java.util.Map;

/**
 * Created by kmall on 7/28/16
 *
 * TODO: Convert to use AbstractSlingResourceUpdateHandler. But this would require a dependency on the unobfuscated jar.
 */
@Component(
        label = "AEM Mobile Commons - Assets Path Rewriter Factory",
        description = "Rewriter pipeline component which rewrites paths of assets to be relative",
        metatype = true)

@Properties({
        @Property(
                label = "Rewriter Pipeline Type",
                description = "Type identifier to be referenced in rewriter pipeline configuration.",
                name = "pipeline.type",
                value = "aem-mobile-commons-asset-path",
                propertyPrivate = true)
})
@Service

@SuppressWarnings("unused")
public class AssetsPathRewriterTransformerFactory implements TransformerFactory {

    @Property(value = {"source:src", "video:poster"} )
    private static final String PROPERTY_ASSETS_MAPPING = "mapping.assets";

    private static final Logger log = LoggerFactory.getLogger(AssetsPathRewriterTransformerFactory.class);

    private static final String[] DEFAULT_ATTRIBUTES = new String[]{"source:src", "video:poster"};
    private static final String DAM_PATH_PREFIX = "/content/dam";

    private Map<String, String[]> attributes;

    @Activate
    protected void activate(final Map<String, Object> config) {
        final String[] attrProp = PropertiesUtil.toStringArray(config.get(PROPERTY_ASSETS_MAPPING), DEFAULT_ATTRIBUTES);
        this.attributes = ParameterUtil.toMap( attrProp, ":", "," );
    }

    protected Attributes rebuildAttributes(final SlingHttpServletRequest slingRequest,
                                           final String elementName, final Attributes attrs) {

        boolean rewriteLinks = false;

        if( slingRequest != null ){
            Object object = slingRequest.getAttribute( "publishPathRewritingOptions" );
            log.info( "publishPathRewritingOptions {}", object );

            if( object != null ){
                rewriteLinks = true;
            }
        }

        if (slingRequest == null || !attributes.containsKey(elementName) ) {
            // element is not defined as a candidate to rewrite
            return attrs;
        }
        final String[] modifiableAttributes = attributes.get(elementName);

        // clone the attributes
        final AttributesImpl newAttrs = new AttributesImpl(attrs);
        final int len = newAttrs.getLength();

        for (int i = 0; i < len; i++) {
            final String attrName = newAttrs.getLocalName(i);
            if (ArrayUtils.contains(modifiableAttributes, attrName)) {
                final String attrValue = newAttrs.getValue(i);
                if (StringUtils.startsWith(attrValue, DAM_PATH_PREFIX)) {

                    if( rewriteLinks ){
                        String relativePath = "." + attrValue;
                        log.info( "rewriting {} to {}", attrValue, relativePath );
                        newAttrs.setValue(i, relativePath);
                    }
                }
            }
        }
        return newAttrs;
    }

    /**
     * {@inheritDoc}
     */
    public Transformer createTransformer() {
        return new AssetPathRewriterTransformer();
    }

    public final class AssetPathRewriterTransformer extends AbstractTransformer {

        private SlingHttpServletRequest slingRequest;

        @Override
        public void init(ProcessingContext context, ProcessingComponentConfiguration config) throws IOException {
            super.init(context, config);
            this.slingRequest = context.getRequest();
        }

        @Override
        public void startElement(String namespaceURI, String localName, String qName, Attributes atts)
                throws SAXException {

            getContentHandler().startElement(namespaceURI, localName, qName,
                    rebuildAttributes(this.slingRequest, localName, atts));


        }
    }
}
