package ro.axonsoft.internship172.api;

/**
 * Interfata folosita la maparea unei carti de identitate pe un judet
 *
 * @author intern
 *
 */
public interface RoIdCardSeriesJudMapper {

    /**
     * maparea pe judetul de care apartine proprietarul cartii de identitate
     *
     * @param idCardSeries
     *            seria de pe buletin
     * @return Judetul detinatorului
     */
    public Judet mapIdCardToJud(String idCardSeries) throws InvalidRoIdCardSeriesException;

}
