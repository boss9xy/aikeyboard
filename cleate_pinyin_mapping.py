#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import json
import os
import pickle
import numpy as np
import tensorflow as tf

def create_comprehensive_pinyin_mappings():
    """Create comprehensive pinyin-to-chinese mappings"""
    
    mappings = {
        # Basic words
        "wo": "我", "ai": "爱", "ni": "你", "hao": "好", "ma": "吗",
        "shi": "是", "de": "的", "le": "了", "zai": "在", "you": "有",
        "woaini": "我爱你", "nihao": "你好", "xiexie": "谢谢", "zaijian": "再见",
        "duibuqi": "对不起", "meiwenti": "没问题", "bukeqi": "不客气",
        
        # Technology terms
        "dianhua": "电话", "duanxin": "短信", "weixin": "微信", "qq": "QQ",
        "diannao": "电脑", "shouji": "手机", "wangluo": "网络", "hulianwang": "互联网",
        "ruanjian": "软件", "yingjian": "硬件", "xitong": "系统", "chengxu": "程序",
        "shujuku": "数据库", "wangzhan": "网站", "youjian": "邮件", "luyouqi": "路由器",
        
        # Cities and places
        "zhongguo": "中国", "beijing": "北京", "shanghai": "上海", "guangzhou": "广州",
        "shenzhen": "深圳", "hangzhou": "杭州", "nanjing": "南京", "xian": "西安",
        "chengdu": "成都", "wuhan": "武汉", "tianjin": "天津", "chongqing": "重庆",
        "dalian": "大连", "qingdao": "青岛", "xiamen": "厦门", "suzhou": "苏州",
        "wuxi": "无锡", "ningbo": "宁波", "fuzhou": "福州", "jinan": "济南",
        "zhengzhou": "郑州", "changsha": "长沙", "nanchang": "南昌", "kunming": "昆明",
        "guiyang": "贵阳", "nanning": "南宁", "haikou": "海口", "sanya": "三亚",
        "lasa": "拉萨", "xining": "西宁", "yinchuan": "银川", "urumqi": "乌鲁木齐",
        "harbin": "哈尔滨", "changchun": "长春", "shenyang": "沈阳", "shijiazhuang": "石家庄",
        "taiyuan": "太原", "huhehaote": "呼和浩特",
        
        # Common phrases
        "zaoshang": "早上", "wanshang": "晚上", "zhongwu": "中午", "xiawu": "下午",
        "jintian": "今天", "zuotian": "昨天", "mingtian": "明天", "xingqi": "星期",
        "nian": "年", "yue": "月", "ri": "日", "shi": "时", "fen": "分", "miao": "秒",
        "nianling": "年龄", "shengri": "生日", "jieri": "节日", "chunjie": "春节",
        "zhongqiujie": "中秋节", "duanwujie": "端午节", "qingmingjie": "清明节",
        
        # Family and relationships
        "baba": "爸爸", "mama": "妈妈", "gege": "哥哥", "jiejie": "姐姐",
        "didi": "弟弟", "meimei": "妹妹", "yeye": "爷爷", "nainai": "奶奶",
        "waigong": "外公", "waipo": "外婆", "shushu": "叔叔", "ayi": "阿姨",
        "pengyou": "朋友", "tongxue": "同学", "laoshi": "老师", "xuesheng": "学生",
        "zhangfu": "丈夫", "qizi": "妻子", "nanpengyou": "男朋友", "nvpengyou": "女朋友",
        
        # Food and drinks
        "fan": "饭", "cai": "菜", "rou": "肉", "yu": "鱼", "ji": "鸡", "niu": "牛",
        "yang": "羊", "zhu": "猪", "shucai": "蔬菜", "shuiguo": "水果", "mianbao": "面包",
        "shuijiao": "水饺", "baozi": "包子", "mantou": "馒头", "miantiao": "面条",
        "cha": "茶", "kafei": "咖啡", "niunai": "牛奶", "shui": "水", "jiu": "酒",
        "yinliao": "饮料", "tian": "甜", "suan": "酸", "la": "辣", "xian": "咸",
        
        # Numbers and counting
        "yi": "一", "er": "二", "san": "三", "si": "四", "wu": "五", "liu": "六",
        "qi": "七", "ba": "八", "jiu": "九", "shi": "十", "bai": "百", "qian": "千",
        "wan": "万", "ling": "零", "di": "第", "ge": "个", "ben": "本",
        "zhang": "张", "tiao": "条", "jian": "件", "ke": "颗", "zhi": "只",
        
        # Colors
        "hong": "红", "huang": "黄", "lan": "蓝", "lv": "绿", "hei": "黑", "bai": "白",
        "zi": "紫", "cheng": "橙", "fen": "粉", "hui": "灰", "zong": "棕", "jin": "金",
        "yin": "银", "se": "色", "yanse": "颜色", "cai": "彩", "caihong": "彩虹",
        
        # Emotions and feelings
        "gaoxing": "高兴", "kuaile": "快乐", "xihuan": "喜欢", "hen": "恨",
        "shengqi": "生气", "shangxin": "伤心", "nan": "难", "rongyi": "容易", "kunnan": "困难",
        "jidan": "简单", "fuzha": "复杂", "mei": "美", "chou": "丑", "huai": "坏",
        "xin": "新", "jiu": "旧", "da": "大", "xiao": "小", "gao": "高", "ai": "矮",
        "pang": "胖", "shou": "瘦", "qiang": "强", "ruo": "弱", "kuai": "快", "man": "慢",
        
        # Weather and nature
        "tianqi": "天气", "qing": "晴", "yin": "阴", "yu": "雨", "xue": "雪", "feng": "风",
        "lei": "雷", "dian": "电", "yun": "云", "taiyang": "太阳", "yueliang": "月亮",
        "xingxing": "星星", "shan": "山", "shui": "水", "he": "河", "hai": "海",
        "hu": "湖", "senlin": "森林", "cao": "草", "hua": "花", "shu": "树", "niao": "鸟",
        "chong": "虫", "gou": "狗", "mao": "猫",
        
        # Transportation
        "che": "车", "qiche": "汽车", "huoche": "火车", "feiji": "飞机", "chuan": "船",
        "zixingche": "自行车", "motuoche": "摩托车", "gonggongqiche": "公共汽车",
        "dianche": "电车", "ditie": "地铁", "chuzuche": "出租车", "lun": "轮", "lunzi": "轮子",
        
        # Work and study
        "gongzuo": "工作", "xuexi": "学习", "kaoshi": "考试", "chengji": "成绩", "fenshu": "分数",
        "ban": "班", "ke": "课", "shu": "书", "benzi": "本子", "bi": "笔", "zhi": "纸",
        
        # Common verbs
        "mei": "没", "bu": "不", "dao": "到",
        "lai": "来", "qu": "去", "zou": "走", "pao": "跑", "fei": "飞", "you": "游",
        "chi": "吃", "he": "喝", "shui": "睡", "qi": "起", "zuo": "坐", "zhan": "站",
        "kan": "看", "ting": "听", "shuo": "说", "du": "读", "xie": "写", "hua": "画",
        "wan": "玩", "gong": "工", "xue": "学", "xi": "习",
        
        # Common adjectives
        "hao": "好", "nan": "难", "rongyi": "容易",
        "kunnan": "困难", "jidan": "简单", "fuzha": "复杂", "gaoxing": "高兴", "kuaile": "快乐",
        "xihuan": "喜欢", "shengqi": "生气", "shangxin": "伤心",
        
        # Common nouns
        "ren": "人", "nan": "男", "nv": "女", "hai": "孩", "zi": "子", "er": "儿",
        "tong": "童", "qing": "青", "lao": "老", "zhong": "中",
        "jia": "家", "ting": "庭", "fu": "父", "mu": "母",
        "xiong": "兄", "di": "弟", "jie": "姐", "mei": "妹", "sun": "孙",
    }
    
    return mappings

def load_neural_model_mappings():
    """Load mappings from trained neural model"""
    try:
        # Load vocabulary
        vocab_path = "neural_chinese_transliterator-master/data/vocab.qwerty.pkl"
        if os.path.exists(vocab_path):
            with open(vocab_path, 'rb') as f:
                vocab = pickle.load(f)
            
            # Create reverse vocabulary
            idx2char = {v: k for k, v in vocab.items()}
            
            # Load model and generate predictions
            model_path = "log/qwerty"
            if os.path.exists(model_path):
                # This would require TensorFlow 1.x compatibility
                # For now, return empty dict and add manual mappings
                return {}
    except Exception as e:
        print(f"Error loading neural model: {e}")
    
    return {}

def create_enhanced_mappings():
    """Create enhanced mappings combining manual and neural predictions"""
    
    # Get manual mappings
    manual_mappings = create_comprehensive_pinyin_mappings()
    
    # Get neural model mappings (if available)
    neural_mappings = load_neural_model_mappings()
    
    # Combine mappings
    all_mappings = {**manual_mappings, **neural_mappings}
    
    # Add tone marks
    tone_mappings = {
        "a1": "ā", "a2": "á", "a3": "ǎ", "a4": "à",
        "e1": "ē", "e2": "é", "e3": "ě", "e4": "è",
        "i1": "ī", "i2": "í", "i3": "ǐ", "i4": "ì",
        "o1": "ō", "o2": "ó", "o3": "ǒ", "o4": "ò",
        "u1": "ū", "u2": "ú", "u3": "ǔ", "u4": "ù",
        "v1": "ǖ", "v2": "ǘ", "v3": "ǚ", "v4": "ǜ"
    }
    
    all_mappings.update(tone_mappings)
    
    return all_mappings

def generate_kotlin_code(mappings):
    """Generate Kotlin code for PinyinComposer"""
    
    kotlin_code = """package com.example.aikeyboard.text

import com.example.aikeyboard.text.composing.Composer

class PinyinComposer : Composer {
    override val id = "pinyin"
    override val label = "Pinyin"
    override val toRead = 10

    // Pinyin to Chinese character mapping - Sắp xếp theo độ dài giảm dần để ưu tiên từ dài hơn
    private val pinyinToChinese = mapOf(
"""
    
    # Sort by length (longest first) for better matching
    sorted_mappings = sorted(mappings.items(), key=lambda x: len(x[0]), reverse=True)
    
    for pinyin, chinese in sorted_mappings:
        kotlin_code += f'        "{pinyin}" to "{chinese}",\n'
    
    kotlin_code += """    )

    // Tone marks for pinyin
    private val toneMarks = mapOf(
        "a1" to "ā", "a2" to "á", "a3" to "ǎ", "a4" to "à",
        "e1" to "ē", "e2" to "é", "e3" to "ě", "e4" to "è",
        "i1" to "ī", "i2" to "í", "i3" to "ǐ", "i4" to "ì",
        "o1" to "ō", "o2" to "ó", "o3" to "ǒ", "o4" to "ò",
        "u1" to "ū", "u2" to "ú", "u3" to "ǔ", "u4" to "ù",
        "v1" to "ǖ", "v2" to "ǘ", "v3" to "ǚ", "v4" to "ǜ"
    )

    override fun getActions(precedingText: String, toInsert: String): Pair<Int, String> {
        val input = precedingText + toInsert
        
        // Nếu là ký tự Latin, xử lý pinyin
        if (toInsert.matches(Regex("[a-zA-Z]"))) {
            // Tìm pinyin đang gõ từ cuối chuỗi
            val pinyinPattern = Regex("[a-z]+$")
            val match = pinyinPattern.find(input.lowercase())
            
            if (match != null) {
                val currentPinyin = match.value
                
                // Kiểm tra tone marks trước
                for ((pattern, replacement) in toneMarks) {
                    if (currentPinyin.endsWith(pattern)) {
                        // Xóa pinyin cũ và thay bằng tone mark
                        return Pair(currentPinyin.length, replacement)
                    }
                }
                
                // Tìm từ phù hợp nhất với pinyin hiện tại
                var bestMatch: String? = null
                var bestLength = 0
                
                for ((pinyin, chinese) in pinyinToChinese) {
                    // Kiểm tra xem pinyin hiện tại có thể tạo thành từ nào
                    if (currentPinyin.endsWith(pinyin) && pinyin.length > bestLength) {
                        bestMatch = chinese
                        bestLength = pinyin.length
                    }
                }
                
                // Nếu tìm thấy từ phù hợp, thay thế
                if (bestMatch != null && bestLength > 0) {
                    return Pair(bestLength, bestMatch)
                }
                
                // Nếu chưa tìm thấy từ hoàn chỉnh, hiển thị pinyin hiện tại
                return Pair(0, toInsert)
            }
        }
        
        // Nếu không phải ký tự Latin hoặc không có pinyin, reset và trả về ký tự gốc
        return Pair(0, toInsert)
    }
}"""
    
    return kotlin_code

def save_mappings_to_files():
    """Save mappings to various formats"""
    
    # Create enhanced mappings
    mappings = create_enhanced_mappings()
    
    # Save as JSON
    with open('pinyin_mappings.json', 'w', encoding='utf-8') as f:
        json.dump(mappings, f, ensure_ascii=False, indent=2)
    
    # Generate Kotlin code
    kotlin_code = generate_kotlin_code(mappings)
    with open('PinyinComposer.kt', 'w', encoding='utf-8') as f:
        f.write(kotlin_code)
    
    print(f"Generated {len(mappings)} pinyin mappings")
    print("Files saved:")
    print("- pinyin_mappings.json")
    print("- PinyinComposer.kt")

if __name__ == "__main__":
    print("Creating comprehensive pinyin mappings...")
    save_mappings_to_files()
    print("Done!") 