package vttp.nus.miniproject2.models;

import java.util.List;

public class Player {

  private String name;
  private Integer score;
  private String gameId;
  // private List<Response> responses;
  private List<String> responses;
  
  public Player() {
  }

  public Player(String name) {
    this.name = name;
    this.score = 0;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getScore() {
    return this.score;
  }

  public void setScore(Integer score) {
    this.score = score;
  }

  public String getGameId() {
    return gameId;
  }

  public void setGameId(String gameId) {
    this.gameId = gameId;
  }

  public List<String> getResponses() {
    return responses;
  }

  public void setResponses(List<String> responses) {
    this.responses = responses;
  }

  // public List<Response> getResponses() {
  //   return responses;
  // }

  // public void setResponses(List<Response> responses) {
  //   this.responses = responses;
  // }

  // public String getResponsesJson() {
  //   return responsesJson;
  // }

  // public void setResponsesJson(String responsesJson) {
  //   this.responsesJson = responsesJson;
  // }

}