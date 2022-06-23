package com.aftab.abentseller.Activities.Main

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aftab.abentseller.Adapters.Recycler.AddImagesAdapter
import com.aftab.abentseller.Listeners.AddImagesListener
import com.aftab.abentseller.Model.ProductCategories
import com.aftab.abentseller.Model.Products
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.*
import com.aftab.abentseller.databinding.ActivityAddNewProductBinding
import com.aftab.abentseller.databinding.PublishedSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.FishBun.Companion.INTENT_PATH
import com.sangcomz.fishbun.MimeType
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import java.io.File
import kotlin.math.abs


@Suppress("deprecation")
class AddNewProductActivity : AppCompatActivity(), AddImagesListener {

    private lateinit var binding: ActivityAddNewProductBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var sh: SharedPref
    private lateinit var usersData: Users

    private lateinit var productCategoriesIntent: ProductCategories
    private var imagesList = ArrayList<Uri>()
    private var colorsList = ArrayList<String>()
    private var imagesUrlList = ArrayList<String>()
    private var availableSizeList = ArrayList<String>()

    private var uploadCount = 0
    private var isValid = false
    private var isAvailableSize = true
    private var videoPath: String? = ""
    private var videoURL: String? = ""
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var price: String
    private lateinit var quantity: String
    private var sizeFrom: String = ""
    private var sizeTo: String = ""


    private lateinit var addImagesAdapter: AddImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewProductBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        clickListeners()
    }

    private fun initUI() {

        sh = SharedPref(this)
        usersData = sh.getUsers()!!
        loadingDialog = LoadingDialog(this, "Uploading")

        productCategoriesIntent =
            intent.getSerializableExtra(Constants.PRODUCT_CATEGORIES) as ProductCategories

        binding.cbAvailableSize.isChecked = true
        binding.cbSelectSize.isChecked = false

        binding.llAvailableSize.alpha = 1f
        binding.llSelectSize.alpha = 0.6f


    }

    private fun clickListeners() {

        binding.btnPublish.setOnClickListener {

            uploadCount = 0
            isValid = false

            isValid = validateFields()

            if (isValid) {

                imagesUrlList.clear()
                videoURL = ""

                progressDialog = ProgressDialog(this, R.style.AppCompatAlertDialogStyle)
                progressDialog.setCancelable(false)
                progressDialog.show()

                uploadImages()

            }

        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        binding.cvAddImages.setOnClickListener {

            choseImage()

        }

        binding.cvChoseVideo.setOnClickListener {

            choseVideo()

        }

        binding.cbAvailableSize.setOnCheckedChangeListener { _, b ->

            isAvailableSize = b

            if (b) {

                binding.etFrom.isFocusableInTouchMode = true
                binding.etTo.isFocusableInTouchMode = true

                binding.cbAvailableSize.isChecked = true
                binding.cbSelectSize.isChecked = false

                binding.llAvailableSize.alpha = 1f
                binding.llSelectSize.alpha = 0.6f

                binding.cbS.isEnabled = false
                binding.cbX.isEnabled = false
                binding.cbM.isEnabled = false
                binding.cbXl.isEnabled = false


            } else {

                binding.etFrom.isFocusableInTouchMode = false
                binding.etTo.isFocusableInTouchMode = false

                binding.cbAvailableSize.isChecked = false
                binding.cbSelectSize.isChecked = true

                binding.llAvailableSize.alpha = 0.6f
                binding.llSelectSize.alpha = 1f

                binding.cbS.isEnabled = true
                binding.cbX.isEnabled = true
                binding.cbM.isEnabled = true
                binding.cbXl.isEnabled = true
            }

        }

        binding.cbSelectSize.setOnCheckedChangeListener { _, b ->

            isAvailableSize = !b

            if (b) {

                binding.etFrom.isFocusableInTouchMode = false
                binding.etTo.isFocusableInTouchMode = false

                binding.cbAvailableSize.isChecked = false
                binding.cbSelectSize.isChecked = true

                binding.llAvailableSize.alpha = 0.6f
                binding.llSelectSize.alpha = 1f

                binding.cbS.isEnabled = true
                binding.cbX.isEnabled = true
                binding.cbM.isEnabled = true
                binding.cbXl.isEnabled = true

            } else {

                binding.etFrom.isFocusableInTouchMode = true
                binding.etTo.isFocusableInTouchMode = true

                binding.cbAvailableSize.isChecked = true
                binding.cbSelectSize.isChecked = false

                binding.llAvailableSize.alpha = 1f
                binding.llSelectSize.alpha = 0.6f

                binding.cbS.isEnabled = false
                binding.cbX.isEnabled = false
                binding.cbM.isEnabled = false
                binding.cbXl.isEnabled = false
            }

        }

        binding.cbRed.setOnCheckedChangeListener { _, b ->

            if (b) {

                colorsList.add(binding.cbRed.text.toString())

            } else {

                colorsList.remove(binding.cbRed.text.toString())

            }

        }
        binding.cbWhite.setOnCheckedChangeListener { _, b ->

            if (b) {

                colorsList.add(binding.cbWhite.text.toString())

            } else {

                colorsList.remove(binding.cbWhite.text.toString())

            }

        }
        binding.cbGreen.setOnCheckedChangeListener { _, b ->

            if (b) {

                colorsList.add(binding.cbGreen.text.toString())

            } else {

                colorsList.remove(binding.cbGreen.text.toString())

            }

        }
        binding.cbBrown.setOnCheckedChangeListener { _, b ->

            if (b) {

                colorsList.add(binding.cbBrown.text.toString())

            } else {

                colorsList.remove(binding.cbBrown.text.toString())

            }

        }
        binding.cbBlue.setOnCheckedChangeListener { _, b ->

            if (b) {

                colorsList.add(binding.cbBlue.text.toString())

            } else {

                colorsList.remove(binding.cbBlue.text.toString())

            }

        }
        binding.cbYellow.setOnCheckedChangeListener { _, b ->

            if (b) {

                colorsList.add(binding.cbYellow.text.toString())

            } else {

                colorsList.remove(binding.cbYellow.text.toString())

            }

        }
        binding.cbBlack.setOnCheckedChangeListener { _, b ->

            if (b) {

                colorsList.add(binding.cbBlack.text.toString())

            } else {

                colorsList.remove(binding.cbBlack.text.toString())

            }

        }
        binding.cbGray.setOnCheckedChangeListener { _, b ->

            if (b) {

                colorsList.add(binding.cbGray.text.toString())

            } else {

                colorsList.remove(binding.cbGray.text.toString())

            }

        }

        binding.cbS.setOnCheckedChangeListener { _, b ->

            if (b) {

                availableSizeList.add(resources.getString(R.string.s))

            } else {

                availableSizeList.remove(resources.getString(R.string.s))

            }

        }
        binding.cbX.setOnCheckedChangeListener { _, b ->

            if (b) {

                availableSizeList.add(resources.getString(R.string.x))

            } else {

                availableSizeList.remove(resources.getString(R.string.x))

            }

        }
        binding.cbM.setOnCheckedChangeListener { _, b ->

            if (b) {

                availableSizeList.add(resources.getString(R.string.m))

            } else {

                availableSizeList.remove(resources.getString(R.string.m))

            }

        }
        binding.cbXl.setOnCheckedChangeListener { _, b ->

            if (b) {

                availableSizeList.add(resources.getString(R.string.xl))

            } else {

                availableSizeList.remove(resources.getString(R.string.xl))

            }

        }

    }

    private fun uploadImages() {

        if (uploadCount < imagesList.size) {

            val filePath: StorageReference =
                FireRef.productPhotosStorage.child("${FireRef.myRef.push().key}.jpg")

            filePath.putFile(imagesList[uploadCount])
                .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                    val firebaseUri =
                        taskSnapshot.storage.downloadUrl
                    firebaseUri.addOnSuccessListener { uri: Uri ->

                        imagesUrlList.add(uri.toString())

                        uploadCount++
                        uploadImages()

                    }.addOnFailureListener { e: Exception ->

                        progressDialog.dismiss()
                        Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()

                    }
                }.addOnFailureListener { e: Exception ->

                    progressDialog.dismiss()
                    Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()

                }.addOnProgressListener {
                    val progress: Double =
                        100.0 * it.bytesTransferred / it.totalByteCount
                    progressDialog.setMessage("Uploading Photos " + uploadCount + "/" + imagesList.size + "      " + progress.toInt() + "%")

                }

        } else {

            progressDialog.dismiss()

            if (videoPath!!.isNotEmpty()) {

                progressDialog.show()
                uploadVideo()

            } else {

                uploadToFS()

            }

        }

    }

    private fun uploadVideo() {

        val file = Uri.fromFile(File(videoPath!!))


        val filePath: StorageReference =
            FireRef.productVideosStorage.child(file.lastPathSegment!!)


        filePath.putFile(file)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                val firebaseUri =
                    taskSnapshot.storage.downloadUrl
                firebaseUri.addOnSuccessListener { uri: Uri ->

                    videoURL = uri.toString()
                    uploadToFS()

                }.addOnFailureListener { e: Exception ->

                    progressDialog.dismiss()
                    Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()

                }
            }.addOnFailureListener { e: Exception ->

                progressDialog.dismiss()
                Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()

            }.addOnProgressListener {
                val progress: Double =
                    100.0 * it.bytesTransferred / it.totalByteCount
                progressDialog.setMessage("Uploading Video " + "1/1" + "      " + progress.toInt() + "%")

            }

    }

    private fun uploadToFS() {

        progressDialog.dismiss()
        loadingDialog.show()

        val id = FireRef.myRef.push().key

        val products = Products(
            id!!,
            productCategoriesIntent.id,
            imagesUrlList,
            colorsList,
            availableSizeList,
            videoURL!!,
            title,
            description,
            price,
            quantity,
            sizeFrom,
            sizeTo,
            usersData.uid,
            isAvailableSize
        )


        FireRef.PRODUCTS_REF.document(id)
            .set(products)
            .addOnCompleteListener {

                loadingDialog.dismiss()
                openPublishBottomSheet()

            }.addOnFailureListener {

                loadingDialog.dismiss()
                Toast.makeText(this, "" + it.message, Toast.LENGTH_SHORT).show()

            }

    }

    private fun validateFields(): Boolean {

        title = binding.etTitle.text.toString().trim()
        description = binding.etDes.text.toString().trim()
        price = binding.etPrice.text.toString().trim()
        quantity = binding.etQuantity.text.toString().trim()


        sizeFrom = binding.etFrom.text.toString().trim()
        sizeTo = binding.etTo.text.toString().trim()


        when {
            title.isEmpty() -> {

                binding.etTitle.error = resources.getString(R.string.required)

            }
            description.isEmpty() -> {

                binding.etDes.error = resources.getString(R.string.required)

            }
            price.isEmpty() -> {

                binding.etPrice.error = resources.getString(R.string.required)

            }
            quantity.isEmpty() -> {

                binding.etQuantity.error = resources.getString(R.string.required)

            }
            !isAvailableSize && availableSizeList.size == 0 -> {

                Toast.makeText(
                    this,
                    resources.getString(R.string.please_select_size),
                    Toast.LENGTH_SHORT
                ).show()

            }
            isAvailableSize && sizeFrom.isEmpty() -> {


                binding.etFrom.error = resources.getString(R.string.required)


            }
            isAvailableSize && sizeTo.isEmpty() -> {


                binding.etTo.error = resources.getString(R.string.required)


            }
            imagesList.size == 0 -> {

                Toast.makeText(
                    this,
                    resources.getString(R.string.select_images),
                    Toast.LENGTH_SHORT
                ).show()

            }
            colorsList.size == 0 -> {

                Toast.makeText(
                    this,
                    resources.getString(R.string.select_colors),
                    Toast.LENGTH_SHORT
                ).show()

            }
            else -> {

                isValid = true

            }
        }

        return isValid
    }

    private fun choseVideo() {

        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Video"), 12
        )


    }

    private fun choseImage() {

        val imagesSize = imagesList.size

        if (imagesSize < 10) {

            val count: Int = if (imagesSize > 0) {

                imagesSize - 10

            } else {

                10

            }

            FishBun.with(this)
                .setImageAdapter(GlideAdapter())
                .setIsUseDetailView(false)
                .setPickerCount(10)
                .setMaxCount(abs(count))
                .setMinCount(1)
                .setPickerSpanCount(6)
                .setActionBarColor(Color.parseColor("#015256"), Color.parseColor("#015256"), false)
                .setActionBarTitleColor(Color.parseColor("#ffffff"))
                .setAlbumSpanCount(2, 4)
                .setButtonInAlbumActivity(false)
                .setCamera(false)
                .setReachLimitAutomaticClose(false)
                .setHomeAsUpIndicatorDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_keyboard_arrow_left_white
                    )
                )
                .setAllViewTitle(resources.getString(R.string.all))
                .setActionBarTitle(resources.getString(R.string.app_name))
                .textOnImagesSelectionLimitReached(resources.getString(R.string.limit_reached))
                .textOnNothingSelected(resources.getString(R.string.nothing_selected))
                .setSelectCircleStrokeColor(Color.BLACK)
                .isStartInAllView(false)
                .exceptMimeType(listOf(MimeType.GIF))
                .startAlbum()

        } else {

            Toast.makeText(this, resources.getString(R.string.limit_reached), Toast.LENGTH_SHORT)
                .show()

        }

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == FishBun.FISHBUN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {

                    val uriList: ArrayList<Uri> = data.getParcelableArrayListExtra(INTENT_PATH)!!

                    if (imagesList.size > 0) {

                        imagesList.addAll(uriList)

                    } else {

                        imagesList = uriList

                    }

                    setAdapter()


                }
            }
        } else if (requestCode == 12) {

            val videoUri: Uri = data?.data!!
            videoPath = Functions.getPath(this, videoUri)!!

            val mMMR = MediaMetadataRetriever()
            mMMR.setDataSource(this, videoUri)

            binding.rlPlaceHolder.visibility = View.GONE
            binding.ivVideoThumbnail.visibility = View.VISIBLE
            binding.ivVideoThumbnail.setImageBitmap(mMMR.frameAtTime)

            Toast.makeText(this, "" + videoUri + "\n\n" + videoPath, Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("InflateParams")
    private fun openPublishBottomSheet() {

        val binding: PublishedSheetBinding = PublishedSheetBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        dialog.show()

        binding.btnDone.setOnClickListener {

            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)

        }

        dialog.setOnDismissListener {

            onBackPressed()

        }


    }

    private fun setAdapter() {

        addImagesAdapter = AddImagesAdapter(this, imagesList, this)
        binding.rvImages.adapter = addImagesAdapter


    }

    override fun removeImage(uri: Uri) {

        imagesList.remove(uri)
        setAdapter()


    }
}