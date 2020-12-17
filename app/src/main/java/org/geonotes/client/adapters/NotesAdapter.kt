package org.geonotes.client.adapters

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson

import org.geonotes.client.R
import org.geonotes.client.model.entity.Note
import org.geonotes.client.activities.NoteActivity


class NotesAdapter(private val notes: Array<Note>, private val activity: Activity) :
    RecyclerView.Adapter<NotesAdapter.NotesHolder>() {

    class NotesHolder(val cardView: MaterialCardView) : RecyclerView.ViewHolder(cardView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesHolder {
        return NotesHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.note_card, parent, false) as MaterialCardView
        )
    }

    override fun onBindViewHolder(holder: NotesHolder, position: Int) {
        val note: Note = notes[position]

        ((holder.cardView[0] as ConstraintLayout)[0] as TextView).text = note.noteBase.title
        ((holder.cardView[0] as ConstraintLayout)[1] as TextView).text = note.geoTags[0].name
        ((holder.cardView[0] as ConstraintLayout)[2] as TextView).text = note.noteBase.text

        holder.cardView.setCardBackgroundColor(notes[position].noteBase.color)
        holder.cardView.setOnClickListener {
            activity.startActivity(
                Intent(activity, NoteActivity::class.java).apply {
                    putExtra("EXTRA_TARGET_NOTE", Gson().toJson(note))
                },
                ActivityOptions.makeSceneTransitionAnimation(activity).toBundle()
            )
        }
    }

    override fun getItemCount() = notes.size
}
