package data;

import com.google.gson.reflect.TypeToken;
import model.Candidate;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CandidateStorage {
    private final JsonListStorage<Candidate> storage;

    public CandidateStorage(Path file) {
        this.storage = new JsonListStorage<>(file, new TypeToken<List<Candidate>>() {}.getType());
    }

    public List<Candidate> getCandidates() {
        List<Candidate> candidates = storage.loadAll();

        for (int i = 0; i < candidates.size(); i++) {
            candidates.get(i).setId(i + 1);
        }

        return candidates;
    }

    public void saveCandidates(List<Candidate> candidates) {
        storage.saveAll(candidates);
    }

    public Candidate findById(int id) {
        for (Candidate candidate : getCandidates()) {
            if (candidate.getId() == id) {
                return candidate;
            }
        }
        return null;
    }

    public void incrementVotes(int id) {
        List<Candidate> candidates = getCandidates();

        for (Candidate candidate : candidates) {
            if (candidate.getId() == id) {
                candidate.setVotes(candidate.getVotes() + 1);
                break;
            }
        }

        saveCandidates(candidates);
    }

    public int getTotalVotes() {
        int total = 0;
        for (Candidate candidate : getCandidates()) {
            total += candidate.getVotes();
        }
        return total;
    }

    public List<Candidate> getCandidatesSortedByVotes() {
        List<Candidate> candidates = new ArrayList<>(getCandidates());
        candidates.sort(Comparator.comparingInt(Candidate::getVotes).reversed());
        return candidates;
    }
}