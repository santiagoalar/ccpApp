package com.example.ccpapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.R
import com.example.ccpapp.models.VisitRecord

class VisitRecordAdapter(
    private var visitRecords: List<VisitRecord> = emptyList()
) : RecyclerView.Adapter<VisitRecordAdapter.VisitViewHolder>() {

    class VisitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvClientName: TextView = itemView.findViewById(R.id.tvClientName)
        //val tvStoreName: TextView = itemView.findViewById(R.id.tvStoreName)
        val tvVisitDate: TextView = itemView.findViewById(R.id.tvVisitDate)
        val tvNotes: TextView = itemView.findViewById(R.id.tvNotes)

        companion object {
            val LAYOUT = R.layout.visit_item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(VisitViewHolder.LAYOUT, parent, false)
        return VisitViewHolder(view)
    }

    override fun onBindViewHolder(holder: VisitViewHolder, position: Int) {
        val visit = visitRecords[position]
        //holder.tvClientName.text = visit.clientName
        holder.tvVisitDate.text = "Fecha: ${visit.visitDate}"
        holder.tvNotes.text = "Notas: ${visit.notes}"
    }

    override fun getItemCount(): Int = visitRecords.size

    fun updateVisits(newVisits: List<VisitRecord>) {
        visitRecords = newVisits
        notifyDataSetChanged()
    }
}
