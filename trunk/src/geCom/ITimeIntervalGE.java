package geCom  ;

import com4j.*;

/**
 * ITimeIntervalGE Interface
 */
@IID("{D794FE36-10B1-4E7E-959D-9638794D2A1B}")
public interface ITimeIntervalGE extends Com4jObject {
    /**
     * property BeginTime
     */
    @VTID(7)
    geCom.ITimeGE beginTime();

    /**
     * property EndTime
     */
    @VTID(8)
    geCom.ITimeGE endTime();

}
