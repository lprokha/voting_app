package data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class JsonListStorage<T> {
    private final Path filePath;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Type listType;

    public JsonListStorage(Path filePath, Type listType) {
        this.filePath = filePath;
        this.listType = listType;
        ensureExists();
    }

    private void ensureExists() {
        try {
            if (filePath.getParent() != null && Files.notExists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
            if (Files.notExists(filePath)) {
                Files.writeString(filePath, "[]", StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot init json file: " + filePath, e);
        }
    }

    public List<T> loadAll() {
        try (Reader r = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            List<T> items = gson.fromJson(r, listType);
            return items != null ? items : new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Cannot read json file: " + filePath, e);
        }
    }

    public void saveAll(List<T> items) {
        try (Writer w = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            gson.toJson(items, listType, w);
        } catch (IOException e) {
            throw new RuntimeException("Cannot write json file: " + filePath, e);
        }
    }
}
