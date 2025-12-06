package org.example;

import org.hibernate.Session;

import java.util.List;
import java.util.Scanner;

public class ScoreMethods {

    //method to look for the student's scores by id
    public List<Score> getScores(String idCard) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT sc " +
                                    "FROM Score sc " +
                                    "JOIN sc.enrollment e " +
                                    "JOIN e.student st " +
                                    "WHERE sc.score IS NULL AND st.idcard = :studentId",
                            Score.class
                    ).setParameter("studentId", idCard)
                    .getResultList();
        }
    }

    public int checkScorevalue(int value) {
        if (value == 99) {
            System.out.println("Skipping subject.");
            return 99;
        }
        if (value < 0 || value > 10) {
            System.out.println("Invalid score. Must be between 0 and 10.");
            return -1;
        }
        return value;
    }

    public void addScores(Session session, List<Score> scores) {
        Scanner sc = new Scanner(System.in);

        //transaccion declarar
        for (Score score : scores) {
            Subject s = score.getSubject();
            int scoreValue;

            while(true) {

                System.out.println("Introduce the score for subject: " + s.getName() + " (0-10 or 99 to skip)");
                //we need to make sure first the score written goes between 0 - 10
                scoreValue = sc.nextInt();

                int checkValue = this.checkScorevalue(scoreValue);

                if (checkValue == 99) {
                    continue;
                }
                if (checkValue == -1) {
                    break;
                }

                score.setScore(scoreValue);
                session.merge(score);
                System.out.println("Score for subject " + s.getName() + " updated correctly.");
                break;
            }
        }
    }
}
