/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


package oneLastTime;

/**
 * يمثل هذا الكلاس (Model) بيانات المستخدم في النظام.
 * تم تبسيطه ليكون مجرد حاوية بيانات (POJO).
 */
public class Users {
    private int id;
    private String firstname;
    private String lastname;
    private String email;

    // مُنشئ يستخدم عند جلب بيانات المستخدم بعد تسجيل الدخول
    public Users(int id, String firstname, String lastname, String email) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
    }
    
    // Getters
    public int getId() { return id; }
    public String getFirstname() { return firstname; }
    public String getLastname() { return lastname; }
    public String getEmail() { return email; }
}