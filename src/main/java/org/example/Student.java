package org.example;

import jakarta.persistence.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "students", schema = "_da_vtschool_2526")
public class Student {
    @Id
    @Column(name = "idcard", nullable = false, length = 8)
    private String idcard;

    @Column(name = "firstname", nullable = false, length = 50)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 100)
    private String lastname;

    @Column(name = "phone", length = 12)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @OneToMany(mappedBy = "student")
    private Set<Enrollment> enrollments = new LinkedHashSet<>();

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(Set<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public Student (){
        this.idcard = null;
        this.firstname = null;
        this.lastname = null;
        this.phone = null;
        this.email = null;
    }

    @Override
    public String toString() {
        return idcard + " " + firstname + " " + lastname + " " + phone + " " + email;
    }

    public List<Student> getStudents() {
        try(Session session = SessionFactory.getSessionFactory().openSession()){
            Query myQuery = session.createQuery("SELECT s FROM Student s, Student.class");
            List<Student> students = myQuery.getResultList();
            return students;
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Student getStudentByIdcard(String idCard) {
        try(Session session = SessionFactory.getSessionFactory().openSession()){
            Query myQuery = session.createQuery("SELECT s FROM Student s WHERE s.idcard = :idCard");
            myQuery.setParameter("idCard", idcard);
            return Student.class.cast(myQuery.getSingleResult());
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean exists() {
        try(Session session = SessionFactory.getSessionFactory().openSession()){
            Student student = getStudentByIdcard(this.idcard);

            return student != null;
        }
    }

    public boolean existsId(String idCard){
        return getStudentByIdcard(idCard) != null;
    }


    public boolean checkEmail(){
        try {
            String check = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
            return this.email.matches(check);
        } catch (RuntimeException e) {
            throw new RuntimeException(this.getFirstname() + " has no valid email");
        }
    }

    public boolean checkPhoneNumber(){
        try{
            return this.phone.matches("\\d{9}");
        }catch (RuntimeException e) {
            throw new RuntimeException(this.getFirstname() + " has no valid phone number");
        }
    }

    public boolean checkIdCard(){
      return this.idcard.length() == 8;
    }

    public boolean completedCourse(String idCard, int courseId) {
        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(sub) " +
                                    "FROM Subject sub " +
                                    "JOIN SubjectCours c " +
                                    "WHERE c.id = :courseId " +
                                    "AND sub.id NOT IN (" +
                                    "    SELECT s.subject.id " +
                                    "    FROM Score s " +
                                    "    JOIN s.enrollment e " +
                                    "    JOIN e.student st " +
                                    "    WHERE st.idcard = :studentId " +
                                    "    AND s.score >= 5" +
                                    ")",
                            Long.class)
                    .setParameter("courseId", courseId)
                    .setParameter("studentId", idCard)
                    .uniqueResult();

            return count != null && count == 0;
        } catch (Exception e) {
            System.err.println("Error checking if student completed course: " + e.getMessage());
            return false;
        }
    }

    public List<Score> studentInfo(String idCard){
        try(Session session = SessionFactory.getSessionFactory().openSession()){
            return session.createQuery(
                            "SELECT sc " +
                                    "FROM Score sc " +
                                    "JOIN sc.enrollment e " +
                                    "JOIN e.student st " +
                                    "JOIN sc.subject sub " +
                                    "WHERE st.idcard = :studentId " +
                                    "ORDER BY e.year DESC",
                            Score.class
                    ).setParameter("studentId", idCard)
                    .getResultList();
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}