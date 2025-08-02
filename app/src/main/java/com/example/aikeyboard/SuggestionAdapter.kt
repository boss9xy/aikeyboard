package com.example.aikeyboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aikeyboard.models.Suggestion

class SuggestionAdapter(
    private var suggestions: List<Suggestion> = emptyList(),
    private val onSuggestionClicked: (Suggestion) -> Unit
) : RecyclerView.Adapter<SuggestionAdapter.SuggestionViewHolder>() {

    class SuggestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordText: TextView = itemView.findViewById(R.id.suggestionWord)
        val definitionText: TextView = itemView.findViewById(R.id.suggestionDefinition)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.suggestion_item, parent, false)
        return SuggestionViewHolder(view)
    }

    override fun onBindViewHolder(holder: SuggestionViewHolder, position: Int) {
        val suggestion = suggestions[position]
        
        holder.wordText.text = suggestion.word
        
        // Hiển thị định nghĩa nếu có
        if (suggestion.definition != null && suggestion.definition.isNotEmpty()) {
            holder.definitionText.text = suggestion.definition
            holder.definitionText.visibility = View.VISIBLE
        } else {
            holder.definitionText.visibility = View.GONE
        }
        
        // Hiển thị màu khác cho clipboard history
        when (suggestion.type) {
            Suggestion.SuggestionType.CLIPBOARD_HISTORY -> {
                holder.wordText.setTextColor(holder.itemView.context.getColor(android.R.color.holo_blue_dark))
                holder.wordText.text = "📋 ${suggestion.word}"
            }
            Suggestion.SuggestionType.WORD_SUGGESTION -> {
                holder.wordText.setTextColor(holder.itemView.context.getColor(android.R.color.black))
            }
            else -> {
                holder.wordText.setTextColor(holder.itemView.context.getColor(android.R.color.black))
            }
        }

        holder.itemView.setOnClickListener {
            onSuggestionClicked(suggestion)
        }
    }

    override fun getItemCount(): Int = suggestions.size

    fun updateSuggestions(newSuggestions: List<Suggestion>) {
        suggestions = newSuggestions
        notifyDataSetChanged()
    }

    fun clearSuggestions() {
        suggestions = emptyList()
        notifyDataSetChanged()
    }

    fun getSuggestions(): List<Suggestion> {
        return suggestions
    }
} 