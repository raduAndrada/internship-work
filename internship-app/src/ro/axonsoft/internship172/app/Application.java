package ro.axonsoft.internship172.app;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import ro.axonsoft.internship172.api.StreamVehicleOwnersProcessor;
import ro.axonsoft.internship172.api.VehicleOwnersProcessor;
import ro.axonsoft.internship172.impl.RoIdCardParserImpl;
import ro.axonsoft.internship172.impl.RoIdCardSeriesJudMapperImpl;
import ro.axonsoft.internship172.impl.RoRegPlateParserImpl;
import ro.axonsoft.internship172.impl.StreamVehicleOwnersProcessorImpl;
import ro.axonsoft.internship172.impl.VehicleOwnersProcessorImpl;

public class Application {

    private static VehicleOwnersProcessor vehicleOwnersProcessor = new VehicleOwnersProcessorImpl(
            new Date());
    private static StreamVehicleOwnersProcessor streamVehicleOwnersProcessor = new StreamVehicleOwnersProcessorImpl(
            new RoRegPlateParserImpl(),
            new RoIdCardParserImpl(new RoIdCardSeriesJudMapperImpl(MapContainer.SERIES_JUD_MAP)),
            vehicleOwnersProcessor);

    public static void main(final String args[]) throws IOException {
        checkArgument(args.length == 2, "Main class needs 2 files to work");
        final File input = new File(args[0]);
        final File output = new File(args[1]);
        checkArgument(input.exists() && !input.isDirectory(), "Input file must exist");
        try (FileOutputStream out = new FileOutputStream(output)) {
            try (FileInputStream in = new FileInputStream(input)) {
                streamVehicleOwnersProcessor.process(in, out);
                System.out.println("Fisierul de intrare:" + args[0] + " a fost procesat cu succes");
                System.out.println("Fisierul de iesire:" + args[1] + " contine rezultatul procesarii");
            }
        }

    }

    private static class MapContainer {

        /**
         * Maparea inversa
         */
        private static final Multimap<String, String> SERIES_JUD_MAP;

        static {
            // citirea proprietatiii
            String prop = System.getProperty("ro.axonsoft.internship.jcis.url");
            checkArgument(prop!=null,"Url-ul de sistem cu proprietatea nu poate fi null");
            final URL jcisUrl;
            try {
                // citirea url-ului
                jcisUrl = new URL(prop);
            } catch (final MalformedURLException e1) {
                throw new IllegalArgumentException("URL-ul asteptat in proprietatea de sistem e null", e1);
            }

            // citirea din fisierul yaml
            YamlReader reader;
            prop = "jcis.yml";
            final ImmutableMultimap.Builder<String, String> mapBuilder = ImmutableMultimap.builder();
            try (final Reader streamReader = new InputStreamReader(jcisUrl.openStream())) {
                // reader
                reader = new YamlReader(streamReader);
                @SuppressWarnings("unchecked")
                final Map<String, List<String>> judMap = (Map<String, List<String>>) reader.read();
                for (final Entry<String, List<String>> entry : judMap.entrySet()) {
                    mapBuilder.putAll(entry.getKey().toUpperCase(),
                            entry.getValue().stream().map(String::toUpperCase)::iterator);
                }

            } catch (final IOException e) {
                throw new IllegalArgumentException(String.format("Eroare parsare JCIS %s", jcisUrl));
            }
            SERIES_JUD_MAP = mapBuilder.build();
        }
    }
}
