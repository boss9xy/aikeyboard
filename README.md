# AI Keyboard - Bàn phím đính kèm ai
Dùng API cá nhân, tuỳ chỉnh, tự túc, dùng bao nhiêu sài bấy nhiêu.
## Tiếng Việt

### Tổng quan
AI Keyboard là ứng dụng bàn phím Android thông minh tích hợp trí tuệ nhân tạo để nâng cao trải nghiệm gõ phím. Ứng dụng có các tính năng xử lý văn bản tiên tiến, mã nguồn đang sử dụng nhiều ngôn ngữ nhưng bản kết xuất apk demo đã loại bỏ chỉ có tiếng việt, chủ yếu giữ lại một ngôn ngữ duy nhất để mở rộng tính năng nhanh chóng, không cần phải biên dịch cho nhiều ngôn ngữ phức tạp, tiết kiệm thời gian đáng kể.

### Tính năng chính

#### 🤖 Công cụ AI
-**Giọng nói thành văn bản**: Dùng api whisper gpt, ghi âm và gửi ghi âm vào api, trả về văn bản. Dùng trực tiếp trên nút Giọng nói- Văn Bản có trên bàn phím, bằng cách bấm nút đó, ghi âm xong thì bấm dừng ghi âm, sau khi dừng nó sẽ gửi vào api để chuyển đổi ra văn bản và trả về in lên màn hình hoặc có công cụ trong cài đặt, tuốt xuống dưới cùng, chọn tệp để chuyển đổi.
- **Nút mic đầu tiên** là dùng chuyển lời nói thành văn bản, in trực tiếp, đó là tính năng chuyển lời nói thành văn bản của google.
- **Nút mic thứ 2** là tương tự mic 1 nhưng khi nói xong đợi một lát nó sẽ gửi văn bản đó vào api gpt và trả về kết quả, ví dụ bạn muốn tính nhanh 223x233 bằng bao nhiêu, nó sẽ in kết quả liền.
- **Các nút dịch thuật** chúng sử dụng văn bản đã sao chép để làm nguồn và lấy ngôn ngữ đầu ra từ danh sách, có trên thanh công cụ, vị trí bên phải nút dán, mặc định đang để là dịch ra việt nam, bấm vào đó và chọn ngôn ngữ đầu ra.
- **Các nút dịch thuật hay nút liên quan đến gpt (trừ nút mic thứ 2)** chúng đều lấy model đầu vào tại danh sách model
- Có một hộp chứa các văn bản đã sao chép, bấm vào dòng nào dòng đó sẽ in ra màn hình
- Trong cài đặt có tuỳ chỉnh prompt cho mỗi nút, tuỳ chỉnh cần bạt nút bên cạnh và bấm lưu để xác nhận dùng prompt tuỳ chỉnh.
- **Kiểm tra chính tả**: Kiểm tra chính tả thông minh với các sửa lỗi được hỗ trợ bởi AI
- **Dịch thuật**: Dịch thuật thời gian thực giữa nhiều ngôn ngữ
- **Chuyển giọng nói thành văn bản**: Nhận dạng giọng nói tiên tiến để gõ không cần tay
- **Hỏi Đáp nhanh**: Hỏi đáp qua api gpt, Deepseek, Olama, rag trợ lý gpt,


#### 🌍 Hỗ trợ ngôn ngữ
- **Ngôn ngữ gõ**: Hỗ trợ gõ Telex tiếng Việt và tiếng Anh
- **Ngôn ngữ hiển thị**: Giao diện ngôn ngữ tiếng việt
- **Ngôn ngữ dịch thuật**: Hỗ trợ 13+ ngôn ngữ bao gồm Trung Quốc, Nhật Bản, Hàn Quốc, Pháp, Đức, Tây Ban Nha, Ý, Nga, Ả Rập, Thái Lan và Hindi

#### 🎯 Tính năng thông minh
- **Chuyển đổi ngôn ngữ kép**: Các nút riêng biệt để chuyển đổi ngôn ngữ gõ (Tiếng Việt/Tiếng Anh) và chuyển đổi ngôn ngữ hiển thị (tất cả ngôn ngữ được hỗ trợ)
- **Xử lý theo ngữ cảnh**: Xử lý văn bản thông minh thích ứng với mô hình đầu vào của người dùng
- **Tối ưu hiệu suất**: Xử lý văn bản tiếng Việt được tối ưu hóa để có trải nghiệm gõ mượt mà

#### 🛠️ Tính năng kỹ thuật
- **Phương pháp gõ Telex**: Phương pháp gõ tiếng Việt tiên tiến với vị trí dấu được tối ưu hóa
- **Kiến trúc mô-đun**: Tách biệt rõ ràng logic ngôn ngữ gõ và hiển thị
- **Giao diện tùy chỉnh**: UI hiện đại với nút chuyển đổi smartbar và lựa chọn ngôn ngữ
- **Sẵn sàng đa nền tảng**: Được xây dựng cho Android với kiến trúc có thể mở rộng

### Mô tả công cụ AI

#### Gợi ý thông minh
AI phân tích ngữ cảnh trò chuyện và tạo ra 3 gợi ý phản hồi độc đáo, kết hợp hài hước với mô hình ngôn ngữ thế hệ Z. Mỗi gợi ý được điều chỉnh để phù hợp với giọng điệu và ngữ cảnh của cuộc trò chuyện.

#### Kiểm tra chính tả
Kiểm tra chính tả tiên tiến vượt ra ngoài các sửa lỗi đơn giản, sử dụng AI để hiểu ngữ cảnh và cung cấp các gợi ý chính xác hơn để cải thiện chất lượng viết.

#### Dịch thuật
Dịch vụ dịch thuật thời gian thực hỗ trợ 13+ ngôn ngữ, cho phép giao tiếp liền mạch giữa các ngôn ngữ khác nhau với độ chính xác cao.

#### Chuyển giọng nói thành văn bản
Nhận dạng giọng nói tinh vi chuyển đổi từ nói thành văn bản với độ chính xác cao, hỗ trợ nhiều ngôn ngữ và phương ngữ.

### Cài đặt & Sử dụng
1. Build dự án bằng Android Studio
2. Cài đặt APK trên thiết bị Android
3. Bật AI Keyboard trong cài đặt hệ thống
4. Cấu hình ngôn ngữ gõ và hiển thị ưa thích
5. Bắt đầu gõ với sự hỗ trợ của AI!

---

[## Contributing
Contributions are welcome! Please feel free to submit a Pull Request.

## License
This project is licensed under the MIT License - see the LICENSE file for details.

## Support
For support and questions, please open an issue on GitHub.

(https://buymeacoffee.com/boss9xy)
