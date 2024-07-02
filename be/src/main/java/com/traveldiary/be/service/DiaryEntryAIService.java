package com.traveldiary.be.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.traveldiary.be.dto.DiaryEntryAIRequest;
import com.traveldiary.be.entity.Album;
import com.traveldiary.be.entity.DiaryEntryAI;
import com.traveldiary.be.entity.Users;
import com.traveldiary.be.entity.WritingDiary;
import com.traveldiary.be.repository.DiaryEntryAIRepository;
import com.traveldiary.be.repository.UserRepository;
import com.traveldiary.be.repository.WritingRepository;
import com.traveldiary.be.repository.AlbumRepository;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.format.DateTimeFormatter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

@Service
public class DiaryEntryAIService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WritingRepository writingRepository;

    @Autowired
    private DiaryEntryAIRepository diaryEntryAIRepository;

    @Autowired
    private AlbumRepository albumRepository;

    private List<JsonNode> categories;

    private final Dotenv dotenv = Dotenv.load();

    private static final Logger logger = Logger.getLogger(DiaryEntryAIService.class.getName());

    // 애플리케이션 시작 시 chat_questions.json 파일을 로드하여 카테고리 데이터를 메모리에 저장
    @PostConstruct
    private void loadCategories() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(new ClassPathResource("chat_questions.json").getInputStream());
        categories = new ArrayList<>();
        if (jsonNode.get("categories").isArray()) {
            for (JsonNode node : (ArrayNode) jsonNode.get("categories")) {
                categories.add(node);
            }
        }
    }

    // 특정 키워드에 대한 랜덤 질문 반환
    public String getRandomQuestion(String keyword) {
        for (JsonNode category : categories) {
            if (category.get("keyword").asText().equals(keyword)) {
                List<JsonNode> questionList = new ArrayList<>();
                if (category.get("questions").isArray()) {
                    for (JsonNode node : (ArrayNode) category.get("questions")) {
                        questionList.add(node);
                    }
                }
                Random random = new Random();
                return questionList.get(random.nextInt(questionList.size())).asText();
            }
        }
        return null;
    }

    // ChatGPT API 호출
//    private String generateDiaryContent(String prompt) {
//        String chatGptApiUrl = dotenv.get("CHATGPT_API_URL");
//        String apiKey = dotenv.get("CHATGPT_API_KEY");
//
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(apiKey);
//
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("model", "text-ada-001");  // 제일 저렴한 모델
//        //requestBody.put("model", "gpt-3.5-turbo");
//        requestBody.put("prompt", prompt);
//        requestBody.put("max_tokens", 300);
//
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
//        String response = restTemplate.postForObject(chatGptApiUrl, entity, String.class);
//
//        JsonNode responseJson;
//        try {
//            responseJson = new ObjectMapper().readTree(response);
//            return responseJson.get("choices").get(0).get("text").asText();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return "일기 생성 중 오류가 발생했습니다.";
//        }
//    }


    // ChatGPT API 호출
    private String generateDiaryContent(String prompt) {
        String chatGptApiUrl = dotenv.get("CHATGPT_API_URL");
        String apiKey = dotenv.get("CHATGPT_API_KEY");

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");  //
        requestBody.put("messages", Collections.singletonList(message));
        requestBody.put("max_tokens", 300);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        String response = restTemplate.postForObject(chatGptApiUrl, entity, String.class);

        JsonNode responseJson;
        try {
            responseJson = new ObjectMapper().readTree(response);
            return responseJson.get("choices").get(0).get("message").get("content").asText();
        } catch (IOException e) {
            e.printStackTrace();
            return "일기 생성 중 오류가 발생했습니다.";
        }
    }



    // 사용자의 응답을 기반으로 AI가 일기 생성
    public WritingDiary generateDiaryFromResponses(int writeId, Users user) {
        List<DiaryEntryAI> responses = getUserResponsesByWriteIdAndUserId(writeId, user.getId());
        if (responses.size() < 6) {
            throw new IllegalArgumentException("일기 생성을 위해서는 최소 6개의 응답이 필요합니다.");
        }

        StringBuilder promptBuilder = new StringBuilder();
        for (DiaryEntryAI response : responses) {
            promptBuilder.append("키워드: ").append(response.getKeyword())
                    .append(", 질문: ").append(response.getQuestion())
                    .append(", 답변: ").append(response.getResponse()).append("\n");
        }
        //promptBuilder.append("이 내용을 토대로 여행일기의 제목과 내용을 작성해줘.");
        promptBuilder.append("이 내용을 토대로 여행일기의 제목과 내용을 작성해줘. 다음 내용을 반영해줘 : 좀 더 상세한 감정 묘사, 사건의 순서 명확히 하기, 긍정적인 어조 유지, 산문 형식으로 작성할 것, 800자 제한,피드백은 글에서 드러나지 않음, 결과물을 하나의 문장으로 표현하는 제목을 작성");

        String prompt = promptBuilder.toString();
        String gptContent = generateDiaryContent(prompt);

        String[] gptResults = gptContent.split("\n", 2);
        String generatedTitle = gptResults.length > 0 ? gptResults[0].trim() : "제목 없음";
        String generatedContent = gptResults.length > 1 ? gptResults[1].trim() : "내용 없음";


        // "제목: "과 "내용: "을 제거
        if (generatedTitle.startsWith("제목: ")) {
            generatedTitle = generatedTitle.substring(4).trim();
        }
        if (generatedContent.startsWith("내용: ")) {
            generatedContent = generatedContent.substring(4).trim();
        }

        WritingDiary diary = writingRepository.findById(writeId)
                .orElseThrow(() -> new RuntimeException("해당 일기를 찾을 수 없습니다."));
        diary.setTitle(generatedTitle);
        diary.setContent(generatedContent);
        diary.setUpdatedAt(LocalDateTime.now());

        return writingRepository.save(diary);
    }



    // 새로운 비어있는 일기 생성
    public WritingDiary createEmptyDiary(Users user) {
        WritingDiary diary = new WritingDiary();
        diary.setTitle("임시 제목");
        diary.setContent("임시 내용");
        diary.setUser(user);
        diary.setCreatedAt(LocalDateTime.now());
        diary.setUpdatedAt(LocalDateTime.now());
        diary.setStartDate(LocalDate.now());  // 시작 날짜를 오늘 날짜로 설정
        diary.setFinalDate(LocalDate.now());  // 종료 날짜를 오늘 날짜로 설정
        diary.setTravelDate(LocalDate.now());  // 여행 날짜를 오늘 날짜로 설정
        diary.setIsPublic(false);
        diary.setUrl("");

        int defaultAlbumId = 1;
        Album album = albumRepository.findById(defaultAlbumId)
                .orElseThrow(() -> new RuntimeException("기본 앨범을 찾을 수 없습니다."));
        diary.setAlbum(album);

        return writingRepository.save(diary);
    }

    // 사용자가 답변한 내용을 저장
    public DiaryEntryAI saveUserResponse(DiaryEntryAI diaryEntryAI) {
        return diaryEntryAIRepository.save(diaryEntryAI);
    }

    // 특정 사용자의 모든 질문과 답변 조회
    public List<DiaryEntryAI> getUserResponses(int userId) {
        return diaryEntryAIRepository.findByUserId(userId);
    }

    // 특정 write_id에 대한 질문과 답변 조회
    public List<DiaryEntryAI> getUserResponsesByWriteIdAndUserId(int writeId, int userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        WritingDiary writingDiary = writingRepository.findById(writeId).orElseThrow(() -> new RuntimeException("해당 일기를 찾을 수 없습니다."));

        if (!writingDiary.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("사용자가 소유한 일기가 아닙니다.");
        }

        return diaryEntryAIRepository.findByWritingDiaryAndUser(writingDiary, user);
    }

    // 사용자가 답변한 내용을 저장하고 특정 write_id에 대한 답변만 반환
    public Map<String, Object> saveUserResponses(DiaryEntryAIRequest request, int userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<DiaryEntryAI> diaryEntryAIs = new ArrayList<>();
        WritingDiary writingDiary = getOrCreateActiveDiary(user);
        int savedResponseCount = getUserResponsesByWriteIdAndUserId(writingDiary.getId(), userId).size();

        for (int i = 0; i < request.getKeywords().size(); i++) {
            if (savedResponseCount >= 6) {
                writingDiary = createEmptyDiary(user);
                savedResponseCount = 0;
            }

            String keyword = request.getKeywords().get(i);
            String question = request.getQuestions().get(i);
            String response = request.getResponses().get(i);

            DiaryEntryAI diaryEntryAI = new DiaryEntryAI();
            diaryEntryAI.setUser(user);
            diaryEntryAI.setKeyword(keyword);
            diaryEntryAI.setQuestion(question);
            diaryEntryAI.setResponse(response);
            diaryEntryAI.setWritingDiary(writingDiary);

            diaryEntryAIs.add(diaryEntryAIRepository.save(diaryEntryAI));
            savedResponseCount++;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("diaryEntryAIs", diaryEntryAIs);
        result.put("writeId", writingDiary.getId());

        return result;
    }


    // 활성화된 일기 가져오기 또는 새로운 일기 생성
    public WritingDiary getOrCreateActiveDiary(Users user) {
        List<WritingDiary> diaries = writingRepository.findByUser(user);

        if (!diaries.isEmpty()) {
            return diaries.get(diaries.size() - 1);
        } else {
            return createEmptyDiary(user);
        }
    }
}
