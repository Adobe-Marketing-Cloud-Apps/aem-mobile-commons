package com.aem.mobile.commons.core.impl.contentsync;

import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.contentsync.config.ConfigEntry;
import com.day.cq.contentsync.handler.ContentUpdateHandler;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.eval.PathPredicateEvaluator;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;
import com.day.text.Text;
import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.apache.sling.jcr.resource.JcrResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.*;

/**
 * Created by kmall on 7/21/16
 */

@Component(metatype = false, factory = "com.day.cq.contentsync.handler.ContentUpdateHandler/referencedassets")

@SuppressWarnings("unused")
public class ReferencedAssetUpdateHandler implements ContentUpdateHandler {

    private static final Logger log = LoggerFactory.getLogger(ReferencedAssetUpdateHandler.class);

    /* Name for config properties */
    private static final String RT_CONFIG_PROPERTY = "resourceTypes"; //resource types to check for referenced assets
    private static final String PN_CONFIG_PROPERTY = "propertyName"; //property name on the resources that give path to the referenced assets
    private static final String RENDITIONS_CONFIG_PROPERTY = "renditions"; //renditions of the asset to be copied, if not set, original is copied.
    private static final String CONFIG_TARGET_PATH_PREFIX = "targetRootDirectory";

    @Reference
    QueryBuilder queryBuilder;

    @Reference(policy = ReferencePolicy.STATIC)
    private JcrResourceResolverFactory resolverFactory;

    @Override
    public boolean updateCacheEntry(ConfigEntry configEntry, Long lastUpdated, String configCacheRoot, Session admin, Session session) {

        boolean changed = false;

        log.info( "Content Syncing Referenced Assets" );

        try{
            // read additional settings
            ResourceResolver resolver = resolverFactory.getResourceResolver(admin);
            ValueMap configOptions = ResourceUtil.getValueMap(resolver.getResource(configEntry.getPath()));

            String[] resourceTypes = configOptions.get(RT_CONFIG_PROPERTY, new String[]{});
            String propertyName = configOptions.get(PN_CONFIG_PROPERTY, new String());
            String[] renditionNames = configOptions.get(RENDITIONS_CONFIG_PROPERTY, new String[]{});

            if( resourceTypes.length > 0 && StringUtils.isNotEmpty( propertyName ) ){

                List<Asset> referencedAssets = getReferencedAssets( configEntry.getContentPath(), resolver, resourceTypes, propertyName );
                Iterator<Asset> assetIterator = referencedAssets.iterator();

                configCacheRoot = getConfigCacheRoot(configEntry, configCacheRoot);

                while(assetIterator.hasNext()) {
                    // handle original rendition
                    Asset asset = assetIterator.next();

                    if(renditionNames.length == 0 && isModified(asset.getOriginal(), lastUpdated, configCacheRoot + asset.getPath(), admin)) {
                        Node parent = JcrUtil.createPath(configCacheRoot + Text.getRelativeParent(asset.getPath(), 1), "sling:Folder", admin);
                        JcrUtil.copy(asset.getOriginal().adaptTo(Node.class), parent, asset.getName());

                        changed |= true;
                    }

                    for (String renditionName : renditionNames) {
                        Rendition rendition = asset.getRendition(renditionName);
                        if (rendition != null && isModified(rendition, lastUpdated, configCacheRoot + rendition.getPath(), admin)) {
                            Node parent = JcrUtil.createPath(configCacheRoot + Text.getRelativeParent(rendition.getPath(), 1), "sling:Folder", admin);
                            JcrUtil.copy(rendition.adaptTo(Node.class), parent, rendition.getName());

                            changed |= true;
                        }
                    }

                    admin.save();
                }

            }
        } catch ( Exception r ){
            log.error( "Exception in ReferencedAssetUpdateHandler {}", r );
        }


        return false;

    }

    /**
     * gets a list of referenced assets in the resources defined by the resourceTypes array
     * @param contentPath Path to search under
     * @param resourceResolver ResourceResolver
     * @param resourceTypes String array of resource types that reference an Asset
     * @param propertyName Property name on resources that have a path to an Asset
     * @return
     * @throws RepositoryException
     */

    protected List<Asset> getReferencedAssets(String contentPath, ResourceResolver resourceResolver, String[] resourceTypes, String propertyName ) throws RepositoryException {

        List<Asset> assets = new ArrayList<>();

        Map<String, String> predicateMap = new HashMap<>();
        predicateMap.put( PathPredicateEvaluator.PATH, contentPath );
        predicateMap.put( "1_property", JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY );

        for( int i = 0; i < resourceTypes.length; i++ ){
            predicateMap.put( "1_property." + i+1 + "_value", resourceTypes[i] );
        }
        predicateMap.put( "p.guessTotal", "true" );

        Query queryObj = this.queryBuilder.createQuery(PredicateGroup.create(predicateMap), resourceResolver.adaptTo(Session.class) );

        SearchResult searchResults = queryObj.getResult();

        if( searchResults != null ){
            List<Hit> hitList = searchResults.getHits();

            for( Hit hit: hitList ){
                Resource resource = hit.getResource();
                ValueMap properties = resource.getValueMap();

                log.debug( "Found resource {}", resource.getPath() );

                String assetPath = properties.get( propertyName, new String() );
                if( StringUtils.isNotEmpty( assetPath ) ){
                    Resource assetResource = resourceResolver.getResource( assetPath );

                    if( assetResource != null ){
                        Asset asset = assetResource.adaptTo( Asset.class );
                        if( asset != null ){
                            log.info( "Found referenced asset {}", asset.getPath() );
                            assets.add( asset );
                        }
                    }
                }

            }
        }

        return assets;

    }

    /**
     * Checks if the rendition has either been modified or the related
     * cache entry is missing.
     *
     * @param rendition Rendition to check
     * @param lastUpdated Timestamp of last cache update
     * @param cachePath The path of the rendition's cache entry
     * @param session The session
     * @return <code>true</code> if rendition is modified or cache entry is missing, <code>false</code> otherwise
     * @throws RepositoryException In case of unexpected error
     */
    protected boolean isModified(Rendition rendition, Long lastUpdated, String cachePath, Session session) throws RepositoryException {
        Long lastModified = rendition.getProperties().get("jcr:lastModified", Long.class);

        return (!session.nodeExists(cachePath) || lastUpdated < lastModified);
    }

    protected String getConfigCacheRoot(ConfigEntry configEntry, String configCacheRoot){
        String prefixPath = configEntry.getValue(CONFIG_TARGET_PATH_PREFIX);
        if(prefixPath!=null){
            return configCacheRoot + "/" + prefixPath;
        }
        return configCacheRoot;
    }

}
