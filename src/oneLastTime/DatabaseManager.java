/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oneLastTime;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import oneLastTime.Patient;


/**
 *
 * @author NITRO5
 */

    
/**
 * هذا الكلاس مسؤول عن إدارة الاتصال بقاعدة بيانات MySQL.
 * يستخدم نمط Singleton لضمان وجود اتصال واحد فقط.
 */
public class DatabaseManager {
    // --- تفاصيل الاتصال بقاعدة البيانات ---
    // !! قم بتغيير هذه القيم لتطابق إعدادات قاعدة البيانات الخاصة بك !!
    private static final String URL = "jdbc:mysql://localhost:3306/clinic_db"; // استبدل clinic_db باسم قاعدة بياناتك
    private static final String USER = "root"; // اسم المستخدم الخاص بك
    private static final String PASSWORD = ""; // كلمة المرور الخاصة بك

    private  static Connection connection = null;

    // لمنع إنشاء نسخ من هذا الكلاس
    private DatabaseManager() {}

    /**
     * يقوم بإنشاء وإرجاع اتصال بقاعدة البيانات.
     * إذا كان هناك اتصال موجود بالفعل، فإنه يعيده.
     * @return كائن Connection للاتصال بقاعدة البيانات.
     * @throws SQLException في حالة حدوث خطأ في الوصول إلى قاعدة البيانات.
     */
    public  static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // تحميل مشغل JDBC
                Class.forName("com.mysql.cj.jdbc.Driver");
                // إنشاء الاتصال
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found!");
                e.printStackTrace();
                throw new SQLException("JDBC Driver not found", e);
            }
        }
        return connection;
    }
}
