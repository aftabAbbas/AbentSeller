package com.aftab.abentseller.Fragments.Main

import android.app.ActionBar
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aftab.abentseller.Activities.Main.EditProfileActivity
import com.aftab.abentseller.Activities.Main.SettingsActivity
import com.aftab.abentseller.Activities.Settings.ChangeAddressActivity
import com.aftab.abentseller.Activities.Settings.ChangeLanguageActivity
import com.aftab.abentseller.Activities.StartUp.LoginActivity
import com.aftab.abentseller.Model.Users
import com.aftab.abentseller.R
import com.aftab.abentseller.Utils.Constants
import com.aftab.abentseller.Utils.FireRef
import com.aftab.abentseller.Utils.LoadingDialog
import com.aftab.abentseller.Utils.SharedPref
import com.aftab.abentseller.databinding.FragmentProfileBinding
import com.aftab.abentseller.databinding.LogoutDialogBinding
import com.bumptech.glide.Glide
import java.util.*

@Suppress("deprecation")
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var sh: SharedPref
    private lateinit var usersData: Users

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        initUI()
        clickListeners()

        return binding.root
    }

    private fun initUI() {

        loadingDialog = LoadingDialog(requireContext(), "Loading")

        sh = SharedPref(requireContext())
        usersData = sh.getUsers()!!
    }

    private fun setData() {

        var fName = usersData.fName
        var lName = usersData.lName
        fName = fName.substring(0, 1).toUpperCase(Locale.ROOT) + fName.substring(1).toLowerCase(
            Locale.ROOT
        )
        lName = lName.substring(0, 1).toUpperCase(Locale.ROOT) + lName.substring(1).toLowerCase(
            Locale.ROOT
        )
        val name = "$fName $lName"
        val email = usersData.email

        binding.tvName.text = name
        binding.tvEmail.text = email

        Glide.with(this).load(usersData.dp)
            .placeholder(R.drawable.place_holder)
            .into(binding.civUser)


    }

    override fun onResume() {
        super.onResume()
        setData()

    }

    private fun clickListeners() {

        binding.btnEditProfile.setOnClickListener {

            startActivity(Intent(requireContext(), EditProfileActivity::class.java))

        }

        binding.llSettings.setOnClickListener {

            startActivity(Intent(requireContext(), SettingsActivity::class.java))

        }

        binding.llLanguage.setOnClickListener {

            startActivity(Intent(requireContext(), ChangeLanguageActivity::class.java))

        }

        binding.llAddress.setOnClickListener {

            startActivity(Intent(requireContext(), ChangeAddressActivity::class.java))

        }

        binding.llLogout.setOnClickListener {

            openLogoutDialog()

        }
    }

    private fun openLogoutDialog() {

        val dialog = Dialog(requireContext())
        val binding = LogoutDialogBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)
        dialog.window!!.setLayout(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        binding.btnClose.setOnClickListener {

            dialog.dismiss()

        }

        binding.btnLogout.setOnClickListener {

            removeFCM()

        }


    }

    private fun removeFCM() {

        loadingDialog.show()

        val userInfo = HashMap<String, Any>()
        userInfo[Constants.FCM] = ""

        FireRef.USERS_REF.document(usersData.uid)
            .update(userInfo)
            .addOnCompleteListener {

                logout()

            }.addOnFailureListener {

                loadingDialog.dismiss()
                Toast.makeText(requireContext(), "FS Error: " + it.message, Toast.LENGTH_SHORT)
                    .show()

            }

    }

    private fun logout() {

        FireRef.mAuth.signOut()
        sh.clearPreferences()

        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)

    }
}