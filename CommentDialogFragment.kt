package my.edu.tarc.itcm

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class CommentDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.comments_layout, null)

            // Set up your dialog layout and functionality here
            // For example, you can add a ListView for comments and an EditText for adding new comments

            builder.setView(view)
                .setTitle("Comments")
                .setPositiveButton("Add") { dialog, id ->
                    // Handle adding a new comment
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    // Cancel the dialog
                    dialog.dismiss()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
