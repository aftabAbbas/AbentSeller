package com.aftab.abentseller.Activities.StartUp

import android.Manifest
import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.aftab.abentseller.Activities.Main.HomeActivity
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.*
import com.aftab.abentseller.databinding.ActivityCreateAccountBinding
import com.github.dhaval2404.imagepicker.ImagePicker.Companion.with
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*

@Suppress("deprecation")
class CreateAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAccountBinding
    private lateinit var sh: SharedPref
    private lateinit var loadingDialog: LoadingDialog

    private var monthsList =
        arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    private var day = 0
    private var month = 0
    private var year = 0
    private lateinit var uid: String
    private lateinit var fName: String
    private lateinit var lName: String
    private lateinit var email: String
    private lateinit var phone: String
    private lateinit var dp: String
    private lateinit var password: String
    private lateinit var gender: String
    private lateinit var country: String
    private lateinit var city: String
    private lateinit var address: String
    private lateinit var dtb: String
    private lateinit var lat: String
    private lateinit var lng: String
    private lateinit var planDate: String
    private var isValidFields: Boolean = false
    private var selectedFileURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        clickListeners()


    }

    private fun initUI() {

        sh = SharedPref(this)
        binding.etAddress.isFocusableInTouchMode = false

        loadingDialog = LoadingDialog(this, "Uploading")
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun clickListeners() {

        binding.tvBirthday.setOnClickListener {

            openBDayDialog()

        }

        binding.etAddress.setOnTouchListener { _: View, event: MotionEvent ->
            val drawableStart = 2
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= binding.etAddress.right - binding.etAddress.compoundDrawables[drawableStart].bounds.width()
                ) {

                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        startActivityForResult(
                            Intent(this, ChooseLocationActivity::class.java)
                                .putExtra(Constants.FROM, Constants.CREATE_ACC), 10
                        )
                    } else {

                        startActivityForResult(Intent(this, AllowLocationActivity::class.java), 10)

                    }

                }
            }
            false
        }


        binding.ibPickImage.setOnClickListener {

            choseImage()

        }

        binding.btnFinish.setOnClickListener {

            isValidFields = false

            isValidFields = validateFields()

            if (isValidFields) {

                createUser()


            }


        }

    }

    private fun createUser() {

        loadingDialog.show()


        fName = sh.getString(Constants.FIRST_NAME)
        lName = sh.getString(Constants.LAST_NAME)
        email = sh.getString(Constants.EMAIL)
        phone = sh.getString(Constants.PHONE)
        password = sh.getString(Constants.PASSWORD)

        FireRef.mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {

                if (it.isSuccessful) {
                    uid = FireRef.mAuth.currentUser?.uid.toString()

                    if (selectedFileURI == null) {

                        dp = ""
                        uploadUserDataToFS()

                    } else {

                        uploadImage()

                    }
                } else {

                    loadingDialog.dismiss()
                    Toast.makeText(this, "Auth Error: " + it.exception?.message, Toast.LENGTH_SHORT)
                        .show()

                }

            }

    }


    private fun uploadUserDataToFS() {

        planDate = System.currentTimeMillis().toString()

        val users = Users(
            uid,
            fName,
            lName,
            email,
            phone,
            dp,
            password,
            gender.trim(),
            dtb,
            "",
            city,
            country,
            lng,
            lat,
            "",
            planDate,
            Constants.FREE,
            Constants.SELLER,
            Constants.ACTIVE,
            Constants.EMAIL,
            "",
            Constants.ENGLISH,
            Constants.ONLINE
        )

        FireRef.USERS_REF.document(uid)
            .set(users)
            .addOnCompleteListener {

                saveToSp(users)

            }


    }

    private fun saveToSp(users: Users) {

        sh.putBoolean(Constants.IS_LOGGED_IN, true)
        sh.putBoolean(Constants.IS_EMAIL_SENT, false)
        sh.putBoolean(Constants.IS_ADDED_INFO, true)
        sh.putBoolean(Constants.ORDER_NOTI, true)
        sh.putBoolean(Constants.CHAT_NOTI, true)
        sh.putBoolean(Constants.REVIEW_NOTI, true)
        sh.putBoolean(Constants.DELIVERY_NOTI, true)
        sh.saveUsers(users)

        /*sh.putString(Constants.UID,uid)
        sh.putString(Constants.FIRST_NAME, fName)
        sh.putString(Constants.LAST_NAME, lName)
        sh.putString(Constants.EMAIL, email)
        sh.putString(Constants.PHONE, phone)
        sh.putString(Constants.PASSWORD, password)
        sh.putString(Constants.DTB, dtb)
        sh.putString(Constants.GENDER, password)
        sh.putString(Constants.COUNTRY, country)
        sh.putString(Constants.CITY, city)
        sh.putString(Constants.DP, dp)
        sh.putString(Constants.PLAN_TYPE, Constants.FREE)
        sh.putString(Constants.PLAN_DATE, planDate)
        sh.putString(Constants.USER_TYPE, Constants.SELLER)
        sh.putString(Constants.USER_STATUS,Constants.ACTIVE)
        sh.putString(Constants.LOGIN_TYPE, Constants.EMAIL)*/


        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)


    }


    private fun uploadImage() {

        val filePath: StorageReference = FireRef.profileStorage.child("$uid.jpg")

        filePath.putFile(selectedFileURI!!)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                val firebaseUri =
                    taskSnapshot.storage.downloadUrl
                firebaseUri.addOnSuccessListener { uri: Uri ->

                    dp = uri.toString()
                    uploadUserDataToFS()

                }.addOnFailureListener { e: Exception ->

                    loadingDialog.dismiss()
                    Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()

                }
            }.addOnFailureListener { e: Exception ->

                loadingDialog.dismiss()
                Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()

            }

    }

    private fun choseImage() {
        with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    private fun validateFields(): Boolean {

        dtb = binding.tvBirthday.text.toString().trim()
        country = binding.etCountry.text.toString().trim()
        city = binding.etCity.text.toString().trim()
        address = binding.etAddress.text.toString().trim()
        gender = binding.spGender.selectedItem.toString()

        when {
            dtb == resources.getString(R.string.select) -> {

                Toast.makeText(
                    this,
                    resources.getString(R.string.please_select_dtb),
                    Toast.LENGTH_SHORT
                ).show()

            }
            country.isEmpty() -> {

                binding.etCountry.error = resources.getString(R.string.required)

            }
            city.isEmpty() -> {

                binding.etCity.error = resources.getString(R.string.required)

            }
            address.isEmpty() -> {

                binding.etAddress.error = resources.getString(R.string.required)

            }
            else -> {

                isValidFields = true

            }
        }

        return isValidFields
    }

    @SuppressLint("SetTextI18n")
    private fun openBDayDialog() {

        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dob_picker_dialog)
        dialog.setCanceledOnTouchOutside(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window
            ?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

        val monthPicker: NumberPicker = dialog.findViewById(R.id.month_picker)
        val dayPicker: NumberPicker = dialog.findViewById(R.id.day_picker)
        val yearPicker: NumberPicker = dialog.findViewById(R.id.year_picker)
        val tvCancel: TextView = dialog.findViewById(R.id.tv_cancel)
        val tvOk: TextView = dialog.findViewById(R.id.tv_ok)

        dayPicker.maxValue = 31
        dayPicker.minValue = 0
        dayPicker.wrapSelectorWheel = true
        dayPicker.setFormatter { i: Int ->
            String.format(
                "%02d",
                i
            )
        }

        dayPicker.value = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        monthPicker.minValue = 0
        monthPicker.maxValue = monthsList.size - 1
        monthPicker.displayedValues = monthsList
        monthPicker.wrapSelectorWheel = true

        // show current month in month picker.

        // show current month in month picker.
        monthPicker.value = Calendar.getInstance().get(Calendar.MONTH)


        yearPicker.minValue = 1900
        yearPicker.maxValue = Calendar.getInstance().get(Calendar.YEAR)
        // show current year in year picker.
        // show current year in year picker.
        yearPicker.value = Calendar.getInstance().get(Calendar.YEAR)

        tvCancel.setOnClickListener { dialog.dismiss() }

        tvOk.setOnClickListener {
            month = monthPicker.value
            day = dayPicker.value
            year = yearPicker.value
            binding.tvBirthday.text = monthsList[month] + " " + day + ", " + year
            dialog.dismiss()
        }

        dialog.show()

    }
/*
    override fun onResume() {
        super.onResume()

        from = intent.getStringExtra(Constants.FROM).toString()

        if (from == Constants.LOCATION) {

            lat = sh.getString(Constants.LAT)
            lng = sh.getString(Constants.LNG)

            address = Functions.getAddress(lat.toDouble(), lng.toDouble(), this)

            binding.etAddress.setText(address)


        }


    }*/

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {

            if (requestCode == 10) {

                lat = data?.getStringExtra(Constants.LAT).toString()
                lng = data?.getStringExtra(Constants.LNG).toString()

                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses =
                    geocoder.getFromLocation(lat.toDouble(), lng.toDouble(), 1)

                if (addresses.size > 0) {

                    val address = addresses[0].getAddressLine(0)

                    binding.etAddress.setText(address)

                }

            } else {

                if (data != null) {
                    selectedFileURI = data.data
                    if (selectedFileURI != null && Objects.requireNonNull<String>(selectedFileURI!!.path)
                            .isNotEmpty()
                    ) {

                        binding.ivUser.setImageURI(selectedFileURI)

                    } else {
                        Functions.showSnackBar(this, "Cannot Get this Image")
                    }
                }
            }

        }
    }


}