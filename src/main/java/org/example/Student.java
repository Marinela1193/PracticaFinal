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

    public boolean exists() {
        List<Student> studentList = this.getStudents();
        for(Student student : studentList){
            if(student.getIdcard().equals(this.idcard)){
                return true;
            }
        }
        return false;
    }

    public void addToDatabase() {

        Transaction transaction;

        try (Session session = SessionFactory.getSessionFactory().openSession()) {

            transaction = session.beginTransaction();

            if (!checkEmail(this.getEmail())) {
                System.out.println("Invalid email");
                return;
            }

            if (!checkPhoneNumber(this.getPhone())) {
                System.out.println("Invalid phone number");
                return;
            }

            session.persist(this);
            transaction.commit();

            System.out.println("Student added correctly");


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkEmail(String email){
        email = this.email;
        String check = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(check);

    }

    public boolean checkPhoneNumber(String number){
        return phone.matches("\\d{9}");
    }
}