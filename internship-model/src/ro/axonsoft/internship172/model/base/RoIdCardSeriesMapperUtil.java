package ro.axonsoft.internship172.model.base;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

public class RoIdCardSeriesMapperUtil {

    public static final Multimap<String, String> JUD_TO_SERIES_MAP = ImmutableMultimap.<String, String> builder()
            .put("CJ", "KX")
            .put("CJ", "CJ")
            .put("B", "DP")
            .put("B", "DR")
            .put("B", "DT")
            .put("B", "RD")
            .put("B", "RR")
            .put("B", "RT")
            .put("B", "RX")
            .put("MM", "MM")
            .put("NT", "NT")
            .build();
}
