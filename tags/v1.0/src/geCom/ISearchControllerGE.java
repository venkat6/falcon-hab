package geCom  ;

import com4j.*;

/**
 * ISearchControllerGE Interface
 */
@IID("{524E5B0F-D593-45A6-9F87-1BAE7D338373}")
public interface ISearchControllerGE extends Com4jObject {
    /**
     * method Search
     */
    @VTID(7)
    void search(
        java.lang.String searchString);

    /**
     * method IsSearchInProgress
     */
    @VTID(8)
    int isSearchInProgress();

    /**
     * method GetResults
     */
    @VTID(9)
    geCom.IFeatureCollectionGE getResults();

    @VTID(9)
    @ReturnValue(defaultPropertyThrough={geCom.IFeatureCollectionGE.class})
    geCom.IFeatureGE getResults(
        int index);

    /**
     * method ClearResults
     */
    @VTID(10)
    void clearResults();

}
