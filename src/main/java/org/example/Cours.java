package org.example;

import jakarta.persistence.*;
import org.hibernate.Session;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "courses", schema = "_da_vtschool_2526")
public class Cours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false, length = 90)
    private String name;

    @OneToMany(mappedBy = "course")
    private Set<Enrollment> enrollments = new LinkedHashSet<>();

    @OneToMany(mappedBy = "course")
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

    public Set<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(Set<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public Set<SubjectCours> getSubjectCourses() {
        return subjectCourses;
    }

    public void setSubjectCourses(Set<SubjectCours> subjectCourses) {
        this.subjectCourses = subjectCourses;
    }

    public boolean checkCourse(int coursCode) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {

            Long count = session.createQuery(
                            "SELECT COUNT(c) " +
                                    "FROM Cours c " +
                                    "WHERE c.id = :code", Long.class)
                    .setParameter("code", coursCode)
                    .uniqueResult();

            return count != null && count > 0;

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public int getTotalSubjects(int courseId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(sc.subject) " +
                                    "FROM SubjectCours sc " +
                                    "WHERE sc.course.id = :courseId", Long.class)
                    .setParameter("courseId", courseId)
                    .uniqueResult();

            return count != null ? count.intValue() : 0;
        } catch (RuntimeException e) {
            System.err.println("Error getting total subjects: " + e.getMessage());
            return 0;
        }
    }

}