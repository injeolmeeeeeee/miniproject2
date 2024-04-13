package vttp.nus.miniproject2;

public class Queries {

    public static final String SQL_QUESTIONS_BY_EDITION = """
        select * from questions 
        where edition = ?;
        """;
}