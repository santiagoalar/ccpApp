package com.example.ccpapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ccpapp.R
import com.example.ccpapp.models.VisitRecord
import java.text.SimpleDateFormat
import java.util.Locale

class VisitRecordAdapter(
    private var visitRecords: List<VisitRecord> = emptyList()
) : RecyclerView.Adapter<VisitRecordAdapter.VisitViewHolder>() {

    class VisitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvClientName: TextView = itemView.findViewById(R.id.tvClientName)
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
        holder.tvClientName.text = visit.clientId

        val formattedDate = formatDate(visit.visitDate)
        holder.tvVisitDate.text = "Fecha: $formattedDate"
        
        holder.tvNotes.text = "Notas: ${visit.notes}"
    }

    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "Fecha desconocida"
        
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: "Formato de fecha incorrecto"
        } catch (e: Exception) {
            try {
                val alternateInputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                
                val date = alternateInputFormat.parse(dateString)
                date?.let { outputFormat.format(it) } ?: "Formato de fecha incorrecto"
            } catch (e: Exception) {
                "Formato de fecha desconocido"
            }
        }
    }

    override fun getItemCount(): Int = visitRecords.size

    fun updateVisits(newVisits: List<VisitRecord>) {
        visitRecords = newVisits
        notifyDataSetChanged()
    }
}
