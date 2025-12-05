package org.example;

import org.hibernate.Session;



public class App {

    Session session = SessionFactory.getSessionFactory().openSession();

    public static void main(String[] args) {

        Menu menu = new Menu(args);
        menu.start();

    }
}
