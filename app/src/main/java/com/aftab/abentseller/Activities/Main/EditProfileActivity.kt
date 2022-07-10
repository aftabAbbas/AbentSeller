package com.aftab.abentseller.Activities.Main

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.*
import com.aftab.abentseller.databinding.ActivityEditProfileBinding
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*


@Suppress("deprecation")
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var sh: SharedPref
    private lateinit var usersData: Users

    private var monthsList =
        arrayOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    private var day = 0
    private var month = 0
    private var year = 0
    private var isEdit = false
    private var isValid = false
    private lateinit var uid: String
    private lateinit var fName: String
    private lateinit var lName: String
    private lateinit var email: String
    private lateinit var dp: String
    private lateinit var gender: String
    private lateinit var country: String
    private lateinit var city: String
    private lateinit var dtb: String
    private lateinit var about: String
    private var selectedFileURI: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initUI()
        clickListeners()
    }

    private fun initUI() {

        sh = SharedPref(this)
        usersData = sh.getUsers()!!
        loadingDialog = LoadingDialog(this, "Uploading")

        setData()


    }

    private fun clickListeners() {

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        binding.tvEdit.setOnClickListener {

            if (isEdit) {

                isEdit = false
                binding.btnSave.visibility = View.GONE

            } else {

                isEdit = true
                binding.btnSave.visibility = View.VISIBLE

            }


            setUI(isEdit)

        }

        binding.ibPickImage.setOnClickListener {

            if (isEdit) {

                choseImage()

            } else {

                Toast.makeText(this, resources.getString(R.string.press_edit), Toast.LENGTH_SHORT)
                    .show()
            }

        }

        binding.etDtb.setOnClickListener {

            if (isEdit) {
                openBDayDialog()
            } else {
                Toast.makeText(this, resources.getString(R.string.press_edit), Toast.LENGTH_SHORT)
                    .show()
            }

        }

        binding.btnSave.setOnClickListener {

            isValid = false

            isValid = validateFields()

            if (isValid) {

                loadingDialog.show()

                if (selectedFileURI == null) {

                    dp = usersData.dp
                    updateUserDataToFS()

                } else {

                    uploadImage()

                }


            }


        }


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
            binding.etDtb.setText(monthsList[month] + " " + day + ", " + year)
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun uploadImage() {

        val filePath: StorageReference = FireRef.profileStorage.child("$uid.jpg")

        filePath.putFile(selectedFileURI!!)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                val firebaseUri =
                    taskSnapshot.storage.downloadUrl
                firebaseUri.addOnSuccessListener { uri: Uri ->

                    selectedFileURI = null
                    dp = uri.toString()
                    updateUserDataToFS()

                }.addOnFailureListener { e: Exception ->

                    loadingDialog.dismiss()
                    Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()

                }
            }.addOnFailureListener { e: Exception ->

                loadingDialog.dismiss()
                Toast.makeText(this, "" + e.message, Toast.LENGTH_SHORT).show()

            }


    }

    private fun updateUserDataToFS() {

        val userInfo = HashMap<String, Any>()
        userInfo[Constants.FIRST_NAME] = fName
        userInfo[Constants.LAST_NAME] = lName
        userInfo[Constants.GENDER] = gender
        userInfo[Constants.DP] = dp
        userInfo[Constants.DTB] = dtb
        userInfo[Constants.CITY] = city
        userInfo[Constants.COUNTRY] = country
        userInfo[Constants.ABOUT] = about


        FireRef.USERS_REF.document(uid)
            .update(userInfo)
            .addOnCompleteListener {

                saveToSp()

            }.addOnFailureListener {

                loadingDialog.dismiss()
                Toast.makeText(this, "FS Error: " + it.message, Toast.LENGTH_SHORT).show()

            }

    }

    private fun saveToSp() {

        val users: Users = usersData

        users.fname = fName
        users.lname = lName
        users.dp = dp
        users.gender = gender
        users.dtb = dtb
        users.country = country
        users.city = city
        users.about = about

        sh.saveUsers(users)

        loadingDialog.dismiss()

        binding.btnSave.visibility = View.GONE
        isEdit = false
        setUI(isEdit)

    }

    private fun choseImage() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start()
    }

    private fun validateFields(): Boolean {

        uid = usersData.uid
        fName = binding.etFName.text.toString().trim()
        lName = binding.etLName.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        dtb = binding.etDtb.text.toString().trim()
        country = binding.etCountry.text.toString().trim()
        city = binding.etCity.text.toString().trim()
        about = binding.etAbout.text.toString().trim()

        val selectedId: Int = binding.rgGender.checkedRadioButtonId
        val radioButton = findViewById<View>(selectedId) as RadioButton
        gender = radioButton.text.toString()


        if (fName.isEmpty()) {

            binding.etFName.error = resources.getString(R.string.required)

        } else if (lName.isEmpty()) {

            binding.etLName.error = resources.getString(R.string.required)

        } else if (email.isEmpty()) {

            binding.etEmail.error = resources.getString(R.string.required)

        } else if (!Functions.isEmailValid(email)) {

            binding.etEmail.error = resources.getString(R.string.valid_email)

        } else if (dtb.isEmpty()) {

            binding.etDtb.error = resources.getString(R.string.required)

        } else if (city.isEmpty()) {

            binding.etCity.error = resources.getString(R.string.required)

        } else if (country.isEmpty()) {

            binding.etCountry.error = resources.getString(R.string.required)

        } else if (about.isEmpty()) {

            binding.etAbout.error = resources.getString(R.string.required)

        } else {

            isValid = true

        }

        return isValid
    }

    private fun setData() {

        setUI(false)

        binding.etFName.setText(usersData.fname)
        binding.etLName.setText(usersData.lname)
        binding.etEmail.setText(usersData.email)
        binding.etDtb.setText(usersData.dtb)
        binding.etCity.setText(usersData.city)
        binding.etCountry.setText(usersData.country)
        binding.etAbout.setText(usersData.about)

        when (usersData.gender) {
            Constants.MAlE -> {

                binding.rgGender.check(R.id.rb_male)

            }
            Constants.FEMAlE -> {

                binding.rgGender.check(R.id.rb_female)

            }
            else -> {

                binding.rgGender.check(R.id.rb_others)

            }
        }

        if (selectedFileURI == null) {
            Glide.with(this).load(usersData.dp)
                .placeholder(R.drawable.place_holder)
                .into(binding.civUser)
        }


    }


    private fun setUI(isEdit: Boolean) {

        if (isEdit) {

            binding.etFName.isFocusableInTouchMode = true
            binding.etLName.isFocusableInTouchMode = true
            binding.etEmail.isFocusableInTouchMode = false
            binding.etDtb.isFocusableInTouchMode = false
            binding.etCity.isFocusableInTouchMode = true
            binding.etCountry.isFocusableInTouchMode = true
            binding.etAbout.isFocusableInTouchMode = true
            binding.rbMale.isEnabled = true
            binding.rbFemale.isEnabled = true
            binding.rbOthers.isEnabled = true


        } else {

            binding.etFName.isFocusableInTouchMode = false
            binding.etLName.isFocusableInTouchMode = false
            binding.etEmail.isFocusableInTouchMode = false
            binding.etDtb.isFocusableInTouchMode = false
            binding.etCity.isFocusableInTouchMode = false
            binding.etCountry.isFocusableInTouchMode = false
            binding.etAbout.isFocusableInTouchMode = false
            binding.rbMale.isEnabled = false
            binding.rbFemale.isEnabled = false
            binding.rbOthers.isEnabled = false

        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (data != null) {
                selectedFileURI = data.data
                if (selectedFileURI != null && Objects.requireNonNull<String>(selectedFileURI!!.path)
                        .isNotEmpty()
                ) {

                    binding.civUser.setImageURI(selectedFileURI)
                    binding.btnSave.visibility = View.VISIBLE

                } else {
                    Functions.showSnackBar(this, "Cannot Get this Image")
                }
            }
        }
    }
}