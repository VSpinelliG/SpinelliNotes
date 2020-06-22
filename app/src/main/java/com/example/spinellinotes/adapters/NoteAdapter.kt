package com.example.spinellinotes.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.spinellinotes.R
import com.example.spinellinotes.model.ArrayNotes
import com.example.spinellinotes.model.Note
import kotlinx.android.synthetic.main.cardview_item.view.*

class NoteAdapter(private val itemClickListener: OnItemClickListener) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cardview_item,
        parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = ArrayNotes.notes[position]
        holder.bind(note,itemClickListener)
    }

    override fun getItemCount() = ArrayNotes.notes.size


    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val title: TextView = itemView.title
        private val resume: TextView = itemView.subtitle
        private val deleteNote: ImageView = itemView.delete_note

        fun bind(note: Note,clickListener: OnItemClickListener)
        {
            title.text = note.title
            resume.text = note.resume

            itemView.setOnClickListener {
                clickListener.onItemClicked(note)
            }

            deleteNote.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    clickListener.onDeleteClicked(note)
                }
            }

            itemView.setBackgroundColor(note.background)
        }

    }

    interface OnItemClickListener{
        fun onItemClicked(note: Note)
        fun onDeleteClicked(note: Note)
    }
}

