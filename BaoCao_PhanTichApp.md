# PHÂN TÍCH ỨNG DỤNG QUẢN LÝ CHI TIÊU

Đây là tài liệu phân tích chi tiết về ứng dụng Quản lý chi tiêu, bao gồm các chức năng cốt lõi và cấu trúc mã nguồn (code) để giúp sinh viên dễ dàng trả lời các câu hỏi phản biện từ giáo viên.

---

## I. TỔNG QUAN VỀ ỨNG DỤNG
Ứng dụng "Quản lý chi tiêu" là một ứng dụng Android Native được viết bằng ngôn ngữ **Java**, với giao diện được thiết kế bằng **XML**. Ứng dụng giúp người dùng ghi chép, theo dõi các khoản thu/chi cá nhân, phân loại theo danh mục và xem tổng quan số dư.

**Các công nghệ sử dụng chính:**
- **Ngôn ngữ:** Java (Android SDK)
- **Giao diện:** XML, Material Design (RecyclerView, CardView, Dialog)
- **Kiến trúc:** Mô hình cơ bản với Singleton Pattern để quản lý dữ liệu toàn cục.
- **Lưu trữ dữ liệu:** Lưu trữ trong bộ nhớ tạm (In-memory DataStore qua `DataManager`). *Lưu ý: Ứng dụng hiện tại chưa sử dụng SQLite hay Room Database, dữ liệu sẽ reset khi tắt app, điều này được làm để tối giản ứng dụng cho mục đích học tập/đồ án.*

---

## II. CÁC CHỨC NĂNG CHÍNH (FEATURES)

### 1. Màn hình Trang Chủ (Home) - `HomeActivity.java`
- Thống kê tổng số dư hiện tại (Balance = Tổng thu - Tổng chi).
- Hiển thị tổng số tiền đã Thu (Income) và tổng số tiền đã Chi (Expense) trong khoảng thời gian.
- Hiển thị danh sách các giao dịch gần đây nhất.

### 2. Màn hình Thêm Giao Dịch (Add Transaction) - `AddTransactionActivity.java`
- Cho phép người dùng nhập khoản thu hoặc khoản chi mới.
- Các trường nhập liệu: 
  - Số tiền (Amount)
  - Loại giao dịch (Thu / Chi)
  - Danh mục (Ví dụ: Ăn uống, Mua sắm, Lương...)
  - Ghi chú (Note)
  - Ngày tháng (Thời gian giao dịch)

### 3. Màn hình Lịch Sử Giao Dịch (Transaction History) - `TransactionHistoryActivity.java`
- Hiển thị toàn bộ danh sách các giao dịch đã nhập.
- Sử dụng `RecyclerView` cùng `TransactionAdapter` để render danh sách mượt mà.

### 4. Màn hình Quản Lý Danh Mục (Category Management) - `CategoryActivity.java`
- Cho phép xem danh sách các danh mục thu/chi (Ăn uống, Giải trí, Lương...).
- Thêm danh mục mới qua Dialog (`dialog_add_category.xml`).
- Xóa / Cập nhật danh mục hiện tại.

---

## III. PHÂN TÍCH CẤU TRÚC CODE (DÀNH CHO GIÁO VIÊN HỎI)

Giáo viên có thể hỏi về cách tổ chức code, các class làm nhiệm vụ gì. Dưới đây là phân tích chi tiết từng phần.

### 1. Phần Models (Lớp thực thể dữ liệu)
- **`Transaction.java`**: Lớp đại diện cho một giao dịch. Chứa các thuộc tính: mã ID, số tiền (`amount`), loại (`type` - Thu/Chi), danh mục (`category`), ghi chú, ngày tháng thời gian, và icon. Hàm khởi tạo (constructor) và các Getter/Setter được định nghĩa rõ ràng.
- **`Category.java`**: Lớp đại diện cho một danh mục. Gồm các thuộc tính: ID, tên danh mục (`name`), icon Resource (`iconRes`), và màu sắc (`color`).

### 2. Phần Data (Quản lý dữ liệu)
- **`DataManager.java`**: 
  - **Câu hỏi giáo viên thường hỏi:** "Dữ liệu được lưu ở đâu? Làm sao các màn hình chia sẻ được dữ liệu với nhau?"
  - **Trả lời:** Em sử dụng Design Pattern tên là **Singleton Pattern**. Class `DataManager` chứa một instance tĩnh (`private static DataManager instance`). Dữ liệu được lưu vào `ArrayList<Transaction>` và `ArrayList<Category>`. Vì nó là Singleton nên mọi màn hình (`Activity`) khi gọi `DataManager.getInstance()` đều trỏ về cùng một vùng nhớ, từ đó chia sẻ chung dữ liệu.
  - Class này chứa các hàm CRUD (Create, Read, Update, Delete) cho Giao dịch và Danh mục, đồng thời có các hàm tính toán `getTotalIncome()`, `getTotalExpense()`, `getBalance()`.
  - Có các hàm `seedCategories()` và `seedTransactions()` để tự động tạo một số dữ liệu giả định ban đầu khi mới mở app.

### 3. Phần Giao Diện & Adapter (Hiển thị danh sách)
- **`TransactionAdapter.java` & `CategoryAdapter.java`**: 
  - **Câu hỏi giáo viên thường hỏi:** "Làm sao danh sách hiển thị lên được màn hình cuộn mượt mà?"
  - **Trả lời:** Em sử dụng `RecyclerView` kết hợp với `Adapter`. Trong Adapter có một lớp con là `ViewHolder` (kế thừa từ `RecyclerView.ViewHolder`) giúp tái sử dụng (recycle) các view item (như thẻ giao dịch) khi cuộn màn hình, tránh việc hao tốn bộ nhớ RAM để tạo lại giao diện liên tục. `onCreateViewHolder` dùng để nạp layout từ XML, còn `onBindViewHolder` dùng để gắn dữ liệu vào các Text/Image.

### 4. Phần Điều Khiển (Activities)
- **`HomeActivity.java`**: Gọi `DataManager.getInstance()` để lấy dữ liệu tính toán tổng thu, tổng chi và gán vào các `TextView`. Khởi tạo `RecyclerView` để nạp danh sách giao dịch gần đây.
- **`AddTransactionActivity.java`**: Bắt sự kiện click nút Lưu (Save). Lấy text từ các trường `EditText`, sau đó gọi `DataManager.getInstance().addNewTransaction(...)` để lưu vào bộ nhớ. Sau đó gọi `finish()` để quay lại màn hình trước.
- **`CategoryActivity.java`**: Sử dụng `AlertDialog` kết hợp với custom layout (`dialog_add_category.xml`) để hiện cửa sổ popup cho người dùng nhập tên danh mục.

---

## IV. BỘ CÂU HỎI TRỌNG TÂM CỦA GIÁO VIÊN & CÁCH TRẢ LỜI

**Câu 1: Dự án này có kết nối với cơ sở dữ liệu trên Cloud (Firebase/API) hay SQLite không?**
*Trả lời:* Hiện tại để đảm bảo tính gọn nhẹ và tối ưu vào việc xây dựng logic UI, ứng dụng sử dụng cơ chế lưu trữ nội bộ tạm thời thông qua `DataManager` (ArrayList in memory). Ở giai đoạn mở rộng tiếp theo, em sẽ tích hợp Room Database của Android Jetpack (hoặc SQLiteHelper) để dữ liệu lưu lại thực sự (Persist local data).

**Câu 2: Làm thế nào ứng dụng tính toán tổng thu và tổng chi?**
*Trả lời:* Trong `DataManager.java`, có phương thức `getTotalIncome()` và `getTotalExpense()`. Nó chạy một vòng lặp (for-each) đi qua tất cả phần tử của danh sách `transactions` (Lịch sử giao dịch), kiểm tra biến `type` xem là "Thu" hay "Chi" để cộng dồn giá trị `amount`.

**Câu 3: Làm sao các danh mục ở màn hình "Thêm Giao Dịch" lấy được dữ liệu động từ màn hình "Quản lý danh mục"?**
*Trả lời:* Màn hình thêm giao dịch có một Spinner (phễu dropdown) hoặc RecyclerView. Khi mở màn hình đó, app sẽ truy vấn mảng `categories` từ `DataManager.getInstance().getCategories()`. Khi có danh mục mới được thêm ở màn hình kia, vào lại màn hình Thêm giao dịch danh sách đó sẽ được cập nhật do chúng lấy cùng từ bộ nhớ Singleton.

---
*Chúc bạn bảo vệ đồ án/bài tập thành công!*
