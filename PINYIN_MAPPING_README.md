# Pinyin Mapping System

## Tổng quan

Hệ thống mapping pinyin-to-chinese này được tạo ra để thay thế cho `PinyinComposer.kt` gốc với các tính năng cải tiến:

### Tính năng chính

1. **288 mappings pinyin-to-chinese** được sắp xếp theo độ dài giảm dần
2. **Hỗ trợ tone marks** (a1, a2, a3, a4, etc.)
3. **Từ vựng đa dạng** bao gồm:
   - Từ cơ bản (wo, ai, ni, hao, ma, etc.)
   - Thuật ngữ công nghệ (dianhua, weixin, qq, etc.)
   - Tên thành phố (beijing, shanghai, guangzhou, etc.)
   - Gia đình và mối quan hệ (baba, mama, gege, etc.)
   - Thực phẩm và đồ uống (fan, cai, rou, cha, etc.)
   - Số đếm và màu sắc (yi, er, san, hong, huang, etc.)
   - Cảm xúc và cảm giác (gaoxing, kuaile, shengqi, etc.)
   - Thời tiết và thiên nhiên (tianqi, qing, yu, xue, etc.)
   - Giao thông (che, qiche, huoche, feiji, etc.)
   - Công việc và học tập (gongzuo, xuexi, kaoshi, etc.)

### Cách hoạt động

1. **Input**: Người dùng gõ pinyin (ví dụ: "woaini")
2. **Processing**: Hệ thống tìm từ dài nhất phù hợp
3. **Output**: Chuyển đổi thành chữ Hán (ví dụ: "我爱你")

### Ưu điểm so với phiên bản cũ

1. **Nhiều từ vựng hơn**: 288 mappings vs ~100 mappings cũ
2. **Sắp xếp thông minh**: Ưu tiên từ dài hơn để tránh nhầm lẫn
3. **Hỗ trợ tone marks**: Có thể xử lý các dấu thanh
4. **Từ vựng hiện đại**: Bao gồm các từ công nghệ và địa danh mới

### Files được tạo

1. **`cleate_pinyin_mapping.py`** - Script Python để tạo mappings
2. **`pinyin_mappings.json`** - File JSON chứa tất cả mappings
3. **`PinyinComposer.kt`** - File Kotlin hoàn chỉnh để thay thế

### Cách sử dụng

1. Chạy script Python để tạo mappings:
   ```bash
   python3 cleate_pinyin_mapping.py
   ```

2. Thay thế file PinyinComposer.kt gốc:
   ```bash
   cp PinyinComposer.kt app/src/main/java/com/example/aikeyboard/text/PinyinComposer.kt
   ```

3. Build và test ứng dụng Android

### Cấu trúc mapping

Mỗi mapping có dạng:
```kotlin
"pinyin" to "chinese_character"
```

Ví dụ:
- `"woaini" to "我爱你"`
- `"nihao" to "你好"`
- `"xiexie" to "谢谢"`

### Tone marks

Hệ thống hỗ trợ các tone marks:
- `a1` → `ā`, `a2` → `á`, `a3` → `ǎ`, `a4` → `à`
- `e1` → `ē`, `e2` → `é`, `e3` → `ě`, `e4` → `è`
- `i1` → `ī`, `i2` → `í`, `i3` → `ǐ`, `i4` → `ì`
- `o1` → `ō`, `o2` → `ó`, `o3` → `ǒ`, `o4` → `ò`
- `u1` → `ū`, `u2` → `ú`, `u3` → `ǔ`, `u4` → `ù`
- `v1` → `ǖ`, `v2` → `ǘ`, `v3` → `ǚ`, `v4` → `ǜ`

### Mở rộng

Để thêm mappings mới, chỉnh sửa function `create_comprehensive_pinyin_mappings()` trong file `cleate_pinyin_mapping.py` và chạy lại script. 