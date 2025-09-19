/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oneLastTime;

/**
 *
 * @author pc
 */
public class Patient {
    
    
    private int id;
    private String name;
    private String phone;
    private String email;

    public Patient(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }
     public Patient(int id, String name) {
        this.id = id;
        this.name = name;
        
    }

    
    public int getId(){
        return id; 
    }
    
    public void setId(int id) {
        this.id = id; 
    }

    public String getName() {
        return name; 
    }
    
    public void setName(String name) { 
        this.name = name; 
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
    public String toString() {
    return this.name; // افترض أن اسم المتغير للاسم هو 'name'
}
}


