package org.example;

import jakarta.persistence.*;

import java.util.LinkedHashSet;
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

}