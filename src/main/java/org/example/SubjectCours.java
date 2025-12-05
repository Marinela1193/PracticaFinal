package org.example;

import jakarta.persistence.*;

@Entity
@Table(name = "subject_courses", schema = "_da_vtschool_2526")
public class SubjectCours {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subject_courses_id_gen")
    @SequenceGenerator(name = "subject_courses_id_gen", sequenceName = "subject_courses_code_seq", allocationSize = 1)
    @Column(name = "code", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Cours course;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Cours getCourse() {
        return course;
    }

    public void setCourse(Cours course) {
        this.course = course;
    }

}