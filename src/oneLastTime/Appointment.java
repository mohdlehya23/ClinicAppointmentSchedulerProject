/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oneLastTime;
import java.time.LocalDate;

/**
 *
 * @author NITRO5
 */


/**
 * يمثل هذا الكلاس (Model) بيانات الموعد في النظام.
 * يحتوي على معرف الموعد، معرف المريض، معرف الطبيب، التاريخ والوقت.
 */
public class Appointment {

    private int id;
    private int patientId;
    private int doctorId;
    private LocalDate date; // استخدام LocalDate هو الأفضل للتعامل مع التواريخ
    private String time;    // يتم تخزينه كنص بصيغة HH:mm

    /**
     * مُنشئ فارغ (Default constructor).
     */
    public Appointment() {
    }

    /**
     * مُنشئ لإنشاء كائن موعد جديد مع كل البيانات.
     * @param id رقم المعرف الفريد للموعد.
     * @param patientId رقم معرف المريض المرتبط بالموعد.
     * @param doctorId رقم معرف الطبيب المرتبط بالموعد.
     * @param date تاريخ الموعد.
     * @param time وقت الموعد.
     */
    public Appointment(int id, int patientId, int doctorId, LocalDate date, String time) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
    }

    // --- Getters and Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
