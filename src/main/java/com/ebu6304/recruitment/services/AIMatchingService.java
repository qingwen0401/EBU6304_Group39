package com.ebu6304.recruitment.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class AIMatchingService {

    private static final String DEEPSEEK_API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final int TIMEOUT_MS = 30000;

    private final Gson gson;

    public AIMatchingService() {
        this.gson = new Gson();
    }

    public AIMatchingResult matchSkills(String apiKey, String jobRequirements,
                                        String jobTitle, List<String> requiredSkills,
                                        String taName, String taBio, List<String> taSkills,
                                        double taGpa, String coverLetter) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("DeepSeek API key is required");
        }

        String prompt = buildPrompt(jobRequirements, jobTitle, requiredSkills,
                                   taName, taBio, taSkills, taGpa, coverLetter);

        try {
            String response = callDeepSeekAPI(apiKey, prompt);
            return parseAIResponse(response);
        } catch (Exception e) {
            throw new RuntimeException("AI matching failed: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(String jobRequirements, String jobTitle,
                               List<String> requiredSkills, String taName,
                               String taBio, List<String> taSkills,
                               double taGpa, String coverLetter) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert HR assistant for academic TA recruitment. ");
        prompt.append("Analyze the match between a job posting and a TA candidate's profile.\n\n");

        prompt.append("**Job Position:**\n");
        prompt.append("Title: ").append(jobTitle).append("\n");
        prompt.append("Requirements: ").append(jobRequirements).append("\n");
        prompt.append("Required Skills: ").append(String.join(", ", requiredSkills)).append("\n\n");

        prompt.append("**Candidate Profile:**\n");
        prompt.append("Name: ").append(taName).append("\n");
        prompt.append("GPA: ").append(taGpa).append("\n");
        prompt.append("Skills: ").append(String.join(", ", taSkills)).append("\n");
        if (taBio != null && !taBio.trim().isEmpty()) {
            prompt.append("Bio: ").append(taBio).append("\n");
        }
        if (coverLetter != null && !coverLetter.trim().isEmpty()) {
            prompt.append("Cover Letter: ").append(coverLetter).append("\n");
        }

        prompt.append("\n**Task:**\n");
        prompt.append("Evaluate how well this candidate matches the job requirements. ");
        prompt.append("You MUST respond with a valid JSON object (no markdown, no code blocks) in this exact format:\n");
        prompt.append("{\n");
        prompt.append("  \"Matching_Score\": <integer 0-100>,\n");
        prompt.append("  \"Matched_Skills\": [\"skill1\", \"skill2\", ...],\n");
        prompt.append("  \"Reason\": \"<one concise sentence in English explaining the match>\"\n");
        prompt.append("}\n\n");
        prompt.append("Rules:\n");
        prompt.append("- Matching_Score: 0-100 integer based on skills overlap, GPA, and overall fit\n");
        prompt.append("- Matched_Skills: list of candidate's skills that match job requirements (empty array if none)\n");
        prompt.append("- Reason: ONE sentence in English, max 150 characters, explaining why this score\n");
        prompt.append("- Output ONLY the JSON object, no other text\n");

        return prompt.toString();
    }

    private String callDeepSeekAPI(String apiKey, String prompt) throws Exception {
        URL url = new URL(DEEPSEEK_API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "deepseek-chat");
        requestBody.addProperty("temperature", 0.3);
        requestBody.addProperty("max_tokens", 500);

        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", prompt);

        com.google.gson.JsonArray messages = new com.google.gson.JsonArray();
        messages.add(message);
        requestBody.add("messages", messages);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    errorResponse.append(line);
                }
                throw new RuntimeException("DeepSeek API error (HTTP " + responseCode + "): " + errorResponse);
            }
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    private AIMatchingResult parseAIResponse(String apiResponse) {
        try {
            JsonObject responseObj = JsonParser.parseString(apiResponse).getAsJsonObject();
            String content = responseObj.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .get("message").getAsJsonObject()
                    .get("content").getAsString().trim();

            content = content.replaceAll("^```json\\s*", "").replaceAll("\\s*```$", "").trim();

            JsonObject result = JsonParser.parseString(content).getAsJsonObject();

            int score = result.get("Matching_Score").getAsInt();
            String reason = result.get("Reason").getAsString();

            List<String> matchedSkills = new ArrayList<>();
            if (result.has("Matched_Skills") && result.get("Matched_Skills").isJsonArray()) {
                result.getAsJsonArray("Matched_Skills").forEach(skill ->
                    matchedSkills.add(skill.getAsString())
                );
            }

            return new AIMatchingResult(score, matchedSkills, reason);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse AI response: " + e.getMessage() +
                                     ". Response: " + apiResponse, e);
        }
    }

    public static class AIMatchingResult {
        private final int matchingScore;
        private final List<String> matchedSkills;
        private final String reason;

        public AIMatchingResult(int matchingScore, List<String> matchedSkills, String reason) {
            this.matchingScore = matchingScore;
            this.matchedSkills = matchedSkills;
            this.reason = reason;
        }

        public int getMatchingScore() {
            return matchingScore;
        }

        public List<String> getMatchedSkills() {
            return matchedSkills;
        }

        public String getReason() {
            return reason;
        }
    }
}
