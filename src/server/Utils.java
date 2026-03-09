package server;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class Utils {
    public static Map<String, String> parseUrlEncoded(String rawLines, String delimiter) {
        if (rawLines == null || rawLines.isBlank()) return Map.of();

        String[] pairs = rawLines.split(delimiter);

        Stream<Map.Entry<String, String>> stream = Arrays.stream(pairs)
                .map(Utils::decode)
                .filter(Optional::isPresent)
                .map(Optional::get);

        return stream.collect(java.util.stream.Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue
        ));
    }

    static Optional<Map.Entry<String, String>> decode(String kv) {
        if (kv == null) return Optional.empty();

        kv = kv.strip();

        if (!kv.contains("=")) return Optional.empty();

        String[] pair = kv.split("=", 2);
        if (pair.length != 2) return Optional.empty();

        String key = URLDecoder.decode(pair[0].strip(), StandardCharsets.UTF_8);
        String value = URLDecoder.decode(pair[1].strip(), StandardCharsets.UTF_8);
        return Optional.of(Map.entry(key, value));
    }
}
