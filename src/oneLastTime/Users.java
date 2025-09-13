/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oneLastTime;

import java.io.FileWriter;
import java.io.IOException;

public class Users {  // Linked list
    private UserNode head;

    // Node class
    public static class UserNode {
        private String id;
        private String firstname;
        private String lastname;
        private String email;
        private String passwordhash;
        UserNode next;

        // constructor to append data to a file
        public UserNode(String firstname, String lastname, String email, String passwordhash) throws IOException {
            this.firstname = firstname;
            this.lastname = lastname;
            this.email = email;
            this.passwordhash = passwordhash;

            FileWriter file = new FileWriter("./src/oneLastTime/users.txt", true);
            file.append(getFirstname() + " " + getLastname() + " " + getEmail() + " " + getPasswordhash() + "\n");
            file.close();
        }
        
        // Another constructor to store  the user first name to pass it later among classes
        public UserNode(String firstname) throws IOException {
            this.firstname = firstname;

        }

        // getters & setters...
        public String getFirstname() { return firstname; }
        public String getLastname() { return lastname; }
        public String getEmail() { return email; }
        public String getPasswordhash() { return passwordhash; }
    }

    // Linked list methods
    public void add(UserNode newUser) {
        if (isEmpty()) {
            head = newUser;
        } else {
            UserNode curr = head;
            while (curr.next != null) {
                curr = curr.next;
            }
            curr.next = newUser;
        }
    }

    public boolean isEmpty() {
        return head == null;
    }
}