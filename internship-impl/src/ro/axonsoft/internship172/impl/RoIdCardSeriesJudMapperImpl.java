package ro.axonsoft.internship172.impl;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Multimap;

import ro.axonsoft.internship172.api.InvalidRoIdCardSeriesException;
import ro.axonsoft.internship172.api.Judet;
import ro.axonsoft.internship172.api.RoIdCardSeriesJudMapper;
/**
 * Maparea unui string pe un judet
 *
 * @author intern
 *
 */
public class RoIdCardSeriesJudMapperImpl implements RoIdCardSeriesJudMapper {


    private final Map<String, Judet> seriesToJudMap;

    public RoIdCardSeriesJudMapperImpl(final Multimap<String, String> judToSeriesMap) {
        final ImmutableMap.Builder<String, Judet> seriesToJudMapBuilder = ImmutableSortedMap.naturalOrder();
        for (final Entry<String, Collection<String>> entry : judToSeriesMap.asMap().entrySet()) {
            final Judet judet = Judet.valueOf(entry.getKey().toUpperCase());
            entry.getValue().forEach(serie -> seriesToJudMapBuilder.put(serie, judet));
        }
        seriesToJudMap = seriesToJudMapBuilder.build();
    }

    /**
     * Metoda de mapare folosind o harta inversa
     */

    @Override
    public Judet mapIdCardToJud(final String idCardSeries) throws InvalidRoIdCardSeriesException {
        final Judet j = seriesToJudMap.get(idCardSeries.toUpperCase());
        if (j== null) {
            throw new InvalidRoIdCardSeriesException();
        }
        return j;
    }


}
