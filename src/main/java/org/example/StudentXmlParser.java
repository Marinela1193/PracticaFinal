package org.example;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class StudentXmlParser extends DefaultHandler {

    protected String tagContent;


    //we declare both so we can use them globally
    private List<Student> students;
    private Student currentStudent;

    public StudentXmlParser() {
        this.students = new ArrayList<>();
        this.currentStudent = null;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    //This method will return the students' list
    public List<Student> getStudentList() {
        return this.students;
    }

    //We start when we read an opening tag to create the object student
    //so in order to create the student we look for the tag that contains all information
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("student")) {
            currentStudent = new Student();
        }
    }

    //Returns the content within the tags
    public void characters(char ch[], int start, int length)
            throws SAXException {
        tagContent = new String(ch, start, length);
    }

    //method that will execute when the tag is of closure
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (currentStudent != null) {

            switch (qName.toLowerCase()) {
                //we set the attributes that will have the student's information
                case "idcard":
                    currentStudent.setIdcard(tagContent);
                    break;
                case "firstname":
                    currentStudent.setFirstname(tagContent);
                    break;
                case "lastname":
                    currentStudent.setLastname(tagContent);
                    break;
                case "email":
                    currentStudent.setEmail(tagContent);
                    break;
                case "phone":
                    currentStudent.setPhone(tagContent);
                    break;
                //when we find the end tag of student, this means that we have all information of this student
                //we save the student within the object Student
                //we reset the information to add a new student
                case "student":
                    if(currentStudent.getIdcard().length()==8) {
                        students.add(currentStudent);
                        currentStudent = null;
                    }else{
                        throw new RuntimeException("The IDCARD must have 8 characteres. No Students added");
                    }
                    break;
            }
        }
    }

    List<Student> read(String file) throws RuntimeException {
        try {
            //we call the parser
            SAXParser saxParser = SAXParserFactory.
                    newInstance().newSAXParser();

            //we parse the file that we indicate in route

            saxParser.parse(file, this);
            return this.getStudents();
        }catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }catch (FileNotFoundException e){
            System.err.println("File not found");
        } catch (Exception e ) {
            e.printStackTrace();
        }
        //we return the students
        return getStudentList();
    }

}