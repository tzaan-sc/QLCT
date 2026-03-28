# Kịch Bản Quay Video Demo Ứng Dụng Quản Lý Chi Tiêu

**Thông tin chung:**
- **Thời lượng dự kiến:** 1.5 - 2 phút.
- **Mục tiêu:** Thể hiện rõ giao diện trực quan, Code UI Native mượt mà, đầy đủ các nghiệp vụ (Thêm, Xóa qua Long-click, Tùy chỉnh danh mục), và tính liên kết dữ liệu ổn định tuyệt đối (nhờ SQLite - DatabaseHelper).
- **Tỉ lệ khung hình:** Dọc (9:16) quay màn hình điện thoại hoặc Ngang (16:9) khi dùng máy ảo Android.

---

## 🎬 Chi tiết phân cảnh (Storyboard)

### Cảnh 1: Giới thiệu chung & Trang Chủ (Home) (0:00 - 0:15)
- **Hành động trên màn hình:** 
  - Mở ứng dụng. Màn hình Home xuất hiện cực kỳ nhanh (dữ liệu load từ SQLite).
  - Chỉ/Tap nhẹ vào các định dạng tiền tệ quen thuộc (ví dụ **₫50.000**) ở phần Tổng số dư (Balance), Tổng Thu (Income), Tổng Chi (Expense).
  - Ở mục "Giao dịch gần đây", cuộn nhẹ danh sách. 
- **Lời bình (Voice/Phụ đề):**
  > *"Xin chào, đây là ứng dụng Quản lý Chi tiêu. Trang chủ lập tức tính toán và hiển thị cho bạn số dư hiện tại, tổng thu chi và các giao dịch gần nhất."*

### Cảnh 2: Thêm một khoản "Chi" & Chọn Ngày Tháng (0:15 - 0:35)
- **Hành động trên màn hình:**
  - Bấm vào nút dấu cộng `(+)` ở góc dưới (FloatingActionButton) để sang màn hình **Thêm Giao Dịch**.
  - Nhập số tiền: `50000`.
  - Giữ ô loại giao dịch là **Chi** (mặc định nếu có).
  - Bấm vào biểu tượng/ô "Ngày": Hiển thị lịch (DatePickerDialog), chọn ngày hôm qua hoặc giữ ngày hiện tại, bấm OK.
  - Bấm vào Danh mục: Danh sách (Spinner) xổ xuống, chọn `Ăn & Uống`.
  - Nhập Ghi chú: `Ăn phở sáng`.
  - Bấm nút `Lưu` dưới cùng. Có Toast thông báo "Đã lưu giao dịch!".
- **Lời bình (Voice/Phụ đề):**
  > *"Giao diện thêm mới hỗ trợ bộ chọn ngày tháng tiện lợi và danh sách danh mục thả xuống mượt mà, giúp ghi chép cực kỳ nhanh chóng."*

### Cảnh 3: Kiểm tra sự cập nhật tức thì (0:35 - 0:45)
- **Hành động trên màn hình:**
  - App tự động quay lại Home. Hiển thị rõ ràng con số **Tổng Chi** và **Số dư** đã được cập nhật chuẩn xác.
  - Khoản chi "Ăn phở sáng" xuất hiện trên cùng của danh sách.
- **Lời bình (Voice/Phụ đề):**
  > *"Sau khi lưu, số liệu lập tức được Database đồng bộ lại lên màn hình chính mà không cần tải lại app."*

### Cảnh 4: Xóa giao dịch siêu nhanh (Long-Click) (0:45 - 0:55)
- **Hành động trên màn hình:**
  - Tại Trang chủ, bấm **nhấn giữ** (Long-press) vào khoản giao dịch vừa thêm (Ăn phở sáng).
  - Hệ thống hiện ra hộp thoại hỏi "Xóa giao dịch?".
  - Bấm nút `Hủy` trước (để show tính năng thôi, không cần xóa thật).
- **Lời bình (Voice/Phụ đề):**
  > *"Ngoài ra, thao tác nhấn giữ một giao dịch bất kỳ sẽ cho phép xóa nhanh khoản thu/chi nếu bạn nhập sai."*

### Cảnh 5: Thêm Danh mục mới với Icon tùy chọn (0:55 - 1:20)
- **Hành động trên màn hình:**
  - Từ Home, bấm nút chuyển vào chức năng **Quản lý danh mục**.
  - Bấm nút `(+)` ở góc màn hình.
  - Hộp thoại "Thêm Danh Mục" nổi lên.
  - Nhập Tên danh mục: `Tiền thưởng`.
  - Mở danh sách Icon: Tùy ý chọn mục `Lương` hoặc `Khác`.
  - Bấm `Lưu`. Toast hiện "Đã thêm danh mục", và "Tiền thưởng" hiện ở cuối danh sách.
- **Lời bình (Voice/Phụ đề):**
  > *"Bạn có thể tạo thêm bất kỳ danh mục mới nào, tự đặt tên và gán Icon cho nó để dễ quản lý hơn."*

### Cảnh 6: Lịch Sử & Tính gắn kết dữ liệu (1:20 - 1:45)
- **Hành động trên màn hình:**
  - Quay lại Home, bấm vào "Xem tất cả" hoặc nút đi tới **Lịch sử giao dịch**.
  - Cuộn lên xuống một danh sách dài để cho thấy hiệu năng tuyệt vời của RecyclerView.
  - Tiếp theo thử quay lại màn hình Thêm giao dịch, mở danh sách danh mục và khoanh tròn nhấn mạnh danh mục `Tiền thưởng` vừa mới tạo lúc nãy đã có sẵn trong danh sách chọn.
  - Về lại Home, kết thúc quay.
- **Lời bình (Voice/Phụ đề):**
  > *"Toàn bộ danh sách lịch sử được tối ưu vô cùng mượt mà. Bên cạnh đó, dữ liệu được truyền tải nhất quán trên toàn hệ thống với SQLite cục bộ. Cảm ơn các bạn đã theo dõi!"*

---

## 💡 Tips để Video Demo ăn điểm tối đa:
1. **Làm sạch màn hình thiết bị:** Bật chế độ Demo Mode trên thiết bị Android (System UI Tuner) để ẩn các thông báo thừa, pin ở mức 100% giúp giao diện clean.
2. **Khoe được Database SQLite:** Vì dữ liệu app bạn hiện tại đã lưu bằng `DatabaseHelper`, hãy nhấn mạn vào việc dữ liệu tồn tại vĩnh viễn (kể cả khi vừa vào app, data đã hiển thị luôn).
3. **Thao tác chậm rãi:** Các thao tác như chọn Ngày (DatePickerDialog) hay mở hộp thoại thêm danh mục nên làm từ từ, để khoảng nghỉ 0.5s giữa các click để người xem dễ bắt nhịp.
