package vttp.nus.miniproject2.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

import vttp.nus.miniproject2.models.Question;
import vttp.nus.miniproject2.repositories.QuestionRepository;


@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;
    
    public List<Question> selectNextQuestion(String gameId) throws JsonProcessingException {
        List<Question> questions = questionRepository.selectQuestionsByEdition(gameId);
        return questions;
    }
}