package org.example;

import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Scanner;


public class Menu {

    private String[] args;

    public Menu(String[] args) {
        this.args = args;
    }

    public void start() {

        if (args.length == 0) {
            System.err.print("No arguments to read \n");
            helpScreen();
            return;
        }

        String arg = args[0];
        switch (arg) {
            case "-h":
            case "--help":
                helpScreen();
                break;
            case "-a":
            case "--add":
                if (this.args.length > 1) {
                    String filename = this.args[1];
                    addStudentsXMLFile(filename);
                } else {
                    System.err.println("The filename is mandatory");
                    helpScreen();
                }
                break;
            case "-e":
            case "--enroll":
                enrollStudent();
                break;
            case "-p":
            case "--print":
                printScores();
                break;
            case "-q":
            case "--qualify":
                introScores();
                break;
        }
    }


    public static void helpScreen() {

        System.out.println("Options:");
        System.out.println("-h, --help: show this help");
        System.out.println("-a, --add {filename.xml}: add the students in the XML file to the database.");
        System.out.println("-e, --enroll {studentId} {courseId}: enroll a student in a course");
        System.out.println("-p, --print {studentId} {courseId}: show the scores of a student in a course");
        System.out.println("-q, --qualify {studentId} {courseId}: introduce the scores obtained by the student in the course.");

    }

    public static void addStudentsXMLFile(String filename) {
        //we declare the class that contains the method to read the students
        StudentXmlParser myXMLStudentsHandler = new StudentXmlParser();

        //we declare a list were the students will be added
        List<Student> studentsXML = myXMLStudentsHandler.leer(filename);

        /*we print the list
        System.out.println("Students list " + studentsXML.size());
        for (Student student : studentsXML) {
            System.out.println(student);
        }*/

        for (Student student : studentsXML) {
            if (!student.exists()) {
                student.addToDatabase();
            } else {
                System.out.println("The student already exists in the system");
            }
        }

    }


    public static void printScores() {

        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Scanner sc = new Scanner(System.in);

            //we request the ID of the student that we want to print
            System.out.println("Introduce the ID of the student to see the information");
            String id = sc.nextLine();

            Transaction transaction2;
            transaction2 = session.beginTransaction();

            //We call the student we are about to update the scores
            Student student = (Student) session.find(Student.class, id);

            //we create a query to list all the student's information
            List<Score> studentInfoList = session.createQuery(
                            "SELECT sc " +
                                    "FROM Score sc " +
                                    "JOIN sc.enrollment e " +
                                    "JOIN e.student st " +
                                    "JOIN sc.subject sub " +
                                    "WHERE st.idcard = :studentId " +
                                    "ORDER BY e.year DESC",
                            Score.class
                    ).setParameter("studentId", id)
                    .getResultList();
            //we need to insert that information into a table
            //we create a loop with certain structure so details are added in order
            System.out.println("Year      Subjets                            Score");
            System.out.println("---------------------------------------------------");
            for (Score s : studentInfoList) {
                int year = s.getEnrollment().getYear();
                String subject = s.getSubject().getName();
                int score = s.getScore();
                System.out.println(year + " " + subject + " " + score);
            }
            transaction2.commit();
        }
    }

    public static void enrollStudent() {}

    public static void introScores() {

        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Scanner sc = new Scanner(System.in);

            //we request the ID of the student that we want to introduce the scores
            System.out.println("Introduce the ID of the student to qualify");
            String id = sc.nextLine();

            Transaction transaction2;
            transaction2 = session.beginTransaction();


            //We call the student we are about to update the scores
            Student student = (Student) session.find(Student.class, id);

            if (student == null) {
                System.out.println("Student with ID " + id + " not found");
                return;
            }

            //We create al ist of the subjects this student is enrolled and
            //that the score is null
            // we create a query
            List<Score> subjectsToScore = session.createQuery(
                            "SELECT sc " +
                                    "FROM Score sc " +
                                    "JOIN sc.enrollment e " +
                                    "JOIN e.student st " +
                                    "WHERE sc.score IS NULL AND st.idcard = :studentId",
                            Score.class
                    ).setParameter("studentId", id)
                    .getResultList();

            if (subjectsToScore.isEmpty()) {
                System.out.println("This student has no pending subjects to qualify.");
                return;
            }

            //Then, we do an update of the scores of those subjects

            for (Score score : subjectsToScore) {

                Subject s = score.getSubject();
                System.out.println("Introduce the score for subject: " + s.getName() + " (0-10 or 99 to skip)");
                //we need to make sure first the score written goes between 0 - 10
                int scoreValue = sc.nextInt();
                //if user writes 99 it means he does not want to qualify that subject yet.
                if (scoreValue == 99) {
                    System.out.println("Skipping subject.");
                    continue;
                }
                if (scoreValue < 0 || scoreValue > 10) {
                    System.out.println("Invalid score. Must be between 0 and 10.");
                    continue;
                }
                score.setScore(scoreValue);
                session.merge(score);

                System.out.println("Score for subject " + s.getName() + " updated correctly.");

            }

            session.getTransaction().commit();
            System.out.println("All scores updated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


