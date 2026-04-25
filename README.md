# HƯỚNG DẪN CÀI ĐẶT VÀ SỬ DỤNG HỆ THỐNG MÔ PHỎNG HÀNH TINH 3D

Dự án: Mô phỏng hành tinh 3D và định tuyến vệ tinh.
Nghiệp vụ: Chạy trên môi trường Java 8, kết nối Microsoft SQL Server.

## 1. Yêu cầu hệ thống
*   **JDK:** Java Development Kit 8 (JRE 1.8).
*   **Database:** Microsoft SQL Server (2014 trở lên).
*   **Thư viện:** Microsoft JDBC Driver for SQL Server (phiên bản jre8).

## 2. Cấu hình Cơ sở dữ liệu (SSMS)
1.  Mở SQL Server Management Studio (SSMS).
2.  Mở và thực thi toàn bộ script (`sql/init_database.sql`) để tạo database `PlanetSim` và dữ liệu mẫu.
3.  Đảm bảo tài khoản `sa` đã được kích hoạt và có mật khẩu là `123456`.
4.  Kích hoạt giao thức **TCP/IP** trong SQL Server Configuration Manager (Port 1433).

## 3. Cài đặt Driver JDBC
1.  Tải file `mssql-jdbc-12.8.0.jre8.jar` từ trang chủ Microsoft.
2.  Đặt file `.jar` vào thư mục `lib/` của dự án.

## 4. Cách chạy ứng dụng
*   Cách 1: Chạy file `run.bat` (Dành cho người dùng Windows). File này sẽ tự động biên dịch và khởi chạy ứng dụng.
*   Cách 2: Sử dụng dòng lệnh:
    ```bash
    # Biên dịch
    javac -d bin -cp "lib/*;src" src/com/planetsim/MainApp.java
    # Chạy
    java -cp "bin;lib/*" com.planetsim.MainApp
    ```

## 5. Tính năng chính
*   **Mô phỏng 3D:** Hiển thị 8 hành tinh trong hệ mặt trời với texture tương ứng. Sử dụng chuột để xoay và cuộn chuột để zoom.
*   **Vật lý quỹ đạo:** Tự động tính toán vận tốc dựa trên độ cao v = sqrt(G*M/r).
*   **Định tuyến Dijkstra:** Tìm đường truyền tin ngắn nhất giữa các node vệ tinh dựa trên khoảng cách Euclidean và tầm phát sóng.
*   **Quản lý dữ liệu:** Đồng bộ hóa danh sách vệ tinh và lịch sử định tuyến vào SQL Server.

## 6. Lưu ý kỹ thuật
*   Môi trường JavaFX 8 mặc định được tích hợp sẵn trong Oracle JDK 8. Nếu sử dụng OpenJDK 8, cần cài đặt thêm OpenJFX 8 tương ứng.
*   Kết nối database sử dụng tham số `encrypt=false` để tương thích với các môi trường không có chứng chỉ SSL.