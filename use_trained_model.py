#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import tensorflow as tf
import numpy as np
import json
import os
import pickle

def load_vocab():
    """Load vocabulary from the neural_chinese_transliterator project"""
    vocab_path = "data/vocab.qwerty.pkl"
    if os.path.exists(vocab_path):
        with open(vocab_path, 'rb') as f:
            vocab = pickle.load(f)
        return vocab
    return None

def create_pinyin_input(pinyin, vocab):
    """Convert pinyin string to model input format"""
    # Convert pinyin to character indices
    indices = []
    for char in pinyin.lower():
        if char in vocab:
            indices.append(vocab[char])
        else:
            indices.append(0)  # Unknown character
    
    # Pad to fixed length
    max_len = 50  # Adjust based on model input size
    while len(indices) < max_len:
        indices.append(0)
    
    return np.array(indices[:max_len])

def load_model_and_predict():
    """Load the trained model and make predictions"""
    
    # Load vocabulary
    vocab = load_vocab()
    if not vocab:
        print("Vocabulary not found!")
        return {}
    
    # Create reverse vocabulary (index to character)
    idx2char = {v: k for k, v in vocab.items()}
    
    # Load the trained model
    model_path = "../ai-keyboard-apk2/log/qwerty"
    
    try:
        # Load the model using TensorFlow 1.x style (since this is an older model)
        tf.compat.v1.disable_eager_execution()
        
        # Create a simple graph to load the model
        with tf.compat.v1.Session() as sess:
            # Load the model
            saver = tf.compat.v1.train.import_meta_graph(model_path + "/model_epoch_20_gs_474480.index")
            saver.restore(sess, tf.train.latest_checkpoint(model_path))
            
            # Get the graph
            graph = tf.compat.v1.get_default_graph()
            
            # Get input and output tensors (you may need to adjust these names)
            x = graph.get_tensor_by_name("x:0")  # Input placeholder
            preds = graph.get_tensor_by_name("preds:0")  # Output predictions
            
            # Test pinyin words
            test_pinyins = [
                "wo", "ai", "ni", "hao", "ma", "shi", "de", "le", "zai", "you",
                "woaini", "nihao", "xiexie", "zaijian", "duibuqi", "meiwenti",
                "dianhua", "duanxin", "weixin", "qq", "zhongguo", "beijing",
                "shanghai", "guangzhou", "shenzhen", "hangzhou", "nanjing",
                "xian", "chengdu", "wuhan", "tianjin", "chongqing", "dalian",
                "qingdao", "xiamen", "suzhou", "wuxi", "ningbo", "fuzhou",
                "jinan", "zhengzhou", "changsha", "nanchang", "kunming",
                "guiyang", "nanning", "haikou", "sanya", "lasa",
                "xining", "yinchuan", "urumqi", "harbin", "changchun",
                "shenyang", "shijiazhuang", "taiyuan", "huhehaote"
            ]
            
            mappings = {}
            
            for pinyin in test_pinyins:
                try:
                    # Create input
                    input_data = create_pinyin_input(pinyin, vocab)
                    input_batch = np.expand_dims(input_data, axis=0)
                    
                    # Get prediction
                    prediction = sess.run(preds, feed_dict={x: input_batch})
                    
                    # Convert prediction to Chinese characters
                    chinese_chars = []
                    for pred_idx in prediction[0]:
                        if pred_idx in idx2char:
                            char = idx2char[pred_idx]
                            if char != '_':  # Skip blank characters
                                chinese_chars.append(char)
                    
                    if chinese_chars:
                        result = ''.join(chinese_chars)
                        mappings[pinyin] = result
                        print(f"{pinyin} -> {result}")
                    
                except Exception as e:
                    print(f"Error processing {pinyin}: {e}")
                    continue
            
            return mappings
            
    except Exception as e:
        print(f"Error loading model: {e}")
        return {}

def create_kotlin_mappings(mappings):
    """Convert Python mappings to Kotlin format for PinyinComposer"""
    
    kotlin_code = """    private val pinyinToChinese = mapOf(
"""
    
    for pinyin, chinese in mappings.items():
        kotlin_code += f'        "{pinyin}" to "{chinese}",\n'
    
    kotlin_code += """    )"""
    
    # Save to file
    with open('../ai-keyboard-apk2/pinyin_mappings.kt', 'w', encoding='utf-8') as f:
        f.write(kotlin_code)
    
    print("Kotlin mappings saved to pinyin_mappings.kt")

if __name__ == "__main__":
    print("Loading trained model and generating pinyin-to-chinese mappings...")
    mappings = load_model_and_predict()
    
    if mappings:
        print(f"\nGenerated {len(mappings)} mappings")
        create_kotlin_mappings(mappings)
        print("Done!")
    else:
        print("No mappings generated. Please check the model files.") 