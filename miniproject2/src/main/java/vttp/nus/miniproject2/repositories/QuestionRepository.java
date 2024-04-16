package vttp.nus.miniproject2.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import vttp.nus.miniproject2.Queries;
import vttp.nus.miniproject2.models.Question;

import java.util.List;

@Repository
public class QuestionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @SuppressWarnings("deprecation")
    public List<Question> selectQuestionsByEdition(String gameId) throws JsonProcessingException {
        String edition = getEditionByGameId(gameId);
        System.out.println("(QuestionRepository) Edition retrieved for gameId " + gameId + ": " + edition);

        List<Question> questions = jdbcTemplate.query(
                Queries.SQL_QUESTIONS_BY_EDITION,
                new Object[] { edition },
                (rs, rowNum) -> {
                    Question question = new Question();
                    question.setId(rs.getLong("id"));
                    question.setQuestionNumber(rs.getInt("question_number")); 
                    question.setQuestionText(rs.getString("question_text"));
                    question.setCategory(rs.getString("category"));
                    question.setEdition(rs.getString("edition"));
                    question.setLevel(rs.getInt("level"));
                    System.out.println("(QuestionRepository) Found question: " + question);
                    return question;
                });

        System.out.println("(QuestionRepository) Retrieved " + questions.size() + " questions for edition " + edition);

        return questions;
    }

    public String getEditionByGameId(String gameId) throws JsonProcessingException {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String gameJson = hashOperations.get(gameId, "game");
        System.out.println("(QuestionRepository) Retrieved game data '" + gameJson + "' for gameId '" + gameId + "'");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode gameNode = objectMapper.readTree(gameJson);
        String edition = gameNode.get("edition").asText();

        System.out.println("(QuestionRepository) Retrieved edition '" + edition + "' for gameId '" + gameId + "'");
        return edition;
    }
}
