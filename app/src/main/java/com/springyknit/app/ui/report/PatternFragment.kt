package com.springyknit.app.ui.report

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.springyknit.app.SpringyKnitApp
import com.springyknit.app.data.model.DrawingData
import com.springyknit.app.databinding.FragmentPatternBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.IOException

class PatternFragment : Fragment() {

    private var _binding: FragmentPatternBinding? = null
    private val binding get() = _binding!!

    private val viewModel: KnittingReportViewModel by activityViewModels {
        val projectId = requireActivity().intent.getLongExtra(KnittingReportActivity.EXTRA_PROJECT_ID, -1)
        KnittingReportViewModelFactory(
            (requireActivity().application as SpringyKnitApp).repository,
            projectId
        )
    }

    private var pdfRenderer: PdfRenderer? = null
    private var currentPage = 0
    private var totalPages = 0
    private var pdfUri: Uri? = null

    private val pdfPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                // Take persistent permission
                requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                loadPdf(uri)
                viewModel.updatePdfUri(uri.toString())
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatternBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        setupDrawingView()
        observeProject()
    }

    private fun setupClickListeners() {
        binding.btnSelectPdf.setOnClickListener { openPdfPicker() }
        binding.btnChangePdf.setOnClickListener { openPdfPicker() }
        binding.btnClearDrawing.setOnClickListener { clearDrawing() }
        binding.btnPrevPage.setOnClickListener { showPreviousPage() }
        binding.btnNextPage.setOnClickListener { showNextPage() }
    }

    private fun setupDrawingView() {
        binding.drawingView.onDrawingChanged = { drawingData ->
            viewModel.updateDrawingData(drawingData)
        }
    }

    private fun observeProject() {
        lifecycleScope.launch {
            viewModel.project.collectLatest { project ->
                project?.let {
                    // Load PDF if exists
                    it.pdfUri?.let { uriString ->
                        val uri = Uri.parse(uriString)
                        if (pdfUri != uri) {
                            loadPdf(uri)
                        }
                    }

                    // Load drawing data if exists
                    it.drawingDataJson?.let { json ->
                        try {
                            val drawingData = Gson().fromJson(json, DrawingData::class.java)
                            binding.drawingView.setDrawingData(drawingData)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    // Restore current page
                    if (currentPage != it.currentPdfPage && pdfRenderer != null) {
                        currentPage = it.currentPdfPage
                        renderPage(currentPage)
                    }
                }
            }
        }
    }

    private fun openPdfPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        pdfPickerLauncher.launch(intent)
    }

    private fun loadPdf(uri: Uri) {
        try {
            pdfRenderer?.close()

            val fileDescriptor: ParcelFileDescriptor? =
                requireContext().contentResolver.openFileDescriptor(uri, "r")

            fileDescriptor?.let { fd ->
                pdfRenderer = PdfRenderer(fd)
                totalPages = pdfRenderer!!.pageCount
                currentPage = 0
                pdfUri = uri

                renderPage(currentPage)
                updateUI(true)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            updateUI(false)
        } catch (e: SecurityException) {
            e.printStackTrace()
            updateUI(false)
        }
    }

    private fun renderPage(pageIndex: Int) {
        if (pageIndex < 0 || pageIndex >= totalPages) return

        pdfRenderer?.let { renderer ->
            val page = renderer.openPage(pageIndex)

            val scale = 2f
            val width = (page.width * scale).toInt()
            val height = (page.height * scale).toInt()

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()

            binding.ivPdfPage.setImageBitmap(bitmap)
            updatePageNumber()
        }
    }

    private fun updateUI(hasPdf: Boolean) {
        binding.emptyState.visibility = if (hasPdf) View.GONE else View.VISIBLE
        binding.ivPdfPage.visibility = if (hasPdf) View.VISIBLE else View.GONE
        binding.drawingView.visibility = if (hasPdf) View.VISIBLE else View.GONE
        binding.bottomControls.visibility = if (hasPdf) View.VISIBLE else View.GONE
    }

    private fun updatePageNumber() {
        binding.tvPageNumber.text = "${currentPage + 1} / $totalPages"
    }

    private fun showPreviousPage() {
        if (currentPage > 0) {
            currentPage--
            renderPage(currentPage)
            viewModel.updateCurrentPdfPage(currentPage)
            // Clear drawing for new page (or you could save per-page)
            binding.drawingView.clear()
        }
    }

    private fun showNextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++
            renderPage(currentPage)
            viewModel.updateCurrentPdfPage(currentPage)
            // Clear drawing for new page
            binding.drawingView.clear()
        }
    }

    private fun clearDrawing() {
        binding.drawingView.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        pdfRenderer?.close()
        _binding = null
    }
}
