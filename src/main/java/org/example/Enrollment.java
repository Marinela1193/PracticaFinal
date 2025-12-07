package org.example;

import jakarta.persistence.*;
import org.hibernate.Session;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "enrollments", schema = "_da_vtschool_2526")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course", nullable = false)
    private Cours course;

    @Column(name = "year", nullable = false)
    private Integer year;

    @OneToMany(mappedBy = "enrollment")
    private Set<Score> scores = new LinkedHashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Cours getCourse() {
        return course;
    }

    public void setCourse(Cours course) {
        this.course = course;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Set<Score> getScores() {
        return scores;
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public boolean checkEnrollment(String idCard, int courseCode) {
        try(Session session = SessionFactory.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                    "FROM Enrollment e " +
                            "WHERE e.student.idcard = :studentId " +
                            "AND e.course.id = :courseId",
                    Long.class
            )
            .setParameter("studentId", idCard)
            .setParameter("courseId", courseCode)
                    .uniqueResult();

           return count != null && count > 0;

        }
    }

    public void createEnrollment(String idCard, int courseCode) {
        try(Session session = SessionFactory.getSessionFactory().openSession()) {

            Enrollment enrollment = new Enrollment();
            //in order to introduce the student in the first year
            //we create the course c in order to assign the year 1
            Cours c = session.find(Cours.class, course);
            enrollment.setStudent(student);
            enrollment.setCourse(c);
            enrollment.setYear(2025);
            //we add the student in all courses of 1 year

            session.persist(enrollment);
        }
    }

}