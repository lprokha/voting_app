package server;

import com.sun.net.httpserver.HttpExchange;
import data.CandidateStorage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import model.Candidate;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteServer extends BasicServer {

    private final Configuration freemarker;
    private final CandidateStorage candidateStorage = new CandidateStorage(Path.of("storage", "candidates.json"));

    private static final String VOTED_COOKIE = "votedCandidateId";

    public VoteServer(String host, int port) throws IOException {
        super(host, port);

        this.freemarker = initFreeMarker();

        registerGet("/", this::candidatesGet);
        registerPost("/vote", this::votePost);
        registerGet("/thankyou", this::thankYouGet);
        registerGet("/votes", this::votesGet);
        registerGet("/error", this::errorGet);
    }

    private static Configuration initFreeMarker() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            cfg.setDirectoryForTemplateLoading(new File("templates"));
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            return cfg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void renderTemplate(HttpExchange exchange, String templateFile, Map<String, Object> model) {
        try {
            Template template = freemarker.getTemplate(templateFile);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {
                template.process(model, writer);
                writer.flush();

                byte[] data = stream.toByteArray();
                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data);
            }

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private double calculatePercent(int votes, int totalVotes) {
        if (totalVotes == 0) {
            return 0.0;
        }
        return votes * 100.0 / totalVotes;
    }

    private void candidatesGet(HttpExchange exchange) {
        List<Candidate> candidates = candidateStorage.getCandidates();

        Map<String, Object> model = new HashMap<>();
        model.put("candidates", candidates);

        String votedId = getCookieValue(exchange, VOTED_COOKIE);
        model.put("alreadyVoted", votedId != null && !votedId.isBlank());

        renderTemplate(exchange, "candidates.ftlh", model);
    }

    private void votePost(HttpExchange exchange) {
        String raw = getBody(exchange);
        Map<String, String> form = Utils.parseUrlEncoded(raw, "&");

        String candidateIdStr = form.getOrDefault("candidateId", "").trim();
        if (candidateIdStr.isBlank()) {
            redirect303(exchange, "/error");
            return;
        }

        int candidateId;
        try {
            candidateId = Integer.parseInt(candidateIdStr);
        } catch (NumberFormatException e) {
            redirect303(exchange, "/error");
            return;
        }

        Candidate candidate = candidateStorage.findById(candidateId);
        if (candidate == null) {
            redirect303(exchange, "/error");
            return;
        }

        candidateStorage.incrementVotes(candidateId);

        Cookie<String> cookie = Cookie.make(VOTED_COOKIE, String.valueOf(candidateId));
        cookie.setHttpOnly(true);
        setCookie(exchange, cookie);

        redirect303(exchange, "/thankyou");
    }

    private void thankYouGet(HttpExchange exchange) {
        String votedIdStr = getCookieValue(exchange, VOTED_COOKIE);
        if (votedIdStr == null || votedIdStr.isBlank()) {
            redirect303(exchange, "/");
            return;
        }

        int candidateId;
        try {
            candidateId = Integer.parseInt(votedIdStr);
        } catch (NumberFormatException e) {
            redirect303(exchange, "/error");
            return;
        }

        Candidate candidate = candidateStorage.findById(candidateId);
        if (candidate == null) {
            redirect303(exchange, "/error");
            return;
        }

        int totalVotes = candidateStorage.getTotalVotes();
        double percent = calculatePercent(candidate.getVotes(), totalVotes);

        Map<String, Object> model = new HashMap<>();
        model.put("candidate", candidate);
        model.put("percent", Math.round(percent));

        renderTemplate(exchange, "thankyou.ftlh", model);
    }

    private void votesGet(HttpExchange exchange) {
        List<Candidate> candidates = candidateStorage.getCandidatesSortedByVotes();
        int totalVotes = candidateStorage.getTotalVotes();

        int maxVotes = 0;
        for (Candidate c : candidates) {
            if (c.getVotes() > maxVotes) {
                maxVotes = c.getVotes();
            }
        }

        Map<String, Object> model = new HashMap<>();
        model.put("candidates", candidates);
        model.put("totalVotes", totalVotes);
        model.put("maxVotes", maxVotes);

        Map<String, String> percents = new HashMap<>();
        for (Candidate candidate : candidates) {
            double percent = calculatePercent(candidate.getVotes(), totalVotes);
            percents.put(String.valueOf(candidate.getId()), String.valueOf(Math.round(percent)));
        }

        model.put("percents", percents);

        renderTemplate(exchange, "votes.ftlh", model);
    }

    private void errorGet(HttpExchange exchange) {
        Map<String, Object> model = new HashMap<>();
        model.put("message", "Candidate not found");

        renderTemplate(exchange, "error.ftlh", model);
    }
}