package org.example;

import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

@Entity
@Table(name = "scores", schema = "_da_vtschool_2526")
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(name = "score")
    private Integer score;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void createScore() {
        Transaction transaction = null;
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Score score = new Score();
            score.setEnrollment(enrollment);
            score.setSubject(subject);
            score.setScore(null);
            session.persist(score);
            transaction.commit();
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error creating score: " + e.getMessage());
        }
    }

    public void printScores(List<Score> scores){
        Transaction transaction = null;
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            System.out.println("Year      Subjets                            Score");
            System.out.println("---------------------------------------------------");
            for (Score s : scores) {
                int year = s.getEnrollment().getYear();
                String subject = s.getSubject().getName();
                int score = s.getScore();
                System.out.println(year + " " + subject + " " + score);
                session.persist(s);
            }
            transaction.commit();
        }catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        }
    }


}