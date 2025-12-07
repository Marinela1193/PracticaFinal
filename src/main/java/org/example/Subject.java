package org.example;

import jakarta.persistence.*;
import org.hibernate.Session;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "subjects", schema = "_da_vtschool_2526")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code", nullable = false)
    private Integer id;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "year")
    private Integer year;

    @Column(name = "hours")
    private Integer hours;

    @OneToMany(mappedBy = "subject")
    private Set<Score> scores = new LinkedHashSet<>();

    @OneToMany(mappedBy = "subject")
    private Set<SubjectCours> subjectCourses = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public Set<SubjectCours> getSubjectCourses() {
        return subjectCourses;
    }

    public void setSubjectCourses(Set<SubjectCours> subjectCourses) {
        this.subjectCourses = subjectCourses;
    }

    public List<Subject> getSubjectsFirstYear(int courseId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            return session.createQuery("SELECT sc.subject FROM SubjectCours sc WHERE sc.course.id = :courseId AND sc.subject.year = 1", Subject.class)
            .setParameter("courseId", courseId)
                    .getResultList();
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Subject> getSubjectsSecondYear(int courseId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            return session.createQuery("SELECT sc.subject " +
                    "FROM SubjectCours sc " +
                    "WHERE sc.course.id = :courseId " +
                    "AND sc.subject.year = 2", Subject.class)
                    .setParameter("courseId", courseId)
                    .getResultList();
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Subject> getSubjectsFailed(String idCard) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            return  session.createNativeQuery(
                    "SELECT * FROM subjectsPending_mps_2526(:studentId)"
            ).setParameter("studentId", idCard)
                    .addEntity(Subject.class)
                    .getResultList();
        }
    }

    public List<Subject> getSubjectsPassed(String idCard) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            return  session.createNativeQuery(
                            "SELECT * FROM subjectsPassed_mps_2526(:studentId)"
                    ).setParameter("studentId", idCard)
                    .addEntity(Subject.class)
                    .getResultList();
        }
    }

    public List<Subject> subjectsStudentIsEnrolled(String idCard) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            return session.createQuery(
                            "SELECT sub " +
                                    "FROM Subject sub " +
                                    "JOIN sub.scores s " +
                                    "JOIN s.enrollment e " +
                                    "JOIN e.student st " +
                                    "WHERE st.idcard = :studentId",
                            Subject.class
                    ).setParameter("studentId", idCard)
                    .getResultList();
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}