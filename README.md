# QLCT - Quản lý chi tiêu cá nhân

Ứng dụng quản lý chi tiêu cá nhân đa nền tảng, giúp bạn theo dõi thu nhập, chi tiêu và quản lý ngân sách một cách hiệu quả.

## 🚀 Tính năng nổi bật

- **Quản lý thu chi**: Dễ dàng ghi lại các khoản thu và chi
- **Quản lý danh mục**: Tùy chỉnh danh mục chi tiêu theo nhu cầu
- **Báo cáo trực quan**: Biểu đồ và thống kê chi tiết
- **Đa nền tảng**: Chạy trên Windows, macOS, Linux
- **Giao diện hiện đại**: Thiết kế tối giản, dễ sử dụng

## 🛠️ Công nghệ sử dụng

- **Electron**: Framework cho ứng dụng desktop
- **React**: Thư viện giao diện người dùng
- **Vite**: Build tool tốc độ cao
- **Tailwind CSS**: Framework CSS tiện ích
- **Lucide React**: Icon library

## 📦 Cài đặt và chạy

### Yêu cầu

- Node.js >= 18.x
- npm hoặc yarn

### Cài đặt

1. Clone repository:
   ```bash
   git clone <repository-url>
   cd QLCT
   ```

2. Cài đặt dependencies:
   ```bash
   npm install
   ```

3. Chạy ứng dụng:
   ```bash
   npm run dev
   ```

### Build ứng dụng

```bash
npm run build
```

## 📂 Cấu trúc dự án

```
QLCT/
├── src/
│   ├── components/      # Các component React
│   ├── pages/           # Các trang ứng dụng
│   ├── services/        # Các service và API
│   ├── utils/           # Các hàm tiện ích
│   ├── App.tsx          # Component chính
│   └── main.tsx         # Điểm vào ứng dụng
├── electron/            # Cấu hình Electron
├── public/              # Các file tĩnh
├── package.json         # Dependencies và scripts
└── vite.config.ts       # Cấu hình Vite
```

## 📝 Ghi chú phát triển

- Ứng dụng sử dụng Electron để đóng gói ứng dụng desktop
- React được sử dụng cho giao diện người dùng
- Tailwind CSS cho styling nhanh chóng
- Dữ liệu được lưu trữ cục bộ trong file JSON

## 🤝 Đóng góp

Chào mừng bạn đóng góp cho dự án! Vui lòng:

1. Fork repository
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit thay đổi (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

## 📄 License

Dự án này được phát hành dưới giấy phép MIT - xem file [LICENSE](LICENSE) để biết thêm chi tiết.

## 👨‍💻 Tác giả

- [Thu Van] - [tzann-sc]
