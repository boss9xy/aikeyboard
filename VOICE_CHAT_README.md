# Voice Chat với AI - Tính năng mới

## Mô tả
Tính năng Voice Chat cho phép bạn trò chuyện liên tục với AI thông qua giọng nói. Khi bạn nói, hệ thống sẽ chuyển đổi giọng nói thành văn bản và gửi đến GPT API để nhận phản hồi, sau đó tự động đọc phản hồi bằng TTS.

## Cách sử dụng

### 1. Kích hoạt tính năng
- Mở bàn phím AI
- Tìm nút Voice Chat (icon microphone với sóng âm) trong smartbar
- Nhấn vào nút để bắt đầu voice chat

### 2. Trò chuyện với AI
- Nhấn nút Voice Chat để bắt đầu lắng nghe
- Nói câu hỏi hoặc yêu cầu của bạn
- Hệ thống sẽ hiển thị transcription real-time trong hộp soạn thảo
- AI sẽ trả lời và hiển thị phản hồi trong hộp soạn thảo
- Phản hồi AI sẽ được đọc tự động bằng TTS

### 3. Điều khiển
- **Nút Voice Chat** (🎤): Bắt đầu voice chat
- **Nút Stop Voice Chat** (⏹️): Dừng voice chat (xuất hiện khi đang hoạt động)
- **Blink Pattern** (👁️): Nhấp nháy 3 lần để gửi văn bản hoàn thiệt

## Tính năng

### Voice Recognition
- Sử dụng Google Speech Recognition API
- Hỗ trợ tiếng Việt
- Hiển thị partial results real-time trong hộp soạn thảo
- Tự động restart khi có lỗi
- Lắng nghe liên tục cho đến khi dừng

### AI Integration
- Sử dụng GPT API để xử lý câu hỏi
- Hiển thị phản hồi trực tiếp trong hộp soạn thảo
- Tự động đọc phản hồi bằng TTS
- Hỗ trợ nhiều model GPT khác nhau

### UI/UX
- Hiển thị kết quả trực tiếp trong hộp soạn thảo
- **Real-time transcription**: Hiển thị ngay khi đang nói (không có prefix)
- **AI response**: Format "🤖 AI: [response]"
- Nút dừng xuất hiện khi đang hoạt động
- Tự động chuyển đổi giữa nút bắt đầu và dừng

## Cách hoạt động

### 1. Bắt đầu Voice Chat
```
Nhấn nút Voice Chat → Bắt đầu lắng nghe → Nút dừng xuất hiện
```

### 2. Quá trình trò chuyện
```
Nói → Transcription hiển thị real-time → GPT xử lý → "🤖 AI: [response]" → TTS đọc (tạm dừng lắng nghe) → Tiếp tục lắng nghe
```

### 3. Cơ chế tránh feedback
```
AI đang đọc → Tạm dừng lắng nghe → User nói → Lưu vào buffer → AI đọc xong → Xử lý buffer → Tiếp tục lắng nghe
```

### 4. Cơ chế timeout
```
Lắng nghe → 2 giây im lặng → Tự động restart lắng nghe → Tiếp tục
```

### 5. Cơ chế Blink Pattern
```
Nói → Text hiển thị → Nhấp nháy 3 lần → Dừng mic → Gửi văn bản hoàn thiệt → AI xử lý
```

### 6. Dừng Voice Chat
```
Nhấn nút Stop → Dừng lắng nghe → Nút bắt đầu xuất hiện
```

## Cài đặt

### API Key
Đảm bảo đã cài đặt GPT API key trong Settings:
1. Mở Settings của ứng dụng
2. Nhập GPT API key
3. Chọn model GPT mong muốn

### Permissions
Tính năng cần quyền microphone:
- RECORD_AUDIO permission
- Tự động yêu cầu quyền khi sử dụng lần đầu

## Technical Details

### Files chính
- `AIKeyboardService.kt`: Xử lý voice chat trực tiếp trong keyboard service
- `GPTAPI.kt`: API integration với method `askGPT()`

### Dependencies
- Android Speech Recognition API
- GPT API integration
- Text-to-Speech (TTS)
- Kotlin Coroutines cho async operations

### Architecture
```
Voice Chat Button
    ↓
Speech Recognition → Input Connection
    ↓
GPT API → Response
    ↓
TTS → Audio Output
```

## Tính năng đặc biệt

### Real-time Transcription
- Hiển thị text ngay khi đang nói
- Cập nhật liên tục cho đến khi hoàn thành
- **Không có prefix**: Hiển thị trực tiếp văn bản, không có "👤 Bạn:"
- **Xử lý text thông minh**: Áp dụng kỹ thuật từ mic cũ
  - Theo dõi thay đổi của user
  - Cập nhật hoặc thêm text tùy theo hành động
  - Tránh ghi đè lên text user đã sửa

### Continuous Listening
- Lắng nghe liên tục sau mỗi lần nói
- Tự động restart khi có lỗi
- Chỉ dừng khi nhấn nút Stop
- **Tạm dừng lắng nghe khi AI đang đọc** để tránh feedback loop
- **Timeout 2 giây**: Tự động restart lắng nghe sau 2 giây im lặng
- **Blink Pattern Detection**: Phát hiện 3 lần blink để gửi văn bản hoàn thiệt

### Auto TTS
- Tự động đọc phản hồi AI
- Sử dụng TTS engine của Android
- Hỗ trợ tiếng Việt

### Smart UI
- Nút chuyển đổi thông minh với icon riêng biệt
- **Voice Chat**: Icon microphone với sóng âm (🎤)
- **Stop Voice Chat**: Icon stop (⏹️)
- Hiển thị trạng thái rõ ràng
- Không ảnh hưởng đến bàn phím
- **Tự động reset trạng thái**: Nút trở về bình thường sau khi TTS đọc xong

### Anti-Feedback Mechanism
- Tạm dừng lắng nghe khi AI đang đọc
- Buffer tạm thời cho văn bản được nói trong lúc AI đọc
- Tự động xử lý buffer sau khi AI đọc xong
- Tránh feedback loop hoàn toàn

### Timeout Mechanism
- **2 giây im lặng**: Tự động restart lắng nghe
- **Giống mic cũ**: Cùng thời gian timeout với mic thường
- **Liên tục**: Không dừng hoàn toàn, chỉ restart để duy trì kết nối

### Blink Pattern Detection
- **3 lần blink**: Phát hiện nhấp nháy 3 lần trong 500ms
- **Tự động gửi**: Dừng mic và gửi văn bản hoàn thiệt vào API
- **Lấy văn bản cuối**: Sử dụng văn bản hiện tại trong input field
- **Cải thiện độ chính xác**: Chỉ kiểm tra khi text đủ dài (>3 ký tự) và có blink trước đó
- **Bỏ qua lỗi coroutine**: Không hiển thị lỗi "StandaloneCoroutine was cancelled"
- **Không hiển thị thông báo**: Loại bỏ thông báo blink khỏi màn hình để tránh spam

## Troubleshooting

### Lỗi thường gặp
1. **"Microphone permission required"**
   - Cấp quyền microphone trong Settings

2. **"GPT API not initialized"**
   - Kiểm tra GPT API key trong Settings

3. **"Speech recognition not available"**
   - Thiết bị không hỗ trợ speech recognition

4. **"Network error"**
   - Kiểm tra kết nối internet

### Debug
- Log tag: "AIKeyboardService"
- Log tag: "GPTAPI"

## So sánh với các tính năng khác

| Tính năng | Voice Chat | Mic thường | Voice→Text |
|-----------|------------|-------------|------------|
| Hiển thị | Input connection | Input connection | Activity riêng |
| Lắng nghe | Liên tục | Một lần | Một lần |
| AI xử lý | Có | Không | Không |
| TTS | Tự động | Không | Không |
| UI | Nút chuyển đổi | Nút đơn | Activity |

## Future Enhancements
- Lưu lịch sử chat
- Export chat history
- Custom voice commands
- Multi-language support
- Voice chat với các AI khác (DeepSeek, Olama) 