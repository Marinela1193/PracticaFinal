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
                if (this.args.length > 2) {
                    String idCard = this.args[1];
                    int courseid  = Integer.parseInt(this.args[2]);
                    enrollStudent(idCard, courseid);
                } else {
                    System.err.println("Two parameters required");
                    helpScreen();
                }
                break;
            case "-p":
            case "--print":
                if (this.args.length > 2) {
                    String idCard = this.args[1];
                    int courseid  = Integer.parseInt(this.args[2]);
                    printScores(idCard, courseid);
                } else {
                    System.err.println("Two parameters required");
                    helpScreen();
                }
                break;
            case "-q":
            case "--qualify":
                if (this.args.length > 2) {
                    String idCard = this.args[1];
                    int courseid  = Integer.parseInt(this.args[2]);
                    introScores(idCard, courseid);
                } else {
                    System.err.println("Two parameters required");
                    helpScreen();
                }
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
        try{
            //we declare the class that contains the method to read the students
            StudentXmlParser myXMLStudentsHandler = new StudentXmlParser();

            //we declare a list were the students will be added
            List<Student> studentsXML = myXMLStudentsHandler.read(filename);

            addListToDataBase(studentsXML);

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addListToDataBase(List<Student> studentList) throws RuntimeException {

        try(Session session = SessionFactory.getSessionFactory().openSession()) {

            Transaction transaction = session.beginTransaction();

            try {
                for (Student student : studentList) {
                    System.out.println(student);
                    if (student.existsId(student.getIdcard())) {
                        throw new RuntimeException("IDCARD:  " + student.getIdcard() + " is already in the system.");
                    }

                    if (!student.checkEmail()) {
                        throw new RuntimeException("IDCARD:  " + student.getIdcard() + " has an invalid email");

                    }

                    if (!student.checkPhoneNumber()) {
                        throw new RuntimeException("IDCARD:  " + student.getIdcard() + " has an invalid phone number");

                    }
                    session.persist(student);
                }
            }catch (Exception e) {
                transaction.rollback();
                System.out.println(e.getMessage());
                throw new RuntimeException("No students added to the system");
            }
            transaction.commit();

            System.out.println(studentList.size() + " Student(s) added correctly");
        }
    }

    public static void printScores(String idCard, int idCourse) {

        try (Session session = SessionFactory.getSessionFactory().openSession()) {
            Scanner sc = new Scanner(System.in);

            //we request the ID of the student that we want to print
            System.out.println("Introduce the ID of the student to see the information");
            String id = sc.nextLine();

            /*if(!Student.existsId(id)){
                System.err.println("The student does not exist in the system");
                return;
            }*/

            Transaction transaction;
            transaction = session.beginTransaction();

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
            transaction.commit();
        }
    }

    public static void enrollStudent(String idCard, int idCourse) {
        /*The -e / --enroll option will enroll an existing student (idCard must be provided)
        in an existing course. */
        try (Session session = SessionFactory.getSessionFactory().openSession()) {}
            //comprobamos que idCard es valido
            //comprobamos si idCard existe

            //comprobar si el course existe en la BD



        /*The student can only be enrolled in one course in a specific
        academic year.*/

        //comprobar que en la tabla enrollments tenga ese ID con ese curso
        //Opcion1. No está matriculado en nada.
            //1. Crear registro en enrollments
            //2. Extraes las asignaturas de primer año de curso buscado
                //devuelve lista asignaturas y hacemos bucle por cada asignatura generamos registro en Scores
            /*The first year the student enrolls in a course, the system must register
            every subject in the first year as enrolled.
            For example, if the student is enrolling in DAW,
            five modules should be added to the table scores.*/
        //Opcion2. El alumno está matriculado en ese curso.
            //1. Recoger asignaturas suspensas
            //2. Añadir las asignaturas de segundo a la tabla scores
        //PASA DE CURSO CON ASIGNATURAS PENDIENTES Y RECIBE LAS ASIGNATURAS NUEVAS DE 2
        //Opcion3. El alumno está matriculado en otro curso ya no podemos seguir.
        //Opcion4. El alumno ha completado ese curso, no puede matricularse

        /*The following years the student may enroll the second-year subjects and every subject
        not passed in the first year. */
        /* This process will be repeated until the student passes all the
        subjects. A subject is passed when the score is equal or superior to five. Once a student
        has finished a course, it can’t enroll in the same course again.
        To simplify the enrollment process, you must create a stored function returning the
        subjects still not passed by a student in a specified course (including first and second
        year subjects). The easiest way to develop this function is starting with the opposite
        function (subjects passed in a specified course).
        Both stored functions are MANDATORY, and must include in the function name YOUR
        INITIALS and the current academic year. That is, the subjects_passed function would
        be name like: subjects_passed_jrgs_2526. The functions will be delivered in a text
        file included in your project (stored_functions.txt). Not including this file will imply
        a zero-score in the corresponding qualification item.*/
    }

    public static void introScores(String idCard, int idCourse) {

        try (Session session = SessionFactory.getSessionFactory().openSession()) {
//            Scanner sc = new Scanner(System.in);

            //We call the student we are about to update the scores
            Student student = (Student) session.find(Student.class, idCard);

            if (student == null) {
                System.err.println("Student with ID " + idCard + " not found");
                return;
            }

            //We create a list of the subjects this student is enrolled and
            ScoreMethods scoreMethods = new ScoreMethods();
            List <Score> scoresStudent = scoreMethods.getScores(idCard);
            scoreMethods.addScores(session, scoresStudent);

            System.out.println("All scores updated successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


            /*List<Score> subjectsToScore = session.createQuery(
                            "SELECT sc " +
                                    "FROM Score sc " +
                                    "JOIN sc.enrollment e " +
                                    "JOIN e.student st " +
                                    "WHERE sc.score IS NULL AND st.idcard = :studentId",
                            Score.class
                    ).setParameter("studentId", id)
                    .getResultList();*/

            /*if (scoresStudent.isEmpty()) {
                System.err.println("This student has no pending subjects to qualify.");
                return;
            }
            //Then, we do an update of the scores of those subjects
            for (Score score : scoresStudent) {
                Subject s = score.getSubject();
                System.out.println("Introduce the score for subject: " + s.getName() + " (0-10 or 99 to skip)");
                //we need to make sure first the score written goes between 0 - 10
                int scoreValue = sc.nextInt();

                int checkValue = scoreMethods.checkScorevalue(scoreValue);

                if(checkValue == 99) {
                    break;
                }
                if(checkValue == -1) {
                    break;
                }

                //if user writes 99 it means he does not want to qualify that subject yet.
                /*if (scoreValue == 99) {
                    System.out.println("Skipping subject.");
                    continue;
                }
                if (scoreValue < 0 || scoreValue > 10) {
                    System.err.println("Invalid score. Must be between 0 and 10.");
                    continue;
                }*/
