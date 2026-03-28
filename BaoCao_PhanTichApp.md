# PHÂN TÍCH ỨNG DỤNG QUẢN LÝ CHI TIÊU

Đây là tài liệu phân tích chi tiết bản cập nhật mới nhất của ứng dụng Quản lý chi tiêu, bao gồm các chức năng cốt lõi, nâng cấp về cơ sở dữ liệu (SQLite) và đồ thị trực quan, giúp sinh viên tự tin trả lời các câu hỏi phản biện từ giáo viên.

---

## I. TỔNG QUAN VỀ ỨNG DỤNG
Ứng dụng "Quản lý chi tiêu" là một ứng dụng Android Native được viết bằng **Java**, với giao diện được thiết kế bằng **XML**. Ở phiên bản cập nhật này, ứng dụng đã được nâng cấp đáng kể về mặt lưu trữ (tích hợp SQLite) và trực quan hóa dữ liệu (thêm thư viện biểu đồ).

**Các công nghệ sử dụng chính:**
- **Ngôn ngữ:** Java (Android SDK)
- **Giao diện:** XML, Material Design (BottomNavigation, CardView, Dialog, DatePicker)
- **Cơ sở dữ liệu:** SQLite thông qua `SQLiteOpenHelper`.
- **Thư viện bên thứ ba:** MPAndroidChart (Vẽ biểu đồ hình tròn & cột).
- **Kiến trúc:** Mô hình MVC cơ bản với `DataManager` đóng vai trò là kho dữ liệu trung gian kết nối giữa UI và CSDL SQLite.

---

## II. CÁC CHỨC NĂNG CHÍNH (FEATURES)

### 1. Màn hình Tổng Quan (Overview) - `OverviewFragment.java`
- Hiển thị Thống kê số dư màn hình chủ: (Balance = T.Thu - T.Chi).
- **Biểu đồ tròn (Pie Chart):** Phân bổ chi tiêu theo từng danh mục (Tính bằng %).
- **Biểu đồ cột (Bar Chart):** Thống kê dòng tiền chi tiêu liên tục trong 7 ngày gần nhất.
- **Tính năng Insights (Phân tích thông minh):** Hệ thống tự động phân tích dữ liệu và đưa ra 4 tóm tắt:
  - 🏆 Danh mục tốn kém nhất.
  - ⚠️ Phát hiện và cảnh báo nếu chi tiêu quátay so với trung bình 7 ngày trước đó.
  - 🔥 Tìm ra ngày chi nhiều tiền nhất trong tháng.
  - 📈/📉 So sánh đánh giá xu hướng tiêu dùng tuần này vs tuần trước.

### 2. Trang chủ & Điều hướng (Home) - `HomeActivity.java`
- Sử dụng `BottomNavigationView` kết hợp với `FrameLayout` để chuyển đổi mượt mà giữa màn hình "Tổng Quan" và màn hình "Nhập liệu / Lịch sử cũ" giống hệt các ứng dụng thực tế. 

### 3. Màn hình Thêm Giao Dịch - `AddTransactionActivity.java`
- Thêm khoản Thu/Chi với đầy đủ thông tin: Số tiền, Danh mục, Ghi chú, Ngày tháng cụ thể do người dùng tùy chọn.

### 4. Lịch Sử Giao Dịch - `TransactionHistoryActivity.java`
- Hiển thị toàn bộ dữ liệu qua `RecyclerView`.
- **Bộ lọc mạnh mẽ kết hợp:**
  - Lọc theo Nhóm (Thu/Chi/Tất cả).
  - Lọc theo Danh mục.
  - **Lọc theo khoảng ngày (Date Picker):** Bổ sung công cụ chọn lịch thông minh hệ điều hành để truy ra giao dịch chính xác nằm trong ngưỡng "Từ ngày" và "Đến ngày".

### 5. Quản Lý Danh Mục - `CategoryActivity.java`
- Thêm mới, xoá các danh mục thu/chi riêng biệt hiển thị thông qua một Dialog tùy chỉnh.

---

## III. PHÂN TÍCH CẤU TRÚC CODE (DÀNH CHO GIÁO VIÊN HỎI)

### 1. Phần Models (Lớp thực thể)
- **`Transaction.java`** & **`Category.java`**: Chứa các trường thuộc tính dữ liệu (POJO). Các lớp này giúp đóng gói nguyên khối thông tin giao dịch để dễ dàng map với các cột trong CSDL.

### 2. Phần Data (Quản lý dữ liệu với SQLite)
- **`DatabaseHelper.java`**: 
  - Kế thừa từ `SQLiteOpenHelper`. Đảm nhiệm toàn bộ vai trò kết nối CSDL, tạo bảng (`CREATE TABLE transactions`, `categories`) và các câu lệnh Query CRUD cơ bản (Insert, Update, Delete, Select).
- **`DataManager.java` (Lớp Wrapper theo mẫu Singleton)**: 
  - **Giáo viên thường hỏi:** "Tại sao em không gọi thẳng DatabaseHelper ở Activity mà lại gọi qua DataManager?"
  - **Trả lời:** Chức năng `DataManager` đóng vai trò như một lớp **Repository** bọc lại kết nối Database. Việc áp dụng kĩ thuật **Singleton** đảm bảo toàn bộ dự án xuyên suốt chỉ sử dụng chung *một điểm truy xuất duy nhất*. Điều này giúp code ở UI (Activity/Fragment) trở nên ngắn gọn, tách biệt nghiệp vụ xử lý logic ra khỏi giao diện, đồng thời rất dễ duy trì bảo mật. Lớp này hỗ trợ phân tích vòng lặp tìm `getTotalIncome()` hay lọc `expenses`.

### 3. Phần Giao Diện & Vẽ Biểu Đồ
- **Biểu đồ MPAndroidChart:** Trong `OverviewFragment.java`, để gom nhóm tổng tiền các hóa đơn thuộc cùng nhóm trước khi vẽ hình tròn, em đã dùng hàm lặp `Map<String, Double>` để tìm kiếm và cộng giá trị cũ. Số liệu gom nhóm sẽ nạp thẳng vào đối tượng `PieEntry` hoặc `BarEntry` để render ra đồ thị.
- **Tối ưu hiển thị màu:** Em cung cấp mảng `colors` là các mã HEX hệ màu **Pastel** (nhẹ nhàng, không gắt) (`#FFADAD`, `#FFD6A5`...) nạp vào `PieDataSet`.

---

## IV. BỘ CÂU HỎI TRỌNG TÂM DÀNH CHO BẢO VỆ ĐỒ ÁN

**Câu 1: Thông tin của app em làm có bị mất khi chạy lại phần mềm không? Lưu trữ rốt cuộc để ở đâu?**
*Trả lời:* Thưa thầy/cô, dữ liệu của ứng dụng sẽ được lưu trữ cục bộ lâu dài (Persisted locally). App tích hợp cơ sở dữ liệu hệ thống **SQLite** (cấu hình trong file `DatabaseHelper`), do đó ngay cả khi người dùng tắt app trên đa nhiệm hay khởi động máy lại thì dữ liệu vẫn còn nguyên vẹn, đảm bảo yêu cầu thực tế của một app Chi Tiêu.

**Câu 2: Làm sao em tìm và hiển thị được danh sách giao dịch qua các khoảng thời gian "Từ ngày - Đến ngày"?**
*Trả lời:* Trong màn `TransactionHistoryActivity`, em sử dụng lệnh kích hoạt `DatePickerDialog` để người dùng chọn thông số 2 ngày. Có được mốc thời gian này, thay vì query Database bằng SQL thô dễ dẫn tới nhầm lẫn định dạng chữ, em kéo toàn lịch sử ra list và áp dụng thư viện thời gian `java.util.Date` kết hợp `Calendar` của Android. Bằng hàm Date so sánh chuẩn là `.before()` và `.after()`, danh sách sẽ tự động giữ lại chi tiêu thoả mãn bộ đếm lọc rồi mới cho Adapter cập nhật.

**Câu 3: Tính năng cảnh báo Insights (Chi tiêu hôm nay rủi ro) thuật toán viết thế nào?**
*Trả lời:* Trong `OverviewFragment`, em lấy đồng hồ hệ thống chạy làm mốc bằng `Calendar.getInstance()`. Sau đó duyệt 1 vòng for trên danh sách giao dịch Chi Tiêu và gom số tiền lại vào 2 giỏ: "Chi hôm nay" (`todayExpense`) và "Chi 7 ngày trước" (`last7DaysExpense`).
Sử dụng công thức chia tỷ lệ trung bình, nếu số tiền tiêu hôm nay lớn hơn trung bình một ngày tiêu của 1 tuần qua, code sẽ đổi màu cảnh báo chữ sang đỏ bằng hàm `setTextColor(Color.parseColor("#E53935"))` nhằm bắt mắt người dùng.

**Câu 4: Làm sao cái biểu đồ của em khi nhiều chữ quá nó không bị dính nét, trềnh ềnh chèn lên nhau?**
*Trả lời:* Thông thường biểu đồ tròn MPAndroidChart mặc định sẽ ghi chữ % và tên Nhóm đè lên từng lát cắt của bánh. Khi tên Nhóm (ví dụ "Chi Phí Giải Trí Cuối Tuần") quá dài, nó sẽ làm bể giao diện. Để xử lý, em đã thêm hàm  `pieChart.setDrawEntryLabels(false);` để giấu phần Label trên hình bánh đi. Trả lại không gian trống hiển thị duy nhất thông số %, còn tên nhóm sẽ được phần mềm tự đẩy xuống bảng đánh dấu dưới đáy (Legend box). Cực kỳ tinh tế.

---
*Chúc bạn bảo vệ đồ án và thuyết trình thành công!*
