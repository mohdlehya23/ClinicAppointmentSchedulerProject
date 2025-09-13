/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oneLastTime;

/**
 *
 * @author NITRO5
 */



/**
 * يمثل هذا الكلاس (Model) بيانات الطبيب في النظام.
 * يحتوي على المعرف (id)، الاسم (name)، والتخصص (specialty).
 */
public class Doctor {

    private int id;
    private String name;
    private String specialty;

    /**
     * مُنشئ فارغ (Default constructor).
     * ضروري لبعض مكتبات JavaFX.
     */
    public Doctor() {
    }

    /**
     * مُنشئ لإنشاء كائن طبيب جديد مع كل البيانات.
     * @param id رقم المعرف الفريد للطبيب.
     * @param name اسم الطبيب الكامل.
     * @param specialty تخصص الطبيب.
     */
    public Doctor(int id, String name, String specialty) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
    }

    // --- Getters and Setters ---
    // هذه الدوال تسمح بالوصول إلى المتغيرات الخاصة وتعديلها.

    public int getId() {
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

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    /**
     * يتم استخدام هذه الدالة لعرض اسم الطبيب في واجهة المستخدم،
     * خصوصاً في عناصر مثل القوائم المنسدلة (ComboBox).
     * @return اسم الطبيب.
     */
    @Override
    public String toString() {
        return this.name;
    }
}
