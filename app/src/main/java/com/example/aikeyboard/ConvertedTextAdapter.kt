package com.example.aikeyboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class ConvertedTextAdapter(
    private val context: Context,
    private val convertedTexts: MutableList<String>
) : RecyclerView.Adapter<ConvertedTextAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val convertedTextTextView: TextView = view.findViewById(R.id.convertedTextTextView)
        val editButton: Button = view.findViewById(R.id.editButton)
        val copyItemButton: Button = view.findViewById(R.id.copyItemButton)
        val deleteItemButton: Button = view.findViewById(R.id.deleteItemButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_converted_text, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = convertedTexts[position]
        
        // Hiển thị text thu gọn (chỉ hiển thị 100 ký tự đầu)
        val displayText = if (text.length > 100) {
            text.substring(0, 100) + "..."
        } else {
            text
        }
        holder.convertedTextTextView.text = displayText

        holder.editButton.setOnClickListener {
            showEditDialog(position, text)
        }

        holder.copyItemButton.setOnClickListener {
            copyToClipboard(text)
        }

        holder.deleteItemButton.setOnClickListener {
            deleteItem(position)
        }
    }

    override fun getItemCount() = convertedTexts.size

    private fun showEditDialog(position: Int, currentText: String) {
        val editText = android.widget.EditText(context).apply {
            setText(currentText)
            setSelection(currentText.length)
        }

        AlertDialog.Builder(context)
            .setTitle("Edit Text")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newText = editText.text.toString()
                if (newText.isNotEmpty()) {
                    convertedTexts[position] = newText
                    notifyItemChanged(position)
                    Toast.makeText(context, "Text updated", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun copyToClipboard(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Converted Text", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Text copied", Toast.LENGTH_SHORT).show()
    }

    private fun deleteItem(position: Int) {
        AlertDialog.Builder(context)
            .setTitle("Delete Text")
            .setMessage("Are you sure you want to delete this text?")
            .setPositiveButton("Delete") { _, _ ->
                convertedTexts.removeAt(position)
                notifyItemRemoved(position)
                saveConvertedTexts()
                Toast.makeText(context, "Text deleted", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    fun addText(text: String) {
        if (text.isNotEmpty()) {
            convertedTexts.add(0, text) // Thêm vào đầu danh sách
            notifyItemInserted(0)
            saveConvertedTexts()
        }
    }

    fun clearAll() {
        convertedTexts.clear()
        notifyDataSetChanged()
        saveConvertedTexts()
    }

    private fun saveConvertedTexts() {
        val prefs = context.getSharedPreferences("VoiceToTextSettings", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putInt("converted_texts_count", convertedTexts.size)
        for (i in convertedTexts.indices) {
            editor.putString("converted_text_$i", convertedTexts[i])
        }
        editor.apply()
    }

    fun loadConvertedTexts() {
        val prefs = context.getSharedPreferences("VoiceToTextSettings", Context.MODE_PRIVATE)
        val count = prefs.getInt("converted_texts_count", 0)
        convertedTexts.clear()
        for (i in 0 until count) {
            val text = prefs.getString("converted_text_$i", "")
            if (!text.isNullOrEmpty()) {
                convertedTexts.add(text)
            }
        }
        notifyDataSetChanged()
    }
} 