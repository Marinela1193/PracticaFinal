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
    /* DefaultHandler maneja la gran mayoría de eventos */
    protected String tagContent;


    //Añadimos Las clases a este nivel para ser usadas a nivel global en la clase
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

    //Este metodo devolverá la lista de Students
    public List<Student> getStudentList() {
        return this.students;
    }

    //Iniciamos evento cuando se lee una etiqueta de apertura para crear un objeto de tipo Student.
    // En este caso usamos la etiqueta <student>
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("student")) {
            currentStudent = new Student();
        }
    }

    //Devuelve el contenido/valor de la etiqueta. <etiqueta>contenido</etiqueta>
    //
    public void characters(char ch[], int start, int length)
            throws SAXException {
        tagContent = new String(ch, start, length);
    }

    //metodo para ejecutarse cuando una etiqueta se cierre
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (currentStudent != null) {
            //Hasta que el objeto Student no se haya creado, no se deben disparar estos eventos de etiquetas
            switch (qName.toLowerCase()) {
                //Para las etiqueta de dentro de student setteamos su atributo
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
                //Para el caso Student, al ser una etiqueta de cierre (por tanto se han setteado todos los atributos de esa registro
                //Añadimos el elemento a la Lista de Students y reiniciamos el objeto Student.
                case "student":
                    System.out.println(currentStudent);
                    students.add(currentStudent);
                    currentStudent = null;
                    break;
            }
        }
    }

    //metodo que llamamos desde un nivel superior (main para este ejemplo) para leer el fichero y rellenar la Lista de Students
    List<Student> leer(String fichero) {
        try {
            //Invocamos el parser
            SAXParser saxParser = SAXParserFactory.
                    newInstance().newSAXParser();

            //Parseamos el fichero que le pasamos por ruta

            saxParser.parse(fichero, this);
            return this.getStudents();
        }catch (FileNotFoundException e){
            System.err.println("Fichero no encontrado");
        } catch (Exception e ) {
            e.printStackTrace();
        }
        //Devolvemos los estudiantes
        return getStudentList();
    }

}