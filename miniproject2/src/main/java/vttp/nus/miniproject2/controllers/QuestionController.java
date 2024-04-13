// package vttp.nus.miniproject2.controllers;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import vttp.nus.miniproject2.models.Question;
// import vttp.nus.miniproject2.repositories.GameRepository;
// import vttp.nus.miniproject2.services.QuestionService;

// @RestController
// @RequestMapping("/api")
// public class QuestionController {

//     @Autowired
//     private QuestionService questionService;

//     @Autowired
//     private GameRepository gameRepository;

//     @GetMapping("/game/{gameId}")
//     public ResponseEntity<Question> getQuestionsByGameId(@PathVariable String gameId) {
//         System.out.println("(QuestionController) getQuestionsByGameId called");
//         try {
//             if (!gameRepository.doesGameExist(gameId)) {
//                 return ResponseEntity.notFound().build();
//             }
//             Question question = questionService.getQuestion(gameId);
//             System.out.println(question);
//             return ResponseEntity.ok(question);
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//         }
//     }

//     @GetMapping("/questions/{gameId}/{edition}/remaining")
//     public List<Question> getRemainingQuestions(@PathVariable String gameId, @PathVariable String edition) {
//         return questionService.getRemainingQuestions(gameId, edition);
//     }
// }